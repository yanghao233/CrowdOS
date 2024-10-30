package com.nuc.securedatabasesystem.service;


import cn.crowdos.kernel.system.DuplicateResourceNameException;
import cn.crowdos.kernel.system.SystemResourceCollection;
import cn.crowdos.kernel.system.resource.Resource;
import com.nuc.securedatabasesystem.controller.CrowdKernelComponent;
import com.nuc.securedatabasesystem.mapper.UserMapper;
import com.nuc.securedatabasesystem.pojo.User;
import com.nuc.securedatabasesystem.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;


/**
 * @Author 6B507
 * @Date 2024/11/12 16:18
 */
@Service
public class UserServerImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    private CrowdKernelComponent crowdKernelComponent = new CrowdKernelComponent();
    private SystemResourceCollection systemResource;
    /**
     * Login 方法的实现，返回username=传进来的name and password=传进来的password的人，
     * 如果找到，证明有这个用户，否则没这个用户
     */


    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public User login(User user) throws DuplicateResourceNameException {
        //首先进行CrowdKernel初始化
        crowdKernelComponent.getKernel();

        User login = userMapper.login(user);
        if (login != null) {
            //  若不为空，调用CrowdKernel提供的SystemResourceCollection类中的register()方法，将user添加到resourceMap中留作备份
            systemResource.register((User)user, user.getUsername());
            return login;
        }
        throw new RuntimeException("登陆失败");
    }

}
