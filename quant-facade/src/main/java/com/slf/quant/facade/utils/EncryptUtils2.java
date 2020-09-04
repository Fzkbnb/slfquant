/*
 * @(#)EncryptUtils2.java 2015-4-16 下午2:27:42
 * Copyright 2015 Playguy, Inc. All rights reserved. com.blocain
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.slf.quant.facade.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import com.slf.quant.facade.consts.CharsetConst;
import com.slf.quant.facade.consts.Digests;
import com.slf.quant.facade.consts.Encodes;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class EncryptUtils2 {
    public static final int HASH_INTERATIONS = 1024;

    public static final int SALT_SIZE = 8;

    private static final String PARSE_KEY = "bitms";

    private static final String ENCRYPT_NAME = "DES";

    /**
     * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
     */
    public static String entryptPassword(String plainPassword) {
        String plain = Encodes.unescapeHtml(plainPassword);
        byte[] salt = Digests.generateSalt(SALT_SIZE);
        byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
        return Encodes.encodeHex(salt) + Encodes.encodeHex(hashPassword);
    }

    /**
     * 验证密码
     *
     * @param plainPassword 明文密码
     * @param password      密文密码
     * @return 验证成功返回true
     */
    public static boolean validatePassword(String plainPassword, String password) {
        String plain = Encodes.unescapeHtml(plainPassword);
        byte[] salt = Encodes.decodeHex(password.substring(0, 16));
        byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
        return password.equals(Encodes.encodeHex(salt) + Encodes.encodeHex(hashPassword));
    }

    /**
     * 返回经过加密的字符串
     *
     * @param password  要加密码的明文字符串
     * @param algorithm 加密运算法则(可以是MD5、MD2、SHA-256、SHA-1等等)
     * @return String 加密后的字符串
     */
    public static String encrypt(String password, String algorithm) {
        String result = null;
        byte[] unencodedPassword = password.getBytes(Charset.forName(CharsetConst.CHARSET_UT));
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        if (null != md) {
            md.update(unencodedPassword);
            byte[] encodedPassword = md.digest();
            StringBuffer buf = new StringBuffer();
            int iLen = encodedPassword.length;
            for (int i = 0; i < iLen; i++) {
                if ((encodedPassword[i] & 0xff) < 0x10) {
                    buf.append("0");
                }
                buf.append(Long.toString(encodedPassword[i] & 0xff, 16));
            }
            result = buf.toString();
        }
        return result;
    }

    /**
     * 验证密码
     *
     * @param password   明文密码
     * @param ciphertext 加密密码
     * @param algorithm  加密方式
     * @return
     */
    public static Boolean unEncrypt(String password, String ciphertext, String algorithm) {
        return StringUtils.equals(ciphertext, encrypt(password, algorithm));
    }

    /**
     * DES加密
     *
     * @param strMing
     * @return
     */
    public static String desEncrypt(String strMing) {
        String strMi = "";
        try {
            byte[] byteMing = strMing.getBytes(CharsetConst.CHARSET_UT);
            byte[] byteMi = encryptByte(byteMing);
            strMi = Base64.encodeBase64String(byteMi);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing SqlMap class. Cause: " + e);
        }
        return strMi;
    }

    /**
     * DES解密
     *
     * @param strMi
     * @return
     */
    public static String desDecrypt(String strMi) {
        String strMing = "";
        try {
            byte[] byteMi = Base64.decodeBase64(strMi);
            byte[] byteMing = decryptByte(byteMi);
            strMing = new String(byteMing, CharsetConst.CHARSET_UT);
        } catch (UnsupportedEncodingException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return strMing;
    }

    static byte[] encryptByte(byte[] byteS) {
        byte[] byteFina = null;
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPT_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, generatorKey(PARSE_KEY));
            byteFina = cipher.doFinal(byteS);
        } catch (Exception e) {
            throw new RuntimeException(e.getCause().getMessage());
        }
        return byteFina;
    }

    static byte[] decryptByte(byte[] byteD) {
        byte[] byteFina = null;
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPT_NAME);
            cipher.init(Cipher.DECRYPT_MODE, generatorKey(PARSE_KEY));
            byteFina = cipher.doFinal(byteD);
        } catch (Exception e) {
            throw new RuntimeException(e.getCause().getMessage());
        }
        return byteFina;
    }

    static Key generatorKey(String parseKey) {
        Key key = null;
        KeyGenerator generator = null;
        try {
            generator = KeyGenerator.getInstance(ENCRYPT_NAME);
        } catch (NoSuchAlgorithmException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        if (null != generator) {
            // generator.init(new SecureRandom(parseKey.getBytes()));//Linux及Solaris下异常
            SecureRandom secureRandom = null;
            try {
                secureRandom = SecureRandom.getInstance("SHA1PRNG");
            } catch (NoSuchAlgorithmException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
            secureRandom.setSeed(parseKey.getBytes(Charset.forName(CharsetConst.CHARSET_UT)));
            generator.init(secureRandom);
            key = generator.generateKey();
        }
        return key;
    }


    /**
     * DES加密
     *
     * @param strMing
     * @return
     */
    public static String desEncrypt(String strMing, String password) {
        String strMi = "";
        try {
            byte[] byteMing = strMing.getBytes(CharsetConst.CHARSET_UT);
            byte[] byteMi = encryptByte(byteMing, password);
            strMi = Base64.encodeBase64String(byteMi);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing SqlMap class. Cause: " + e);
        }
        return strMi;
    }

    static byte[] encryptByte(byte[] byteS, String password) {
        byte[] byteFina = null;
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPT_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, generatorKey(password));
            byteFina = cipher.doFinal(byteS);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing SqlMap class. Cause: " + e);
        }
        return byteFina;
    }

    /**
     * DES解密
     *
     * @param strMi
     * @return
     */
    public static String desDecrypt(String strMi, String password) {
        String strMing = "";
        try {
            byte[] byteMi = Base64.decodeBase64(strMi);
            byte[] byteMing = decryptByte(byteMi, password);
            strMing = new String(byteMing, CharsetConst.CHARSET_UT);
        } catch (UnsupportedEncodingException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return strMing;
    }

    static byte[] decryptByte(byte[] byteD, String password) {
        byte[] byteFina = null;
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPT_NAME);
            cipher.init(Cipher.DECRYPT_MODE, generatorKey(password));
            byteFina = cipher.doFinal(byteD);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing SqlMap class. Cause: " + e);
        }
        return byteFina;
    }

    public static void main(String[] args) {

        System.out.println(EncryptUtils2.desEncrypt("test123", "fuzaMIMA@123"));
        System.out.println(EncryptUtils2.desDecrypt("9HSCRHchvpI=", "fuzaMIMA@123"));

        System.out.println(EncryptUtils2.desEncrypt("test123"));

        System.out.println(EncryptUtils2.desEncrypt("bitmsTest"));
        System.out.println(EncryptUtils2.desEncrypt("Test123456"));
        System.out.println(EncryptUtils2.desEncrypt("biex"));
        System.out.println(EncryptUtils2.desDecrypt("8AViz/qOiJo="));
        System.out.println(EncryptUtils2.desEncrypt("fuzaMIMA123@biex.com"));
        System.out.println(EncryptUtils2.desDecrypt("lzo1T9pY5xypOKrAEFaNfj9dKHm76bLq48mXwFvTgl14Pwb4Js/gcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnr/Ry"));
        System.out.println(EncryptUtils2.desEncrypt("biexTest"));
        System.out.println(EncryptUtils2.desEncrypt("biexDev"));
        System.out.println(EncryptUtils2.desEncrypt("tokenvinDev"));
        System.out.println(EncryptUtils2.desEncrypt("tokenvinTest"));
        System.out.println(EncryptUtils2.desDecrypt("4lavlg0WomZGZLZXWizxx9kUkfRXRtLUlorjwRxttG6fv6CNV+miGw="));

        /*
        while (true)
        {
           new Thread(new Runnable() {
               @Override
               public void run() {
                   String key = "gcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnrgcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnrgcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnrgcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnrgcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnrgcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnrgcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnr" +
                           "gcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnrgcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnrgcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnrgcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnrgcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnrgcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnrgcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnr" +
                           "" +
                           "gcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnrgcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnrgcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnrgcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnrgcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnr" +
                           "gcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnrgcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnrgcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnrgcNnIpc7fM9HUj7RyflSnUsFXxreV2T3GTrHkStAnr";
                   String str = EncryptUtils2.desEncrypt(key);
                  // System.out.println(key);
                   System.out.println(str);
                   System.out.println(EncryptUtils2.desDecrypt(str));
               }
           }).start();

        }
        */
    }
}
