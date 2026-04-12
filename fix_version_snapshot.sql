-- 修复版本快照数据脚本
-- 为现有版本记录补充快照数据

USE course_training;

-- 1. 为现有版本记录补充快照数据
UPDATE version_history vh
SET snapshot_data = (
    SELECT JSON_ARRAYAGG(
        JSON_OBJECT(
            'id', tpd.id,
            'trainingProgramId', tpd.training_program_id,
            'courseId', tpd.course_id,
            'courseNature', tpd.course_nature,
            'courseName', tpd.course_name,
            'collegeId', tpd.college_id,
            'majorId', tpd.major_id,
            'totalCredits', tpd.total_credits,
            'totalHours', tpd.total_hours,
            'totalWeeks', tpd.total_weeks,
            'hoursUnit', tpd.hours_unit,
            'hourTeach', tpd.hour_teach,
            'hourPractice', tpd.hour_practice,
            'hourOperation', tpd.hour_operation,
            'hourOutside', tpd.hour_outside,
            'hourWeek', tpd.hour_week,
            'term', tpd.term,
            'requiredElective', tpd.required_elective,
            'courseChooseId', tpd.course_choose_id,
            'remark', tpd.remark,
            'version', tpd.version,
            'createTime', tpd.create_time,
            'updateTime', tpd.update_time,
            'delFlag', tpd.del_flag
        )
    )
    FROM training_program_detail tpd
    WHERE tpd.training_program_id = vh.training_program_id
    AND tpd.del_flag = 0
)
WHERE vh.snapshot_data IS NULL
AND vh.del_flag = 0;

-- 2. 验证快照数据是否正确保存
SELECT 
    id,
    version_name,
    CHAR_LENGTH(snapshot_data) as snapshot_size,
    LEFT(snapshot_data, 100) as snapshot_preview
FROM version_history 
WHERE del_flag = 0
LIMIT 3;
