package com.dus.discord_emoji_count_server.service;

import com.dus.discord_emoji_count_server.domain.MessageInfo;
import com.dus.discord_emoji_count_server.domain.UserClickInfo;
import com.dus.discord_emoji_count_server.domain.UserRank;
import com.dus.discord_emoji_count_server.repository.MessageRepository;
import org.h2.engine.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class MessageService {

    private Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }


    /**
     * 카운팅 해야하는 메세지 정보 저장
     * @param messageInfo
     */
    public void saveMessage(MessageInfo messageInfo){
        messageRepository.save(messageInfo);
    }

    /**
     * 카운팅 해야하는 모든 메세지 정보 가져옴
     * @return
     */
    public List<String> findMessages(){
        List<MessageInfo> messageInfoList = messageRepository.findAllMessageInfo();
        List<String> result = new ArrayList<>();
        for(MessageInfo mi: messageInfoList){
            result.add(mi.getMessageId());
        }

        return result;
    }


    /**
     * 유저 랭크 증가
     * @param userRank
     */
    public void increaseRank(UserRank userRank){
        String userId = userRank.getUserId();
        Optional<UserRank> optionalUserRank = messageRepository.findUserRankByUserId(userId);
        if(optionalUserRank.isPresent()){
            UserRank newUserRank = optionalUserRank.get();
            logger.info("유저 정보가 이미 있음");

            Long cnt = newUserRank.getClickCnt() + 1L;
            newUserRank.setClickCnt(cnt);
        }else {
            logger.info("유저 랭크가 없어서 처음 타는 곳");
            UserRank newUserRank = new UserRank();
            newUserRank.setUserId(userRank.getUserId());
            newUserRank.setUserTag(userRank.getUserTag());
            newUserRank.setClickCnt(1L);

            saveOrUpdateUserRank(newUserRank);
        }
    }

    /**
     * 유저 랭크 감소
     * @param userRank
     */
    public void decreaseRank(UserRank userRank){
        String userId = userRank.getUserId();
        Optional<UserRank> optionalUserRank = messageRepository.findUserRankByUserId(userId);
        if(optionalUserRank.isPresent()){
            UserRank newUserRank = optionalUserRank.get();
            logger.info("유저 정보가 이미 있음");

            Long cnt = newUserRank.getClickCnt() - 1L;
            newUserRank.setClickCnt(cnt);
        }
    }

    /**
     * 유저 랭크 저장
     * @param userRank
     */
    public void saveOrUpdateUserRank(UserRank userRank){
        messageRepository.save(userRank);
    }

    /**
     * 유저 랭크 모두 가져오기
     * @return
     */
    public List<UserRank> findAllUserRank(){
        List<UserRank> allUserRank = messageRepository.findAllUserRank();
        return sortUserRank(allUserRank);
    }

    /**
     * 유저 랭크 카운팅 기준으로 내림차순 정렬
     * @param userRanks
     * @return
     */
    public List<UserRank> sortUserRank(List<UserRank> userRanks){
        Collections.sort(userRanks, new Comparator<UserRank>() {
            @Override
            public int compare(UserRank o1, UserRank o2) {
                return (int) (o2.getClickCnt() - o1.getClickCnt());
            }
        });

        return userRanks;
    }


    /**
     * 클릭 정보 저장
     * @param userClickInfo
     */
    public void saveUserClickInfo(UserClickInfo userClickInfo){
        messageRepository.save(userClickInfo);
    }

    /**
     * 클릭 정보 삭제
     * @param messageId
     * @param userId
     */
    public void deleteUserClickInfo(String messageId, String userId){
        messageRepository.delete(messageId, userId);
    }

    /**
     * 모든 유저의 클릭 정보 모두 가져옴
     * @return List<UserClickInfo>
     */
    public List<UserClickInfo> findUserClickInfos(){
        return messageRepository.findAllUserClickInfo();
    }

    /**
     * 특정 유저의 클릭 정보 모두 가져옴
     * @param userId
     * @return
     */
    public List<UserClickInfo> findUserClickInfosByUserId(String userId){
        return messageRepository.findUserClickInfoByUserId(userId);
    }

    /**
     * 특정 메세지와 관련된 클릭 정보 모두 가져옴
     * @param messageId
     * @return
     */
    public List<UserClickInfo> findUserClickInfosByMessageId(String messageId){
        return messageRepository.findUserClickInfoByMessageId(messageId);
    }


}
