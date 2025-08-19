
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest(classes = org.example.fudanPoem.Application.class)
public class RedisTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private Environment env;

    @Test
    public void testRedisConnection() {
        // 存入键值对
        redisTemplate.opsForValue().set("test:key", "Hello Redis");
        // 获取值
        Object value = redisTemplate.opsForValue().get("test:key");
        System.out.println("从 Redis 获取的值：" + value); // 预期输出：Hello Redis
    }

    @Test
    public void testEnvironment() {
        // 测试 Environment 是否正确加载
        String jwtExpiration = env.getProperty("jwt.expiration");
        System.out.println("JWT 过期时间：" + jwtExpiration); // 预期输出：24小时（或配置的实际值）
    }
}
