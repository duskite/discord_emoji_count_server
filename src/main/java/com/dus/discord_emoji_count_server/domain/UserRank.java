package com.dus.discord_emoji_count_server.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class UserRank {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String userId;
    String userTag;

    Long clickCnt;

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

    public Long getClickCnt() {
        return clickCnt;
    }

    public void setClickCnt(Long clickCnt) {
        this.clickCnt = clickCnt;
    }

    public String getUserTag() {
        return userTag;
    }

    public void setUserTag(String userTag) {
        this.userTag = userTag;
    }
}
