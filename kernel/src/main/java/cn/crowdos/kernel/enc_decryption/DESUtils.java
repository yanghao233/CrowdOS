package cn.crowdos.kernel.enc_decryption;

public class DESUtils {

    // 提供静态方法供外部调用
    // 加密
    public static String encrypt(String key, String data) {
        DESEncAndDecImpl ed = new DESEncAndDecImpl();
        return ed.encryptData(key, data);
    }

    // 解密
    public static String decrypt(String key, String data) {
        DESEncAndDecImpl ed = new DESEncAndDecImpl();
        return ed.decryptData(key, data);
    }
}
