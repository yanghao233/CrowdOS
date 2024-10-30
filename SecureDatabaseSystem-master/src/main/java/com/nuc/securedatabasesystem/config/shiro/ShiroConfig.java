package com.nuc.securedatabasesystem.config.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author 6B507
 * @Date 2024/10/25 17:11
 */
@Configuration
@Slf4j
public class ShiroConfig {

    @Value("${jwt.auth}")
    private boolean auth;
    @Autowired
    AuthFilter authFilter;

    @Bean
    // 返回一个 CustomWebSubjectFactory 实例，用于创建 Subject，Shiro 中的 Subject 代表用户或程序实体。
    // 自定义的 CustomWebSubjectFactory 可以对 Subject 的行为进行定制
    public DefaultWebSubjectFactory subjectFactory() {
        return new CustomWebSubjectFactory();
    }

    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(new CustomRealm());
        securityManager.setSubjectFactory(subjectFactory());
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        // 禁用 session 存储
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);
        // 禁用 rememberMe
        securityManager.setRememberMeManager(null);
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager());
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("customFilter", authFilter);
        shiroFilterFactoryBean.setFilters(filterMap);
        Map<String, String> filterChainDefinitionMap = new HashMap<>();
        String flag = auth ? "customFilter" : "anon";
        filterChainDefinitionMap.put("/api/login/**", "anon");
        filterChainDefinitionMap.put("/api/login", "anon");
        filterChainDefinitionMap.put("/api/**", flag);
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }
}
