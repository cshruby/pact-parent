package me.jcala.pact.oauth.service;

import me.jcala.pact.oauth.domain.Result;
import me.jcala.pact.oauth.feign.UserLoginFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author zhipeng.zuo
 * Created on 17-11-22.
 */
@Service
public class OauthServiceImpl implements OauthService {

    private UserLoginFeignClient userLoginFeignClient;

    @Autowired
    public OauthServiceImpl(UserLoginFeignClient userLoginFeignClient) {
        this.userLoginFeignClient = userLoginFeignClient;
    }

    @Override
    public  ResponseEntity<Result<String>> checkOauth(String name, String pass) {
        Result<String> re = userLoginFeignClient.login(name, pass);
        Result<String> result = new Result<>();
        if (re.getCode() == 1) {
            result.setCode(1);
            result.setMessage("success");
            result.setData("success");
            return new ResponseEntity<>(result, HttpStatus.OK);
        }else {
            result.setCode(0);
            result.setMessage("fail");
            result.setData("fail");
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @Override
    public  ResponseEntity<Result<String>> getOauthJwt(String name, String pass) {
        Result<String> re = userLoginFeignClient.login(name, pass);
        Result<String> result = new Result<>();
        if (re.getCode() == 1) {
            result.setCode(1);
            result.setMessage("success");
            result.setData("abc");
            return new ResponseEntity<>(result, HttpStatus.OK);
        }else {
            result.setCode(0);
            result.setMessage("fail");
            result.setData("");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
    }

}
