package com.king.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.sys.bean.entity.system.UserFavor;
import com.king.sys.bean.vo.FavorVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *     mapper 接口
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-05
 **/
public interface UserFavorMapper extends BaseMapper<UserFavor> {

    /**
     * 通过收藏的文章id进行删除
     * @param id 收藏的文章id
     */
    void delById(@Param("id") Long id);

    /**
     * 根据收藏的id1修改状态
     * @param id 收藏的id
     */
    void updateDeletedById(@Param("id") Long id);

    /**
     * 根据用户唯一标识查询用户收藏的文章
     * @param identify 用户唯一标识
     * @return {@link List}
     */
    List<FavorVo> findAllByIdentify(@Param("identify") String identify);

    /**
     * 查询单条记录, 包括逻辑删除
     * @param id 收藏的id
     * @param identify 用户唯一标识
     * @return {@link UserFavor}
     */
    UserFavor selectOne(@Param("b_id") Long bid, @Param("identify") String identify);

}
