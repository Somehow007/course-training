package com.ujs.trainingprogram.tp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ujs.trainingprogram.tp.common.result.Result;
import com.ujs.trainingprogram.tp.common.result.ResultMessage;
import com.ujs.trainingprogram.tp.common.web.Results;
import com.ujs.trainingprogram.tp.dao.entity.CollegeDO;
import com.ujs.trainingprogram.tp.dao.entity.MajorDO;
import com.ujs.trainingprogram.tp.dto.req.major.MajorPageReqDTO;
import com.ujs.trainingprogram.tp.dto.req.major.MajorSaveReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.major.MajorPageRespDTO;
import com.ujs.trainingprogram.tp.service.CollegeService;
import com.ujs.trainingprogram.tp.service.MajorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 专业业务控制层
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "专业请求管理")
public class MajorController {

    private final MajorService majorService;

    private final CollegeService collegeService;

    /**
     * 分页查询专业
     *
     * @param requestParam 分页请求实体
     * @return 分页请求返回实体
     */
    @Operation(summary = "分页查询专业")
    @RequestMapping("/major/page")
    public Result<IPage<MajorPageRespDTO>> pageMajors(MajorPageReqDTO requestParam) {
//        QueryWrapper<MajorDO> wrapper = new QueryWrapper<>();
//        collegeName = "计算机";
//        if (!StringUtils.isEmpty(majorId)) {
//            wrapper.eq("major_id", majorId);
//        } else {
//            wrapper.eq(!majorName.equals(""), "major_name", majorName);
//            if (!StringUtils.isEmpty(collegeName)) {
//                List<CollegeDO> collegeDOList = collegeService.getCollegeLikeByName(collegeName);
//                if (collegeDOList.size() > 0) {
//                    wrapper.and(i -> {
//                        i.like("college_id", collegeDOList.get(0).getCollegeId());
//                    });
//                }
//            }
//        }
        return Results.success(majorService.pageMajors(requestParam));
    }

    /**
     * 添加专业
     */
    @Operation(summary = "添加专业")
    @PostMapping("/major/mainAdmin/add")
    public Result<Void> createMajor(@RequestBody MajorSaveReqDTO requestParam) {
//        if (StringUtils.isAnyEmpty(majorName, collegeName)) return ResultMessage.PARAM_MISS;
//        QueryWrapper<CollegeDO> collegeWrapper = new QueryWrapper<>();
//        collegeWrapper.eq("college_name", collegeName);
//        CollegeDO collegeDO = collegeService.getOne(collegeWrapper);
//        if (collegeDO == null) return ResultMessage.ADD_ERROR;
//        MajorDO majorDO = new MajorDO();
//        majorDO.setCourseNum(0);
//        majorDO.setMajorName(majorName);
//        majorDO.setCollegeId(collegeDO.getCollegeId());
//        QueryWrapper<MajorDO> majorWrapper = new QueryWrapper<>();
//        int id = Integer.parseInt(majorService.getMaxMajorId(majorWrapper)) + 1;
//        String newId = String.valueOf(id);
//        int length = newId.length();
//        while (length < 4) {
//            newId = "0" + newId;
//            length++;
//        }
//        majorDO.setMajorId(newId);
        majorService.createMajor(requestParam);
        return Results.success();
    }

    /**
     * 删除专业
     */
    @Operation(summary = "删除专业")
    @DeleteMapping("/major/mainAdmin/delete")
    public Result<Void> deleteCollege(@RequestParam(value = "majorCode", defaultValue = "") String majorCode) {
        majorService.deleteMajor(majorCode);
        return Results.success();
    }

    /**
     * 修改专业
     */
    @Operation(summary = "修改专业")
    @PutMapping("/major/mainAdmin/update")
    public ResultMessage updateMajor(@RequestParam(value = "majorId", defaultValue = "") String majorId,
                                  @RequestParam(value = "majorName", defaultValue = "") String majorName) {
        if (StringUtils.isAnyEmpty(majorId, majorName)) return ResultMessage.PARAM_MISS;
        MajorDO majorDO = majorService.getById(majorId);
        if (majorDO == null) return ResultMessage.PARAM_MISS;
        majorDO.setMajorName(majorName);
        return majorService.saveOrUpdate(majorDO) ? ResultMessage.UPDATE_SUCCESS : ResultMessage.UPDATE_ERROR;
    }
}
