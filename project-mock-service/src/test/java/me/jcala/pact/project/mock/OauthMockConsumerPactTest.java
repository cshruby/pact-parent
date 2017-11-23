package me.jcala.pact.project.mock;

import com.atlassian.ta.wiremockpactgenerator.WireMockPactGenerator;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
/**
 * @author zhipeng.zuo
 * Created on 17-11-23.
 */
public class OauthMockConsumerPactTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final WireMockServer wireMockServer = new WireMockServer(10002);

    private RestTemplate restTemplate = new RestTemplate();

    @Before
    public void setRequestHeaderAndMockServer() {
        wireMockServer.addMockServiceRequestListener(
                WireMockPactGenerator
                        .builder("oauthFeignClient", "oauth_provider")
                        .build()
        );

        restTemplate.getInterceptors().add(
                (HttpRequest request, byte[] body, ClientHttpRequestExecution execution) -> {
                request.getHeaders().add("name", "alice");
                request.getHeaders().add("pass", "alice");
                return execution.execute(request, body);
        });
    }

    @After
    public void mockServerStop() {
        wireMockServer.stop();
    }

    @Test
    public void checkOauthPact() {
        wireMockServer.addStubMapping(get(urlEqualTo("/check"))
                .inScenario("check oauth")
//                .whenScenarioStateIs("check oauth state")
                .withHeader("name", new EqualToPattern("alice"))
                .withHeader("pass", new EqualToPattern("alice"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"code\":1,\"message\":\"success\",\"data\":\"success\"}")
                         .withHeader("Content-Type","application/json; charset=UTF-8"))
                .build()
        );
        wireMockServer.addStubMapping(get(urlEqualTo("/oauth"))
                .inScenario("get oauth jwt")
                //.whenScenarioStateIs("get oauth jwt state")
                .withHeader("name", new EqualToPattern("alice"))
                .withHeader("pass", new EqualToPattern("alice"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"code\":1,\"message\":\"success\",\"data\":\"abc\"}")
                        .withHeader("Content-Type","application/json; charset=UTF-8"))
                .build()
        );
        wireMockServer.start();
        restTemplate.getForObject("http://localhost:10002/check", String.class);
        restTemplate.getForObject("http://localhost:10002/oauth", String.class);
    }
}
