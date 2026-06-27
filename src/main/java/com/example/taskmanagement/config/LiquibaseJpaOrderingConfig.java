package com.example.taskmanagement.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import java.util.Arrays;
import java.util.LinkedHashSet;

@Configuration
public class LiquibaseJpaOrderingConfig {

    private static final String LIQUIBASE_BEAN_NAME = "liquibase";

    @Bean
    public static BeanFactoryPostProcessor entityManagerFactoryDependsOnLiquibasePostProcessor() {
        return beanFactory -> {
            if (!hasLiquibaseBean(beanFactory)) {
                return;
            }
            if (!(beanFactory instanceof BeanDefinitionRegistry registry)) {
                return;
            }

            for (String beanName : beanFactory.getBeanNamesForType(
                    LocalContainerEntityManagerFactoryBean.class, false, false)) {
                BeanDefinition definition = registry.getBeanDefinition(beanName);
                LinkedHashSet<String> dependsOn = new LinkedHashSet<>();
                if (definition.getDependsOn() != null) {
                    dependsOn.addAll(Arrays.asList(definition.getDependsOn()));
                }
                dependsOn.add(LIQUIBASE_BEAN_NAME);
                definition.setDependsOn(dependsOn.toArray(String[]::new));
            }
        };
    }

    private static boolean hasLiquibaseBean(org.springframework.beans.factory.config.ConfigurableListableBeanFactory beanFactory) {
        if (beanFactory.containsBean(LIQUIBASE_BEAN_NAME)) {
            return true;
        }
        return beanFactory.getBeanNamesForType(SpringLiquibase.class, false, false).length > 0;
    }
}
