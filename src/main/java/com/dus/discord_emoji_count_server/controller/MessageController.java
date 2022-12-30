package com.dus.discord_emoji_count_server.controller;

import com.dus.discord_emoji_count_server.domain.MessageInfo;
import com.dus.discord_emoji_count_server.domain.UserClickInfo;
import com.dus.discord_emoji_count_server.domain.UserRank;
import com.dus.discord_emoji_count_server.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    /*
        카운팅 해야하는 메세지 정보와
        유저가 메세지에 리액션한 클릭 정보를 처리하는 컨트롤러

        기본적으로 삽입, 삭제를 주로 담당
     */

    private Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/list")
    public List<String> getMessageList(){
        return messageService.findMessages();
    }


    @PostMapping("/messageId")
    public void setMessage(@RequestParam(name = "messageId") String messageId){

        logger.info("메세지id 넘어온 값: " + messageId);

        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setMessageId(messageId);

        messageService.saveMessage(messageInfo);
    }

    // 이미 한번이라도 누른적 있으면 새로 눌렀을때


    @PostMapping("/userClickInfo")
    public void setUserClickInfo(@RequestBody UserClickInfo userClickInfo){
        userClickInfo.setClickDate(getClickTime());
        messageService.saveUserClickInfo(userClickInfo);

        UserRank userRank = new UserRank();
        userRank.setUserId(userClickInfo.getUserId());
        userRank.setUserTag(userClickInfo.getUserTag());
        increaseUserRank(userRank);
    }

    public LocalDate getClickTime(){
        return LocalDate.now();
    }

    @DeleteMapping("/deleteUserClick/{messageId}/{userId}")
    public void deleteUserClickInfo(@PathVariable("messageId") String messageId, @PathVariable("userId") String userId){
        messageService.deleteUserClickInfo(messageId, userId);

        UserRank userRank = new UserRank();
        userRank.setUserId(userId);
        decreaseUserRank(userRank);
    }

    public void increaseUserRank(UserRank userRank){
        messageService.increaseRank(userRank);
    }
    public void decreaseUserRank(UserRank userRank){
        messageService.decreaseRank(userRank);
    }

}