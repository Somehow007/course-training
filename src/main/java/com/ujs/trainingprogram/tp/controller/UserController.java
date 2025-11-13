package com.ujs.trainingprogram.tp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ujs.trainingprogram.tp.common.Constant;
import com.ujs.trainingprogram.tp.common.result.ResultData;
import com.ujs.trainingprogram.tp.common.result.ResultMessage;
import com.ujs.trainingprogram.tp.dao.entity.CollegeDO;
import com.ujs.trainingprogram.tp.dao.entity.UserDO;
import com.ujs.trainingprogram.tp.service.CollegeService;
import com.ujs.trainingprogram.tp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.List;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    private CollegeService collegeService;

    static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @GetMapping("/")
    public ResultData getPersonalMessage() {
        log.info("开始获取个人信息");
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDO userDO = (UserDO) principal;
        String userName = userDO.getUserId();
        System.out.println(userName);
        if (userDO != null) {
            UserReturn userReturn = new UserReturn();
            userReturn.userId = userDO.getUserId();
            userReturn.userState = userDO.getUserState();
            CollegeDO collegeDO = collegeService.getById(userDO.getCollegeCode());
            if (collegeDO != null) {
                userReturn.collegeName = collegeDO.getCollegeName();
            }
            long i = 1;
            return new ResultData(i, i, i, userReturn);
        }
        return new ResultData();
    }

    /**
     * 条件、分页查询
     */
    @GetMapping("/mainAdmin/getUser")
    public ResultData getAllUser(@RequestParam(value = "page_size", defaultValue = "0") Integer cur,
                                 @RequestParam(value = "total_size", defaultValue = "10") Integer size,
                                 @RequestParam(value = "user_id", defaultValue = "") String userId,
                                 @RequestParam(value = "user_state", defaultValue = "") String userState,
                                 @RequestParam(value = "college_name", defaultValue = "") String collegeName) {
        QueryWrapper<UserDO> wrapper = new QueryWrapper<>();
        wrapper.like(!userId.equals(""), "user_id", userId);
        wrapper.like(!userState.equals(""), "user_state", userState);
        if (!collegeName.isEmpty()) {
            List<CollegeDO> collegeDOList = collegeService.getCollegeLikeByName(collegeName);
            if (collegeDOList.size() > 0) {
                //可进行模糊查找
                wrapper.and(i -> {
                    i.like("college_id", collegeDOList.get(0).getCollegeCode());
                    for (int j = 1; j < collegeDOList.size(); j++) {
                        i.or().like("college_id", collegeDOList.get(j).getCollegeCode());
                    }
                });
            }
        }
        return userService.selectWithWrapper(cur, size, wrapper);
    }

    /**
     * 非系统管理员
     * 重置密码
     */
    @PutMapping("/user/resetPassword")
    public ResultMessage resetPassword(@RequestParam(value = "password", defaultValue = "") String password) {
        if (password.equals("")) return ResultMessage.PARAM_MISS;
        UserDO userDO = (UserDO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userDO.setUserPassword(encoder.encode(password));
        return userService.saveOrUpdate(userDO) ? ResultMessage.UPDATE_SUCCESS : ResultMessage.UPDATE_ERROR;
    }

    /**
     * 非系统管理员
     * 重置密码与用户名相同
     */
    @PutMapping("/user/resetPasswordEqualUserId")
    public ResultMessage resetPasswordEqualUserId() {
        UserDO userDO = (UserDO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDO == null) return ResultMessage.UPDATE_ERROR;
        userDO.setUserPassword(encoder.encode(userDO.getUserId()));
        return userService.saveOrUpdate(userDO) ? ResultMessage.UPDATE_SUCCESS : ResultMessage.UPDATE_ERROR;
    }

    /**
     * 非系统管理员
     * 生成随机八位密码
     */
    @PutMapping("/user/resetPasswordByRandom")
    public ResultMessage resetPasswordByRandom() {
        UserDO userDO = (UserDO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDO == null) return ResultMessage.UPDATE_ERROR;
        String newPassword = passRandom();
        userDO.setUserPassword(encoder.encode(newPassword));
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setCode(Constant.OK);
        resultMessage.setMsg(newPassword);
        return userService.saveOrUpdate(userDO) ? resultMessage : ResultMessage.UPDATE_ERROR;
    }

    /**
     * 添加用户
     */
    @PostMapping("/mainAdmin/add")
    public ResultMessage addUser(@RequestParam(value = "user_id", defaultValue = "") String userId,
                                 @RequestParam(value = "user_state", defaultValue = "") String userState,
                                 @RequestParam(value = "college_name", defaultValue = "") String collegeName,
                                 @RequestParam(value = "user_password", defaultValue = "") String userPassword) {
        if (StringUtils.isAnyEmpty(userId, userPassword, userState, collegeName)) {
            return ResultMessage.PARAM_MISS;
        }
        QueryWrapper<CollegeDO> wrapper = new QueryWrapper<>();
        wrapper.eq("college_name", collegeName);
        CollegeDO collegeDO = collegeService.getOne(wrapper);
        if (collegeDO == null) return ResultMessage.ADD_ERROR;
        if (!userState.equals("21") || !userState.equals("22")) return ResultMessage.ADD_ERROR;
        UserDO userDO = new UserDO();
        userDO.setUserPassword(encoder.encode(userPassword));
        userDO.setUserState(userState);
        userDO.setUserId(userId);
        userDO.setCollegeCode(collegeDO.getCollegeCode());

        return userService.save(userDO) ? ResultMessage.ADD_SUCCESS : ResultMessage.ADD_ERROR;
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/mainAdmin/delete")
    public ResultMessage deleteUser(@RequestParam(value = "user_id", defaultValue = "") String userId) {
        if (StringUtils.isEmpty(userId)) return ResultMessage.PARAM_MISS;
        UserDO userDO = userService.getById(userId);
        if (userDO.getUserState().equals("20")) return ResultMessage.DELETE_ERROR;

        return userService.removeById(userId) ? ResultMessage.DELETE_SUCCESS : ResultMessage.DELETE_ERROR;
    }

    /**
     * 修改用户
     */
    @PutMapping("/mainAdmin/update")
    public ResultMessage updateUser(@RequestParam(value = "user_id", defaultValue = "") String userId,
                                    @RequestParam(value = "user_state", defaultValue = "") String userState,
                                    @RequestParam(value = "college_name", defaultValue = "") String collegeName,
                                    @RequestParam(value = "user_password", defaultValue = "") String userPassword) {
        if (!StringUtils.isAnyEmpty(userId, userState, collegeName, userPassword)) return ResultMessage.UPDATE_ERROR;
        if (!userState.equals("21") || !userState.equals("22")) return ResultMessage.UPDATE_ERROR;
        QueryWrapper<CollegeDO> wrapper = new QueryWrapper<>();
        wrapper.eq("college_name", collegeName);
        CollegeDO collegeDO = collegeService.getOne(wrapper);
        if (collegeDO == null) return ResultMessage.UPDATE_ERROR;
        UserDO userDO = userService.getById(userId);
        if (!StringUtils.isEmpty(userPassword)) {
            if (userPassword.length() < 8) return ResultMessage.UPDATE_ERROR;
            userDO.setUserPassword(encoder.encode(userPassword));
        }
        userDO.setUserState(userState);
        userDO.setCollegeCode(collegeDO.getCollegeCode());

        return userService.saveOrUpdate(userDO) ? ResultMessage.UPDATE_SUCCESS : ResultMessage.UPDATE_ERROR;
    }

    /**
     * 系统管理员
     * 重置密码与用户名一样
     */
    @PutMapping("/mainAdmin/resetPasswordEqualUserId")
    public ResultMessage resetPasswordEqualMainAdminId() {
        UserDO userDO = (UserDO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDO == null) return ResultMessage.UPDATE_ERROR;
        userDO.setUserPassword(encoder.encode(userDO.getUserId()));
        return userService.saveOrUpdate(userDO) ? ResultMessage.UPDATE_SUCCESS : ResultMessage.UPDATE_ERROR;
    }

    /**
     * 系统管理员
     * 重置密码为随机八位数
     */
    @PutMapping("/mainAdmin/resetPasswordByRandom")
    public ResultMessage resetMainAdminPasswordByRandom() {
        UserDO userDO = (UserDO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDO == null) return ResultMessage.UPDATE_ERROR;
        String newPassword = passRandom();
        userDO.setUserPassword(encoder.encode(newPassword));
        return userService.saveOrUpdate(userDO) ? ResultMessage.UPDATE_SUCCESS : ResultMessage.UPDATE_ERROR;
    }




    /**
     * 生成随机密码
     */
    public static String passRandom() {
        StringBuilder text = new StringBuilder();
        String reference = "QWERTYUIOPASDFGHJKLZXCVBNM1234567890qwertyuiopasdfghjklzxcvbnm!@#$%^&*()";
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 8; i++) {
            int ran = random.nextInt(reference.length());
            text.append(reference.charAt(ran));
        }
        return text.toString();
    }

    public static class UserReturn{
        public String userId;
        public String collegeName;
        public String userState;
    }


}
