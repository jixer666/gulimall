package com.ljx.auth.domain.vo;

import lombok.Data;
import java.util.Date;

@Data
public class GithubUserVO {
    /**
     * 登录用户名
     */
    private String login;
    
    /**
     * GitHub用户ID
     */
    private Long id;
    
    /**
     * GitHub节点ID
     */
    private String nodeId;
    
    /**
     * 头像URL
     */
    private String avatarUrl;
    
    /**
     * Gravatar ID
     */
    private String gravatarId;
    
    /**
     * GitHub API URL
     */
    private String url;
    
    /**
     * GitHub主页URL
     */
    private String htmlUrl;
    
    /**
     * 粉丝列表URL
     */
    private String followersUrl;
    
    /**
     * 关注列表URL
     */
    private String followingUrl;
    
    /**
     * Gists列表URL
     */
    private String gistsUrl;
    
    /**
     * 星标项目URL
     */
    private String starredUrl;
    
    /**
     * 订阅列表URL
     */
    private String subscriptionsUrl;
    
    /**
     * 组织列表URL
     */
    private String organizationsUrl;
    
    /**
     * 仓库列表URL
     */
    private String reposUrl;
    
    /**
     * 事件列表URL
     */
    private String eventsUrl;
    
    /**
     * 接收事件列表URL
     */
    private String receivedEventsUrl;
    
    /**
     * 用户类型
     */
    private String type;
    
    /**
     * 用户视图类型
     */
    private String userViewType;
    
    /**
     * 是否是站点管理员
     */
    private Boolean siteAdmin;
    
    /**
     * 显示名称
     */
    private String name;
    
    /**
     * 公司
     */
    private String company;
    
    /**
     * 博客/网站
     */
    private String blog;
    
    /**
     * 位置
     */
    private String location;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 是否可雇佣
     */
    private Boolean hireable;
    
    /**
     * 个人简介
     */
    private String bio;
    
    /**
     * Twitter用户名
     */
    private String twitterUsername;
    
    /**
     * 通知邮箱
     */
    private String notificationEmail;
    
    /**
     * 公开仓库数量
     */
    private Integer publicRepos;
    
    /**
     * 公开Gists数量
     */
    private Integer publicGists;
    
    /**
     * 粉丝数量
     */
    private Integer followers;
    
    /**
     * 关注数量
     */
    private Integer following;
    
    /**
     * 创建时间
     */
    private Date createdAt;
    
    /**
     * 更新时间
     */
    private Date updatedAt;
    
    /**
     * Access Token（需要手动设置）
     */
    private String accessToken;
}