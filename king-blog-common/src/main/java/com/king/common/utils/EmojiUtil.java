package com.king.common.utils;

import com.github.binarywang.java.emoji.EmojiConverter;

/**
 * <p>
 *    Emoji表情包处理工具类
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-06-21
 **/
public final class EmojiUtil {

    private static final EmojiConverter emojiConverter = EmojiConverter.getInstance();

    private EmojiUtil(){
        throw new AssertionError("No com.king.common.utils.EmojiUtil instances for you!");
    }

    /**
     * 将 emojiStr 转为 带有表情的字符
     * @param emojiStr emojiStr
     * @return str
     */
    public static String emojiConverterUnicodeStr(String emojiStr){
        return emojiConverter.toUnicode(emojiStr);
    }

    /**
     * 带有表情的字符串转换为编码
     * @param str emoji
     * @return str
     */
    public static String emojiConverterToAlias(String str){
        return emojiConverter.toAlias(str);
    }

    /**
     * 将emoji表情转换为html code
     * @param str emoji
     * @return string
     */
    public static String emojiConverterToHtml(String str){
        return emojiConverter.toHtml(str); //&#128539;
    }
}
