package com.king.common.utils;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Date;

/**
 * <p>
 *    邮件发送工具类
 * </p>
 * @author king
 * @version 1.0
 * @since 2023-06-21
 **/
public final class EmailUtil {

    private EmailUtil(){
        throw new AssertionError("No com.king.common.utils.EmailUtil instances for you!");
    }

    /**
     * 纯文本邮件
     *
     * @param from 发送者
     * @param to 接收者
     * @param content 发送内容
     * @return {@link SimpleMailMessage}
     */
    public static SimpleMailMessage simple(String from, String to, String content){
        SimpleMailMessage message= new SimpleMailMessage();
        message.setFrom(from);  // 发送人
        message.setTo(to);   // 接收者
        message.setSentDate(new Date());
        message.setSubject("【king`blog】注册邮箱验证");
        message.setText(content);
        return message;
    }

    /**
     * 纯文本邮件
     *
     * @param from 发送者
     * @param to 接受者
     * @param subject 主题
     * @param content 发送内容
     * @return {@link SimpleMailMessage}
     */
    public static SimpleMailMessage simple(String from, String to, String subject, String content){
        SimpleMailMessage message= new SimpleMailMessage();
        message.setFrom(from);  // 发送人
        message.setTo(to);   // 接收者
        message.setSentDate(new Date());
        message.setSubject(subject);
        message.setText(content);
        return message;
    }

    /**
     * 多媒体邮件
     *
     * @param message {@link MimeMessage}
     * @param from 发送者
     * @param to 接收者
     * @return {@link MimeMessage}
     */
    public static MimeMessage mimeMessage(MimeMessage message, String from, String to) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(message,true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject("复杂邮件主题");
        helper.setText("<p style='color: deepskyblue'>这是浅蓝色的文字</p>", true);
        helper.addAttachment("1.docx", new File("C:\\Users\\Splay\\Desktop\\说明书.docx"));
        helper.addAttachment("1.pdf", new File("C:\\Users\\Splay\\Desktop\\参考.pdf"));
        return message;
    }
}
