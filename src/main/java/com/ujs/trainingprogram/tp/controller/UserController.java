package com.ujs.trainingprogram.tp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ujs.trainingprogram.tp.common.Constant;
import com.ujs.trainingprogram.tp.common.result.ResultData;
import com.ujs.trainingprogram.tp.common.result.ResultMessage;
import com.ujs.trainingprogram.tp.model.College;
import com.ujs.trainingprogram.tp.model.User;
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
        User user = (User) principal;
        String userName = user.getUserId();
        System.out.println(userName);
        if (user != null) {
            UserReturn userReturn = new UserReturn();
            userReturn.userId = user.getUserId();
            userReturn.userState = user.getUserState();
            College college = collegeService.getById(user.getCollegeId());
            if (college != null) {
                userReturn.collegeName = college.getCollegeName();
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
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like(!userId.equals(""), "user_id", userId);
        wrapper.like(!userState.equals(""), "user_state", userState);
        if (!collegeName.isEmpty()) {
            List<College> collegeList = collegeService.getCollegeLikeByName(collegeName);
            if (collegeList.size() > 0) {
                //可进行模糊查找
                wrapper.and(i -> {
                    i.like("college_id", collegeList.get(0).getCollegeId());
                    for (int j = 1; j < collegeList.size(); j++) {
                        i.or().like("college_id", collegeList.get(j).getCollegeId());
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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setUserPassword(encoder.encode(password));
        return userService.saveOrUpdate(user) ? ResultMessage.UPDATE_SUCCESS : ResultMessage.UPDATE_ERROR;
    }

    /**
     * 非系统管理员
     * 重置密码与用户名相同
     */
    @PutMapping("/user/resetPasswordEqualUserId")
    public ResultMessage resetPasswordEqualUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) return ResultMessage.UPDATE_ERROR;
        user.setUserPassword(encoder.encode(user.getUserId()));
        return userService.saveOrUpdate(user) ? ResultMessage.UPDATE_SUCCESS : ResultMessage.UPDATE_ERROR;
    }

    /**
     * 非系统管理员
     * 生成随机八位密码
     */
    @PutMapping("/user/resetPasswordByRandom")
    public ResultMessage resetPasswordByRandom() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) return ResultMessage.UPDATE_ERROR;
        String newPassword = passRandom();
        user.setUserPassword(encoder.encode(newPassword));
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setCode(Constant.OK);
        resultMessage.setMsg(newPassword);
        return userService.saveOrUpdate(user) ? resultMessage : ResultMessage.UPDATE_ERROR;
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
        QueryWrapper<College> wrapper = new QueryWrapper<>();
        wrapper.eq("college_name", collegeName);
        College college = collegeService.getOne(wrapper);
        if (college == null) return ResultMessage.ADD_ERROR;
        if (!userState.equals("21") || !userState.equals("22")) return ResultMessage.ADD_ERROR;
        User user = new User();
        user.setUserPassword(encoder.encode(userPassword));
        user.setUserState(userState);
        user.setUserId(userId);
        user.setCollegeId(college.getCollegeId());

        return userService.save(user) ? ResultMessage.ADD_SUCCESS : ResultMessage.ADD_ERROR;
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/mainAdmin/delete")
    public ResultMessage deleteUser(@RequestParam(value = "user_id", defaultValue = "") String userId) {
        if (StringUtils.isEmpty(userId)) return ResultMessage.PARAM_MISS;
        User user = userService.getById(userId);
        if (user.getUserState().equals("20")) return ResultMessage.DELETE_ERROR;

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
        QueryWrapper<College> wrapper = new QueryWrapper<>();
        wrapper.eq("college_name", collegeName);
        College college = collegeService.getOne(wrapper);
        if (college == null) return ResultMessage.UPDATE_ERROR;
        User user = userService.getById(userId);
        if (!StringUtils.isEmpty(userPassword)) {
            if (userPassword.length() < 8) return ResultMessage.UPDATE_ERROR;
            user.setUserPassword(encoder.encode(userPassword));
        }
        user.setUserState(userState);
        user.setCollegeId(college.getCollegeId());

        return userService.saveOrUpdate(user) ? ResultMessage.UPDATE_SUCCESS : ResultMessage.UPDATE_ERROR;
    }

    /**
     * 系统管理员
     * 重置密码与用户名一样
     */
    @PutMapping("/mainAdmin/resetPasswordEqualUserId")
    public ResultMessage resetPasswordEqualMainAdminId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) return ResultMessage.UPDATE_ERROR;
        user.setUserPassword(encoder.encode(user.getUserId()));
        return userService.saveOrUpdate(user) ? ResultMessage.UPDATE_SUCCESS : ResultMessage.UPDATE_ERROR;
    }

    /**
     * 系统管理员
     * 重置密码为随机八位数
     */
    @PutMapping("/mainAdmin/resetPasswordByRandom")
    public ResultMessage resetMainAdminPasswordByRandom() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) return ResultMessage.UPDATE_ERROR;
        String newPassword = passRandom();
        user.setUserPassword(encoder.encode(newPassword));
        return userService.saveOrUpdate(user) ? ResultMessage.UPDATE_SUCCESS : ResultMessage.UPDATE_ERROR;
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
