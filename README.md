## spring cloud contract与传统pact对比
### 总体

```
spring cloud contract
优点:
1. groovy编写契约，使用简单，效率高。
2. 可以生成sub jar，当编写的服务依赖未完成的服务时，也可以测试运行。
3. 可以利用插件自动生成提供端的契约验证代码。
缺点：
1. 契约要放置于提供端，需要消费端告知提供端需求，或者消费端编写契约手动传给提供端
2. 对其他语言支持较弱，比如js

pact
优点:
1. 契约由消费端直接在单元测试中编写，生成契约json，契约json可以上传pact broker，
   提供端只需知道契约的state，便可以从pact broker获取该契约。
2. 支持多种语言。
 
缺点：
1. 相比contract开发效率稍低。
2. 无法生成sub jar，消费端在提供端未编写完成时，只能通过mock等方式测试。
```
### 流程
**spring cloud contract**大致流程
```
消费端编写需求告知提供端 -> 提供端根据需求编写groovy契约 -> spring cloud contract插件生成sub jar -> 将sub jar发布到maven库 -> 消费端获取jar,运行模拟真实服务并测试 -> 提供端编写相关接口 ->提供端编写相关接口 -> 利用spring cloud contract插件生成契约验证代码，验证契约
```
**pact**大致流程
```
编写消费端api -> 在单元测试使用java编写契约 -> 单元测试中编写验证契约代码 -> 运行单元测试生成契约json -> 将契约json传到Pact Broker -> 提供端编写接口 -> 提供端通过pact broker拿到契约json，编写接口并验证契约
```
因为pact的支持多种编程语言及存在pact broker，选用了pact。

### pact契约编写
> spring cloud contract使用groovy或者spring rest docs；pact使用java DSL编写。

**spring cloud contract**的契约编写
```groovy
Contract.make {
    description "return all customers"
    request {
        url "/api/customers"
        method GET()
    }
    response {
        status 200
        headers {
            header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
        }
        body("""{"data":[{"id":1,"name":"sam"},{"id":2,"name":"andy"}]}""")
    }
}

```
**pact**的契约编写
```java
  @Pact(state = "a single address", provider = "customerServiceProvider", consumer = "addressClient")
  public RequestResponsePact createAddressResourcePact(PactDslWithProvider builder) {
    return builder
            .given("a single address")
            .uponReceiving("a request to the address resource")
            .path("/addresses/1")
            .method("GET")
            .willRespondWith()
            .status(200)
            .body("{\"data\":[{\"id\":1,\"name\":\"sam\"},{\"id\":2,\"name\":\"andy\"}]}",
                    "application/hal+json")
            .toPact();
```

## pact 事例项目组成
```
project-service <- oauth-service <- user-service 
project-service服务调用oauth-service服务获取jwt , oauth-service服务调用user-service查询用户

user-service：pact的提供端
project-service：pact的消费端
oauth-service： 既是pact的消费端也是pact的提供端

user-service的pact契约验证代码：user-service/src/test/java/com/hand/hap/cloud/pact/user/controller/UserLoginControllerProviderTest.java
project-service的pact契约生成代码：project-service/src/test/java/com/hand/hap/cloud/pact/project/OauthConsumerPactTest.java
oauth-service的pact契约生成代码：oauth-service/src/test/java/com/hand/hap/cloud/pact/oauth/feign/UserLoginConsumerPactTest.java
oauth-service的pact契约验证代码：oauth-service/src/test/java/com/hand/hap/cloud/pact/oauth/controller/OauthControllerProviderTest.java
```

## 项目使用
1. 本地mysql创建user_service数据库

2. 启动pact broker
```
cd pact_broker
docker compose up
```

3. 以oauth-service调用user-service为例
```
1. 运行oauth-service中的UserLoginConsumerPactTest测试类
   会在/target/pacts中生成pact契约json文件
2. 在oauth-service下运行mvn pact:publish，将契约上传到pact broker
3. 运行user-service下的UserLoginControllerProviderTest验证是否符合契约 
```

## pact上传和下载契约到pact broker的方法
- maven添加插件

```xml
<plugin>
  <groupId>au.com.dius</groupId>
  <artifactId>pact-jvm-provider-maven_2.12</artifactId>
  <version>3.5.10</version>
  <configuration>
      <pactBrokerUrl>http://localhost:80</pactBrokerUrl>
      <pactBrokerPassword>pact</pactBrokerPassword>
      <pactBrokerUsername>pact</pactBrokerUsername>
  </configuration>
</plugin>
```
- 上传契约

```
mvn pact:publish
```
- 获取契约

```java
@PactBroker(host = "localhost", port = "80",
  authentication = @PactBrokerAuth(username = "pact", password = "pact"))
```

## 生成pact契约
> 存在的问题: 要想调用pact的返回结果做mock，只能放在同一个测试文件中，如果一个服务中多次调用同一个feign，就比较麻烦
```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class OauthPactTest {
     /**
     * 此处起了一个server,用于模拟feign调用的服务
     */
    @Rule
    public PactProviderRuleMk2 stubProvider = new PactProviderRuleMk2("oauth_test_provider",
            "127.0.0.1", 10001, this);
    @Autowired
    private OauthFeignClient oauthFeignClient;
    @Pact(state = "check oauth state", provider = "oauth_test_provider", consumer = "oauthFeignClient")
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
                    .body("{\"result\":\"passed\"}")
                .toPact();
    }
    @Test
    @PactVerification(fragment = "checkOauthPact")
    public void verifyCheckOauthPact() {
        String result = oauthFeignClient.checkOauth("alice","alice");
        System.out.println(result);
        Assert.assertTrue(result.contains("passed"));
    }
}
```

## 验证pact契约

```java
@RunWith(SpringRestPactRunner.class)
//指定provider
@Provider("oauth_provider")
//设置pact broker
@PactBroker(host = "localhost", port = "80",
        authentication = @PactBrokerAuth(username = "pact", password = "pact"))
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {
        "server.port=10002"
})
public class OauthControllerProviderTest {
     /**
     * oauthController调用oauthService，oauthService又调用userLoginFeignClient
     * 可以用mock 设置userLoginFeignClient逻辑，以满足pact契约验证
     * 为什么使用mock设置？因为可以用来模拟server没有的数据，但又不影响最终接口的验证
     */
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
    }
}
```

## 利用mock生成语法pact 契约
```java
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
```

## 其他测试部分使用mock完成测试
```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class OauthServiceTest {

    @Mock
    UserLoginFeignClient userLoginFeignClient;

    @InjectMocks
    private OauthService oauthService = new OauthServiceImpl(userLoginFeignClient);

    @Test
    public void checkOauthTest() {
        when(userLoginFeignClient.login("alice","alice")).thenReturn(
                new Result<>(1, "success",null)
        );
        Result<String> response =oauthService.checkOauth("alice","alice").getBody();
        System.out.println(response);
        Assert.assertEquals(1, response.getCode());
    }

    @Test
    public void getOauthJwt() {
        when(userLoginFeignClient.login("alice","alice")).thenReturn(
                new Result<>(1, "success",null)
        );
        Result<String> response =oauthService.getOauthJwt("alice","alice").getBody();
        System.out.println(response);
        Assert.assertEquals(1, response.getCode());
    }
}
```

## 简化测试，利用父类复用mock定义
```java
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

@RunWith(SpringRunner.class)
@SpringBootTest
public class OauthServiceTest extends BaseMockTest{

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

```