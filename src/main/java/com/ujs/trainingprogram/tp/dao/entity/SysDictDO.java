package com.ujs.trainingprogram.tp.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ujs.trainingprogram.tp.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统字典实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_dict")
public class SysDictDO extends BaseDO {

    /**
     * 主键
     */
    @TableId("id")
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