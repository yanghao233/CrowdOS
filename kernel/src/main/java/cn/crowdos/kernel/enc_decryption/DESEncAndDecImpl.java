package cn.crowdos.kernel.enc_decryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class DESEncAndDecImpl {

    private static byte[] m_Key = new byte[8]; // 存储生成的DES加密密钥

    // 默认硬编码密钥
    private static final String DEFAULT_KEY = "(@jhtchina@)";

    // 随机生成IV（初始化向量）用于CBC模式加密
    private static byte[] generateIV() {
        byte[] iv = new byte[8]; // DES算法需要8字节IV
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    // 加密函数
    public String encryptData(String strKey, String strData) {
        // 检查数据长度，避免过大的数据影响加密
        if (strData.length() > 92160) {
            return "Error. Data String too large. Keep within 90Kb.";
        }

        // 初始化密钥，如果失败则返回错误信息
        if (!initKey(strKey)) {
            return "Error. Fail to generate key for encryption";
        }

        // 数据长度附加到数据头部，用于解密后恢复原始数据
        strData = String.format("%05d%s", strData.length(), strData);

        try {
            // 将字符串数据转为字节数组
            byte[] rbData = strData.getBytes("UTF-8");

            // 生成DES密钥规格
            DESKeySpec desKeySpec = new DESKeySpec(m_Key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

            // 创建DES加密对象，使用CBC模式和PKCS5填充
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

            // 生成新的IV
            byte[] m_IV = generateIV();
            IvParameterSpec ivParams = new IvParameterSpec(m_IV);

            // 使用加密模式初始化Cipher
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);

            // 加密数据流，结果写入ByteArrayOutputStream
            ByteArrayInputStream bais = new ByteArrayInputStream(rbData);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            // 分块加密数据，适应大数据
            while ((bytesRead = bais.read(buffer)) != -1) {
                baos.write(cipher.update(buffer, 0, bytesRead));
            }
            baos.write(cipher.doFinal());

            // 获取加密后的字节数组
            byte[] encryptedData = baos.toByteArray();

            // 将IV和加密数据组合，方便解密
            byte[] combined = new byte[m_IV.length + encryptedData.length];
            System.arraycopy(m_IV, 0, combined, 0, m_IV.length);
            System.arraycopy(encryptedData, 0, combined, m_IV.length, encryptedData.length);

            // 返回Base64编码的加密数据字符串
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error during encryption: " + e.getMessage();
        }
    }

    // 解密函数
    public String decryptData(String strKey, String strData) {
        // 初始化密钥，如果失败则返回错误信息
        if (!initKey(strKey)) {
            return "Error. Fail to generate key for decryption";
        }

        try {
            // 解码Base64的加密数据
            byte[] decodedData = Base64.getDecoder().decode(strData);

            // 检查数据长度是否有效
            if (decodedData.length < 8) {
                return "Error. Invalid data format.";
            }

            // 提取IV和加密数据部分
            byte[] iv = new byte[8];
            System.arraycopy(decodedData, 0, iv, 0, iv.length);
            byte[] encryptedData = new byte[decodedData.length - iv.length];
            System.arraycopy(decodedData, iv.length, encryptedData, 0, encryptedData.length);

            // 生成DES密钥规格
            DESKeySpec desKeySpec = new DESKeySpec(m_Key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

            // 创建DES解密对象，使用CBC模式和PKCS5填充
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

            // 解密数据流，结果写入ByteArrayOutputStream
            ByteArrayInputStream bais = new ByteArrayInputStream(encryptedData);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            // 分块解密数据
            while ((bytesRead = bais.read(buffer)) != -1) {
                baos.write(cipher.update(buffer, 0, bytesRead));
            }
            baos.write(cipher.doFinal());

            // 转换解密后的字节数据为字符串
            String strResult = new String(baos.toByteArray(), "UTF-8");

            // 提取原始数据长度并返回实际数据内容
            int len = Integer.parseInt(strResult.substring(0, 5));
            return strResult.substring(5, 5 + len);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error during decryption: " + e.getMessage();
        }
    }

    // 初始化密钥的函数，将字符串密钥转为固定长度的字节数组
    private boolean initKey(String strKey) {
        try {
            byte[] bp = strKey.getBytes("UTF-8");

            // 使用SHA-256算法生成哈希，再截取前8字节作为DES密钥
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] bpHash = sha.digest(bp);
            System.arraycopy(bpHash, 0, m_Key, 0, 8);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
