package com.example.datn.common;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

public class ExcelHelper {

    // Kiểm tra file tải lên có đúng định dạng .xlsx không
    public static boolean hasExcelFormat(MultipartFile file) {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(file.getContentType());
    }

    // Hàm đọc file Excel tổng quát (Generic)
    public static <T> List<T> readExcel(MultipartFile file, Function<Row, T> rowMapper) {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            List<T> list = new ArrayList<>();
            int rowIndex = 0;

            for (Row row : sheet) {
                // Bỏ qua dòng tiêu đề (Header - Dòng 0)
                if (rowIndex++ == 0) continue;

                // Bỏ qua dòng trống
                if (isRowEmpty(row)) continue;

                T item = rowMapper.apply(row);
                if (item != null) {
                    list.add(item);
                }
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage());
        }
    }

    // --- CÁC HÀM TIỆN ÍCH LẤY GIÁ TRỊ Ô (Tránh lỗi Sai Kiểu Dữ Liệu Cell) ---

    public static String getStringValue(Cell cell) {
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }

    public static Integer getIntegerValue(Cell cell) {
        if (cell == null) return 0;
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }
        String val = getStringValue(cell);
        return val.isEmpty() ? 0 : Integer.parseInt(val);
    }

    public static LocalDate getLocalDateValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            Date date = cell.getDateCellValue();
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
    }

    private static boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) return false;
        }
        return true;
    }
}