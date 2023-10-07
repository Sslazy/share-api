package top.zxy.share.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.zxy.share.common.exception.BusinessException;
import top.zxy.share.common.exception.BusinessExceptionEnum;
import top.zxy.share.common.util.JwtUtil;
import top.zxy.share.common.util.SnowUtil;
import top.zxy.share.user.domain.dto.LoginDTO;
import top.zxy.share.user.domain.entity.User;
import top.zxy.share.user.domain.resp.UserLoginResp;
import top.zxy.share.user.mapper.UserMapper;

import java.util.Date;
import java.util.Map;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    public Long count(){
        return userMapper.selectCount(null);
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
}
