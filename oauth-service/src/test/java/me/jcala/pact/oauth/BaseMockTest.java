package me.jcala.pact.oauth;

import me.jcala.pact.oauth.domain.Result;
import me.jcala.pact.oauth.feign.UserLoginFeignClient;
import org.junit.Before;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

/**
 * @author zhipeng.zuo
 * Created on 17-11-23.
 */
public class BaseMockTest {

    @Mock
    protected UserLoginFeignClient userLoginFeignClient;

    @Before
    public void setMockData() {
        when(userLoginFeignClient.login("alice","alice")).thenReturn(
                new Result<>(1, "success",null)
        );
    }
}
