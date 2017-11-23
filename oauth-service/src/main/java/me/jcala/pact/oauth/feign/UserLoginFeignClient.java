package me.jcala.pact.oauth.feign;

import me.jcala.pact.oauth.domain.Result;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zhipeng.zuo
 * Created on 17-11-23.
 */
@FeignClient(value = "user-service")
public interface UserLoginFeignClient {

    @GetMapping("/login")
    Result<String> login(@RequestParam("name") String name, @RequestParam("pass") String pass);
}
