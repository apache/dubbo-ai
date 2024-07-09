# Dubbo AI
How to use Dubbo AI
use in spring boot
1. add dependency
```xml
<dependencies>
    <dependency>
                <groupId>org.apache.dubbo</groupId>
                <artifactId>dubbo-ai-spring-boot-starter</artifactId>
                <version>1.0-SNAPSHOT</version>
    </dependency>
    <dependency>
                <groupId>org.apache.dubbo</groupId>
                <artifactId>dubbo-ai-openai</artifactId>
                <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```
2. create a dubbo ai service like this 
```java
@DubboAiService(providerConfigs = "m1",configPath = "dubbo-ai.properties")
public interface MyAiService {
    
    @Prompt("""
            你是一个超高级的人工智能，请你以json的map格式回答一下问题: {userMessage}
            """)
    String chat(String userMessage);
}

```

3. use in spring service
```java
@Service
public class AiService implements ApplicationRunner {

    @DubboReference
    private MyAiService myAiService;

    public String chat(String msg) {
        return myAiService.chat(msg);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(myAiService.chat("hi，你是谁"));
    }
}
```


