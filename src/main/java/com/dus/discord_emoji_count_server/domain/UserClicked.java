package com.dus.discord_emoji_count_server.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
public class UserClicked {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String userId;
    String messageId;
    LocalDate firstClickDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public LocalDate getFirstClickDate() {
        return firstClickDate;
    }

    public void setFirstClickDate(LocalDate firstClickDate) {
        this.firstClickDate = firstClickDate;
    }
}
