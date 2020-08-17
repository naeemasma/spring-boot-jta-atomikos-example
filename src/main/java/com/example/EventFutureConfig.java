package com.example;

import java.util.HashMap;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.example.repository.eventFuture.EventFutureDatasourceProperties;

@Configuration
@DependsOn("transactionManager")
@EnableJpaRepositories(basePackages = "com.example.repository.eventFuture", entityManagerFactoryRef = "eventFutureEntityManager", transactionManagerRef = "transactionManager")
@EnableConfigurationProperties(EventFutureDatasourceProperties.class)
public class EventFutureConfig {

	@Autowired
	private JpaVendorAdapter jpaVendorAdapter;

	@Autowired
	private EventFutureDatasourceProperties eventFutureDatasourceProperties;

	@Bean(name = "eventFutureDataSource", initMethod = "init", destroyMethod = "close")
	public DataSource eventFutureDataSource() {
		AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
	       dataSource.setUniqueResourceName("xadseventfuture");
	       dataSource.setXaDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
	       Properties xaProperties = new Properties();
	       xaProperties.put("url", eventFutureDatasourceProperties.getUrl());
	       xaProperties.put("user", eventFutureDatasourceProperties.getUsername());
	       xaProperties.put("password", eventFutureDatasourceProperties.getPassword());
	       dataSource.setXaProperties(xaProperties);
	       dataSource.setPoolSize(10);
	       return dataSource;

	}

	@Bean(name = "eventFutureEntityManager")
	public LocalContainerEntityManagerFactoryBean eventFutureEntityManager() throws Throwable {

		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
		properties.put("javax.persistence.transactionType", "JTA");

		LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
		entityManager.setJtaDataSource(eventFutureDataSource());
		entityManager.setJpaVendorAdapter(jpaVendorAdapter);
		entityManager.setPackagesToScan("com.example.domain.eventFuture");
		entityManager.setPersistenceUnitName("eventFuturePersistenceUnit");
		entityManager.setJpaPropertyMap(properties);
		return entityManager;
	}

}
