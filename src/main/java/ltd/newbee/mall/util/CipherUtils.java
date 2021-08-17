/**
 * @Title: EncryptUtil.java
 * @Package com.start.spring.beans
 * @Description:  描述文件作用
 * @author PenseeStroller
 * @date 2015年4月17日 下午2:33:02
 * @version V1.0.0
 *
 *          Copyright (c) 2015 All Rights Reserved.
 */
package ltd.newbee.mall.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * @ClassName: CipherUtils
 * @Description:  对称加密，key必须为16位
 * @author PeneeStroller
 * @date 2015年4月17日 下午2:33:02
 *
 */
public class CipherUtils {
    protected final static Logger log = LoggerFactory.getLogger(CipherUtils.class);

    /**
     * @throws Exception
     * @Title: main
     * @Description:  描述方法作用
     * @param @param args
     * @return void
     * @throws
     */
    public static String aesEncrypt(String str, String key) {
        try {
            if (str == null || key == null) {
                return null;
            }
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes("utf-8"), "AES"));
            byte[] bytes = cipher.doFinal(str.getBytes("utf-8"));
            return new Base64().encodeToString(bytes);
        } catch (Exception e) {
            log.error("加密出错：", e);
            return null;
        }
    }

    public static String aesDecrypt(String str, String key) throws Exception {
        if (str == null || key == null) {
            return null;
        }
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes("utf-8"), "AES"));
        byte[] bytes = new Base64().decode(str);
        bytes = cipher.doFinal(bytes);
        return new String(bytes, "utf-8");
    }

    public static RSAPublicKey loadPublicKeyByStr(String publicKeyStr) throws Exception {
        byte[] buffer = Base64.decodeBase64(publicKeyStr);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    public static byte[] encryptRSA(RSAPublicKey publicKey, byte[] plainTextData) throws Exception {
        if (publicKey == null) {
            throw new Exception("加密公钥为空, 请设置");
        }
        Cipher cipher = null;
        // 使用默认RSA
        cipher = Cipher.getInstance("RSA");
        // cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] output = cipher.doFinal(plainTextData);
        return output;
    }

    public static String aesEncryptNoPadding(String str, String key) throws Exception {
        if (str == null || key == null) {
            return null;
        }
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes("utf-8"), "AES"));
        byte[] bytes = cipher.doFinal(str.getBytes("utf-8"));
        return new Base64().encodeToString(bytes);
    }

    public static byte[] aesDecryptNoPadding(byte[] content, byte[] raw) throws Exception {
        SecretKey aesKey = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decodedBytes = cipher.doFinal(content);
        return decodedBytes;

    }

}
