package kitt.core.config;


import com.github.pagehelper.PageHelper;
import com.zaxxer.hikari.HikariDataSource;
import kitt.ext.mybatis.LocalDateTimeTypeHandler;
import kitt.ext.mybatis.LocalDateTypeHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by joe on 10/26/14.
 */
@Configuration
@PropertySource("classpath:/jdbc.properties")
@MapperScan(basePackages = {"kitt.core.persistence","kitt.core.mapper"}/*, sqlSessionFactoryRef="mySessionFactory"*/)
public class DatabaseConfig {
    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;
    @Value("${jdbc.driverClassName}")
    private String driverClassName;

    @Value("${jdbc.maxpoolsize}")
    private int maxpoolsize;

    @Bean
    public DataSource dataSource() {
        /*DriverManagerDataSource dataSource = new DriverManagerDataSource(url, username, password);
        dataSource.setDriverClassName(driverClassName);
        return dataSource;
        */
        if(maxpoolsize<10)
        {
            maxpoolsize=10;
        }
        if(maxpoolsize>100){
            maxpoolsize=100;
        }
        final HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(maxpoolsize);
        ds.setDriverClassName(driverClassName);
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        //sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("/mybatis/**/*Mapper.xml"));
        //sessionFactory.setTypeAliasesPackage("kitt.core.entity");
        sessionFactory.setTypeHandlers(new TypeHandler<?>[]{new LocalDateTypeHandler(), new LocalDateTimeTypeHandler()});

        //分页插件
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("returnPageInfo", "check");
        properties.setProperty("params", "count=countSql");
        pageHelper.setProperties(properties);
        sessionFactory.setPlugins(new Interceptor[]{pageHelper});
        return sessionFactory.getObject();
    }

}
