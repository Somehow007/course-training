//package com.ujs.trainingprogram.tp.controller;
//
//import com.ujs.trainingprogram.tp.dao.entity.CourseDO;
//import com.ujs.trainingprogram.tp.service.CollegeService;
//import com.ujs.trainingprogram.tp.service.CourseService;
//import com.ujs.trainingprogram.tp.service.MajorService;
//import com.ujs.trainingprogram.tp.service.UserService;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Excel业务处理控制层
// */
//@RestController
//@RequestMapping("/excel")
//@RequiredArgsConstructor
//public class ExcelController {
//
//    private final CollegeService collegeService;
//
//    private final MajorService majorService;
//
//    private final CourseService courseService;
//
////    private final UserService userService;
//
//    /**
//     * 管理员通过Excel上传培养计划
//     * @param uploadFile
//     * @return
//     * @throws Exception
//     */
////    @GetMapping
////    public ResultMessage addTp(MultipartFile uploadFile, @RequestParam) throws Exception {
////
////    }
//
//    /**
//     * 下载培养计划
//     * @return
//     */
////    @RequestMapping(value = "/downloadTemplate", method = RequestMethod.GET)
////    public ResponseEntity<byte[]> downloadTemplate() {
////
////    }
//
//    /**
//     * 导出注册用户名单
//     * @return
//     */
////    @GetMapping("/exportUsers")
////    public ResponseEntity<byte[]> exportUsers() {
////
////    }
//
//    /**
//     * 下载历史培养计划
//     * @return
//     */
////    @GetMapping("/downloadHistory")
////    public ResponseEntity<byte[]> downloadHistory() {
////
////    }
//
//    @GetMapping("/users")
//    public void downloadUsers(HttpServletResponse response) throws IOException {
//        userService.exportUserToExcel(response);
//    }
//
//    private static List<CourseDO> readCoursesForList(Workbook workbook) {
//        List<CourseDO> list = new ArrayList<>();
//        String target = "专业";
//        final String sheetString = "课程设置及学时分配表(四年制)";// 工作簿名称
//        final String[] colNames = {"课程类别", "课程性质", "开课学院", "课程名称", "总学分", "总学时", "授课"};
//        return null;
//    }
//}
