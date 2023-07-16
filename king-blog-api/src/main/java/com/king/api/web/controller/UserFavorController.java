package com.king.api.web.controller;

import com.king.common.module.domain.ResponseResult;
import com.king.core.aspectj.preven.Prevent;
import com.king.sys.bean.dto.UserFavorDto;
import com.king.sys.bean.vo.FavorVo;
import com.king.sys.service.system.IUserFavorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *    用户收藏表 前端控制器
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-05
 **/
@RestController
@RequestMapping("/v1/user-favor")
public class UserFavorController {

    @Autowired
    private IUserFavorService iUserFavorService;

    /**
     * 保存用户收藏文章
     * @param userFavorDto dto
     */
    @Prevent(message = "请勿重复请求", value = "10")
    @PostMapping("/save")
    public ResponseResult<Object> saveFavor(@RequestBody @Validated UserFavorDto userFavorDto){
        iUserFavorService.saveUserFavor(userFavorDto);
        return ResponseResult.success();
    }

    /**
     * 查询文章是否已收藏
     */
    @GetMapping("/selExist")
    public ResponseResult<Boolean> isExist(@RequestParam(name = "identify", required = false) String identify,
                                   @RequestParam(name = "blogId") String bid ){
        return ResponseResult.success(iUserFavorService.isFavor(identify, bid));
    }

    /**
     * 删除用户收藏的文章
     * @param userFavorDto dto
     */
    @Prevent(message = "请勿重复请求", value = "10")
    @DeleteMapping("/delete")
    public ResponseResult<List<FavorVo>> delete(@RequestBody @Validated UserFavorDto userFavorDto){
        return ResponseResult.success(iUserFavorService.deleteFavorByIdAndBlogId(userFavorDto));
    }

    /**
     * 通过用户唯一标识获取用户收藏的文章
     * @param identify 用户唯一标识
     */
    @GetMapping("/selectAll")
    public ResponseResult<Object> selectAll(@RequestParam(name = "identify") String identify){ ;
        return ResponseResult.success(iUserFavorService.selectAllByIdentify(identify));
    }

}
