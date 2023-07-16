package com.king.sys.assets;

import com.king.common.exception.ServiceException;

/**
 * <p>
 *    失败的行为(处理)
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-09
 **/
public final class FailureAction {

    //private FailureAction(){}


    /**
     * 接收一个 {@link Throwable} 异常错误, 做相应的处理
     * @param throwable {@link Throwable}
     */
    public static void accept(Throwable throwable) {
        throwable.printStackTrace();
        throw new ServiceException();
    }

}
