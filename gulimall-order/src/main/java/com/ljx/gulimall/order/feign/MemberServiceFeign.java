package com.ljx.gulimall.order.feign;

import com.ljx.common.utils.R;
import com.ljx.gulimall.order.model.entity.MemberEntity;
import com.ljx.gulimall.order.model.vo.MemberReceiveAddressVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("gulimall-member")
public interface MemberServiceFeign {

    @GetMapping("/member/memberreceiveaddress/list/{memberId}")
    R<List<MemberReceiveAddressVO>> listByMemberId(@PathVariable("memberId") Long memberId);

    @RequestMapping("/member/member/info/{id}")
    R<MemberEntity> getMemberInfo(@PathVariable("id") Long id);

}
