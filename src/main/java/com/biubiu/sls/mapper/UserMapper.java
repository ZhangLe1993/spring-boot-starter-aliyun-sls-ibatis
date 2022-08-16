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

    @SlsSelect("*|select userId, name from user ")
    @SlsTable(project = "dms-app", logStore = "dms-app-prod")
    List<User> select(@SlsFrom int from, @SlsTo int to);

    @SlsSelect("*|select userId, name from user where userId=#{userId} ")
    @SlsTable(project = "dms-app", logStore = "dms-app-prod")
    User getUserById(@SlsParam("userId") String userId, @SlsFrom int from, @SlsTo int to);

}
