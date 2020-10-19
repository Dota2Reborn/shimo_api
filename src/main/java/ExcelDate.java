import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ExcelDate {
    public Object[][] testData(String file) {
        ArrayList<String> arrKey = new ArrayList<String>();
        Workbook workbook = getWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
//        获取总行数
        int rowTotalNum = sheet.getLastRowNum()+1;
//        总列数
        int columns = sheet.getRow(0).getPhysicalNumberOfCells();

        HashMap<String, String>[][] map = new HashMap[rowTotalNum - 1][1];
        // 对数组中所有元素hashmap进行初始化
        if (rowTotalNum > 1) {
            for (int i = 0; i < rowTotalNum - 1; i++) {
                map[i][0] = new HashMap();
            }
        } else {
            System.out.println("测试的Excel" + file + "中没有数据");
        }
        // 获得首行的列名，作为hashmap的key值
        for (int c = 0; c < columns; c++) {
            String cellValue = getCellValue(sheet, 0, c);
            arrKey.add(cellValue);
        }
        // 遍历所有的单元格的值添加到hashmap中
        for (int r = 1; r < rowTotalNum; r++) {
            for (int c = 0; c < columns; c++) {
                String cellValue = getCellValue(sheet, r, c);
                map[r - 1][0].put(arrKey.get(c), cellValue);
            }
        }
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;

    }

    public static Workbook getWorkbook(String filePath) {
        Workbook wb = null;
        try {
            if (filePath.endsWith(".xls")) {
                File file = new File(filePath);
                InputStream is = new FileInputStream(file);
                wb = new HSSFWorkbook(is);
            } else if (filePath.endsWith(".xlsx") || filePath.endsWith(".xlsm")) {
                wb = new XSSFWorkbook(filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }

    public static String getCellValue(Sheet sheet, int rowNum, int cellNum) {
        Cell cell = sheet.getRow(rowNum).getCell(cellNum);
        if(cell == null){
            cell = sheet.getRow(rowNum).createCell(cellNum);
            cell.setBlank();
        }
        String value = getCellValue(cell);
        return value;
    }

    public static String getCellValue(Cell cell) {
        String value = "";
        switch (cell.getCellType()) {
            case STRING:
                value = String.valueOf(cell.getRichStringCellValue());
                return value;
            case NUMERIC:
                value = String.valueOf(cell.getNumericCellValue());
                return value;
            case BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                return value;

            case FORMULA:
                value = String.valueOf(cell.getCellFormula());
                return value;

            case ERROR:
                value = String.valueOf(cell.getErrorCellValue());
                return value;
            case BLANK:
                return value;
            default:
                System.out.println("未知该单元格类型");
                return value;

        }
    }
}
