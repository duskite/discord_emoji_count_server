package com.dus.discord_emoji_count_server.controller;

import com.dus.discord_emoji_count_server.domain.UserClickInfo;
import com.dus.discord_emoji_count_server.domain.UserRank;
import com.dus.discord_emoji_count_server.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class HomeController {

    /*
        관리자 기준 화면 구성을 위한 컨트롤러
        DB 데이터를 웹으로 보여주는 것에 초점
     */

    private Logger logger = LoggerFactory.getLogger(HomeController.class);
    private final MessageService messageService;

    public HomeController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/")
    public String home(){
        return "home";
    }

    @GetMapping("/allList")
    public String allClickInfo(Model model){
        List<UserClickInfo> userClickInfos = messageService.findUserClickInfos();
        model.addAttribute("userClickInfos", userClickInfos);
        model.addAttribute("cntClick", userClickInfos.size());

        return "list/allList";
    }

    @GetMapping("/listByUserId")
    public String clickInfoByUserId(@RequestParam("userId") String userId, Model model){
        List<UserClickInfo> userClickInfos = messageService.findUserClickInfosByUserId(userId);
        model.addAttribute("userClickInfos", userClickInfos);
        model.addAttribute("cntClick", userClickInfos.size());

        return "list/listByUserId";
    }

    @GetMapping("/listByMessageId")
    public String clickInfoByMessageId(@RequestParam("messageId") String messageId, Model model){
        List<UserClickInfo> userClickInfos = messageService.findUserClickInfosByMessageId(messageId);
        model.addAttribute("userClickInfos", userClickInfos);
        model.addAttribute("cntClick", userClickInfos.size());

        List<UserRank> userRanks = messageService.createUserRankByMessageID(userClickInfos);
        model.addAttribute("userRanks", userRanks);
        model.addAttribute("cntPeople", userRanks.size());

        return "list/listByMessageId";
    }

    @GetMapping("/board")
    public String rank(Model model){
        List<UserRank> userRanks = messageService.findAllUserRank();
        model.addAttribute("userRanks", userRanks);
        model.addAttribute("cntPeople", userRanks.size());

        return "board/totalRank";
    }

    @GetMapping("/listByDay")
    public String listByDay(@RequestParam("clickDate") String strDate, Model model){

        if(strDate.equals("")){
            return "redirect:/";
        }

        List<UserClickInfo> userClickInfos = messageService.findUserClickInfosByDay(strDate);
        model.addAttribute("userClickInfos", userClickInfos);
        model.addAttribute("cntClick", userClickInfos.size());

        List<UserRank> userRanks = messageService.createDayUserRank(userClickInfos);
        model.addAttribute("userRanks", userRanks);
        model.addAttribute("cntPeople", userRanks.size());

        return "list/listByDay";
    }

}
