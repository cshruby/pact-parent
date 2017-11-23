package me.jcala.pact.user.service;

import me.jcala.pact.user.domain.Result;
import me.jcala.pact.user.domain.User;
import me.jcala.pact.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author zhipeng.zuo
 * Created on 17-11-23.
 */
@Service
public class UserServiceImpl implements UserService {

    private UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public ResponseEntity<Result<String>> login(String name, String pass) {
        User user = new User(name);
        Result<String> result = new Result<>();
        if (userMapper.select(user).size() < 1){
            result.setCode(0);
            result.setMessage("user not found");
            result.setData("fail");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
        user.setPass(pass);
        if (userMapper.select(user).size() < 1){
            result.setCode(2);
            result.setMessage("pass error");
            result.setData("fail");
            return new ResponseEntity<>(result, HttpStatus.OK);
        }else {
            result.setCode(1);
            result.setMessage("name pass valid");
            result.setData("success");
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

    }

}
