package com.cw.swagger.controller;

import com.cw.swagger.config.BaseResult;
import com.cw.swagger.entity.User;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.*;

@Api(value = "用户管理", description = "用户管理API", position = 100, protocols = "http")
@RestController
@RequestMapping(value = "/user")
public class UserController {
    static Map<Long, User> users = Collections.synchronizedMap(new HashMap<>());

    @ApiOperation(
            value = "获取用户列表",
            notes = "查询用户")
    @RequestMapping(value = {""}, method = RequestMethod.GET)
    @ApiResponses({
            @ApiResponse(code = 100, message = "异常数据")
    })
    public List<User> getUserList() {
        return new ArrayList<>(users.values());
    }

    @ApiOperation(
            value = "创建用户信息",
            notes = "新增用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "age", value = "年龄", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "ipAdd", value = "ip地址", required = false, dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "", method = RequestMethod.POST)
    public BaseResult<User> postUser(@ApiIgnore User user) {
        users.put(user.getId(), user);
        return BaseResult.successWithData(user);
    }

    @ApiOperation(
            value = "获取详细信息",
            notes = "根据ID获取用户详细信息")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable Long id) {
        return users.get(id);
    }

    @ApiOperation(
            value = "更新用户信息",
            notes = "根据ID更新用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "age", value = "年龄", required = true, dataType = "Long", paramType = "query")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public BaseResult<User> putUser(@PathVariable Long id, @ApiIgnore User user) {
        User u = users.get(id);
        u.setName(user.getName());
        u.setAge(user.getAge());
        users.put(id, u);
        return BaseResult.successWithData(u);
    }
    @ApiOperation(
            value = "删除用户信息",
            notes = "根据ID删除用户")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String deleteUser(@PathVariable Long id) {
        users.remove(id);
        return "success";
    }

}