package top.zxy.share.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.zxy.share.common.exception.BusinessException;
import top.zxy.share.common.exception.BusinessExceptionEnum;
import top.zxy.share.common.util.JwtUtil;
import top.zxy.share.common.util.SnowUtil;
import top.zxy.share.user.domain.dto.LoginDTO;
import top.zxy.share.user.domain.dto.UserAddBonusMsgDTO;
import top.zxy.share.user.domain.entity.BonusEventLog;
import top.zxy.share.user.domain.entity.User;
import top.zxy.share.user.domain.resp.UserLoginResp;
import top.zxy.share.user.mapper.BonusEventLogMapper;
import top.zxy.share.user.mapper.UserMapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private BonusEventLogMapper bonusEventLogMapper;

    public Long count(){
        return userMapper.selectCount(null);
    }

    public User findById(Long userId){
        return userMapper.selectById(userId);
    }

    public UserLoginResp login(LoginDTO loginDTO){
        // 根据手机号查询用户
        User userDB = userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getPhone,loginDTO.getPhone()));
        // 没找到，抛出运行时异常
        if ( userDB == null ){
            throw new BusinessException(BusinessExceptionEnum.PHONE_EXIST);
        }
        // 密码错误
        if ( !userDB.getPassword().equals(loginDTO.getPassword()) ){
            throw new BusinessException(BusinessExceptionEnum.PASSWORD_ERROR);
        }

        // 都正确，返回
        UserLoginResp userLoginResp = UserLoginResp.builder()
                .user(userDB)
                .build();
//        String key = "InfinityX7";
//        Map<String,Object> map = BeanUtil.beanToMap(userLoginResp);
//        String token = JWTUtil.createToken(map,key.getBytes());
        String token = JwtUtil.createToken(userLoginResp.getUser().getId(), userLoginResp.getUser().getPhone());
        userLoginResp.setToken(token);
        return userLoginResp;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBonus(UserAddBonusMsgDTO userAddBonusMsgDTO){
        System.out.println(userAddBonusMsgDTO);
        // 1.为用户修改积分
        Long userId = userAddBonusMsgDTO.getUserId();
        Integer bonus = userAddBonusMsgDTO.getBonus();
        User user = userMapper.selectById(userId);
        user.setBonus(user.getBonus() + bonus);
        userMapper.update(user,new QueryWrapper<User>().lambda().eq(User::getId,userId));

        // 2.记录日志到bouns_event_log 表里
        bonusEventLogMapper.insert(
                BonusEventLog.builder()
                        .userId(userId)
                        .value(bonus)
                        .description(userAddBonusMsgDTO.getDescription())
                        .event(userAddBonusMsgDTO.getEvent())
                        .createTime(new Date())
                        .build()
        );
        log.info("积分添加完毕...");
    }

    public Long register(LoginDTO loginDTO){
        // 根据手机号查询
        User userDB = userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getPhone,loginDTO.getPhone()));
        // 找到了，手机号已被注册
        if ( userDB != null ){
            throw new BusinessException(BusinessExceptionEnum.PHONE_EXIST);
        }
        User savedUser = User.builder()
                //使用雪花算法生成ID
                .id(SnowUtil.getSnowflakeNextID())
                .phone(loginDTO.getPhone())
                .password(loginDTO.getPassword())
                .nickname("新用户")
                .roles("user")
                .avatarUrl("https://public-cdn-oss.mosoteach.cn/avatar/2023/03/429222a08ce85726f8c6a85dc9c62fc9.jpg?v=1678678626&x-oss-process=style/s300x300")
                .bonus(100)
                .createTime(new Date())
                .updateTime(new Date())
                .build();
        userMapper.insert(savedUser);
        return savedUser.getId();
    }

    public List<BonusEventLog> getBonusEventLog(Long userId){
        LambdaQueryWrapper<BonusEventLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BonusEventLog::getUserId,userId);
        List<BonusEventLog> bonusEventLogList = bonusEventLogMapper.selectList(wrapper);
        return bonusEventLogList;
    }
}
