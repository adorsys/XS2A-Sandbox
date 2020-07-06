package de.adorsys.psd2.sandbox.tpp.rest.server.config;

import de.adorsys.psd2.sandbox.tpp.db.api.TppAppDbBasePackage;
import de.adorsys.psd2.sandbox.tpp.db.api.domain.OperationInfoEntity;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@PropertySource({"classpath:application.yml"})
@ComponentScan(basePackageClasses = {TppAppDbBasePackage.class})
@EntityScan(basePackageClasses = {OperationInfoEntity.class, Jsr310JpaConverters.class})
@EnableJpaRepositories(
    basePackages = "de.adorsys.psd2.sandbox.tpp.db.api",
    entityManagerFactoryRef = "tppEntityManager",
    transactionManagerRef = "tppTransactionManager")
public class PersistenceTppAutoConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "spring.tpp.datasource.hikari")
    public DataSource tppDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean tppEntityManager(Environment env) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(tppDataSource());
        em.setPackagesToScan("de.adorsys.psd2.sandbox.tpp.db.api");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public PlatformTransactionManager tppTransactionManager(Environment env) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(tppEntityManager(env).getObject());
        return transactionManager;
    }
}
