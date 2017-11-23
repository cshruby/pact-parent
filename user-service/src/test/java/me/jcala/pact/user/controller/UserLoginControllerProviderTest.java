package me.jcala.pact.user.controller;

import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.loader.PactBrokerAuth;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.spring.SpringRestPactRunner;
import me.jcala.pact.user.domain.User;
import me.jcala.pact.user.mapper.UserMapper;
import me.jcala.pact.user.service.UserService;
import me.jcala.pact.user.service.UserServiceImpl;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * @author zhipeng.zuo
 * Created on 17-11-22.
 */


@RunWith(SpringRestPactRunner.class)
@Provider("user_provider")
@PactBroker(host = "localhost", port = "80",
        authentication = @PactBrokerAuth(username = "pact", password = "pact"))
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {
        "server.port=10001"
})
public class UserLoginControllerProviderTest {

    @MockBean
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService = new UserServiceImpl(userMapper);

    @InjectMocks
    private UserLoginController userLoginController = new UserLoginController(userService);


    @TestTarget
    public final Target target = new HttpTarget(10001);

    @State("user login state")
    public void toCreateCheckOauthState() {
        when(userMapper.select(any(User.class)))
                .thenReturn(Collections.singletonList(new User(10L,"alice","alice")));
    }

}
