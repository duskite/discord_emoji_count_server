package com.dus.discord_emoji_count_server.service;

import com.dus.discord_emoji_count_server.domain.MessageInfo;
import com.dus.discord_emoji_count_server.domain.UserClickInfo;
import com.dus.discord_emoji_count_server.domain.FirstClicked;
import com.dus.discord_emoji_count_server.domain.UserRank;
import com.dus.discord_emoji_count_server.repository.MessageRepository;
import org.h2.engine.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

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

    public void deleteMessageId(String messageId){
        messageRepository.delete(messageId);
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
     * 토탈 유저 랭크 카운팅 기준으로 내림차순 정렬
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
     * 날짜 내림차순 정렬
     * @param userClickInfos
     * @return
     */
    public List<UserClickInfo> sortUserClickInfo(List<UserClickInfo> userClickInfos){
        Collections.sort(userClickInfos, new Comparator<UserClickInfo>() {
            @Override
            public int compare(UserClickInfo o1, UserClickInfo o2) {
                LocalDate localDate1 = o1.getClickDate();
                LocalDate localDate2 = o2.getClickDate();

                return -(localDate2.compareTo(localDate1));
            }
        });

        return userClickInfos;
    }

    /**
     * 날짜별 유저 랭크 리스트 생성
     * @param userClickInfos
     * @return
     */
    public List<UserRank> createDayUserRank(List<UserClickInfo> userClickInfos){

        return createUserRanks(userClickInfos);
    }

    /**
     * 메세지별 유저 랭크 리스트 생성
     * @param userClickInfos
     * @return
     */
    public List<UserRank> createUserRankByMessageID(List<UserClickInfo> userClickInfos){
        return createUserRanks(userClickInfos);
    }

    public List<UserRank> createUserRanks(List<UserClickInfo> userClickInfos){
        Map<String, Long> map = new HashMap<>();
        for(UserClickInfo userClickInfo: userClickInfos){
            String userTag = userClickInfo.getUserTag();
            Long cnt = map.getOrDefault(userTag, 0L);
            cnt++;

            map.put(userTag, cnt);
        }

        List<UserRank> userRanks = new ArrayList<>();
        for(String tag: map.keySet()){
            UserRank rank = new UserRank();
            rank.setUserTag(tag);
            rank.setClickCnt(map.get(tag));

            userRanks.add(rank);
        }

        return sortUserRank(userRanks);
    }


    /**
     * 클릭 정보 저장
     * @param userClickInfo
     */
    public void saveUserClickInfo(UserClickInfo userClickInfo){
        String userId = userClickInfo.getUserId();
        String messageId = userClickInfo.getMessageId();
        Optional<UserClickInfo> optionalUserClickInfo = messageRepository.findOneUserClickInfo(userId, messageId);
        if(!optionalUserClickInfo.isPresent()){
            messageRepository.save(userClickInfo);
        }
    }

    public Optional<UserClickInfo> getUserClickInfo(UserClickInfo userClickInfo){
        String userId = userClickInfo.getUserId();
        String messageId = userClickInfo.getMessageId();
        return messageRepository.findOneUserClickInfo(userId, messageId);
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

    /**
     * 날짜 별로 클리 정보 모두 가져옴
     * @param strDate
     * @return
     */
    public List<UserClickInfo> findUserClickInfosByDay(String strDate){

        LocalDate clickDate = createLocalDate(strDate);
        return messageRepository.findUserClickInfoByDay(clickDate);
    }

    private LocalDate createLocalDate(String day){
        String[] tmp = day.split("-");
        int[] yearMonthDay = Stream.of(tmp).mapToInt(Integer::parseInt).toArray();

        LocalDate localDate = LocalDate.of(yearMonthDay[0], yearMonthDay[1], yearMonthDay[2]);
        return localDate;
    }

    /**
     * 최초 유저 클릭 가져옴
     * @param userClickInfo
     * @return
     */
    public Optional<FirstClicked> findFirstClicked(UserClickInfo userClickInfo){

        String userId = userClickInfo.getUserId();
        String messageId = userClickInfo.getMessageId();

        Optional<FirstClicked> firstClicked = messageRepository.findFirstClicked(userId, messageId);

        return firstClicked;
    }

    /**
     * 첫 클릭 저장
     * @param firstClicked
     */
    public void saveFirstClicked(FirstClicked firstClicked){
        messageRepository.save(firstClicked);
    }

}
