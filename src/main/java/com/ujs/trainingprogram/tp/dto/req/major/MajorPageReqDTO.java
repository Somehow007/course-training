package com.ujs.trainingprogram.tp.dto.req.major;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分页查询专业请求实体
 */
@Data
@Schema(description = "分页查询专业请求实体")
public class MajorPageReqDTO extends Page {

    /**
     * 专业ID
     */
    @Schema(description = "专业ID", example = "[\"1988533145819754496\", \"1988456399996981248\"]")
    private List<String> majorIds;

    /**
     * 专业名
     */
    @Schema(description = "专业名", example = "[\"软件工程\", \"计算机科学与通信工程\"]")
    private List<String> majorNames;

    /**
     * 学院名
     */
    @Schema(description = "学院名", example = "[\"计算机学院\", \"马克思学院\"]")
    private List<String> collegeNames;

    /**
     * 专业分类（0:工学 1:理学 2:文科）
     */
    @Schema(description = "专业分类（0:工学 1:理学 2:文科）", example = "0")
    private Integer categoryId;
}
