package com.liangzhicheng.config.db;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@MapperScan(basePackages = {"com.liangzhicheng.**.mapper*"}, sqlSessionFactoryRef = "sqlSessionFactory")
@Configuration
public class DataSourceConfig {

    /**
     * mapper的xml位置
     */
    public static final String[] DATASOURCE_MAPPER_LOACTIONS = {"classpath*:com/liangzhicheng/**/*Mapper.xml"};

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druidDataSource(){
        return new DruidDataSource();
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(this.getMapperResource());
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        bean.setPlugins(interceptor);
        return bean.getObject();
    }

    /**
     * mapper的xml路径处理，把String[]转Resource[]
     * @return Resource[]
     */
    private Resource[] getMapperResource(){
        try {
            if(DATASOURCE_MAPPER_LOACTIONS != null && DATASOURCE_MAPPER_LOACTIONS.length > 0) {
                List<Resource> resourceList = new ArrayList<>();
                for (String location : DATASOURCE_MAPPER_LOACTIONS) {
                    Resource[] resourceArr = new Resource[0];
                    resourceArr = new PathMatchingResourcePatternResolver().getResources(location);
                    for (Resource resource : resourceArr) {
                        resourceList.add(resource);
                    }
                }
                Resource[] resources = new Resource[resourceList.size()];
                for (int i = 0; i < resourceList.size(); i++) {
                    resources[i] = resourceList.get(i);
                }
                return resources;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解决druid新版本报错discard long time none received connection jdbc xxx
     */
    @PostConstruct
    public void setProperties(){
        System.setProperty("druid.mysql.usePingMethod", "false");
    }

}
