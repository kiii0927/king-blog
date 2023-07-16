package com.king.common.module.domain;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * <p>
 *    message result
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-05-23
 **/

@Setter
@Getter
public class MessageResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(MessageResult.class);

    private String message;

    private Dots dots;

    public MessageResult() {}

    public MessageResult(String message, Dots dots) {
        this.message = message;
        this.dots = dots;
    }

    public static Dots buildDots(String topic){
        Dots dots = new Dots();
        if (!StringUtils.hasLength(topic)) {
            logger.warn("topic没有定义");
        } else {
            switch (topic) {
                case "reply":
                    dots.setReply(true);
                    break;
                case "love":
                    dots.setLove(true);
                    break;
                case "attention":
                    dots.setAttention(true);
                    break;
                case "system":
                    dots.setSystem(true);
                    break;
                default:
                    logger.warn("不支持 dot 类型");
                    break;
            }
        }

        return dots;
    }

    @Getter
    @Setter
    //@Builder
    public static class Dots{
        private Boolean reply;

        private Boolean love;

        private Boolean attention;

        private Boolean system;

        public Dots() {
            this.reply = false;
            this.love = false;
            this.attention = false;
            this.system = false;
        }
    }

}
