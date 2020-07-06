package de.adorsys.psd2.sandbox.cms.starter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Slf4j
@Configuration
@PropertySource("classpath:cms.properties")
@Import(value = Xs2aCmsConfig.class)
public class Xs2aCmsAutoConfiguration {

    public Xs2aCmsAutoConfiguration() {
        log.info("------- XS2A cms auto configuration was loaded -------");
    }

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSource cmsDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean cmsEntityManager(Environment env) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(cmsDataSource());
        em.setPackagesToScan("de.adorsys.psd2.aspsp",
                             "de.adorsys.psd2.event",
                             "de.adorsys.psd2.consent",
                             "de.adorsys.psd2.sandbox.cms.starter",
                             "de.adorsys.psd2.sandbox.tpp.cms",
                             "de.adorsys.psd2.core.mapper");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public PlatformTransactionManager cmsTransactionManager(Environment env) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(cmsEntityManager(env).getObject());
        return transactionManager;
    }
}
