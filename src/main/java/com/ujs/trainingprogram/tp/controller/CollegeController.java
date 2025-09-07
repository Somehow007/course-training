package com.ujs.trainingprogram.tp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ujs.trainingprogram.tp.common.result.ResultData;
import com.ujs.trainingprogram.tp.common.result.ResultMessage;
import com.ujs.trainingprogram.tp.model.College;
import com.ujs.trainingprogram.tp.model.Major;
import com.ujs.trainingprogram.tp.model.User;
import com.ujs.trainingprogram.tp.service.CollegeService;
import com.ujs.trainingprogram.tp.service.MajorService;
import com.ujs.trainingprogram.tp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学院业务控制层
 */
@Slf4j
@RestController
@RequiredArgsConstructor public class CollegeController {

    private final CollegeService collegeService;

    private final UserService userService;

    private final MajorService majorService;

    /**
     * 查询所有学院
     *
     * @param page 分页页数
     * @param size 分页大小
     * @param collegeId 学院id
     * @param collegeName 学院名称
     * @return 统一返回值
     */
    @RequestMapping("/college/")
    public ResultData getAllColleges(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                     @RequestParam(value = "size", defaultValue = "10") Integer size,
                                     @RequestParam(value = "college_id", defaultValue = "") String collegeId,
                                     @RequestParam(value = "college_name", defaultValue = "") String collegeName) {
        log.info("请求参数: page= {}", page,
                ", size = " + size,
                ", college_id = " + collegeId,
                ", college_name = " + collegeName);
        QueryWrapper<College> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(collegeId)) {
            wrapper.eq(!collegeName.equals(""), "college_name", collegeName);
        }
        return collegeService.selectWithWrapper(page, size, wrapper);
    }

    /**
     * 增加学院
     * @param collegeName
     * @return
     */
    @PostMapping("/college/mainAdmin/add")
    public ResultMessage addCollege(@RequestParam(value = "college_name", defaultValue = "") String collegeName) {
        if (StringUtils.isEmpty(collegeName)) return ResultMessage.PARAM_MISS;
        College sqlCollege = collegeService.getCollegeByName(collegeName);
        if (sqlCollege != null) return new ResultMessage(-1, "该学院已存在");
        College college = new College();
        college.setCourseNum(0);
        college.setCollegeName(collegeName);
        int id = Integer.parseInt(collegeService.getMaxCollegeId()) + 1;
        String newId = String.valueOf(id);
        college.setCollegeId(newId);
        return collegeService.save(college) ? ResultMessage.ADD_SUCCESS : ResultMessage.ADD_ERROR;
    }

    /**
     * 删除学院
     * @param collegeId
     * @return
     */
    @DeleteMapping("/college/mainAdmin/delete")
    public ResultMessage deleteCollege(@RequestParam(value = "college_id", defaultValue = "") String collegeId) {
        if (StringUtils.isEmpty(collegeId)) return ResultMessage.PARAM_MISS;
        College college = collegeService.getById(collegeId);
        if (college == null) return ResultMessage.DELETE_ERROR;

        QueryWrapper<User> userWrapper =  new QueryWrapper<>();
        userWrapper.eq("college_id", college.getCollegeId());
        List<User> userList = userService.getBaseMapper().selectList(userWrapper);
        if (userList.size() > 0) return ResultMessage.DELETE_ERROR;

        List<Major> majors = majorService.getMajorByCollegeId(collegeId);
        if (majors.size() > 0) return ResultMessage.DELETE_ERROR;

        return collegeService.removeById(collegeId) ? ResultMessage.DELETE_SUCCESS : ResultMessage.DELETE_ERROR;
    }

    /**
     * 修改学院
     * @param collegeId
     * @param collegeName
     * @return
     */
    @PutMapping("/college/mainAdmin/update")
    public ResultMessage updateCollege(@RequestParam(value = "college_id", defaultValue = "") String collegeId,
                                       @RequestParam(value = "college_name", defaultValue = "") String collegeName) {
        if (StringUtils.isAnyEmpty(collegeId, collegeName)) return ResultMessage.PARAM_MISS;
        College college = collegeService.getById(collegeId);
        if (college == null) return ResultMessage.PARAM_MISS;

        college.setCollegeName(collegeName);
        return collegeService.saveOrUpdate(college) ? ResultMessage.UPDATE_SUCCESS : ResultMessage.UPDATE_ERROR;
    }
}
