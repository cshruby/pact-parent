package me.jcala.pact.oauth.service;

import me.jcala.pact.oauth.domain.Result;
import org.springframework.http.ResponseEntity;

/**
 * @author zhipeng.zuo
 * Created on 17-11-22.
 */
public interface OauthService {

    ResponseEntity<Result<String>> checkOauth(String name, String pass);

    ResponseEntity<Result<String>> getOauthJwt(String name, String pass);
}
