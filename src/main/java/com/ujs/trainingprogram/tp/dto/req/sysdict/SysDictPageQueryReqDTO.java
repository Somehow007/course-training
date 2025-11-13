package com.ujs.trainingprogram.tp.dto.req.sysdict;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页查询系统字典请求实体
 */
@Data
@Schema(description = "分页查询系统字典请求实体")
public class SysDictPageQueryReqDTO extends Page {

    /**
     * 字典类型（如：course_type, user_state）
     */
    @Schema(description = "字典类型", example = "course_type", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictType;

    /**
     * 字典编码（程序内部引用，如GENERAL_EDUCATION）
     */
    @Schema(description = "字典编码", example = "GENERAL_EDUCATION", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dictCode;

    /**
     * 字典名称（用户界面显示，如通识教育）
     */
    @Schema(description = "字典名称", example = "通识教育", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictName;

}
