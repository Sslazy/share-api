package top.zxy.share.content.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.zxy.share.common.resp.CommonResp;

@FeignClient(value = "user-service",path = "/user")
public interface UserService {

    @GetMapping("/{id}")
    CommonResp<User> getUser(@PathVariable Long id);

    @PutMapping("/update-bonus")
    CommonResp<User> updateBouns(@RequestBody UserAddBonusMsgDTO userAddBonusMsgDTO);
}
