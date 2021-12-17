# websocket-spring-boot-starter

# readme

#### 介绍
一个简单，快速，低配的spring-boot-starter,是对`spring-boot-starter-websocket`的扩展与二次封装，简化了springboot应用对websocket的操作

#### 特点
1. 简单，低配
2. 支持websocket连接可控，可以防止前端恶意连接，重复连接
3. 支持单点发送，部分发送，所有发送
4. 支持消息中转，集群环境跨机器传递消息(redis 做的消息中转，需要配置redis)
5. 可监控实时连接数


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
//        wsTemplate.sendMessageToAll(...);
//        wsTemplate.sendMessageToPart(...);
//        wsTemplate.sendMessageToSingle(...);
    }
}
``` 
4. 收消息
```java
// TestParam 只是一个普通的JavaBean
public class HalloMessageHandler implements MessageHandler<TestParam>{
    
     // 处理前端发送的消息
    public void handleMessage(TestParam testparam,Identity identity){
        
    }
    
    // 定义一个场景
    public String getScene(){
        return "test";
    }
}
```

#### 测试html

```html
<!DOCTYPE HTML>
<html>
<head>
    <meta charset="utf-8">
    <title>菜鸟教程(runoob.com)</title>
</head>
<style>
    .box{
        border: 1px solid red;
        width: 1000px;
        height: 600px;
        overflow-y: auto;
    }

</style>
<body>
<div class="box">
</div>
<input type="text" id="input">
<button id="send">发消息</button>
<button id="end">结束</button>
</body>
<script src="http://libs.baidu.com/jquery/2.0.0/jquery.min.js"></script>
<script type="text/javascript">

    // 发送消息参数示例  ：  {"scene":"test","data":{"name":"Jack","description":"一只菜鸟"}}
    // 连接参数  ：  ws://ip:port/ws?Authorization={{授权信息}}


    var ws = new WebSocket("ws://127.0.0.1:8000/ws?Authorization=abc");
    ws.onopen = function(){
        // Web Socket 已连接上，使用 send() 方法发送数据
        alert("连接成功...");
    };
    ws.onclose = function(e){
        // 关闭 websocket
        alert("连接已关闭..."+ e.status + " " + e.reason);
    };

    ws.onmessage = function (evt) {
        var received_msg = evt.data;
        $(".box").append('收到：' + received_msg + '<br>');
    };

    ws.onerror = function (evt) {
        var received_msg = evt.data;
        alert("error" + received_msg)
        $(".box").append(received_msg + '<br>');
    };

    $('#send').click(function(){
        $(".box").append($('#input').val() + '<br>');
        ws.send($('#input').val());
        $('#input').val('')
    })

    $('#end').click(function(){
        ws.close()
    })

    self.setInterval("send()",10000);

    function send() {
        $(".box").append('发送：ping' + '<br>');
        ws.send("ping")
    }

</script>
</html>

```

