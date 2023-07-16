package com.king.common.module.domain;

import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *    事务消费者接口
 *      函数式接口
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-06-29
 **/
@FunctionalInterface
public interface TransactionConsumer {

    @Transactional
    void doInTransaction();
}
