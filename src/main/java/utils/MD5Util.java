package utils;

import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by ooopic on 2017/8/3.
 */
public class MD5Util {
    private static SecureRandom random = new SecureRandom();

    public static String getRandomString() {
        return new BigInteger(130, random).toString(32);
    }

    public static String KeyCreate() {
        StringBuffer Keysb = new StringBuffer(getRandomString());
        return Keysb.toString();
    }

    /**
     *          * 对随机秘钥进行MD5加密
     *          * @param Keysb
     *          * @return	SecretKey
     *          * @throws NoSuchAlgorithmException
     *          * @throws UnsupportedEncodingException
     *          
     */


    public static String OnSecreatKey(String Keysb) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        String SecretKey = base64en.encode(md5.digest(Keysb.getBytes("utf-8")));
        return SecretKey;
    }
}
