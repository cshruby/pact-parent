package me.jcala.pact.project.mock.feign;

import me.jcala.pact.project.mock.domain.Result;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(value = "oauth-service")
public interface OauthFeignClient {

  @RequestMapping(method = RequestMethod.GET, path = "/check")
  Result<String> checkOauth(@RequestHeader("name") String name, @RequestHeader("pass") String pass);

  @RequestMapping(method = RequestMethod.GET, path = "/oauth")
  Result<String> getOauthJwt(@RequestHeader("name") String name, @RequestHeader("pass") String pass);

}
