package top.zxy.share.user.rocketmq;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;
import top.zxy.share.user.domain.dto.UserAddBonusMsgDTO;
import top.zxy.share.user.domain.entity.BonusEventLog;
import top.zxy.share.user.domain.entity.User;
import top.zxy.share.user.mapper.BonusEventLogMapper;
import top.zxy.share.user.mapper.UserMapper;

import java.util.Date;

@Service
@RocketMQMessageListener(consumerGroup = "test-group",topic = "add-bonus")
@Slf4j
public class AddBonusListener implements RocketMQListener<UserAddBonusMsgDTO> {
    @Resource
    private UserMapper userMapper;

    @Resource
    private BonusEventLogMapper bonusEventLogMapper;

    @Override
    public void onMessage(UserAddBonusMsgDTO userAddBonusMsgDTO){
        log.info(String.valueOf(userAddBonusMsgDTO));
        // 1.为用户加积分
        Long userId = userAddBonusMsgDTO.getUserId();
        User user = userMapper.selectById(userId);
        user.setBonus(user.getBonus()+ userAddBonusMsgDTO.getBonus());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId,userId);
        userMapper.update(user,wrapper);
        // 2.新增积分日志
        bonusEventLogMapper.insert(BonusEventLog.builder()
                .userId(userId)
                .value(userAddBonusMsgDTO.getBonus())
                .event("CONTRIBUTE")
                .createTime(new Date())
                .description("投稿加积分")
                .build());
    }
}
