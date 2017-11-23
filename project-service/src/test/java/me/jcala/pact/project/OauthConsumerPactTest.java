package me.jcala.pact.project;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import me.jcala.pact.project.domain.Result;
import me.jcala.pact.project.feign.OauthFeignClient;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhipeng.zuo
 * Created on 17-11-22.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "oauth-service.ribbon.listOfServers: localhost:10002"
})
public class OauthConsumerPactTest {

    /**
     * 此处起了一个server,用于模拟feign调用的服务
     */
    @Rule
    public PactProviderRuleMk2 stubProvider = new PactProviderRuleMk2("oauth_provider",
            "127.0.0.1", 10002, this);

    @Autowired
    private OauthFeignClient oauthFeignClient;


    @Pact(state = "check oauth state", provider = "oauth_provider", consumer = "oauthFeignClient")
    public RequestResponsePact checkOauthPact(PactDslWithProvider pactDslWithProvider) {
        Map<String, String> headers = new HashMap<>();
        headers.put("name", "alice");
        headers.put("pass", "alice");

        return pactDslWithProvider
                .given("check oauth state")
                .uponReceiving("check oauth by name and pass")
                    .path("/check")
                    .method("GET")
                    .headers(headers)
                .willRespondWith()
                    .status(200)
                    .body("{\"code\":1,\"message\":\"success\",\"data\":\"success\"}",
                            "application/json; charset=UTF-8")
                .toPact();
    }

    @Pact(state = "get oauth jwt state", provider = "oauth_provider", consumer = "oauthFeignClient")
    public RequestResponsePact getOauthJwtPact(PactDslWithProvider pactDslWithProvider) {
        Map<String, String> headers = new HashMap<>();
        headers.put("name", "alice");
        headers.put("pass", "alice");

        return pactDslWithProvider
                .given("get oauth jwt state")
                .uponReceiving("get jwt by name and pass")
                    .method("GET")
                    .headers(headers)
                    .path("/oauth")
                .willRespondWith()
                    .status(200)
                    .body("{\"code\":1,\"message\":\"success\",\"data\":\"abc\"}",
                            "application/json; charset=UTF-8")
                .toPact();
    }

    @Test
    @PactVerification(fragment = "checkOauthPact")
    public void verifyCheckOauthPact() {
        Result<String> result = oauthFeignClient.checkOauth("alice","alice");
        System.out.println(result);
        Assert.assertEquals(1, result.getCode());
    }

    @Test
    @PactVerification(fragment = "getOauthJwtPact")
    public void verifyGetOauthJwtPact() {
        Result<String> result  = oauthFeignClient.getOauthJwt("alice","alice");
        System.out.println(result);
        Assert.assertEquals(1, result.getCode());
    }
}
