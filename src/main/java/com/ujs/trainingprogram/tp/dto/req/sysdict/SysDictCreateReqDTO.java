package com.ujs.trainingprogram.tp.dto.req.sysdict;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

/**
 * 创建系统字典请求实体
 */
@Data
@Schema(description = "创建系统字典请求实体")
public class SysDictCreateReqDTO {

    /**
     * 字典类型（如：course_type, user_state）
     */
    @Schema(description = "字典类型", example = "course_type", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictType;

    /**
     * 字典编码（程序内部引用，如GENERAL_EDUCATION）
     */
    @Schema(description = "专业编号", example = "GENERAL_EDUCATION", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dictCode;

    /**
     * 字典名称（用户界面显示，如通识教育）
     */
    @Schema(description = "字典名称（用户界面显示，如通识教育）", example = "通识教育", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictName;

    /**
     * 排序号
     */
    @Schema(description = "排序号", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer sortOrder;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "课程类型的字典，通识教育", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String remark;
}
