package com.ljx.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.ljx.gulimall.member.domain.vo.GithubUserVO;
import com.ljx.gulimall.member.domain.vo.LoginVO;
import com.ljx.gulimall.member.domain.vo.MemberVO;
import com.ljx.gulimall.member.domain.vo.RegisterVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ljx.gulimall.member.domain.entity.MemberEntity;
import com.ljx.gulimall.member.service.MemberService;
import com.ljx.common.utils.PageUtils;
import com.ljx.common.utils.R;



/**
 * 会员
 *
 * @author LiJunXi
 * @email 2770063826@qq.com
 * @date 2025-07-15 21:52:55
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/register")
    public R register(@RequestBody RegisterVO registerVO){
        return memberService.register(registerVO);
    }

    @PostMapping("/login")
    public R login(@RequestBody LoginVO loginVO){
        return memberService.login(loginVO);
    }

    @PostMapping("/login/github")
    public R<MemberVO> githubLogin(@RequestBody GithubUserVO githubUserVO) {
        return memberService.githubLogin(githubUserVO);
    }

}
