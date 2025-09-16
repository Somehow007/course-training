package com.ujs.trainingprogram.tp.dto.req.major;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页查询专业请求实体
 */
@Data
@Schema(description = "分页查询专业请求实体")
public class MajorPageReqDTO extends Page {

    /**
     * 专业编号
     */
    @Schema(description = "专业编号", example = "0001")
    private String majorCode;

    /**
     * 专业名
     */
    @Schema(description = "专业名", example = "软件工程")
    private String majorName;

    /**
     * 专业类别 0-工学 1-理学 2-文科
     */
    @Schema(description = "专业编号", example = "0001")
    private Integer majorType;
}
