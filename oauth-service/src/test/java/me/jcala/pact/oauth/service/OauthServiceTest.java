package me.jcala.pact.oauth.service;

import me.jcala.pact.oauth.BaseMockTest;
import me.jcala.pact.oauth.domain.Result;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zhipeng.zuo
 * Created on 17-11-23.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OauthServiceTest extends BaseMockTest {

    @InjectMocks
    private OauthService oauthService = new OauthServiceImpl(userLoginFeignClient);

    @Test
    public void checkOauthTest() {
        Result<String> response =oauthService.checkOauth("alice","alice").getBody();
        System.out.println(response);
        Assert.assertEquals(1, response.getCode());
    }

    @Test
    public void getOauthJwt() {
        Result<String> response =oauthService.getOauthJwt("alice","alice").getBody();
        System.out.println(response);
        Assert.assertEquals(1, response.getCode());
    }
}
