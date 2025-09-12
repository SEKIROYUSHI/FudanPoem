import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest(classes = org.example.fudanPoem.CommonApplication.class)
public class JwtTest {

    @Autowired
    private Environment env;

    @Test
    public void testJwtExpiration() {
        // 测试 Environment 是否正确加载
        String jwtExpiration = env.getProperty("jwt.expiration");
        System.out.println("JWT 过期时间：" + jwtExpiration); // 预期输出：24小时（或配置的实际值）
    }
}
