package ltd.newbee.mall.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * excel导入 excel下载
 * 
 * @author wulirong
 *
 */
public class ExcelUtil {
    protected final static Logger log = LoggerFactory.getLogger("system");

    /**
     * 得到file流，把file文件保存到自己建的目录上
     * 
     * @param stream
     * @param path
     * @param savefile
     * @throws IOException
     */
    public static void SaveFileFromInputStream(InputStream stream, String path, String savefile)
            throws IOException {

        File file = new File(path + "/" + savefile);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fs = new FileOutputStream(file);
        byte[] buffer = new byte[1024 * 1024];
        int bytesum = 0;
        int byteread = 0;
        while ((byteread = stream.read(buffer)) != -1) {
            bytesum += byteread;
            fs.write(buffer, 0, byteread);
            fs.flush();
        }
        fs.close();
        stream.close();
    }

    private static String getHSValue(HSSFCell hssfCell) {
        if (hssfCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            DecimalFormat df = new DecimalFormat("#.00");
            return String.valueOf(df.format(hssfCell.getNumericCellValue()));
        } else {
            return null;
        }
    }

    private static String getXsValue(XSSFCell xssfCell) {
        if (xssfCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            DecimalFormat df = new DecimalFormat("#.00");
            return String.valueOf(df.format(xssfCell.getNumericCellValue()));
        } else {
            return null;
        }
    }

