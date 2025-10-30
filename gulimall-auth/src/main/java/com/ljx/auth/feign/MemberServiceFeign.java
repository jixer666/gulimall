package com.ljx.auth.feign;

import com.ljx.auth.domain.vo.GithubUserVO;
import com.ljx.auth.domain.vo.LoginVO;
import com.ljx.auth.domain.vo.MemberVO;
import com.ljx.auth.domain.vo.RegisterVO;
import com.ljx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("gulimall-member")
public interface MemberServiceFeign {

    @GetMapping("/member/member/register")
    R register(@RequestBody RegisterVO registerVO);

    @PostMapping("/member/member/login")
    R login(@RequestBody LoginVO loginVO);

    @PostMapping("/member/member/login/github")
    R<MemberVO> githubLogin(@RequestBody GithubUserVO githubUserVO);
}
