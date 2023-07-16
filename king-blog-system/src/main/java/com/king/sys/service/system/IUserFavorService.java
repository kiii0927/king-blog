package com.king.sys.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.sys.bean.dto.UserFavorDto;
import com.king.sys.bean.entity.system.UserFavor;
import com.king.sys.bean.vo.FavorVo;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * <p>
 *    用户信息表 服务类
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-05
 **/
public interface IUserFavorService extends IService<UserFavor> {
    /**
     * 保存或取消用户收藏
     * @param userFavorDto {@link UserFavorDto}
     */
    void saveUserFavor(UserFavorDto userFavorDto);

    /**
     * 查询文章是否已收藏
     * @param id 文章收藏id
     * @return true or false
     */
    boolean isFavor(String id);

    /**
     * 查询文章是否已收藏
     * @param identify 用户唯一标识
     * @param bid 文章id
     * @return true or false
     */
    boolean isFavor(String identify, String bid);


    /**
     * 通过用户唯一标识查询用户收藏的文章
     * @param identify 唯一标识
     * @return {@link List}
     */
    @Nullable
    List<FavorVo> selectAllByIdentify(String identify);

    /**
     * 删除用户收藏的文章
     * @param userFavorDto {@link UserFavorDto}
     * @return {@link List}
     */
    //List<FavorVo> deleteFavorById(UserFavorDto userFavorDto);
    List<FavorVo> deleteFavorByIdAndBlogId(UserFavorDto userFavorDto);

    /**
     * 通过文章id、 identify进行删除, identify查询剩余的数据
     * @param identify 唯一标识
     * @param bid blog id
     * @return 删除的收藏数据 {@link List}
     */
    List<FavorVo> deleteFavorByIdAndBlogId(String identify, String bid);
}
