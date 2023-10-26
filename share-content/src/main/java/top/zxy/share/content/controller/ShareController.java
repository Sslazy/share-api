package top.zxy.share.content.controller;


import cn.hutool.json.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.zxy.share.common.resp.CommonResp;

import top.zxy.share.common.util.JwtUtil;
import top.zxy.share.content.domain.dto.ExchangeDTO;
import top.zxy.share.content.domain.dto.ShareRequestDTO;
import top.zxy.share.content.domain.entity.Notice;
import top.zxy.share.content.domain.entity.Share;
import top.zxy.share.content.domain.resp.ShareResp;
import top.zxy.share.content.feign.MyUserService;
import top.zxy.share.content.service.NoticeService;
import top.zxy.share.content.service.ShareService;
import top.zxy.share.user.domain.entity.BonusEventLog;

import java.util.List;

@RestController
@RequestMapping(value = "/share")
@Slf4j
public class ShareController {

    @Resource
    private NoticeService noticeService;

    @Resource
    private ShareService shareService;

    // 定义每页最多的数据量，以防前端定义传递超大参数，造成页面数据量多大
    private final int MAX = 100;

    @GetMapping("/{id}")
    public CommonResp<ShareResp> getShareById(@PathVariable Long id){
        ShareResp shareResp = shareService.findById(id);
        CommonResp<ShareResp> commonResp = new CommonResp<>();
        commonResp.setData(shareResp);
        return commonResp;
    }

    @GetMapping(value = "/notice")
    public CommonResp<Notice> getLatestNotice(){
        CommonResp<Notice> commonResp = new CommonResp<>();
        commonResp.setData(noticeService.getLatest());
        return commonResp;
    }

    @GetMapping("/list")
    public CommonResp<List<Share>> getShareList(@RequestParam(required = false) String title,
                                                @RequestParam(required = false,defaultValue = "1") Integer pageNo,
                                                @RequestParam(required = false,defaultValue = "3") Integer pageSize,
                                                @RequestHeader(value = "token", required = false) String token){
        if(pageSize > MAX){
            pageSize = MAX;
        }
        long userId = getUserIdFromToken(token);
        CommonResp<List<Share>> commonResp = new CommonResp<>();
        commonResp.setData(shareService.getList(title,userId,pageNo,pageSize));
        return commonResp;
    }

    private long getUserIdFromToken(String token){
        log.info(">>>>>>> token"+ token);
        long userId = 0;
        String noToken = "no-token";
        if ( !noToken.equals(token) ){
            JSONObject jsonObject = JwtUtil.getJSONObject(token);
            log.info("解析到 token 的 json 数据为:{}",jsonObject);
            userId = Long.parseLong(jsonObject.get("id").toString());
        }else {
            log.info("没有 token");
        }
        return userId;
    }

    @PostMapping("/exchange")
    public CommonResp<Share> exchange(@RequestBody ExchangeDTO exchangeDTO){
        System.out.println(exchangeDTO);
        CommonResp<Share> commonResp = new CommonResp<>();
        commonResp.setData(shareService.exchange(exchangeDTO));
        return commonResp;
    }

    @GetMapping("/my-exchange")
    public CommonResp<List<Share>> myExchange(@RequestHeader(value = "token", required = false) String token) {
        long id = getUserIdFromToken(token);
        List<Share> shares = shareService.myExchange(id);
        CommonResp<List<Share>> resp = new CommonResp<>();
        resp.setData(shares);
        return resp;
    }

    @PostMapping("/contribute")
    public CommonResp<Integer> contributeShare(@RequestBody ShareRequestDTO shareRequestDTO,@RequestHeader(value = "token",required = false) String token){
        long userId = getUserIdFromToken(token);
        shareRequestDTO.setUserId(userId);
        CommonResp<Integer> commonResp = new CommonResp<>();
        commonResp.setData(shareService.contribute(shareRequestDTO));
        return commonResp;
    }

    @GetMapping("/my-contribute")
    public CommonResp<List<Share>> myContribute(
            @RequestParam(required = false,defaultValue = "1") Integer pageNo,
            @RequestParam(required = false,defaultValue = "3") Integer pageSize,
            @RequestHeader(value = "token", required = false) String token){
        if(pageSize > MAX){
            pageSize = MAX;
        }
        long userId = getUserIdFromToken(token);
        CommonResp<List<Share>> commonResp = new CommonResp<>();
        commonResp.setData(shareService.myContribute(pageNo,pageSize,userId));
        return commonResp;
    }


}
