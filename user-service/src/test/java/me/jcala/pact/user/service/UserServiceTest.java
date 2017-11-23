package me.jcala.pact.user.service;

import me.jcala.pact.user.domain.Result;
import me.jcala.pact.user.domain.User;
import me.jcala.pact.user.mapper.UserMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * @author zhipeng.zuo
 * Created on 17-11-23.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService = new UserServiceImpl(userMapper);

    @Test
    public void loginTest() {
        when(userMapper.select(any(User.class)))
                .thenReturn(Collections.singletonList(new User(10L,"alice","alice")));
        Result<String> response = userService.login("alice","alice").getBody();
        System.out.println(response);
        Assert.assertEquals(1, response.getCode());
    }
}
