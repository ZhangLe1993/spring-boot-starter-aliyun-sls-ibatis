### 阿里云SLS日志查询框架

![index.png](https://socialify.git.ci/ZhangLe1993/spring-boot-starter-aliyun-sls-ibatis/image?description=1&font=Rokkitt&forks=1&issues=1&language=1&name=1&pattern=Signal&pulls=1&stargazers=1&theme=Light)

### 使用示例

#####  本地安装 install

```shell
git clone https://github.com/ZhangLe1993/spring-boot-starter-aliyun-sls-ibatis.git
cd spring-boot-starter-aliyun-sls-ibatis
mvn install
```

#####  springboot项目中引入

```xml
<dependency>
    <groupId>com.biubiu</groupId>
    <artifactId>spring-boot-starter-aliyun-sls-ibatis</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```
#####  在配置文件中配置sls的配置信息

```properties
# sls的 endpoint示例
sls.ibatis.endpoint=cn-beijing.log.aliyuncs.com
# sls.ibatis.endpoint=cn-hangzhou.log.aliyuncs.com
# sls的 ak/sk
sls.ibatis.accessKeyId=xxxxxx
sls.ibatis.accessKeySecret=xxxxxx
```

##### 启动类上配置 mapper 扫描

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 包名就是自定义注解 SlsMapper的 mapper 的包路径 ，示例如下
@SpringBootApplication
@SlsMapperScan(basePackages = {"com.biubiu.sls.mapper1", "com.biubiu.sls.mapper2"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
```

##### 定义一个对象

```java
/**
 * @author ：张音乐
 * @date ：Created in 2021/5/30 下午12:46
 * @description：测试对象
 * @email: zhangyule1993@sina.com
 * @version:
 */
public class User {

    private String id;

    private String name;

    public User() {
    }

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

##### 定义一个Mapper接口

```java

package com.biubiu.sls.mapper;

import com.biubiu.sls.annotation.*;

import java.util.List;

/**
 * @author ：张音乐
 * @date ：Created in 2021/5/30 下午12:45
 * @description： 测试 Mapper
 * @email: zhangyule1993@sina.com
 * @version:
 */
@SlsMapper
public interface UserMapper {

    @SlsSelect("* | select userId, name from log ")
    @SlsTable(project = "dms-app", logStore = "dms-app-prod")
    List<User> select(@SlsFrom int from, @SlsTo int to);

    @SlsSelect("* | select userId, name from log where userId=#{userId} ")
    @SlsTable(project = "dms-app", logStore = "dms-app-prod")
    User getUserById(@SlsParam("userId") String userId, @SlsFrom int from, @SlsTo int to);

}

```


##### 在 Service中使用

```java

import com.biubiu.sls.mapper.User;
import com.biubiu.sls.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    public void testSelect() {
        int start = new Date("2022-08-16 00:00:00").getTime() / 1000;
        int end = new Date("2022-08-17 00:00:00").getTime() / 1000;

        // 批量查询 示例
        List<User> list = userMapper.select(start, end);

        // 单条查询 示例
        User user = userMapper.getUserById("105281", start, end);
        
    }
}

```

##### 其他文档： 阿里云sls官网文档

[阿里云sls SQL语法文档地址](https://help.aliyun.com/document_detail/322174.html)
