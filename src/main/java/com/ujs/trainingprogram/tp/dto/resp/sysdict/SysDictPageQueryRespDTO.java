package com.ujs.trainingprogram.tp.dto.resp.sysdict;

import lombok.Data;

/**
 * 分页查询系统字典请求返回实体
 */
@Data
public class SysDictPageQueryRespDTO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 字典类型（如：course_type, user_state）
     */
    private String dictType;

    /**
     * 字典编码（程序内部引用，如GENERAL_EDUCATION）
     */
    private String dictCode;

    /**
     * 字典名称（用户界面显示，如通识教育）
     */
    private String dictName;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 备注
     */
    private String remark;
}
