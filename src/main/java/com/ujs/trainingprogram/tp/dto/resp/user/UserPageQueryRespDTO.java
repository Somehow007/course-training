package com.ujs.trainingprogram.tp.dto.resp.user;

import lombok.Data;

/**
 * 分页查询用户返回实体
 */
@Data
public class UserPageQueryRespDTO {

    /**
     * 主键
     */
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 所属学院
     */
    private String collegeName;

    /**
     * 用户权限名称
     */
    private String dictName;
}
