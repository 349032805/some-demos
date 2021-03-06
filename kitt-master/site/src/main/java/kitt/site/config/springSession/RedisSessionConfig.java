package kitt.site.config.springSession;

import org.redisson.Config;
import org.redisson.Redisson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.ExpiringSession;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Created by hongpf
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 7200)//site模块session默认两小时
public class RedisSessionConfig {



    private @Value("${redis.hostname}") String redisHostName;
    private @Value("${redis.port}") int redisPort;


    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory fact =  new JedisConnectionFactory() ;
        fact.setHostName(redisHostName);
        fact.setPort(redisPort);
        fact.setUsePool(true);
        return  fact ;
    }

    /**
     * 个性化持久类，session中保存的bean改变时不报错
     * @param connectionFactory
     * @return
     */

    @Bean
    public RedisTemplate<String,ExpiringSession> sessionRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ExpiringSession> template = new RedisTemplate<String, ExpiringSession>();
        template.setHashValueSerializer(new KittSerializationRedisSerializer());
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Bean
    public Redisson getRedisson(){
        Config config = new Config();
        config.useSingleServer().setAddress(redisHostName+":"+redisPort).setConnectionPoolSize(5);
        return Redisson.create(config);
    }
}