package me.jcala.pact.oauth.controller;

import me.jcala.pact.oauth.service.OauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhipeng.zuo
 * Created on 17-11-22.
 */
@RestController
public class OauthController {

    private OauthService oauthService;

    @Autowired
    public OauthController(OauthService oauthService) {
        this.oauthService = oauthService;
    }

    @GetMapping(value = "/check", produces = "application/json; charset=UTF-8")
    public ResponseEntity<?> checkOauth(@RequestHeader("name") String name,
                                     @RequestHeader("pass") String pass){
        return oauthService.checkOauth(name, pass);
    }

    @GetMapping(value = "/oauth", produces = "application/json; charset=UTF-8")
    public ResponseEntity<?> getOauthJwt(@RequestHeader("name") String name,
                              @RequestHeader("pass") String pass) {
        return oauthService.getOauthJwt(name, pass);

    }
}
