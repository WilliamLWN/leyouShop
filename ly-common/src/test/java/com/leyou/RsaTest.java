package com.leyou;

import com.leyou.common.auth.utils.RsaUtils;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class RsaTest {
    private final String pubKeyPath = "D:\\software\\rsa_key\\rsa-key.pub";
    private final String priKeyPath = "D:\\software\\rsa_key\\rsa-key";


    /**
     * 生成密钥对
     * @throws Exception
     */
    @Test
    public void testGeneratekey() throws Exception {
        /**
         * 参数一：公钥存放位置
         * 参数二：私钥存放位置
         * 参数三：密文
         * 参数四：密钥大小（单位：b 字节）
         */
        RsaUtils.generateKey(pubKeyPath,priKeyPath,"itheima",2048);
    }

    /**
     * 读取公钥
     * @throws Exception
     */
    @Test
    public void testGetPubKey() throws Exception {
        PublicKey publicKey = RsaUtils.getPublicKey(pubKeyPath);
        System.out.println(publicKey);
    }

    /**
     * 读取公钥
     * @throws Exception
     */
    @Test
    public void testGetPriKey() throws Exception {
        PrivateKey privateKey = RsaUtils.getPrivateKey(priKeyPath);
        System.out.println(privateKey);
    }
}

