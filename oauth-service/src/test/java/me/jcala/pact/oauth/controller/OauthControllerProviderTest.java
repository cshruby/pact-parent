package me.jcala.pact.oauth.controller;

import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.loader.PactBrokerAuth;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.spring.SpringRestPactRunner;
import me.jcala.pact.oauth.domain.Result;
import me.jcala.pact.oauth.feign.UserLoginFeignClient;
import me.jcala.pact.oauth.service.OauthService;
import me.jcala.pact.oauth.service.OauthServiceImpl;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * @author zhipeng.zuo
 * Created on 17-11-22.
 */


@RunWith(SpringRestPactRunner.class)
@Provider("oauth_provider")
@PactBroker(host = "localhost", port = "80",
        authentication = @PactBrokerAuth(username = "pact", password = "pact"))
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {
        "server.port=10002"
})
public class OauthControllerProviderTest {

    @MockBean
    private UserLoginFeignClient userLoginFeignClient;

    @InjectMocks
    private OauthService oauthService = new OauthServiceImpl(userLoginFeignClient);

    @InjectMocks
    private OauthController oauthController = new OauthController(oauthService);

    @TestTarget
    public final Target target = new HttpTarget(10002);

    @State("check oauth state")
    public void toCreateCheckOauthState() {
        when(userLoginFeignClient.login("alice", "alice"))
                .thenReturn(new Result<>(1,"name pass valid","success"));
    }

    @State("get oauth jwt state")
    public void toCreateGetOauthJwtState() {
        when(userLoginFeignClient.login("alice", "alice"))
                .thenReturn(new Result<>(1,"name pass valid","success"));
        /*
         造成失败的情况
         when(userLoginFeignClient.login("alice", "alice"))
                .thenReturn(new Result<>(0,"name pass not valid","fail"));
         */
    }
}
