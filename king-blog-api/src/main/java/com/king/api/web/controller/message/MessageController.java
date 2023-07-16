package com.king.api.web.controller.message;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.king.common.exception.WrongInputException;
import com.king.common.module.domain.ResponseResult;
import com.king.common.module.domain.ScrollResult;
import com.king.sys.bean.dto.message.MessageConsumerDto;
import com.king.sys.bean.dto.message.Message;
import com.king.sys.service.message.IMessageService;
import com.king.sys.service.message.IWebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *    前端控制器
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-09
 **/
@RestController
@RequestMapping("/v1/message")
public class MessageController {

    @Autowired
    private IWebSocketService webSocketService;

    // 注入message服务类
    @Autowired
    private List<IMessageService> messageServices;

    /**
     * 获取 MessageService 实例
     */
    private IMessageService getMessageServiceInstance(String type){
        return messageServices.stream()
                .filter(item -> item.isMessageType(type)).findFirst()
                .orElseThrow(WrongInputException::new);
    }

    /**
     * 获取回复的消息
     *  key 是被回复人id(接收者,消费者)
     *  value 是回复消息的内容
     *  source 时间戳
     *
     * @param identify 用户唯一标识
     * @param consumerDto 查询条件
     * @return result
     */
    @PostMapping("/{identify}/getMessage")
    public ResponseResult<ScrollResult> getMessage(@PathVariable String identify,
                                                   @RequestParam(name = "type", defaultValue = "system") String type,
                                                   @RequestBody MessageConsumerDto consumerDto){
        IMessageService messageService = this.getMessageServiceInstance(type);
        consumerDto.setIdentify(identify);
        return ResponseResult.success(messageService.queryMessage(consumerDto));
    }

    /**
     * 生成消息 controller
     * @param type 类型
     * @param message message
     * @return result
     */
    @PostMapping("/producer")
    public ResponseResult<String> producerMessage(@RequestParam(name = "type") String type,
                                          @RequestBody Message message){
        IMessageService messageService = this.getMessageServiceInstance(type);
        messageService.producerMessage(message);
        return ResponseResult.success();
    }

    @GetMapping("/getTime")
    public String getTime(@RequestParam(value = "timestamp") String timestamp){
        DateTime date = DateUtil.date(Long.parseLong(timestamp));
        return date.toString();
    }


    @GetMapping("/sendMsg")
    public ResponseResult<String> sendMessage(@RequestParam("text") String text){
        webSocketService.broadcastMessage(text);
        return ResponseResult.success("ok");
    }
}
