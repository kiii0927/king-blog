package com.king.common.module.constant;

/**
 * @author k
 * @version 1.0
 * @since 2023-06-20
 **/
public interface Constant {

    /**
     * error msg
     */
    String ERROR_MSG = "操作失败，系统繁忙！";

    /**
     * 默认的时间格式化
     */
    String DEFAULT_DATE_FORMAT_PATTERN = "YYYY-MM-dd HH:mm:ss";

    /**
     * 标准日期格式，精确到分：yyyy年MM月dd日 HH时mm分
     */
    String CHINESE_DATE_TIME_PATTERN_MINUTE = "yyyy年MM月dd日 HH:mm";

    /**
     * md5 加密 salt
     */
    String SALT = "1t2g3a4d";

    /**
     * 15天的时间戳
     */
    long DAYS_15TIME = 1000 * 60 * 60 * 24 * 15;

    /**
     * 1天的时间戳
     */
    long DAYS_TIME = 1000 * 60 * 60 * 24;

    /**
     * blog 详情前缀的key
     */
    String BLOG_DETAIL_PREFIX_KEY = "bd";

    /**
     * 默认页面大小
     */
    Integer DEFAULT_PAGE_SIZE = 5;

    /**
     * 评论页面大小
     */
    Integer COMMENT_PAGE_SIZE = 5;

    /**
     * 回复总数页面大小
     */
    Integer TOTAL_REPLY_PAGE_SIZE = COMMENT_PAGE_SIZE * 5;

    /**
     * base upload url
     */
    String BASE_UPLOAD_URl = "/upload/";

    /**
     * 滚动加载默认数量
     */
    long SOURCE_COUNT = 20;

    /**
     * 偏移天数 复：减 正：加
     */
    long OFFSET_NUMBER = -30;

    /**
     * 单页分页条数限制
     */
    long maxLimit = 50;

    /**
     * 项目根目录路径
     */
    String BASE_ROOT_STATIC_PATH = "king-blog-api/src/main/resources/static";

    /**
     * file上传位置
     */
    String DEFAULT_AVATAR_UPLOAD_PATH = BASE_ROOT_STATIC_PATH + "/img/upload/avatar";
    //String DEFAULT_UPLOAD_PATH = "K:\\Program\\Pictures\\avatar\\";

    /**
     * 头像上传绝对路径
     */
    String AVATAR_UPLOAD_ABSTRACT_PATH = "F:\\project\\java\\myblog\\king-blog\\king-blog-api\\src\\main\\resources\\static\\img\\upload\\avatar\\";

}