    /**
     * excel模板的下载
     * 
     * @param request
     * @param response
     * @param downLoadPath
     * @param excelname
     * @throws Exception
     */
    public static void downloadExcel(HttpServletRequest request, HttpServletResponse response,
            String downLoadPath, String excelname) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("UTF-8");
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            long fileLength = new File(downLoadPath).length();
            response.setContentType("application/x-msdownload;");
            response.setHeader("Content-disposition", "attachment; filename="
                    + new String(excelname.getBytes(), "ISO8859-1"));
            response.setHeader("Content-Length", String.valueOf(fileLength));
            bis = new BufferedInputStream(new FileInputStream(downLoadPath));
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
        }
    }


    public static boolean checkDate(String ye, String mon) {
        boolean flag = true;
        int year = new Date().getYear() + 1900;
        int month = new Date().getMonth() + 1;
        try {
            if (year < Integer.valueOf(ye).intValue() || Integer.valueOf(ye).intValue() < 1980)
                flag = false;
            if ((year == Integer.valueOf(ye).intValue())
                    && (month < Integer.valueOf(mon).intValue()))
                flag = false;
            if (ye.length() != 4)
                flag = false;
            if (Integer.valueOf(mon).intValue() > 12 || Integer.valueOf(mon).intValue() < 1)
                flag = false;
        } catch (Exception e) {
            flag = false;
        }

        return flag;
    }

    private static final String aesKey = "zjaisino_zjjrpta";

    /**
     * 读取Excel中的内容
     * 
     * @param excelPath ：Excel文件绝对路径
     * @param decoded :true:需要解密,false:不需要解密
     * @return
     */
    public static List<Map<Integer, String>> readExcelFile(String excelPath, boolean decoded) {
        List<Map<Integer, String>> resultList = new ArrayList<Map<Integer, String>>();
        try {
            if (excelPath.endsWith(".xls")) {
                InputStream inp = new FileInputStream(excelPath);
                HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inp);
                HSSFFormulaEvaluator evaluator = new HSSFFormulaEvaluator(hssfWorkbook);
                inp.close();
                int sheetNum = hssfWorkbook.getNumberOfSheets();
                for (int num = 0; num < sheetNum; num++) {
                    Sheet sheet = hssfWorkbook.getSheetAt(num);
                    List<Map<Integer, String>> dataList = readExcelSheet(evaluator, sheet, decoded);
                    resultList.addAll(dataList);
                }
            } else if (excelPath.endsWith(".xlsx")) {
                InputStream inp = new FileInputStream(excelPath);
                XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inp);
                XSSFFormulaEvaluator evaluator = new XSSFFormulaEvaluator(xssfWorkbook);
                inp.close();
                int sheetNum = xssfWorkbook.getNumberOfSheets();
                for (int num = 0; num < sheetNum; num++) {
                    Sheet sheet = xssfWorkbook.getSheetAt(num);
                    List<Map<Integer, String>> dataList = readExlSheet(evaluator, sheet, decoded);
                    resultList.addAll(dataList);
                }
            }
            return resultList;
        } catch (Exception e) {
            System.out.println("文件：" + excelPath + "读取异常;" + e.getMessage());
            return null;
        }
    }

    public static List<Map<Integer, String>> readExcelFile(String excelPath, boolean decoded,
            InputStream inp) {
        List<Map<Integer, String>> resultList = new ArrayList<Map<Integer, String>>();
        try {
            if (excelPath.endsWith(".xls")) {
                HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inp);
                HSSFFormulaEvaluator evaluator = new HSSFFormulaEvaluator(hssfWorkbook);
                inp.close();
                int sheetNum = hssfWorkbook.getNumberOfSheets();
                for (int num = 0; num < sheetNum; num++) {
                    Sheet sheet = hssfWorkbook.getSheetAt(num);
                    List<Map<Integer, String>> dataList = readExcelSheet(evaluator, sheet, decoded);
                    resultList.addAll(dataList);
                }
            } else if (excelPath.endsWith(".xlsx")) {
                XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inp);
                XSSFFormulaEvaluator evaluator = new XSSFFormulaEvaluator(xssfWorkbook);
                inp.close();
                int sheetNum = xssfWorkbook.getNumberOfSheets();
                for (int num = 0; num < sheetNum; num++) {
                    Sheet sheet = xssfWorkbook.getSheetAt(num);
                    List<Map<Integer, String>> dataList = readExlSheet(evaluator, sheet, decoded);
                    resultList.addAll(dataList);
                }
            }
            return resultList;
        } catch (Exception e) {
            System.out.println("文件：" + excelPath + "读取异常;" + e.getMessage());
            return null;
        }
    }


    /***
     * 读取通用格式的excel文件
     * 
     * @param excelPath
     * @param decoded
     * @return
     */
    public static List<Map<Integer, String>> readGeneralExcelFile(String excelPath, boolean decoded) {
        List<Map<Integer, String>> resultList = new ArrayList<Map<Integer, String>>();
        try {
            File excelFile = new File(excelPath); // 创建文件对象
            FileInputStream is = new FileInputStream(excelFile); // 文件流
            Workbook workbook = WorkbookFactory.create(is); // 这种方式 Excel
                                                            // 2003/2007/2010
                                                            // 都是可以处理的
            int sheetCount = workbook.getNumberOfSheets(); // Sheet的数量
            // 遍历每个Sheet
            for (int s = 0; s < sheetCount; s++) {
                Sheet sheet = workbook.getSheetAt(s);
                List<Map<Integer, String>> dataList = new ArrayList<Map<Integer, String>>();
                int rowCount = sheet.getLastRowNum(); // 获取总行数
                // 遍历每一行
                for (int r = 0; r <= rowCount; r++) {
                    Row row = sheet.getRow(r);
                    if (isRowEmpty(row))
                        continue;
                    Map<Integer, String> dataMap = new LinkedHashMap<Integer, String>();
                    int startCell = row.getFirstCellNum();
                    int endCell = row.getLastCellNum();
                    for (int c = startCell; c < endCell; c++) {
                        Cell cell = row.getCell(c);
                        if (cell == null)
                            continue;
                        int cellType = cell.getCellType();
                        switch (cellType) {
                            case Cell.CELL_TYPE_ERROR: {
                                dataMap.put(c, "");
                            }
                            case Cell.CELL_TYPE_BLANK:// 空白单元格
                                dataMap.put(c, "");
                                break;
                            case Cell.CELL_TYPE_STRING:// 字符类型
                                String value = cell.getStringCellValue();
                                if (StringUtils.isEmpty(value) || "null".equals(value)) {
                                    dataMap.put(c, "");
                                } else {
                                    dataMap.put(c,
                                            decoded == true ? CipherUtils.aesDecrypt(value, aesKey)
                                                    : value);
                                }
                                break;
                            case Cell.CELL_TYPE_NUMERIC:// 数字
                            {
                                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                    Date date = cell.getDateCellValue();
                                    dataMap.put(
                                            c,
                                            decoded == true ? CipherUtils.aesDecrypt(
                                                    DateUtils.formatDateTime(date), aesKey)
                                                    : DateUtils.formatDateTime(date));
                                } else {
                                    dataMap.put(
                                            c,
                                            decoded == true ? CipherUtils.aesDecrypt(
                                                    cell.getNumericCellValue() + "", aesKey) : cell
                                                    .getNumericCellValue() + "");
                                }
                                break;
                            }
                            case Cell.CELL_TYPE_FORMULA: // 公式
                            {
                                log.info("Excel第" + (r + 1) + "行第" + (c + 1) + "列数据使用了公式，提取不了数据");
                                return null;
                            }
                            default: // 其他类型抛运行时异常
                            {
                                System.out.println("Excel数据解析异常");
                                throw new Exception("Excel数据解析异常");
                            }

                        }
                    }
                    dataList.add(dataMap);
                }
                resultList.addAll(dataList);
            }
            return resultList;
        } catch (Exception e) {
            System.out.println("文件：" + excelPath + "读取异常;" + e.getMessage());
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                return false;
            }
        }
        return true;
    }

    /**
     * 读取Excel中的sheet内容
     * 
     * @param sheet
     * @param decoded
     * @return
     * @throws Exception
     */
    private static List<Map<Integer, String>> readExcelSheet(HSSFFormulaEvaluator evaluator,
            Sheet sheet, boolean decoded) throws Exception {
        List<Map<Integer, String>> dataList = new ArrayList<Map<Integer, String>>();
        int endRowNum = sheet.getLastRowNum();
        for (int rowNum = 0; rowNum <= endRowNum; rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row == null)
                continue;
            int startCellNum = row.getFirstCellNum();
            int endCellNum = row.getLastCellNum();
            Map<Integer, String> dataMap = new LinkedHashMap<Integer, String>();
            for (int cellNum = startCellNum; cellNum < endCellNum; cellNum++) {
                Cell cell = row.getCell(cellNum);
                if (cell == null)
                    continue;
                int type = cell.getCellType();
                switch (type) {
                    case Cell.CELL_TYPE_ERROR: {
                        dataMap.put(cellNum, "");
                    }
                    case Cell.CELL_TYPE_BLANK:// 空白单元格
                        dataMap.put(cellNum, "");
                        break;
                    case Cell.CELL_TYPE_STRING:// 字符类型
                        String value = cell.getStringCellValue();
                        if (StringUtils.isEmpty(value) || "null".equals(value)) {
                            dataMap.put(cellNum, "");
                        } else {
                            dataMap.put(cellNum,
                                    decoded == true ? CipherUtils.aesDecrypt(value, aesKey) : value);
                        }
                        break;
                    case Cell.CELL_TYPE_NUMERIC:// 数字
                    {
                        if (HSSFDateUtil.isCellDateFormatted(cell)) {
                            Date date = cell.getDateCellValue();
                            dataMap.put(
                                    cellNum,
                                    decoded == true ? CipherUtils.aesDecrypt(
                                            DateUtils.formatDateTime(date), aesKey) : DateUtils
                                            .formatDateTime(date));
                        } else {
                            DecimalFormat df = new DecimalFormat("0");
                            dataMap.put(
                                    cellNum,
                                    decoded == true ? CipherUtils.aesDecrypt(
                                            cell.getNumericCellValue() + "", aesKey) : df
                                            .format(cell.getNumericCellValue()) + "");
                        }
                        break;
                    }
                    case Cell.CELL_TYPE_FORMULA: // 公式
                    {
                        String stringValue = evaluator.evaluate(cell).formatAsString();
                        log.info("Excel公式单元格的值是：" + stringValue);
                        break;
                    }
                    default: // 其他类型抛运行时异常
                    {
                        System.out.println("Excel数据解析异常");
                        throw new Exception("Excel数据解析异常");
                    }

                }
            }
            dataList.add(dataMap);
        }
        return dataList;
    }

    private static List<Map<Integer, String>> readExlSheet(XSSFFormulaEvaluator evaluator,
            Sheet sheet, boolean decoded) throws Exception {
        List<Map<Integer, String>> dataList = new ArrayList<Map<Integer, String>>();
        int endRowNum = sheet.getLastRowNum();
        for (int rowNum = 0; rowNum <= endRowNum; rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row == null)
                continue;
            int startCellNum = row.getFirstCellNum();
            int endCellNum = row.getLastCellNum();
            Map<Integer, String> dataMap = new LinkedHashMap<Integer, String>();
            for (int cellNum = startCellNum; cellNum < endCellNum; cellNum++) {
                Cell cell = row.getCell(cellNum);
                if (cell == null)
                    continue;
                int type = cell.getCellType();
                switch (type) {
                    case Cell.CELL_TYPE_ERROR: {
                        dataMap.put(cellNum, "");
                    }
                    case Cell.CELL_TYPE_BLANK:// 空白单元格
                        dataMap.put(cellNum, "");
                        break;
                    case Cell.CELL_TYPE_STRING:// 字符类型
                        String value = cell.getStringCellValue();
                        if (StringUtils.isEmpty(value) || "null".equals(value)) {
                            dataMap.put(cellNum, "");
                        } else {
                            dataMap.put(cellNum,
                                    decoded == true ? CipherUtils.aesDecrypt(value, aesKey) : value);
                        }
                        break;
                    case Cell.CELL_TYPE_NUMERIC:// 数字
                    {
                        if (HSSFDateUtil.isCellDateFormatted(cell)) {
                            Date date = cell.getDateCellValue();
                            dataMap.put(
                                    cellNum,
                                    decoded == true ? CipherUtils.aesDecrypt(
                                            DateUtils.formatDateTime(date), aesKey) : DateUtils
                                            .formatDateTime(date));
                        } else {
                            DecimalFormat df = new DecimalFormat("0.00");
                            dataMap.put(
                                    cellNum,
                                    decoded == true ? CipherUtils.aesDecrypt(
                                            cell.getNumericCellValue() + "", aesKey) : df
                                            .format(cell.getNumericCellValue()) + "");
                        }
                        break;
                    }
                    case Cell.CELL_TYPE_FORMULA: // 公式
                    {
                        String stringValue = evaluator.evaluate(cell).formatAsString();
                        log.info("Excel公式单元格的值是：" + stringValue);
                        break;
                    }
                    default: // 其他类型抛运行时异常
                    {
                        System.out.println("Excel数据解析异常");
                        throw new Exception("Excel数据解析异常");
                    }

                }
            }
            dataList.add(dataMap);
        }
        return dataList;
    }


    public static String doEscape(String content) {
        if (content == null) {
            return "";
        }
        if (content.length() == 0) {
            return "";
        }
        if (!StringUtils.isEmpty(content)) {
            content = content.replaceAll(" ", "");
            content = content.trim();
        }
        return content;
    }


    /**
     * 将数据写到Excel工作薄中
     * 
     * @param targetSheet
     * @param title
     * @param dataList
     * @param headerStyle
     * @param rowStyle
     * @param crypt :true:加密false:不加密
     * @throws Exception
     */
    public static void saveDataToSheet(HSSFSheet targetSheet, String[] title,
            List<Map<Integer, String>> dataList, CellStyle headerStyle, CellStyle rowStyle,
            boolean crypt) throws Exception {
        int rowNumber = 0;
        HSSFCell cell = null;
        HSSFRow headerRow = targetSheet.createRow(rowNumber);
        headerRow.setHeightInPoints(25);
        for (int i = 0; i < title.length; i++) {
            cell = headerRow.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(headerStyle);
        }
        rowNumber++;
        for (int i = 0; i < dataList.size(); i++) {
            HSSFRow dataRow = targetSheet.createRow(rowNumber);
            Map<Integer, String> map = dataList.get(i);
            int j = 0;
            for (Object obj : map.values()) {
                String value = (obj == null) ? "" : String.valueOf(obj);
                HSSFCell rowCell = dataRow.createCell(j);
                rowCell.setCellValue(crypt == true ? CipherUtils.aesEncrypt(value, aesKey) : value);
                rowCell.setCellStyle(rowStyle);
                j++;
            }
            rowNumber++;
        }
    }

    /**
     * 将多个Excel打包成zip文件
     * 
     * @param xlsFile
     * @param zipfile
     */
    public static void zipFiles(File xlsFile, File zipfile) {
        byte[] buf = new byte[1024];
        try {
            // Create the ZIP file
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
            // Compress the files
            FileInputStream in = new FileInputStream(xlsFile);
            // Add ZIP entry to output stream.
            out.putNextEntry(new ZipEntry(xlsFile.getName()));
            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            // Complete the entry
            out.closeEntry();
            in.close();
            // Complete the ZIP file
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToExcel(List<Map<String, Object>> dateList, String[] headerArr,
            String sheetName, String fileName, String destPath) throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        // 标题字体
        HSSFFont columnHeadFont = wb.createFont();
        columnHeadFont.setFontName("宋体");
        columnHeadFont.setFontHeightInPoints((short) 10);
        columnHeadFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        // 标题样式
        HSSFCellStyle columnHeadStyle = wb.createCellStyle();
        columnHeadStyle.setFont(columnHeadFont);
        columnHeadStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
        columnHeadStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
        columnHeadStyle.setLocked(true);
        columnHeadStyle.setWrapText(false);
        columnHeadStyle.setFillBackgroundColor(HSSFColor.MAROON.index);
        columnHeadStyle.setLeftBorderColor(HSSFColor.BLACK.index);// 左边框的颜色
        columnHeadStyle.setBorderLeft((short) 1);// 边框的大小
        columnHeadStyle.setRightBorderColor(HSSFColor.BLACK.index);// 右边框的颜色
        columnHeadStyle.setBorderRight((short) 1);// 边框的大小
        columnHeadStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 设置单元格的边框为粗体
        columnHeadStyle.setBottomBorderColor(HSSFColor.BLACK.index); // 设置单元格的边框颜色

        // 单元格字体
        HSSFFont cellFont = wb.createFont();
        cellFont.setFontName("黑体");
        cellFont.setFontHeightInPoints((short) 10);// 字体大小

        // 单元格样式
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFont(cellFont);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
        cellStyle.setLocked(true);
        cellStyle.setWrapText(true);// 自动换行
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);// 左边框的颜色
        cellStyle.setBorderLeft((short) 1);// 边框的大小
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);// 右边框的颜色
        cellStyle.setBorderRight((short) 1);// 边框的大小
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 设置单元格的边框为粗体
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index); // 设置单元格的边框颜色

        // HSSFSheet mainSheet = wb.createSheet("mainSheet");
        // HSSFSheet detailSheet = wb.createSheet("detailSheet");
        // mainSheet.setDefaultColumnWidth(20);
        HSSFSheet sheet = wb.createSheet(sheetName);
        sheet.setDefaultColumnWidth((short) 25);
        HSSFRow row = sheet.createRow(0);
        row.setHeightInPoints(25);
        // 画出title
        for (int i = 0; i < headerArr.length; i++) {
            HSSFCell headerCell = row.createCell((short) i);
            headerCell.setCellValue(headerArr[i]);
            headerCell.setCellStyle(columnHeadStyle);
        }
        // 画出数据
        for (int i = 0; i < dateList.size(); i++) {
            row = sheet.createRow(i + 1);
            row.setHeightInPoints(20);
            Map<String, Object> map = dateList.get(i);
            int j = 0;
            for (Object obj : map.values()) {
                String value = (obj == null) ? "" : String.valueOf(obj);
                HSSFCell dataCell = row.createCell((short) j);
                dataCell.setCellStyle(cellStyle);
                dataCell.setCellValue(value);
                j++;
            }
        }
        // 全路径
        File fileDir = new File(destPath);
        if (!fileDir.exists() && !fileDir.isDirectory()) {
            fileDir.mkdir();
        }
        String filePath = destPath + "/" + fileName;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            wb.write(fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File writeExcelToFileTemp(List<Map<String, Object>> dateList, String[] headerArr,
                                    String sheetName, String fileName) throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        // 标题字体
        HSSFFont columnHeadFont = wb.createFont();
        columnHeadFont.setFontName("宋体");
        columnHeadFont.setFontHeightInPoints((short) 10);
        columnHeadFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        // 标题样式
        HSSFCellStyle columnHeadStyle = wb.createCellStyle();
        columnHeadStyle.setFont(columnHeadFont);
        columnHeadStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
        columnHeadStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
        columnHeadStyle.setLocked(true);
        columnHeadStyle.setWrapText(false);
        columnHeadStyle.setFillBackgroundColor(HSSFColor.MAROON.index);
        columnHeadStyle.setLeftBorderColor(HSSFColor.BLACK.index);// 左边框的颜色
        columnHeadStyle.setBorderLeft((short) 1);// 边框的大小
        columnHeadStyle.setRightBorderColor(HSSFColor.BLACK.index);// 右边框的颜色
        columnHeadStyle.setBorderRight((short) 1);// 边框的大小
        columnHeadStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 设置单元格的边框为粗体
        columnHeadStyle.setBottomBorderColor(HSSFColor.BLACK.index); // 设置单元格的边框颜色

        // 单元格字体
        HSSFFont cellFont = wb.createFont();
        cellFont.setFontName("黑体");
        cellFont.setFontHeightInPoints((short) 10);// 字体大小

        // 单元格样式
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFont(cellFont);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
        cellStyle.setLocked(true);
        cellStyle.setWrapText(true);// 自动换行
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);// 左边框的颜色
        cellStyle.setBorderLeft((short) 1);// 边框的大小
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);// 右边框的颜色
        cellStyle.setBorderRight((short) 1);// 边框的大小
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 设置单元格的边框为粗体
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index); // 设置单元格的边框颜色

        // HSSFSheet mainSheet = wb.createSheet("mainSheet");
        // HSSFSheet detailSheet = wb.createSheet("detailSheet");
        // mainSheet.setDefaultColumnWidth(20);
        HSSFSheet sheet = wb.createSheet(sheetName);
        sheet.setDefaultColumnWidth((short) 25);
        HSSFRow row = sheet.createRow(0);
        row.setHeightInPoints(25);
        // 画出title
        for (int i = 0; i < headerArr.length; i++) {
            HSSFCell headerCell = row.createCell((short) i);
            headerCell.setCellValue(headerArr[i]);
            headerCell.setCellStyle(columnHeadStyle);
        }
        // 画出数据
        for (int i = 0; i < dateList.size(); i++) {
            row = sheet.createRow(i + 1);
            row.setHeightInPoints(20);
            Map<String, Object> map = dateList.get(i);
            int j = 0;
            for (Object obj : map.values()) {
                String value = (obj == null) ? "" : String.valueOf(obj);
                HSSFCell dataCell = row.createCell((short) j);
                dataCell.setCellStyle(cellStyle);
                dataCell.setCellValue(value);
                j++;
            }
        }
        int idx = fileName.lastIndexOf(".");
        // createTempFile要求[文件名]参数的长度大于三个字符
        String name = "safe_prefix_" + fileName.substring(0, idx);
        String suffix = fileName.substring(idx);
        if (suffix.length() < 1) return null;

        File tempFile = File.createTempFile(name, suffix);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(tempFile.getAbsolutePath());
            wb.write(fos);
            fos.flush();
            fos.close();
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static List<Map<Integer, Object>> parseExcelFile(InputStream fis) throws Exception {
        Workbook workbook = WorkbookFactory.create(fis);
        Sheet sheet = workbook.getSheetAt(0);

        List<Map<Integer, Object>> dataList = new ArrayList<Map<Integer, Object>>();
        int endRowNum = sheet.getLastRowNum();
        for (int rowNum = 1; rowNum <= endRowNum; rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row == null)
                continue;
            int startCellNum = row.getFirstCellNum();
            int endCellNum = row.getLastCellNum();
            Map<Integer, Object> dataMap = new HashMap<Integer, Object>();
            for (int cellNum = startCellNum; cellNum < endCellNum; cellNum++) {
                Cell cell = row.getCell(cellNum);
                if (cell == null) {
                    continue;
                }
                cell.setCellType(Cell.CELL_TYPE_STRING);
                int type = cell.getCellType();
                switch (type) {
                    case Cell.CELL_TYPE_BLANK:// 空白单元格
                        dataMap.put(cellNum, "");
                        break;
                    case Cell.CELL_TYPE_NUMERIC:// 数字类型
                        Double value1 = cell.getNumericCellValue();
                        dataMap.put(cellNum, value1);
                        break;
                    case Cell.CELL_TYPE_STRING:// 字符类型
                        String value = cell.getStringCellValue();
                        if (StringUtils.isEmpty(value) || "null".equals(value)) {
                            dataMap.put(cellNum, "");
                        } else {
                            dataMap.put(cellNum, value);
                        }
                        break;

                    default: // 其他类型抛运行时异常
                    {
                    }

                }
            }
            dataList.add(dataMap);
        }
        return dataList;
    }

    /***
     * 读取第一个sheet文件
     * 
     * @param excelPath
     * @param decoded
     * @return
     */
    public static List<Map<Integer, String>> readFileFirstSheet(String excelPath, boolean decoded) {
        List<Map<Integer, String>> resultList = new ArrayList<Map<Integer, String>>();
        try {
            File excelFile = new File(excelPath); // 创建文件对象
            FileInputStream is = new FileInputStream(excelFile); // 文件流
            Workbook workbook = WorkbookFactory.create(is); // 这种方式 Excel
                                                            // 2003/2007/2010
                                                            // 都是可以处理的
            int sheetCount = workbook.getNumberOfSheets(); // Sheet的数量
            // 当sheet数大于0时，读取第一个sheet文件
            if (sheetCount > 0) {
                Sheet sheet = workbook.getSheetAt(0);
                List<Map<Integer, String>> dataList = new ArrayList<Map<Integer, String>>();
                int rowCount = sheet.getLastRowNum(); // 获取最后一行行数
                // 遍历每一行
                for (int r = 0; r <= rowCount; r++) {
                    Row row = sheet.getRow(r);
                    if (row == null)
                        continue;
                    Map<Integer, String> dataMap = new LinkedHashMap<Integer, String>();
                    int startCell = row.getFirstCellNum();
                    int endCell = row.getLastCellNum();
                    for (int c = startCell; c < endCell; c++) {
                        Cell cell = row.getCell(c);
                        if (cell == null)
                            continue;
                        int cellType = cell.getCellType();
                        switch (cellType) {
                            case Cell.CELL_TYPE_ERROR: {
                                dataMap.put(c, "");
                            }
                            case Cell.CELL_TYPE_BLANK:// 空白单元格
                                dataMap.put(c, "");
                                break;
                            case Cell.CELL_TYPE_STRING:// 字符类型
                                String value = cell.getStringCellValue();
                                if (StringUtils.isEmpty(value) || "null".equals(value)) {
                                    dataMap.put(c, "");
                                } else {
                                    dataMap.put(c,
                                            decoded == true ? CipherUtils.aesDecrypt(value, aesKey)
                                                    : value);
                                }
                                break;
                            case Cell.CELL_TYPE_NUMERIC:// 数字
                            {
                                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                    Date date = cell.getDateCellValue();
                                    dataMap.put(
                                            c,
                                            decoded == true ? CipherUtils.aesDecrypt(
                                                    DateUtils.formatDateTime(date), aesKey)
                                                    : DateUtils.formatDateTime(date));
                                } else {
                                    dataMap.put(
                                            c,
                                            decoded == true ? CipherUtils.aesDecrypt(
                                                    cell.getNumericCellValue() + "", aesKey) : cell
                                                    .getNumericCellValue() + "");
                                }
                                break;
                            }
                            case Cell.CELL_TYPE_FORMULA: // 公式
                            {
                                log.info("Excel第" + (r + 1) + "行第" + (c + 1) + "列数据使用了公式，提取不了数据");
                                return null;
                            }
                            default: // 其他类型抛运行时异常
                            {
                                System.out.println("Excel数据解析异常");
                                throw new Exception("Excel数据解析异常");
                            }

                        }
                    }
                    dataList.add(dataMap);
                }
                resultList.addAll(dataList);
            }
            return resultList;
        } catch (Exception e) {
            System.out.println("文件：" + excelPath + "读取异常;" + e.getMessage());
            log.error(e.getMessage(), e);
            return null;
        }
    }


    /**
     * 将处理好的内容写出到excel
     *
     * @param wb
     * @param destPath
     * @param fileName
     */
    public static void writeToExcel(Workbook wb, String destPath, String fileName) {
        // 全路径
        File fileDir = new File(destPath);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        String filePath = destPath + "/" + fileName;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            wb.write(fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 将execl中的工作空间取出来
     * 
     * @param excelPath
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbook(String excelPath) {
        try {
            File file = new File(excelPath);
            InputStream inp = new FileInputStream(file.getAbsoluteFile());
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inp);
            return hssfWorkbook;
        } catch (Exception e) {
            return null;
        }
    }

    public static void writeDataToExcel(List<Map<Integer, String>> contentList, String fileName,
            String destPath, boolean encode,String[] mainTitle) throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        // 标题字体
        HSSFFont columnHeadFont = wb.createFont();
        columnHeadFont.setFontName("宋体");
        columnHeadFont.setFontHeightInPoints((short) 10);
        columnHeadFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        // 标题样式
        HSSFCellStyle columnHeadStyle = wb.createCellStyle();
        columnHeadStyle.setFont(columnHeadFont);
        columnHeadStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
        columnHeadStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
        columnHeadStyle.setLocked(true);
        columnHeadStyle.setWrapText(false);
        columnHeadStyle.setFillBackgroundColor(HSSFColor.MAROON.index);
        columnHeadStyle.setLeftBorderColor(HSSFColor.BLACK.index);// 左边框的颜色
        columnHeadStyle.setBorderLeft((short) 1);// 边框的大小
        columnHeadStyle.setRightBorderColor(HSSFColor.BLACK.index);// 右边框的颜色
        columnHeadStyle.setBorderRight((short) 1);// 边框的大小
        columnHeadStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 设置单元格的边框为粗体
        columnHeadStyle.setBottomBorderColor(HSSFColor.BLACK.index); // 设置单元格的边框颜色

        // 单元格字体
        HSSFFont cellFont = wb.createFont();
        cellFont.setFontName("黑体");
        cellFont.setFontHeightInPoints((short) 10);// 字体大小

        // 单元格样式
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFont(cellFont);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
        cellStyle.setLocked(true);
        cellStyle.setWrapText(true);// 自动换行
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);// 左边框的颜色
        cellStyle.setBorderLeft((short) 1);// 边框的大小
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);// 右边框的颜色
        cellStyle.setBorderRight((short) 1);// 边框的大小
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 设置单元格的边框为粗体
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index); // 设置单元格的边框颜色

        HSSFSheet mainSheet = wb.createSheet("mainSheet");
        HSSFSheet detailSheet = wb.createSheet("detailSheet");
        mainSheet.setDefaultColumnWidth(20);

        // 主记录
        if (contentList != null) {
            saveDataToSheet(mainSheet, mainTitle, contentList, columnHeadStyle, cellStyle, encode);
        }
        // 全路径
        File fileDir = new File(destPath);
        if (!fileDir.exists() && !fileDir.isDirectory()) {
            fileDir.mkdir();
        }
        String filePath = destPath + "/" + fileName;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            wb.write(fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void writeToExcel(List<Map<String, Object>> dateList, String[] headerArr,
                                    String sheetName,  HttpServletResponse response) throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        // 标题字体
        HSSFFont columnHeadFont = wb.createFont();
        columnHeadFont.setFontName("宋体");
        columnHeadFont.setFontHeightInPoints((short) 10);
        columnHeadFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        // 标题样式
        HSSFCellStyle columnHeadStyle = wb.createCellStyle();
        columnHeadStyle.setFont(columnHeadFont);
        columnHeadStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
        columnHeadStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
        columnHeadStyle.setLocked(true);
        columnHeadStyle.setWrapText(false);
        columnHeadStyle.setFillBackgroundColor(HSSFColor.MAROON.index);
        columnHeadStyle.setLeftBorderColor(HSSFColor.BLACK.index);// 左边框的颜色
        columnHeadStyle.setBorderLeft((short) 1);// 边框的大小
        columnHeadStyle.setRightBorderColor(HSSFColor.BLACK.index);// 右边框的颜色
        columnHeadStyle.setBorderRight((short) 1);// 边框的大小
        columnHeadStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 设置单元格的边框为粗体
        columnHeadStyle.setBottomBorderColor(HSSFColor.BLACK.index); // 设置单元格的边框颜色

        // 单元格字体
        HSSFFont cellFont = wb.createFont();
        cellFont.setFontName("黑体");
        cellFont.setFontHeightInPoints((short) 10);// 字体大小

        // 单元格样式
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFont(cellFont);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
        cellStyle.setLocked(true);
        cellStyle.setWrapText(true);// 自动换行
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);// 左边框的颜色
        cellStyle.setBorderLeft((short) 1);// 边框的大小
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);// 右边框的颜色
        cellStyle.setBorderRight((short) 1);// 边框的大小
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 设置单元格的边框为粗体
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index); // 设置单元格的边框颜色

        // HSSFSheet mainSheet = wb.createSheet("mainSheet");
        // HSSFSheet detailSheet = wb.createSheet("detailSheet");
        // mainSheet.setDefaultColumnWidth(20);
        HSSFSheet sheet = wb.createSheet(sheetName);
        sheet.setDefaultColumnWidth((short) 25);
        HSSFRow row = sheet.createRow(0);
        row.setHeightInPoints(25);
        // 画出title
        for (int i = 0; i < headerArr.length; i++) {
            HSSFCell headerCell = row.createCell((short) i);
            headerCell.setCellValue(headerArr[i]);
            headerCell.setCellStyle(columnHeadStyle);
        }
        // 画出数据
        for (int i = 0; i < dateList.size(); i++) {
            row = sheet.createRow(i + 1);
            row.setHeightInPoints(20);
            Map<String, Object> map = dateList.get(i);
            int j = 0;
            for (Object obj : map.values()) {
                String value = (obj == null) ? "" : String.valueOf(obj);
                HSSFCell dataCell = row.createCell((short) j);
                dataCell.setCellStyle(cellStyle);
                dataCell.setCellValue(value);
                j++;
            }
        }
        try {
            response.reset();
            //设置response的Header
            response.setHeader("Content-Disposition", "attachment; filename=" + new String((sheetName + ".xls").getBytes(), "iso-8859-1"));
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            //将excel写入到输出流中
            wb.write(os);
            os.flush();
            os.close();

//            ServletOutputStream fos = response.getOutputStream();
//            wb.write(fos);
//            fos.flush();
//            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
