package com.biubiu.sls.handler;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogContent;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.GetLogsRequest;
import com.aliyun.openservices.log.response.GetLogsResponse;
import com.biubiu.SpringContextUtil;
import com.biubiu.sls.annotation.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author ：张音乐
 * @date ：Created in 2021/5/30 上午10:36
 * @description：日志查询代理类
 * @email: zhangyule1993@sina.com
 * @version:
 */
public class MapperInvocationHandler implements InvocationHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(MapperInvocationHandler.class);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 判断方法上是否有 @SlsSelect注解
        SlsSelect select = method.getDeclaredAnnotation(SlsSelect.class);
        // 判断方法上是否有 @SlsTable注解
        SlsTable table = method.getDeclaredAnnotation(SlsTable.class);
        if(select != null && table != null) {
            return doSelect(select, table, method, args);
        }
        return null;
    }

    private Object doSelect(SlsSelect select, SlsTable table, Method method, Object[] args) throws ClassNotFoundException, IllegalAccessException, LogException, InstantiationException {
        // 获取select注解上的sql语句
        String hql = select.value();
        // 获取table注解上传入的目标数据源
        String project = table.project();
        String logStore = table.logStore();
        if(StringUtils.isBlank(hql) || StringUtils.isBlank(project) || StringUtils.isBlank(logStore)) {
            return null;
        }
        // 获取方法上的参数名，和实际传入的参数值绑定在一起
        ConcurrentHashMap<String, Object> actualParamsMap = getActualParamsMap(method, args);
        // 将sql语句里#占位符里的参数替换成实际 value
        String newSQL = questionMark(hql, actualParamsMap);
        LOGGER.info("Aliyun Sls Execute SQL={}" , newSQL);

        // 使用反射获取方法返回值类型
        Class<?> returnType = method.getReturnType();
        // 判断返回结果是否是集合
        boolean asList = Collection.class.isAssignableFrom(returnType);
        if(asList) {
            // 处理 List 结果集
            return doList(method, actualParamsMap, project, logStore, newSQL);
        } else {
            // 处理单条数据结果
            return doOne(method, actualParamsMap, project, logStore, newSQL, returnType);
        }
    }

    private Object doList(Method method, ConcurrentHashMap<String, Object> actualParamsMap, String project, String logStore, String newSQL) throws ClassNotFoundException, LogException, IllegalAccessException, InstantiationException {
        // System.out.println("集合处理");
        // 创建返回结果结合
        List<Object> list = Collections.synchronizedList(new ArrayList<>());
        // 获取返回的结果集合中对象的类型
        // Type type = method.getAnnotatedReturnType().getType();
        ParameterizedType parameterizedType = (ParameterizedType) method.getAnnotatedReturnType().getType();
        Type type = parameterizedType.getActualTypeArguments()[0];
        // 获取对象类型clazz
        Class<?> clazz = Class.forName(type.getTypeName());
        // 获取sls client
        Client client = SpringContextUtil.getBean("slsClient");
        // 获取所有contents
        List<Map<String, String>> contents = selectList(client, project, logStore, newSQL, (Integer) actualParamsMap.getOrDefault("from", 0), (Integer)actualParamsMap.getOrDefault("to", 0));
        // 便利结果
        for(Map<String, String> content : contents) {
            // 每一条数据实例化一个对象
            Object record = clazz.newInstance();
            // 获取所有字段属性
            Field[] fields = record.getClass().getDeclaredFields();
            // 遍历所有字段，每个字段设置 value
            for(Field field : fields) {
                String fieldName = field.getName();
                Object fieldValue = content.get(fieldName);
                field.setAccessible(true);
                if (field.getType() == String.class) {
                    field.set(record, fieldValue);
                }
                //
                else if (field.getType() == Integer.class) {
                    field.set(record, Integer.parseInt(fieldValue.toString()));
                }

                else if (field.getType() == Long.class) {
                    field.set(record, Long.parseLong(fieldValue.toString()));
                }
                // TODO 省略... 待完善
                else {
                    field.set(record, fieldValue);
                }
            }
            list.add(record);
        }
        return list;
    }

    private Object doOne(Method method, ConcurrentHashMap<String, Object> actualParamsMap, String project, String logStore, String newSQL, Class<?> returnType) throws LogException, IllegalAccessException, InstantiationException {
        // 获取当前返回值类型的所有属性字段
        Field[] declaredFields = returnType.getDeclaredFields();
        // 获取 spring 初始化的 sls client信息
        Client client = SpringContextUtil.getBean("slsClient");
        // 获取所有contents
        List<Map<String, String>> contents = selectList(client, project, logStore, newSQL, (Integer) actualParamsMap.getOrDefault("from", 0), (Integer)actualParamsMap.getOrDefault("to", 0));
        if(contents.size() == 0) {
            return null;
        }
        Map<String, String> content = contents.get(0);
        // 实例化一个对应类型的对象
        Object record = returnType.newInstance();
        // 使用反射对对象的每个字段进行赋值
        for(Field field : declaredFields) {
            String fieldName = field.getName();
            Object fieldValue = content.get(fieldName);
            field.setAccessible(true);
            field.set(record, fieldValue);
        }
        return record;
    }


    private ConcurrentHashMap<String, Object> getActualParamsMap(Method method,  Object[] args) {
        //
        ConcurrentHashMap<String, Object> paramsMap = new ConcurrentHashMap<>();
        // 获取方法上的参数

        Parameter[] parameters = method.getParameters();
        for(int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            SlsParam param = parameter.getDeclaredAnnotation(SlsParam.class);
            if(param != null) {
                // 参数名称
                String paramName = param.value();
                // 实际传入的参数值
                Object paramValue = args[i];
                //
                paramsMap.put(paramName, paramValue);
            }
            SlsFrom from = parameter.getDeclaredAnnotation(SlsFrom.class);
            if(from != null) {
                // 参数名称
                String paramName = "from";
                // 参数值
                Object paramValue = args[i];
                paramsMap.put(paramName, paramValue);
            }
            SlsTo to = parameter.getDeclaredAnnotation(SlsTo.class);
            if(to != null) {
                // 形参
                String paramName = "to";
                // 参数值
                Object paramValue = args[i];
                paramsMap.put(paramName, paramValue);
            }
        }
        return paramsMap;
    }

    /**
     * 将SQL语句的占位符号替换成 实际的 value
     * @param sql
     * @param actualParamsMap
     * @return
     */
    public static String questionMark(String sql, ConcurrentHashMap<String, Object> actualParamsMap) {
        for(Map.Entry<String, Object> entry : actualParamsMap.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue().toString();
            sql = sql.replace("#{" + name + "}", "'" + value + "'");
        }
        return sql;
    }

    /**
     * sls数据处理
     * @param client
     * @param project
     * @param logStore
     * @param sql
     * @param from
     * @param to
     * @return
     * @throws LogException
     */
    public static List<Map<String, String>> selectList(Client client, String project, String logStore, String sql, Integer from, Integer to) throws LogException {
        GetLogsRequest getLogsRequest = new GetLogsRequest(project, logStore, from, to, null,  sql);
        GetLogsResponse response = client.GetLogs(getLogsRequest);
        List<QueriedLog> queriedLogs = response.GetLogs();
        List<Map<String, String>> list = Collections.synchronizedList(new ArrayList<>());
        for(QueriedLog queriedLog : queriedLogs) {
            List<LogContent> logContents = queriedLog.GetLogItem().GetLogContents();
            Map<String, String> contentMap = logContents.stream().collect(Collectors.toMap(LogContent::GetKey, LogContent::GetValue, (old, cur) -> cur));
            list.add(contentMap);
        }
        return list;
    }


}
