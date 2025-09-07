package com.ujs.trainingprogram.tp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ujs.trainingprogram.tp.common.result.ResultData;
import com.ujs.trainingprogram.tp.common.result.ResultMessage;
import com.ujs.trainingprogram.tp.model.College;
import com.ujs.trainingprogram.tp.model.Major;
import com.ujs.trainingprogram.tp.service.CollegeService;
import com.ujs.trainingprogram.tp.service.MajorService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/major")
public class MajorController {
    @Autowired
    private MajorService majorService;
    @Autowired
    private CollegeService collegeService;

    /**
     * 条件分页查询
     * @param majorId
     * @param majorName
     * @param collegeName
     * @param page
     * @param size
     * @return
     */
    @RequestMapping("/")
    public ResultData getCollegeMajors(@RequestParam(value = "major_id", defaultValue = "") String majorId,
                                       @RequestParam(value = "major_name", defaultValue = "") String majorName,
                                       @RequestParam(value = "college_name", defaultValue = "") String collegeName,
                                       @RequestParam(value = "page", defaultValue = "0") Integer page,
                                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        QueryWrapper<Major> wrapper = new QueryWrapper<>();
        collegeName = "计算机";
        if (!StringUtils.isEmpty(majorId)) {
            wrapper.eq("major_id", majorId);
        } else {
            wrapper.eq(!majorName.equals(""), "major_name", majorName);
            if (!StringUtils.isEmpty(collegeName)) {
                List<College> collegeList = collegeService.getCollegeLikeByName(collegeName);
                if (collegeList.size() > 0) {
                    wrapper.and(i -> {
                        i.like("college_id", collegeList.get(0).getCollegeId());
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
    @PostMapping("/mainAdmin/add")
    public ResultMessage addMajor(@RequestParam(value = "major_name", defaultValue = "") String majorName,
                                  @RequestParam(value = "college_name", defaultValue = "") String collegeName) {
        if (StringUtils.isAnyEmpty(majorName, collegeName)) return ResultMessage.PARAM_MISS;
        QueryWrapper<College> collegeWrapper = new QueryWrapper<>();
        collegeWrapper.eq("college_name", collegeName);
        College college = collegeService.getOne(collegeWrapper);
        if (college == null) return ResultMessage.ADD_ERROR;
        Major major = new Major();
        major.setCourseNum(0);
        major.setMajorName(majorName);
        major.setCollegeId(college.getCollegeId());
        QueryWrapper<Major> majorWrapper = new QueryWrapper<>();
        int id = Integer.parseInt(majorService.getMaxMajorId(majorWrapper)) + 1;
        String newId = String.valueOf(id);
        int length = newId.length();
        while (length < 4) {
            newId = "0" + newId;
            length++;
        }
        major.setMajorId(newId);
        return majorService.save(major) ? ResultMessage.ADD_SUCCESS : ResultMessage.ADD_ERROR;
    }

    /**
     * 删除专业
     * @param majorId
     * @return
     */
    @DeleteMapping("/mainAdmin/delete")
    public ResultMessage deleteCollege(@RequestParam(value = "major_id", defaultValue = "") String majorId) {
        if (StringUtils.isEmpty(majorId)) return ResultMessage.ADD_ERROR;
        Major major = majorService.getById(majorId);
        if (major == null) return ResultMessage.DELETE_ERROR;
        return majorService.removeById(majorId) ? ResultMessage.DELETE_SUCCESS : ResultMessage.DELETE_ERROR;
    }

    /**
     * 修改专业
     * @param majorId
     * @param majorName
     * @return
     */
    @PutMapping("/mainAdmin/update")
    public ResultMessage updateMajor(@RequestParam(value = "major_id", defaultValue = "") String majorId,
                                  @RequestParam(value = "major_name", defaultValue = "") String majorName) {
        if (StringUtils.isAnyEmpty(majorId, majorName)) return ResultMessage.PARAM_MISS;
        Major major = majorService.getById(majorId);
        if (major == null) return ResultMessage.PARAM_MISS;
        major.setMajorName(majorName);
        return majorService.saveOrUpdate(major) ? ResultMessage.UPDATE_SUCCESS : ResultMessage.UPDATE_ERROR;
    }
}
