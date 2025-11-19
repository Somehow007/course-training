package com.ujs.trainingprogram.tp.dto.req.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户登陆请求体
 */
@Data
@Schema(description = "用户登陆请求体")
public class UserLoginReqDTO {

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "somehow")
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码", example = "123456")
    private String password;
}
