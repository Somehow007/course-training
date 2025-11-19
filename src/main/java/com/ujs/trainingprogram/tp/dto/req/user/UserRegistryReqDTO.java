package com.ujs.trainingprogram.tp.dto.req.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 注册用户请求体
 */
@Data
@Schema(description = "注册用户请求体")
public class UserRegistryReqDTO {

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "Somehow", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    /**
     * 所属学院ID（引用 college.id）
     */
    @Schema(description = "所属学院ID", example = "1988456399996981248", requiredMode = Schema.RequiredMode.REQUIRED)
    private String collegeId;

    /**
     * 用户权限（关联字典表）
     */
    @Schema(description = "用户权限ID", example = "1988934680181559296", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictId;
}
