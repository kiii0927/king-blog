package com.king.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * <p>EntityUtils工具类用于基于Lambda表达式实现类型转换，具有如下优点：</p>
 * <p>1. 实现对象转对象；集合转集合；分页对象转分页对象</p>
 * <p>2. 实体类转Vo、实体类转DTO等都能应用此工具类</p>
 * <p>3. 转换参数均为不可变类型，业务更加安全</p>
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/06/19 17:23
 **/
@SuppressWarnings(value = { "unchecked"})
public final class EntityUtil {

    private EntityUtil() {
        throw new AssertionError("No com.king.common.utils.EntityUtil instances for you!");
    }

    /**
     * 将对象集合按照一定规则映射后收集为另一种形式的集合
     *
     * @param <R>       最终结果的泛型
     * @param <S>       原始集合元素的类泛型
     * @param <T>       转换后元素的中间状态泛型
     * @param <A>       最终结果收集器泛型
     * @param source    最原始的集合实例
     * @param action    转换规则
     * @param collector 收集器的类型
     * @return 变换后存储新元素的集合实例
     */
    public static <R, S, T, A> R collectCommon(final Collection<S> source, Function<? super S, ? extends T> action, Collector<? super T, A, R> collector) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(collector);
        return source.stream().map(action).collect(collector);
    }

    /**
     * 将对象集合按照一定规则映射后收集为另一种形式的集合
     *
     * @param <S>    原始集合元素的类泛型
     * @param <T>    转换后元素的中间状态泛型
     * @param source 最原始的集合实例
     * @param action 转换规则
     * @return 变换后存储新元素的集合实例
     */
    @SafeVarargs
    public static <S, T> Set<T> collectSet(Function<? super S, ? extends T> action, final S... source) {
        Objects.requireNonNull(source);
        return collectSet(Arrays.asList(source), action);
    }


    /**
     * 将对象集合按照一定规则映射后收集为另一种形式的集合
     *
     * @param <S>    原始集合元素的类泛型
     * @param <T>    转换后元素的中间状态泛型
     * @param source 最原始的集合实例
     * @param action 转换规则
     * @return 变换后存储新元素的集合实例
     */
    public static <S, T> Set<T> collectSet(final Collection<S> source, Function<? super S, ? extends T> action) {
        Objects.requireNonNull(source);
        return source.stream().map(action).collect(Collectors.toSet());
    }

    /**
     * 将对象集合按照一定规则映射后收集为List集合
     *
     * @param <S>    原始集合元素的类泛型
     * @param source 最原始的集合实例
     * @param action 转换规则
     * @return 变换后存储新元素的集合实例
     */
    public static <S> List<? extends S> collectList(final Collection<S> source, Function<? super S, ? extends S> action) {
        return collectCommon(source, action, Collectors.toList());
    }

    /**
     * 将对象以一种类型转换成另一种类型
     *
     * @param <T>    源数据类型
     * @param <R>    变换后数据类型
     * @param source 源List集合
     * @param action 映射Lmabda表达式
     * @return 变换后的类型，如果source为null,则返回null
     */
    public static <T, R> R toObj(final T source, final Function<? super T, ? extends R> action) {
        Objects.requireNonNull(action);
        return Optional.ofNullable(source).map(action).orElse(null);
    }

    /**
     * <p>将{@code List}集合换成另一种类型</p>
     * <pre>
     *     public class User {
     *         private Long userId;
     *         private String userName;
     *         private String sex;
     *     }
     * </pre>
     * <p>通过方法引用获得任意列组成的新{@code List}集合</p>
     * <pre>
     *     List&lt;Long&gt; userIds = EntityUtils.toList(list,User::getUserId)
     * </pre>
     * <p>在{@code User}类中添加有如下构造器</p>
     * <pre>
     *     public User(User user) {
     *         if(user != null) {
     *             this.userId = user.userId;
     *             this.userName = user.userName;
     *             this.sex = user.sex;
     *         }
     *     }
     * </pre>
     * <pre>
     *     public class UserVo extends User {
     *         private String deptName;
     *
     *         public UserVo (User user) {
     *             super(user);
     *         }
     *     }
     * </pre>
     * 通过如下代码可实现DO 转 VO
     * <pre>
     *     List&lt;Long&gt; userVos = EntityUtils.toList(list,UserVo::new)
     * </pre>
     *
     * @param <T>    源数据类型
     * @param <R>    变换后数据类型
     * @param source 源List集合
     * @param action 映射Lmabda表达式
     * @return 变换后的类型集合，如果source为null,则返回空集合
     */
    public static <T, R> List<R> toList(final Collection<T> source, final Function<? super T, ? extends R> action) {
        Objects.requireNonNull(action);
        if (Objects.nonNull(source)) {
            return source.stream().map(action).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * 将Array数组以一种类型转换成另一种类型
     *
     * @param <T>    源数据类型
     * @param <R>    变换后数据类型
     * @param source 源Array数组
     * @param action 映射Lmabda表达式
     * @return 变换后的类型集合，如果source为null,则返回空集合
     */
    public static <T, R> R[] toArray(final T[] source, final Function<? super T, ? extends R> action) {
        Objects.requireNonNull(action);
        if (Objects.nonNull(source)) {
            return (R[]) Arrays.stream(source).map(action).toArray();
        }
        return (R[]) new ArrayList<R>().toArray();
    }

    /**
     * 将IPaged对象以一种类型转换成另一种类型
     *
     * @param source 源Page
     * @param action 转换规则
     * @param <E>    源Page类型泛型
     * @param <T>    源实体类
     * @param <R>    目标Page类型泛型
     * @return 变换后的分页类型
     */
    public static <E extends IPage<T>, T, R> IPage<R> toPage(E source, final Function<? super T, ? extends R> action) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(action);
        return source.convert(action);
    }

    /**
     * 将集合转化成Map
     *
     * @param lists       集合实例
     * @param keyAction   key转换规则
     * @param valueAction value转换规则
     * @param <T>         集合实体类泛型
     * @param <K>         Key实体类泛型
     * @param <V>         Value实体类泛型
     * @return Map实例
     */
    public static <T, K, V> Map<K, V> toMap(final Collection<T> lists, Function<? super T, ? extends K> keyAction, Function<? super T, ? extends V> valueAction) {
        Objects.requireNonNull(lists);
        Objects.requireNonNull(keyAction);
        Objects.requireNonNull(valueAction);
        return lists.stream().collect(Collectors.toMap(keyAction, valueAction));
    }

    /**
     * 将List集合以一种类型转换成Set集合
     *
     * @param <T>    源数据类型
     * @param <R>    变换后数据类型
     * @param source 源List集合
     * @param action 映射Lmabda表达式
     * @return 变换后的类型集合，如果source为null,则返回空集合
     */
    public static <T, R> Set<R> toSet(final Collection<T> source, final Function<? super T, ? extends R> action) {
        Objects.requireNonNull(action);
        if (Objects.nonNull(source)) {
            return source.stream().map(action).collect(Collectors.toSet());
        }
        return new HashSet<>();
    }
}
