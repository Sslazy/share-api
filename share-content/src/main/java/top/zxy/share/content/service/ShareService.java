package top.zxy.share.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.zxy.share.content.domain.entity.MidUserShare;
import top.zxy.share.content.domain.entity.Share;
import top.zxy.share.content.mapper.MidUserShareMapper;
import top.zxy.share.content.mapper.ShareMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShareService {
    @Resource
    private ShareMapper shareMapper;

    @Resource
    private MidUserShareMapper midUserShareMapper;

    public List<Share> getList(String title,Long userId,Integer pageNo,Integer pageSize){
        // 构造查询条件
        LambdaQueryWrapper<Share> wrapper = new LambdaQueryWrapper<>();
        // 按照id降序查询所有数据
        wrapper.orderByDesc(Share::getId);
        // 如标题关键字不空，则加上模糊查询条件，否则结果即所有数据
        if( title != null){
            wrapper.like(Share::getTitle,title);
        }
        // 过滤出所有已经通过审核的数据并需要显示的数据
        wrapper.eq(Share::getAuditStatus,"PASS").eq(Share::getShowFlag,true);

        // 内置的分页对象
        Page<Share> page = Page.of(pageNo,pageSize);
        // 执行按条件查询
        List<Share> shares = shareMapper.selectList(page,wrapper);


        // 处理后的Share数据列表
        List<Share> sharesDeal;
        // 1.如果用户未登录，那么 downloadUrl 全部设为null
        if(userId == null){
            sharesDeal = shares.stream().peek(share -> share.setDownloadUrl(null)).collect(Collectors.toList());
        }
        // 2.如果用户登录了，那么查询 mid_user_share，如果没有数据，那么这条share的downl 也设为null
        // 只有自己分享的资源才能直接看到下载链接，否则显示“兑换”
        else{
            sharesDeal = shares.stream().peek(share -> {
                MidUserShare midUserShare = midUserShareMapper.selectOne(new QueryWrapper<MidUserShare>().lambda()
                        .eq(MidUserShare::getUserId,userId)
                        .eq(MidUserShare::getShareId,share.getId()));
                if ( midUserShare == null ){
                    share.setDownloadUrl(null);
                }
            }).collect(Collectors.toList());
        }
        return sharesDeal;
    }
}
