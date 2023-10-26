package top.zxy.share.content.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.zxy.share.common.resp.CommonResp;
import top.zxy.share.user.domain.dto.UserAddBonusMsgDTO;

@FeignClient(value = "user-service", path = "/user")
public interface MyUserService {


    @GetMapping("/{id}")
    CommonResp<User> getUser(@PathVariable Long id);

    @PutMapping("/update-bonus")
    CommonResp<User> updateBonus(@RequestBody UserAddBonusMsgDTO userAddBonusMsgDTO);

}
