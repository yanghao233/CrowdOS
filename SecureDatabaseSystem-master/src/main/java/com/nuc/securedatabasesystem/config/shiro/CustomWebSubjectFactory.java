package com.nuc.securedatabasesystem.config.shiro;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;

/**
 * @Author 6B507
 * @Date 2024/10/25 16:13
 */
public class CustomWebSubjectFactory extends DefaultWebSubjectFactory {

    @Override
    public Subject createSubject(SubjectContext context) {
        // 禁用session
        context.setSessionCreationEnabled(false);
        return super.createSubject(context);
    }
}