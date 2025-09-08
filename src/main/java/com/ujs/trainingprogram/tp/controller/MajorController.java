package com.ujs.trainingprogram.tp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ujs.trainingprogram.tp.common.result.ResultData;
import com.ujs.trainingprogram.tp.common.result.ResultMessage;
import com.ujs.trainingprogram.tp.dao.entity.CollegeDO;
import com.ujs.trainingprogram.tp.dao.entity.MajorDO;
import com.ujs.trainingprogram.tp.service.CollegeService;
import com.ujs.trainingprogram.tp.service.MajorService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 专业业务控制层
 */
@RestController
@RequiredArgsConstructor
public class MajorController {

    private final MajorService majorService;

    private final CollegeService collegeService;

    /**
     * 条件分页查询
     * @param majorId
     * @param majorName
     * @param collegeName
     * @param page
     * @param size
     * @return
     */
    @RequestMapping("/major/")
    public ResultData getCollegeMajors(@RequestParam(value = "major_id", defaultValue = "") String majorId,
                                       @RequestParam(value = "major_name", defaultValue = "") String majorName,
                                       @RequestParam(value = "college_name", defaultValue = "") String collegeName,
                                       @RequestParam(value = "page", defaultValue = "0") Integer page,
                                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        QueryWrapper<MajorDO> wrapper = new QueryWrapper<>();
        collegeName = "计算机";
        if (!StringUtils.isEmpty(majorId)) {
            wrapper.eq("major_id", majorId);
        } else {
            wrapper.eq(!majorName.equals(""), "major_name", majorName);
            if (!StringUtils.isEmpty(collegeName)) {
                List<CollegeDO> collegeDOList = collegeService.getCollegeLikeByName(collegeName);
                if (collegeDOList.size() > 0) {
                    wrapper.and(i -> {
                        i.like("college_id", collegeDOList.get(0).getCollegeId());
                    });
                }
            }
        }
        return majorService.selectWithWrapper(page, size, wrapper);
    }

    /**
     * 添加专业
     * @param majorName
     * @param collegeName
     * @return
     */
    @PostMapping("/major/mainAdmin/add")
    public ResultMessage addMajor(@RequestParam(value = "major_name", defaultValue = "") String majorName,
                                  @RequestParam(value = "college_name", defaultValue = "") String collegeName) {
        if (StringUtils.isAnyEmpty(majorName, collegeName)) return ResultMessage.PARAM_MISS;
        QueryWrapper<CollegeDO> collegeWrapper = new QueryWrapper<>();
        collegeWrapper.eq("college_name", collegeName);
        CollegeDO collegeDO = collegeService.getOne(collegeWrapper);
        if (collegeDO == null) return ResultMessage.ADD_ERROR;
        MajorDO majorDO = new MajorDO();
        majorDO.setCourseNum(0);
        majorDO.setMajorName(majorName);
        majorDO.setCollegeId(collegeDO.getCollegeId());
        QueryWrapper<MajorDO> majorWrapper = new QueryWrapper<>();
        int id = Integer.parseInt(majorService.getMaxMajorId(majorWrapper)) + 1;
        String newId = String.valueOf(id);
        int length = newId.length();
        while (length < 4) {
            newId = "0" + newId;
            length++;
        }
        majorDO.setMajorId(newId);
        return majorService.save(majorDO) ? ResultMessage.ADD_SUCCESS : ResultMessage.ADD_ERROR;
    }

    /**
     * 删除专业
     * @param majorId
     * @return
     */
    @DeleteMapping("/major/mainAdmin/delete")
    public ResultMessage deleteCollege(@RequestParam(value = "major_id", defaultValue = "") String majorId) {
        if (StringUtils.isEmpty(majorId)) return ResultMessage.ADD_ERROR;
        MajorDO majorDO = majorService.getById(majorId);
        if (majorDO == null) return ResultMessage.DELETE_ERROR;
        return majorService.removeById(majorId) ? ResultMessage.DELETE_SUCCESS : ResultMessage.DELETE_ERROR;
    }

    /**
     * 修改专业
     * @param majorId
     * @param majorName
     * @return
     */
    @PutMapping("/major/mainAdmin/update")
    public ResultMessage updateMajor(@RequestParam(value = "major_id", defaultValue = "") String majorId,
                                  @RequestParam(value = "major_name", defaultValue = "") String majorName) {
        if (StringUtils.isAnyEmpty(majorId, majorName)) return ResultMessage.PARAM_MISS;
        MajorDO majorDO = majorService.getById(majorId);
        if (majorDO == null) return ResultMessage.PARAM_MISS;
        majorDO.setMajorName(majorName);
        return majorService.saveOrUpdate(majorDO) ? ResultMessage.UPDATE_SUCCESS : ResultMessage.UPDATE_ERROR;
    }
}
