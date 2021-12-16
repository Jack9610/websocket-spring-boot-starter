# websocket-spring-boot-starter

# readme

#### 介绍
一个简单，快速，低配的spring-boot-starter，方便springboot集成websocket

#### 特点
1. 简单，低配
2. 支持websocket连接可控，可以防止前端恶意连接，重复连接
3. 支持单点发送，部分发送，所有发送


#### 安装教程
1. 先把项目下载到本地
```
git clone git@github.com:Jack9610/websocket-spring-boot-starter.git
```
2. 将项目install到本地
```
maven install 
```
3. 依赖进自己的项目
```xml
        <dependency>
            <groupId>com.jack</groupId>
            <artifactId>websocket-spring-boot-starter</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
```
#### 使用说明
1.  实现 `Identity` 
```java
@Data
public class User implements Identity {

    private String userId;

    private String username;

    private String password;

    @Override
    public String getIdentityId() {
        // 可以自定义返回任何信息，确保每个客户端唯一即可
        return userId;
    }
}
```
2. 实现 `IdentityService`
```java
@Service
public class IdentityServiceImpl implements IdentityService {

    @Resource
    private UserService userService;

    @Override
    public Identity getIdentity(String s) {
        String userId = JwtUtil.getUserId(s);
        // 这里演示，从数据库直接读出user
        return userService.get(userId);
    }
}
```
3. 发消息
```java
public class BusinessService{
    
    @Autowired
    private WsTemplate wsTemplate;

    public void test(){
        wsTemplate.sendMessageToAll();
    }
}
``` 
