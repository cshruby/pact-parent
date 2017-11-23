package me.jcala.pact.user.controller;

import me.jcala.pact.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhipeng.zuo
 * Created on 17-11-23.
 */
@RestController
public class UserLoginController {

    private UserService userService;

    @Autowired
    public UserLoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/login", produces = "application/json; charset=UTF-8")
    public ResponseEntity<?> checkOauth(@RequestParam("name") String name,
                                        @RequestParam("pass") String pass){
        return userService.login(name, pass);
    }
}
