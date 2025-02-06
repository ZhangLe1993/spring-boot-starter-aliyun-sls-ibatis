package com.biubiu.sls;

import com.biubiu.sls.annotation.SlsMapper;
import com.biubiu.sls.annotation.SlsMapperScan;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ：张音乐
 * @date ：Created in 2021/5/30 上午10:21
 * @description：
 * @email: zhangyule1993@sina.com
 * @version:
 */
public class SlsMapperScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    public SlsMapperScannerRegistrar() {
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {

        AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(SlsMapperScan.class.getName()));

        List<String> basePackages = new ArrayList<>();

        String[] packages = attributes.getStringArray("basePackages");

        for(int i = 0; i < packages.length; i++) {
            String pkg = packages[i];
            if(StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }

        for(String pkg : basePackages) {
            try {
                registerProxy(pkg, beanDefinitionRegistry);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 扫描包，并实例化bean
     * @param pkg
     * @param registry
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void registerProxy(String pkg, BeanDefinitionRegistry registry) throws IOException, ClassNotFoundException {
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        //
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        // 这里特别注意一下，类路径必须这样写
        // 获取指定package下的所有类
        Resource[] resources = resourcePatternResolver.getResources("classpath*:/" + pkg.replace(".", File.separator) + File.separator + "*.class");

        MetadataReaderFactory metadata = new SimpleMetadataReaderFactory();
        for(Resource resource : resources) {
            MetadataReader metadataReader = metadata.getMetadataReader(resource);
            ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
            sbd.setResource(resource);
            sbd.setSource(resource);
            // sbd.setScope("singleton");
            candidates.add(sbd);
        }
        for(BeanDefinition beanDefinition : candidates) {
            String className = beanDefinition.getBeanClassName();
            // 扫描注解
            Class<?> beanClass = Class.forName(className);
            Annotation[] annotations = beanClass.getAnnotations();
            for(Annotation annotation : annotations) {
                if(annotation.annotationType() == SlsMapper.class) {
                    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
                    GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
                    definition.getPropertyValues().add("mapperInterface", definition.getBeanClassName());
                    // 设置工厂class
                    definition.setBeanClass(SlsMapperFactoryBean.class);
                    definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
                    // 注册bean名，一般以类名首字母小写
                    String simpleName = org.apache.commons.lang3.StringUtils.substringAfterLast(className, ".");
                    String firstName = simpleName.substring(0, 1);
                    String firstLowerName = firstName.toLowerCase();
                    String afterName = org.apache.commons.lang3.StringUtils.substringAfterLast(simpleName, firstName);

                    // definition.setBeanClassName(firstLowerName + afterName);
                    // definition.setAutowireCandidate(PatternMatchUtils.simpleMatch("", ""));
                    registry.registerBeanDefinition(firstLowerName + afterName, definition);
                }
            }
        }
    }
}
