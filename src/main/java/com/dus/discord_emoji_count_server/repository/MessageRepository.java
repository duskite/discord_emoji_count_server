package com.dus.discord_emoji_count_server.repository;

import com.dus.discord_emoji_count_server.domain.MessageInfo;
import com.dus.discord_emoji_count_server.domain.UserClickInfo;
import com.dus.discord_emoji_count_server.domain.UserClicked;
import com.dus.discord_emoji_count_server.domain.UserRank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class MessageRepository {

    private final EntityManager em;

    public MessageRepository(EntityManager em) {
        this.em = em;
    }

    /**
     * 카운팅 해야하는 메세지 정보 저장
     * @param messageInfo
     */
    public void save(MessageInfo messageInfo){
        em.persist(messageInfo);
    }

    /**
     * 카운팅 체크해야하는 메세지 고유 id 모두 가져옴
     * @return
     */
    public List<MessageInfo> findAllMessageInfo(){
        return em.createQuery("select m from MessageInfo m", MessageInfo.class)
                .getResultList();
    }


    /**
     * 유저의 랭크 (클릭 횟수) 가져오기
     * @param userId
     * @return
     */
    public Optional<UserRank> findUserRankByUserId(String userId){
        List<UserRank> userRank = em.createQuery("select m from UserRank m where m.userId=:userId", UserRank.class)
                .setParameter("userId", userId).getResultList();

        return userRank.stream().findAny();
    }
    /**
     * 유저 랭크 저장
     * @param userRank
     */
    public void save(UserRank userRank){
        em.persist(userRank);
    }

    /**
     * 유저 랭크 가져오기
     * @return
     */
    public List<UserRank> findAllUserRank(){
        return em.createQuery("select m from UserRank m", UserRank.class)
                .getResultList();
    }

    /**
     * 유저가 최초로 이모지 클리한 날짜 기록이 목적임
     * @param userClicked
     */
    public void save(UserClicked userClicked){
        em.persist(userClicked);
    }

    /**
     * 유저가 최초로 클릭했던 정보 가져옴
     * @param userId
     * @param messageId
     * @return
     */
    public Optional<UserClicked> findUserClicked(String userId, String messageId){
        List<UserClicked> userClickeds = em.createQuery("select m from UserClicked m where m.userId=:userId and m.messageId=:messageId", UserClicked.class)
                .setParameter("userId", userId)
                .setParameter("messageId", messageId)
                .getResultList();

        return userClickeds.stream().findAny();
    }

    /**
     * 클릭 정보 저장
     * @param userClickInfo
     */
    public void save(UserClickInfo userClickInfo){
        em.persist(userClickInfo);
    }

    /**
     * 클릭 정보를 삭제함
     * @param messageId
     * @param userId
     */
    public void delete(String messageId, String userId){
        List<UserClickInfo> resultList = em.createQuery("select m from UserClickInfo m where m.userId=:userId and m.messageId=:messageId", UserClickInfo.class)
                .setParameter("messageId", messageId)
                .setParameter("userId", userId)
                .getResultList();

        Optional<UserClickInfo> optionalUserClickInfo = resultList.stream().findAny();
        em.remove(optionalUserClickInfo.get());
    }

    /**
     * 모든 유저에 대한 클릭 정보 가져옴
     * @return
     */
    public List<UserClickInfo> findAllUserClickInfo(){
        return em.createQuery("select m from UserClickInfo m", UserClickInfo.class)
                .getResultList();
    }

    /**
     * 유저 고유 id로 클릭 정보들 모두 가져옴
     * @param userId
     * @return List<UserClickInfo>
     */
    public List<UserClickInfo> findUserClickInfoByUserId(String userId){
        return em.createQuery("select m from UserClickInfo m where m.userId=:userId", UserClickInfo.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    /**
     * 메세지 고유 id로 모든 클릭 정보 가져옴
     * @param messageId
     * @return List<UserClickInfo>
     */
    public List<UserClickInfo> findUserClickInfoByMessageId(String messageId){
        return em.createQuery("select m from UserClickInfo m where m.messageId=:messageId", UserClickInfo.class)
                .setParameter("messageId", messageId)
                .getResultList();
    }


    /**
     * 날짜를 가지고 해당 날짜에 눌린 모든 클릭 정보 가져옴
     * @param localDate
     * @return
     */
    public List<UserClickInfo> findUserClickInfoByDay(LocalDate localDate){
        return em.createQuery("select m from UserClickInfo m where m.clickDate=:localDate", UserClickInfo.class)
                .setParameter("localDate", localDate)
                .getResultList();
    }
}
