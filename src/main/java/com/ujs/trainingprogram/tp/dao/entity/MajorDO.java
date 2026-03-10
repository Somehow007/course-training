package com.ujs.trainingprogram.tp.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ujs.trainingprogram.tp.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 专业实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("major")
public class MajorDO extends BaseDO {

    /**
     * 主键
     */
    @TableId("id")
    private Long id;

    /**
     * 专业编号
     */
    private String majorCode;

    /**
     * 关联学院Id
     */
    private Long collegeId;

    /**
     * 专业分类（0:工学 1:理学 2:文科）
     */
    private Integer category;

    /**
     * 专业名
     */
    private String majorName;

    /**
     * 课程总数
     * todo 因选课问题，每个专业每个人的课程数会有差异，因此此字段可删除
     */
    private Integer courseNum;
}
