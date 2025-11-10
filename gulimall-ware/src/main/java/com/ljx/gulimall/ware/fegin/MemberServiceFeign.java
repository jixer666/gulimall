package com.ljx.gulimall.ware.fegin;

import com.ljx.common.utils.R;
import com.ljx.gulimall.ware.model.entity.MemberReceiveAddressEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-member")
public interface MemberServiceFeign {

    @GetMapping("/member/memberreceiveaddress//info/{id}")
    R<MemberReceiveAddressEntity> getAddInfo(@PathVariable("id") Long id);


}
