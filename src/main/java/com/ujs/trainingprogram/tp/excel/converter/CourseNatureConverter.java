package com.ujs.trainingprogram.tp.excel.converter;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.util.Objects;

/**
 * 课程性质转换器（字符串转整数）
 */
public class CourseNatureConverter implements Converter<Integer> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return Integer.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Integer convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        String value = cellData.getStringValue();
        if (StrUtil.isBlank(value)) {
            // 未知
            return 2;
        } else if (Objects.equals(value, "必修")) {
            return 0;
        } else if (Objects.equals(value, "选修")) {
            return 1;
        }
        return 2;
    }


    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<Integer> context) throws Exception {
        Object valueObj = context.getValue();
        // 处理 null 值
        if (valueObj == null) {
            return new WriteCellData<>("未知");
        }

        // 如果是 String 类型，先转换为 Integer
        Integer value;
        if (valueObj instanceof String) {
            String strValue = (String) valueObj;
            if (StrUtil.isBlank(strValue)) {
                return new WriteCellData<>("未知");
            }
            try {
                value = Integer.parseInt(strValue.trim());
            } catch (NumberFormatException e) {
                return new WriteCellData<>("未知");
            }
        } else if (valueObj instanceof Integer) {
            value = (Integer) valueObj;
        } else {
            // 其他类型尝试转换为 Integer
            try {
                value = Integer.parseInt(valueObj.toString());
            } catch (NumberFormatException e) {
                return new WriteCellData<>("未知");
            }
        }

        // 根据数值返回对应的中文
        if (Objects.equals(value, 0)) {
            return new WriteCellData<>("必修");
        }
        if (Objects.equals(value, 1)) {
            return new WriteCellData<>("选修");
        }
        return new WriteCellData<>("未知");
    }
}
