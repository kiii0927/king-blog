package com.king.common.utils;

import cn.hutool.crypto.SecureUtil;
import com.king.common.module.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author king
 * @version 1.0
 * @since 2023-06-21
 **/
public final class MD5Util {

    private static final Logger log = LoggerFactory.getLogger(MD5Util.class);

    private static final String salt = Constant.SALT;

    private MD5Util(){}

    private static String md5(String str){
        return SecureUtil.md5(str);
    }

    /**
     * MD5第一次加密
     * @param inputPass 输入的密码
     * @return str
     */
    private static String inputPassToFromPass(String inputPass){
        String str =  salt.charAt(0)+ salt.charAt(2)+inputPass + salt.charAt(5)+ salt.charAt(4);
        return md5(str);
    }

    /**
     * MD5第二次加密
     * @param formPass 输入(从那来)的密码
     * @param salt salt
     * @return str
     */
    private static String fromPassToDBPass(String formPass, String salt){
        String str = salt.charAt(6)+salt.charAt(2)+salt.charAt(4)+formPass+salt.charAt(7);
        return md5(str);
    }

    /**
     * MD5加密  web到数据库 (结合第一二次加密)
     * @param inputPass 输入的密码
     * @param salt salt
     * @return str
     */
    public static String inputPassToDBPass(String inputPass, String salt){
        String formPass = inputPassToFromPass(inputPass);
        return fromPassToDBPass(formPass, salt);
    }

    /**
     * 加密/解密算法 执行一次加密，两次解密
     * @param inStr str
     * @return str
     */
    public static String convertMD5(String inStr){
        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++){
            a[i] = (char) (a[i] ^ 't');
        }
        return new String(a);
    }

    public static void main(String[] args) {

        System.out.println(inputPassToFromPass("123456")); //e0b77b3f918a014b4c28ba52786e3310
        System.out.println(fromPassToDBPass("e0b77b3f918a014b4c28ba52786e3310", salt)); // b9489806d07344df105fb6593d2dd9ff
        System.out.println(inputPassToDBPass("123456", salt));


        String s = "123456";
        System.out.println("原始：" + s);
        System.out.println("MD5后：" + MD5Util.inputPassToDBPass(s, salt));
        //System.out.println("加密的：" + convertMD5(s));
        System.out.println("解密的：" + convertMD5(convertMD5(s)));
        System.out.println("s = " + s);

        //String pass = MD5Util.inputPassToDBPass("asfadsfasf123", Constant.salt);
        //System.out.println(pass);
    }

}
