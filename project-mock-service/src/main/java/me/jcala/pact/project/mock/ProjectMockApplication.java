package me.jcala.pact.project.mock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * @author zhipeng.zuo
 * Created on 17-11-23.
 */
@SpringBootApplication
@EnableFeignClients
public class ProjectMockApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectMockApplication.class, args);
    }
}
