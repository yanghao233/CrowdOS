package com.nuc.securedatabasesystem.exception.interfaces;

/**
 * @Author 6B527
 * @Date 2024/10/25 19:32
 * 异常的接口，以后的自定义异常都继承自该类
 */
public interface BasicException {
    /**
     * 错误码
     * @return
     */
    String getResultCode();

    /**
     * 错误描述
     * @return
     */
    String getResultMsg();
}
