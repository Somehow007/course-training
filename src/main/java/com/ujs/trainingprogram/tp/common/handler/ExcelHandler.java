//package com.ujs.trainingprogram.tp.common.handler;
//
//import com.ujs.trainingprogram.tp.model.College;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Excel 处理类
// */
//public class ExcelHandler {
//    /**
//     * 读取文件并转换为工作簿
//     * @param file
//     * @return
//     * @throws Exception
//     */
//    private static Workbook load(File file) throws Exception {
//        if (file == null) throw new Exception("文件不能为空");
//        int index = file.getName().lastIndexOf(".");
//        if (index == -1) throw new Exception("无法加载此文件");
//        String suffix = file.getName().substring(index + 1).toLowerCase();
//        Workbook workbook;
//        FileInputStream fis = new FileInputStream(file);
//        if (suffix.equals("xlsx")) {
//            workbook = new XSSFWorkbook(fis);
//        } else if (suffix.equals("xls")) {
//            workbook = new HSSFWorkbook(fis);
//        } else {
//            throw new Exception("无法加载此文件");
//        }
//        fis.close();
//        return workbook;
//    }
//
//    private static List<College> readCollegeForList(Workbook workbook) {
//        List<College> list = new ArrayList<>();
//        final String colString = "开课学院";// 学院 列名
//
//    }
//
//}
