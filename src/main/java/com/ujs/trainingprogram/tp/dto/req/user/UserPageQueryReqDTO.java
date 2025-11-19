package com.ujs.trainingprogram.tp.dto.req.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页查询用户信息请求体
 */
@Data
@Schema(description = "分页查询用户信息请求体")
public class UserPageQueryReqDTO extends Page {

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "Somehow")
    private String username;

    /**
     * 所属学院ID（引用 college.id）
     */
    @Schema(description = "所属学院ID", example = "1988456399996981248")
    private String collegeId;

    /**
     * 学院名称
     */
    @Schema(description = "学院名称", example = "计算机科学与通信工程学院")
    private String collegeName;

    /**
     * 用户权限（关联字典表）
     */
    @Schema(description = "用户权限ID", example = "1988934680181559296")
    private String dictId;

    /**
     * 用户权限名称
     */
    @Schema(description = "用户权限名称", example = "教务处")
    private String dictName;
}
