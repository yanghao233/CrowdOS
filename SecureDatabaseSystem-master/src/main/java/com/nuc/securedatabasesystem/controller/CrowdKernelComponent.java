package com.nuc.securedatabasesystem.controller;

import org.springframework.stereotype.Component;
import cn.crowdos.kernel.CrowdKernel;
import cn.crowdos.kernel.Kernel;
@Component
// 初始化Crowd Kernel
public class CrowdKernelComponent {
    public CrowdKernel getKernel() {
        CrowdKernel kernel = Kernel.getKernel();
        if(!kernel.isInitialed()) kernel.initial();
        return kernel;
    }
}
