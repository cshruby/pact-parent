package me.jcala.pact.user.service;

import me.jcala.pact.user.domain.Result;
import org.springframework.http.ResponseEntity;

/**
 * @author zhipeng.zuo
 * Created on 17-11-23.
 */
public interface UserService {

    ResponseEntity<Result<String>> login(String name, String pass);

}
