package com.dus.discord_emoji_count_server.service;

import com.dus.discord_emoji_count_server.domain.MessageInfo;
import com.dus.discord_emoji_count_server.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MessageServiceTest {

    @Autowired MessageService messageService;
    @Autowired MessageRepository messageRepository;


    @Test
    public void 메세지_저장(){
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setMessageId("aaaaaaa");

        messageService.saveMessage(messageInfo);

    }

}