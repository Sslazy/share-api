package top.zxy.share.content.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.zxy.share.common.resp.CommonResp;
import top.zxy.share.content.domain.entity.Notice;
import top.zxy.share.content.domain.entity.Share;
import top.zxy.share.content.service.NoticeService;
import top.zxy.share.content.service.ShareService;

import java.util.List;

@RestController
@RequestMapping(value = "/share")
public class ShareController {

    @Resource
    private NoticeService noticeService;

    @Resource
    private ShareService shareService;

    @GetMapping(value = "/notice")
    public CommonResp<Notice> getLatestNotice(){
        CommonResp<Notice> commonResp = new CommonResp<>();
        commonResp.setData(noticeService.getLatest());
        return commonResp;
    }

    @GetMapping("/list")
    public CommonResp<List<Share>> getShareList(@RequestParam(required = false) String title){
        CommonResp<List<Share>> commonResp = new CommonResp<>();
        Long userId = 2L;
        commonResp.setData(shareService.getList(title,userId));
        return commonResp;
    }
}
