package com.ujs.trainingprogram.tp.utils;

import com.ujs.trainingprogram.tp.common.constant.RedisKeyConstant;
import com.ujs.trainingprogram.tp.dao.entity.CollegeDO;
import com.ujs.trainingprogram.tp.dao.entity.CourseDO;
import com.ujs.trainingprogram.tp.dao.entity.SysDictDO;
import com.ujs.trainingprogram.tp.service.CollegeService;
import com.ujs.trainingprogram.tp.service.CourseService;
import com.ujs.trainingprogram.tp.service.SysDictService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 加载数据库信息至缓存工具类
 */
@Component
@RequiredArgsConstructor
public class LoadCacheUtils {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final SysDictService sysDictService;
    private final CourseService courseService;
    private final CollegeService collegeService;


    /**
     * 预加载所有学院数据到缓存中
     */
    public void loadCollegeCache() {
        Boolean hasCollegeCache = stringRedisTemplate.hasKey(RedisKeyConstant.COLLEGE_ID_NAME_CACHE_KEY);
        if (hasCollegeCache) {
            return;
        }

        RLock lock = redissonClient.getLock(RedisKeyConstant.LOCK_COLLEGE_ID_KEY);
        lock.lock();
        try {
            if (stringRedisTemplate.hasKey(RedisKeyConstant.COLLEGE_ID_NAME_CACHE_KEY)) {
                return;
            }
            List<CollegeDO> colleges = collegeService.listColleges();

            Map<String, String> idNameMap = colleges.stream()
                    .collect(Collectors.toMap(
                            CollegeDO::getCollegeName,
                            college -> college.getId().toString(),
                            (v1, v2) -> v1  // 如果有重复的 ID，保留第一个
                    ));

            stringRedisTemplate.opsForHash().putAll(RedisKeyConstant.COLLEGE_ID_NAME_CACHE_KEY, idNameMap);
            stringRedisTemplate.expire(RedisKeyConstant.COLLEGE_ID_NAME_CACHE_KEY, 7, TimeUnit.DAYS);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 预加载数据字典缓存
     */
    public void loadSysDictCache() {
        if (stringRedisTemplate.hasKey(RedisKeyConstant.SYS_DICT_TYPE_NAME_KEY)) {
            return;
        }

        RLock lock = redissonClient.getLock(RedisKeyConstant.LOCK_SYS_DICT_NAME_KEY);
        lock.lock();
        try {
            if (stringRedisTemplate.hasKey(RedisKeyConstant.SYS_DICT_TYPE_NAME_KEY)) {
                return;
            }
            List<SysDictDO> sysDictDOList = sysDictService.listSysDict();

            Map<String, String> typeNameMap = sysDictDOList.stream()
                    .collect(Collectors.toMap(
                            SysDictDO::getDictType,
                            SysDictDO::getDictName,
                            (v1, v2) -> v1  // 如果有重复的 ID，保留第一个
                    ));

            stringRedisTemplate.opsForHash().putAll(RedisKeyConstant.SYS_DICT_TYPE_NAME_KEY, typeNameMap);
            stringRedisTemplate.expire(RedisKeyConstant.SYS_DICT_TYPE_NAME_KEY, 7, TimeUnit.DAYS);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 预加载课程缓存
     */
    public void loadCourseCache() {
        if (stringRedisTemplate.hasKey(RedisKeyConstant.COURSE_NAME_ID_KEY)) {
            return;
        }

        RLock lock = redissonClient.getLock(RedisKeyConstant.LOCK_COURSE_ID_KEY);
        lock.lock();
        try {
            if (stringRedisTemplate.hasKey(RedisKeyConstant.COURSE_NAME_ID_KEY)) {
                return;
            }
            List<CourseDO> courseDOList = courseService.listCourses();

            Map<String, String> idNameMap = courseDOList.stream()
                    .collect(Collectors.toMap(
                            CourseDO::getCourseName,
                            courseDO -> courseDO.getId().toString(),
                            (v1, v2) -> v1  // 如果有重复的 ID，保留第一个
                    ));

            stringRedisTemplate.opsForHash().putAll(RedisKeyConstant.COURSE_NAME_ID_KEY, idNameMap);
            stringRedisTemplate.expire(RedisKeyConstant.COURSE_NAME_ID_KEY, 7, TimeUnit.DAYS);
        } finally {
            lock.unlock();
        }
    }
}
