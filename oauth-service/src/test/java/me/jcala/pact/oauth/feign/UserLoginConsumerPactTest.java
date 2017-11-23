package me.jcala.pact.oauth.feign;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import me.jcala.pact.oauth.domain.Result;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zhipeng.zuo
 * Created on 17-11-23.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "user-service.ribbon.listOfServers: localhost:10001"
})
public class UserLoginConsumerPactTest {
    /**
     * 此处起了一个server,用于模拟feign调用的服务
     */
    @Rule
    public PactProviderRuleMk2 stubProvider = new PactProviderRuleMk2("user_provider",
            "127.0.0.1", 10001, this);

    @Autowired
    private UserLoginFeignClient userLoginFeignClient;


    @Pact(state = "user login state", provider = "user_provider", consumer = "userLoginFeignClient")
    public RequestResponsePact loginPact(PactDslWithProvider pactDslWithProvider) {
        return pactDslWithProvider
                .given("user login state")
                .uponReceiving("user login by name and pass")
                    .path("/login")
                    .matchQuery("name", "alice")
                    .matchQuery("pass", "alice")
                    .method("GET")
                .willRespondWith()
                    .status(200)
                    .body("{\"code\":1,\"message\":\"name pass valid\",\"data\":\"success\"}",
                            "application/json; charset=UTF-8")
                .toPact();
    }


    @Test
    @PactVerification(fragment = "loginPact")
    public void verifyLoginPact() {
        Result<String> result = userLoginFeignClient.login("alice","alice");
        System.out.println(result);
        Assert.assertEquals(1, result.getCode());
    }
}
