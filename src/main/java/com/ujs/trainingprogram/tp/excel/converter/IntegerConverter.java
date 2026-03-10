//package com.ujs.trainingprogram.tp.excel.converter;
//
//import cn.hutool.core.util.StrUtil;
//import com.alibaba.excel.converters.Converter;
//import com.alibaba.excel.enums.CellDataTypeEnum;
//import com.alibaba.excel.metadata.GlobalConfiguration;
//import com.alibaba.excel.metadata.data.ReadCellData;
//import com.alibaba.excel.metadata.data.WriteCellData;
//import com.alibaba.excel.metadata.property.ExcelContentProperty;
//
//import java.math.BigDecimal;
//
///**
// * 通用 Integer 转换器，处理 Excel 读取时的类型转换
// * 解决 Excel 数字类型（Double）到 Java Integer 的转换问题
// */
//public class IntegerConverter implements Converter<Integer> {
//
//    @Override
//    public Class<?> supportJavaTypeKey() {
//        return Integer.class;
//    }
//
//    @Override
//    public CellDataTypeEnum supportExcelTypeKey() {
//        return CellDataTypeEnum.NUMBER;
//    }
//
//    @Override
//    public Integer convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
//        // 优先尝试获取数字值
//        if (cellData.getType() == CellDataTypeEnum.NUMBER) {
//            BigDecimal doubleValue = cellData.getNumberValue();
//            if (doubleValue != null) {
//                return doubleValue.intValue();
//            }
//        }
//
//        // 如果是字符串类型，尝试解析
//        String stringValue = cellData.getStringValue();
//        if (StrUtil.isNotBlank(stringValue)) {
//            try {
//                return Integer.parseInt(stringValue.trim());
//            } catch (NumberFormatException e) {
//                return null;
//            }
//        }
//
//        return null;
//    }
//
//    @Override
//    public WriteCellData<?> convertToExcelData(com.alibaba.excel.converters.WriteConverterContext<Integer> context) throws Exception {
//        Integer value = context.getValue();
//        return value != null ? new WriteCellData<>(String.valueOf(value)) : new WriteCellData<>("");
//    }
//}
