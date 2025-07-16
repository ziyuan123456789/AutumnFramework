package org.example.Controller;

import org.example.Annotations.CheckParameter;
import org.example.Annotations.EnableAop;
import org.example.FrameworkUtils.AutumnCore.Annotation.Lazy;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyController;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.Orm.MyRedis.MyRedisTemplate;
import org.example.mapper.UpdateMapper;
import org.example.mapper.UserMapper;
import org.example.service.LoginService;
import org.example.service.TransactionService;

import java.sql.SQLException;

/**
 * @author ziyuan
 * @since 2025.06
 */
@MyController
@MyRequestMapping("/orm")
public class AutumnOrmController {

    @MyAutoWired
    private TransactionService transactionService;

    @MyAutoWired
    private UpdateMapper updateMapper;

    @MyAutoWired
    @Lazy
    private MyRedisTemplate myRedisTemplate;

    @MyAutoWired
    private LoginService loginService;

    @MyAutoWired
    private UserMapper userMapper;


    //测试事务
    @MyRequestMapping("/transaction")
    public String transactionTest() throws SQLException {
        return transactionService.transactionTest();
    }


    //测试MineBatis增删改查
    @MyRequestMapping("/crud")
    public Integer crudKing(String method) {
        return switch (method) {
            case "insert" -> updateMapper.insertUser("test", "0", "test", "收到");
            case "update" -> updateMapper.updateUserById("test1", "0", "test3", 1);
            case "delete" -> updateMapper.deleteUserById(1);
            default -> Integer.MAX_VALUE;
        };
    }

    //测试redis
    @MyRequestMapping("/redis")
    public String redis() {
        myRedisTemplate.init();
        myRedisTemplate.set("test", "test");
        return myRedisTemplate.toString() + "\n" + myRedisTemplate.get("test");
    }

    //测试数据库功能
    @EnableAop
    @MyRequestMapping("/Login")
    public String login(@CheckParameter String username,
                        String password) {
        if (loginService.checkLogin(username, password)) {
            return "登录成功";

        } else {
            return "登录失败";
        }

    }

    //测试数据库功能
    @MyRequestMapping("/getall")
    public String getAll() {
        return userMapper.getAllUser(0).toString();
    }


}
