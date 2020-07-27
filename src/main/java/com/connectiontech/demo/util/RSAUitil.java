package com.connectiontech.demo.util;
import com.connectiontech.demo.common.ClientException;
import com.connectiontech.demo.common.ClientExceptionConstants;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * rsa工具类
 * @author Nero
 */
public class RSAUitil {
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 获取密钥对
     *
     * @return 密钥对
     */
    public static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        return generator.generateKeyPair();
    }

    /**
     * 获取私钥
     *
     * @param privateKey 私钥字符串
     * @return
     */
    public static PrivateKey getPrivateKey(String privateKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedKey = decoder.decode(privateKey.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 获取公钥
     *
     * @param publicKey 公钥字符串
     * @return
     */
    public static PublicKey getPublicKey(String publicKey)  {
        try{
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] decodedKey = decoder.decode(publicKey.getBytes());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
            return keyFactory.generatePublic(keySpec);
        }catch (Exception e){
            throw  new ClientException(ClientExceptionConstants.PUBLIC_KEY_ERROR);
        }
    }

    /**
     * RSA加密
     *
     * @param data 待加密数据
     * @param privateKey 私钥
     * @return
     */
    public static String encrypt(String data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        int inputLen = data.getBytes().length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data.getBytes(), offset, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data.getBytes(), offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        // 获取加密内容使用base64进行编码,并以UTF-8为标准转化成字符串
        // 加密后的字符串
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(encryptedData);
    }

    /**
     * RSA解密
     *
     * @param data 待解密数据
     * @param publicKey 公钥
     * @return
     */
    public static String decrypt(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] dataBytes = decoder.decode(data);
        int inputLen = dataBytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(dataBytes, offset, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(dataBytes, offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        // 解密后的内容
        return new String(decryptedData, "UTF-8");
    }

    /**
     * 签名
     *
     * @param data 待签名数据
     * @param privateKey 私钥
     * @return 签名
     */
    public static String sign(String data, PrivateKey privateKey) throws Exception {
        byte[] keyBytes = privateKey.getEncoded();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(key);
        signature.update(data.getBytes());
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(signature.sign());
    }

    /**
     * 验签
     *
     * @param srcData 原始字符串
     * @param publicKey 公钥
     * @param sign 签名
     * @return 是否验签通过
     */
    public static boolean verify(String srcData, PublicKey publicKey, String sign)  {
        byte[] keyBytes = publicKey.getEncoded();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        try{
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey key = keyFactory.generatePublic(keySpec);
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initVerify(key);
            signature.update(srcData.getBytes());
            Base64.Decoder decoder = Base64.getDecoder();
            return signature.verify(decoder.decode(sign.getBytes()));
        }catch (Exception e){
            throw new ClientException(ClientExceptionConstants.SIGN_VERIFY_ERROR);
        }
    }

    /**
     * 生成按字母升序排列非空请求参数
     * @param params
     * @return
     */
    public static String mapToString(Map<String,String> params){
        List<String> keys = new ArrayList<>(params.keySet());
        Map<String, String> filterNullMap = new HashMap<>(8);
        for(int i = 0; i < keys.size(); i++){
            String key = keys.get(i);
            if(key == null || "".equals(key)) continue;
            String value = params.get(key);
            if (value == null || "".equals(value)) continue;
            filterNullMap.put(key, value);
        }
        List<String> sortKeys = new ArrayList<>(filterNullMap.keySet());
        Collections.sort(sortKeys);
        StringBuffer prestr = null;
        for (String key : sortKeys) {
            if (params.get(key) == null) continue;
            String value = params.get(key);
            if (prestr == null) {
                prestr = new StringBuffer(key + "=" + value);
            } else {
                prestr.append("&").append(key).append("=").append(value);
            }
        }
        return prestr == null ? "" : prestr.toString();
    }

    public static void main(String[] args) {
        Map map = new HashMap();
        map.put("b","www");
        map.put("a","new Date()");
        String s = mapToString(map);
        System.out.println(s);


        try {
            // 生成密钥对
            KeyPair keyPair = getKeyPair();
            Base64.Encoder encoder = Base64.getEncoder();
            String privateKey = new String(encoder.encode(keyPair.getPrivate().getEncoded()));
            String publicKey = new String(encoder.encode(keyPair.getPublic().getEncoded()));
            System.out.println("私钥:" + privateKey);
            System.out.println("公钥:" + publicKey);
            // RSA加密
            String data = "待加密的文字内容";
            String encryptData = encrypt(data, keyPair.getPrivate());
            System.out.println("加密后内容:" + encryptData);
            // RSA解密
            String decryptData = decrypt(encryptData, keyPair.getPublic());
            System.out.println("解密后内容:" + decryptData);

            // RSA签名
            String sign = sign(data, getPrivateKey(privateKey));
            // RSA验签
            boolean result = verify(data, getPublicKey(publicKey), sign);
            System.out.print("验签结果:" + result);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print("加解密异常");
        }
    }

}
