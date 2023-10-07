package top.zxy.share.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.zxy.share.user.domain.dto.LoginDTO;
import top.zxy.share.user.domain.entity.User;
import top.zxy.share.user.mapper.UserMapper;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    public Long count(){
        return userMapper.selectCount(null);
    }

    public User login(LoginDTO loginDTO){
        // 根据手机号查询用户
        User userDB = userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getPhone,loginDTO.getPhone()));
        // 没找到，抛出运行时异常
        if ( userDB == null ){
            throw new RuntimeException("手机号不存在");
        }
        // 密码错误
        if ( !userDB.getPassword().equals(loginDTO.getPassword()) ){
            throw new RuntimeException("密码错误");
        }

        // 都正确，返回
        return userDB;
    }
}
