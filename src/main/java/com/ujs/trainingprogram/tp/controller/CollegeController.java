package com.ujs.trainingprogram.tp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ujs.trainingprogram.tp.common.result.Result;
import com.ujs.trainingprogram.tp.common.result.ResultMessage;
import com.ujs.trainingprogram.tp.common.web.Results;
import com.ujs.trainingprogram.tp.dao.entity.CollegeDO;
import com.ujs.trainingprogram.tp.dao.entity.MajorDO;
import com.ujs.trainingprogram.tp.dao.entity.UserDO;
import com.ujs.trainingprogram.tp.dto.req.college.CollegePageReqDTO;
import com.ujs.trainingprogram.tp.dto.req.college.CollegeSaveReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.CollegePageRespDTO;
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
     * 分页查询所有学院
     */
    @RequestMapping("/college/page")
    public Result<IPage<CollegePageRespDTO>> getAllColleges(CollegePageReqDTO requestParam) {
        log.info("请求参数: page= {}", requestParam.getPages(),
                ", size = " + requestParam.getSize(),
                ", college_id = " + requestParam.getCollegeId(),
                ", college_name = " + requestParam.getCollegeName());
//        QueryWrapper<CollegeDO> wrapper = new QueryWrapper<>();
//        if (!StringUtils.isEmpty(collegeId)) {
//            wrapper.eq(!collegeName.equals(""), "college_name", collegeName);
//        }
        return Results.success(collegeService.pageCollege(requestParam));
    }


    /**
     * 添加学院
     */
    @PostMapping("/college/mainAdmin/add")
    public Result<Void> createCollege(@RequestBody CollegeSaveReqDTO requestParam) {
        collegeService.createCollege(requestParam);
        return Results.success();
//        if (StringUtils.isEmpty(collegeName)) return ResultMessage.PARAM_MISS;
//        CollegeDO sqlCollegeDO = collegeService.getCollegeByName(collegeName);
//        if (sqlCollegeDO != null) return new ResultMessage(-1, "该学院已存在");
//        CollegeDO collegeDO = new CollegeDO();
//        collegeDO.setCourseNum(0);
//        collegeDO.setCollegeName(collegeName);
//        int id = Integer.parseInt(collegeService.getMaxCollegeId()) + 1;
//        String newId = String.valueOf(id);
//        collegeDO.setCollegeId(newId);
//        return collegeService.save(collegeDO) ? ResultMessage.ADD_SUCCESS : ResultMessage.ADD_ERROR;
    }


//    /**
//     * 添加学院
//     */
//    @PostMapping("/college/mainAdmin/add")
//    public ResultMessage createCollege(@RequestParam(value = "college_name", defaultValue = "") String collegeName) {
//        if (StringUtils.isEmpty(collegeName)) return ResultMessage.PARAM_MISS;
//        CollegeDO sqlCollegeDO = collegeService.getCollegeByName(collegeName);
//        if (sqlCollegeDO != null) return new ResultMessage(-1, "该学院已存在");
//        CollegeDO collegeDO = new CollegeDO();
//        collegeDO.setCourseNum(0);
//        collegeDO.setCollegeName(collegeName);
//        int id = Integer.parseInt(collegeService.getMaxCollegeId()) + 1;
//        String newId = String.valueOf(id);
//        collegeDO.setCollegeId(newId);
//        return collegeService.save(collegeDO) ? ResultMessage.ADD_SUCCESS : ResultMessage.ADD_ERROR;
//    }

    /**
     * 删除学院
     * @param collegeId
     * @return
     */
    @DeleteMapping("/college/mainAdmin/delete")
    public ResultMessage deleteCollege(@RequestParam(value = "college_id", defaultValue = "") String collegeId) {
        if (StringUtils.isEmpty(collegeId)) return ResultMessage.PARAM_MISS;
        CollegeDO collegeDO = collegeService.getById(collegeId);
        if (collegeDO == null) return ResultMessage.DELETE_ERROR;

        QueryWrapper<UserDO> userWrapper =  new QueryWrapper<>();
        userWrapper.eq("college_id", collegeDO.getCollegeId());
        List<UserDO> userDOList = userService.getBaseMapper().selectList(userWrapper);
        if (userDOList.size() > 0) return ResultMessage.DELETE_ERROR;

        List<MajorDO> majorDOS = majorService.getMajorByCollegeId(collegeId);
        if (majorDOS.size() > 0) return ResultMessage.DELETE_ERROR;

        return collegeService.removeById(collegeId) ? ResultMessage.DELETE_SUCCESS : ResultMessage.DELETE_ERROR;
    }

    /**
     * 修改学院
     */
    @PutMapping("/college/mainAdmin/update")
    public ResultMessage updateCollege(@RequestParam(value = "college_id", defaultValue = "") String collegeId,
                                       @RequestParam(value = "college_name", defaultValue = "") String collegeName) {
        if (StringUtils.isAnyEmpty(collegeId, collegeName)) return ResultMessage.PARAM_MISS;
        CollegeDO collegeDO = collegeService.getById(collegeId);
        if (collegeDO == null) return ResultMessage.PARAM_MISS;

        collegeDO.setCollegeName(collegeName);
        return collegeService.saveOrUpdate(collegeDO) ? ResultMessage.UPDATE_SUCCESS : ResultMessage.UPDATE_ERROR;
    }
}
