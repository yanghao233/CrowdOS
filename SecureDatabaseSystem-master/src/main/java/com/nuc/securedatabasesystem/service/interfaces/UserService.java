package com.nuc.securedatabasesystem.service.interfaces;

import cn.crowdos.kernel.system.DuplicateResourceNameException;
import cn.crowdos.kernel.system.resource.Resource;
import com.nuc.securedatabasesystem.pojo.User;

/**
 * @Author 6B507
 * @Date 2024/11/20 16:19
 */
public interface UserService {
    /**
     * 登录接口
     * @param user
     * @return
     */
    User login(User user) throws DuplicateResourceNameException;

}
