package com.example;

import java.util.HashMap;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.example.repository.eventMessage.EventMessageDatasourceProperties;
@Configuration
@DependsOn("transactionManager")
@EnableJpaRepositories(basePackages = "com.example.repository.eventMessage", entityManagerFactoryRef = "eventMessageEntityManager", transactionManagerRef = "transactionManager")
@EnableConfigurationProperties(EventMessageDatasourceProperties.class)
public class EventMessageConfig {

	@Autowired
	private JpaVendorAdapter jpaVendorAdapter;

	@Autowired
	private EventMessageDatasourceProperties eventMessageDatasourceProperties;

	@Primary
	@Bean(name = "eventMessageDataSource", initMethod = "init", destroyMethod = "close")
	public DataSource eventMessageDataSource() {
		AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
	       dataSource.setUniqueResourceName("xadseventmessage");
	       dataSource.setXaDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
	       Properties xaProperties = new Properties();
	       xaProperties.put("url", eventMessageDatasourceProperties.getUrl());
	       xaProperties.put("user", eventMessageDatasourceProperties.getUsername());
	       xaProperties.put("password", eventMessageDatasourceProperties.getPassword());
	       dataSource.setXaProperties(xaProperties);
	       dataSource.setPoolSize(10);
	       return dataSource;

	}

	@Primary
	@Bean(name = "eventMessageEntityManager")
	@DependsOn("transactionManager")
	public LocalContainerEntityManagerFactoryBean eventMessageEntityManager() throws Throwable {

		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
		properties.put("javax.persistence.transactionType", "JTA");

		LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
		entityManager.setJtaDataSource(eventMessageDataSource());
		entityManager.setJpaVendorAdapter(jpaVendorAdapter);
		entityManager.setPackagesToScan("com.example.domain.eventMessage");
		entityManager.setPersistenceUnitName("eventMessagePersistenceUnit");
		entityManager.setJpaPropertyMap(properties);
		return entityManager;
	}

}
