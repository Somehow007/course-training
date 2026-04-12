package com.ujs.trainingprogram.tp.dto.req.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "重置密码请求体")
public class ResetPasswordReqDTO {

    @NotBlank(message = "用户ID不能为空")
    @Schema(description = "目标用户ID", example = "1991081015491952640", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @NotBlank(message = "新密码不能为空")
    @Schema(description = "新密码", example = "newPassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}
