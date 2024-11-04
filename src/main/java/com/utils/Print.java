package main.java.com.utils;

import main.java.com.constant.Constant;
import main.java.com.result.Result;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;


/**
 * 结果输出到Excel中
 *
 */
public class Print {
//	static DecimalFormat df = new DecimalFormat("#.00");

	public static int getRandomNumber(int min, int max){
		Random random = new Random();
		return (random.nextInt(max - min + 1) + min);
	}

	public static void main(String[] args) throws IOException {

		for (int i = 0; i < 20; i++) {
			System.out.println(getRandomNumber(1, 2));

		}
	}

	/**
	 * 算法比较
	 * @param results
	 * @throws IOException
	 */
	public static void exportToExcel_Node(List<Result> results) throws IOException {
//		df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));

		// **创建工作簿
		HSSFWorkbook wb = new HSSFWorkbook();

		// 1、创建工作表
		HSSFSheet sheet = wb.createSheet("执行数据");
		for (int i = 0; i < 16; i++) {
			// 设置列宽
			sheet.setColumnWidth(i, 3000);
		}

		// 2、标题样式
		// 创建单元格样式
		HSSFCellStyle cellStyle = wb.createCellStyle();
		// 设置单元格的背景颜色为green
		cellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		// 设置单元格居中对齐
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		// 设置单元格垂直居中对齐
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		// 创建单元格内容显示不下时自动换行
		cellStyle.setWrapText(true);

		// 3、设置单元格字体样式
		HSSFFont font = wb.createFont();
		// 设置字体加粗
		font.setBold(true);
		font.setFontName("宋体");
		font.setFontHeight((short) 200);
		cellStyle.setFont(font);
		// 设置单元格边框为细线条
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);

		HSSFRow row1 = sheet.createRow(0);
		// 标题信息
//		String[] titles = { "Vehicle Number","Average Task Number", "Task Data Size",
//				" Algorithm", "Mean Completion Time","RPD","Runtime" };
//		for (int i = 0; i < 7; i++) {
//			HSSFCell cell1 = row1.createCell(i);
//			cell1.setCellValue(titles[i]);
//		}
		String[] titles = { "Iteration","Edge Num", "VM Num","Task Num","Algorithm","OTSS","OD","RA",
				"Mean Completion Time","totalCompletionTime","successTaskNum",
				"Successful Task Ratio","RPD","Runtime","Data Size Range","Deadline Range"  };

		for (int i = 0; i < 16; i++) {
			HSSFCell cell1 = row1.createCell(i);
			cell1.setCellValue(titles[i]);
		}

		/// 4、内容样式
		// 创建单元格样式
		HSSFCellStyle cellStyle2 = wb.createCellStyle();
		// 设置单元格居中对齐
		cellStyle2.setAlignment(HorizontalAlignment.CENTER);
		// 设置单元格垂直居中对齐
		cellStyle2.setVerticalAlignment(VerticalAlignment.CENTER);
		// 创建单元格内容显示不下时自动换行
		cellStyle2.setWrapText(true);

		// 设置单元格字体样式
		HSSFFont font2 = wb.createFont();
		// 设置字体加粗
		font2.setBold(true);
		font2.setFontName("宋体");
		font2.setFontHeight((short) 200);
		cellStyle2.setFont(font2);
		// 设置单元格边框为细线条
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		// 循环赋值
		for (int i = 0; i < results.size(); i++) {
			HSSFRow row2 = sheet.createRow(i + 1);
			Result result = results.get(i);
			HSSFCell cell0 = row2.createCell(0);
			HSSFCell cell1 = row2.createCell(1);
			HSSFCell cell2 = row2.createCell(2);
			HSSFCell cell3 = row2.createCell(3);
			HSSFCell cell4 = row2.createCell(4);
			HSSFCell cell5 = row2.createCell(5);
			HSSFCell cell6 = row2.createCell(6);
			HSSFCell cell7 = row2.createCell(7);
			HSSFCell cell8 = row2.createCell(8);
			HSSFCell cell9 = row2.createCell(9);
			HSSFCell cell10 = row2.createCell(10);
			HSSFCell cell11 = row2.createCell(11);
			HSSFCell cell12 = row2.createCell(12);
			HSSFCell cell13 = row2.createCell(13);
			HSSFCell cell14 = row2.createCell(14);
			HSSFCell cell15 = row2.createCell(15);

			cell0.setCellValue(result.iteration);
			cell1.setCellValue(result.edgeNum);
			cell2.setCellValue(result.vmNum);
			cell3.setCellValue(result.taskNum);
			cell4.setCellValue(result.Algorithm);
			cell5.setCellValue(result.OTSS);
			cell6.setCellValue(result.OD);
			cell7.setCellValue(result.RA);
//			cell8.setCellValue(Double.parseDouble(df.format(result.meanCompletionTime)));
//			cell9.setCellValue(Double.parseDouble(df.format(result.totalCompletionTime)));
			cell8.setCellValue(result.meanCompletionTime);
			cell9.setCellValue((result.totalCompletionTime));
			cell10.setCellValue(result.successTaskNum);
			cell11.setCellValue(result.successTaskRatio);
			cell12.setCellValue(result.RPD);
//			cell13.setCellValue(Double.parseDouble(df.format(result.runtime)));
			cell13.setCellValue(result.runtime);
			cell14.setCellValue(result.taskDataSizeRange);
			cell15.setCellValue(result.deadlineRange);
			// for(int j = 0; j < 9; j++){
//			HSSFCell cell0 = row2.createCell(0);
//			HSSFCell cell1 = row2.createCell(1);
//			// HSSFCell cell2 = row2.createCell(2);
//			HSSFCell cell3 = row2.createCell(2);
//			HSSFCell cell4 = row2.createCell(3);
//			HSSFCell cell5 = row2.createCell(4);
//			HSSFCell cell6 = row2.createCell(5);
//			HSSFCell cell7 = row2.createCell(6);
//
//			cell0.setCellValue(result.vehicleNumber);
//
//			cell1.setCellValue(result.averageTaskNumber);
//			// cell2.setCellValue(Arrays.toString(result.taskRatio));
//			cell3.setCellValue(Arrays.toString(result.taskDataSize));
//			cell4.setCellValue(result.Algorithm);
//			cell5.setCellValue(result.meanCompletionTime);
//			cell6.setCellValue(result.RPD);
//			cell7.setCellValue(Double.parseDouble(df.format(result.runtime)));
		}

		// 生成目录
		File fileDir = new File(Constant.ALGORITHM_COMPARATION_DIR);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		Date dNow = new Date( );
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH_mm_ss");
		String fileName2 = fileDir.getAbsolutePath() + "/算法比较_" + ft.format(dNow) + "_.xls";
		System.out.println("filename: "+fileName2);

//		String fileName2 = fileDir.getAbsolutePath() + "/算法比较.xls";
		File file = new File(fileName2);
		if (!file.exists()) {
			file.createNewFile();
		}
		// 保存Excel文件
		FileOutputStream fileOut = new FileOutputStream(file);
		wb.write(fileOut);
		fileOut.close();
		wb.close();
	}

	/**
	 * 参数校正
	 * @param results
	 * @param fileName
	 * @throws IOException
	 */
	public static void exportToExcel_TS(List<Result> results, String fileName) throws IOException {
		// **创建工作簿
		HSSFWorkbook wb = new HSSFWorkbook();
		// 1、创建工作表
		HSSFSheet sheet = wb.createSheet("执行数据");
		for (int i = 0; i < 14; i++) {
			// 设置列宽
			sheet.setColumnWidth(i, 3000);
		}
		// 2、标题样式

		// 创建单元格样式
		HSSFCellStyle cellStyle = wb.createCellStyle();
		// 设置单元格的背景颜色为green
		cellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		// 设置单元格居中对齐
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		// 设置单元格垂直居中对齐
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		// 创建单元格内容显示不下时自动换行
		cellStyle.setWrapText(true);

		// 3、设置单元格字体样式
		HSSFFont font = wb.createFont();
		// 设置字体加粗
		font.setBold(true);
		font.setFontName("宋体");
		font.setFontHeight((short) 200);
		cellStyle.setFont(font);
		// 设置单元格边框为细线条
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);


		HSSFRow row1 = sheet.createRow(0);
		// 标题信息
		String[] titles = { "Iteration","Edge Num", "VM Num","Task Num","Algorithm","OTSS","OD","RA",
				"Mean Completion Time","totalCompletionTime","successTaskNum",
				"Successful Task Ratio","RPD","Runtime"};

		for (int i = 0; i < 14; i++) {
			HSSFCell cell1 = row1.createCell(i);
			cell1.setCellValue(titles[i]);
		}

		/// 4、内容样式
		// 创建单元格样式
		HSSFCellStyle cellStyle2 = wb.createCellStyle();
		// 设置单元格居中对齐
		cellStyle2.setAlignment(HorizontalAlignment.CENTER);
		// 设置单元格垂直居中对齐
		cellStyle2.setVerticalAlignment(VerticalAlignment.CENTER);
		// 创建单元格内容显示不下时自动换行
		cellStyle2.setWrapText(true);

		// 设置单元格字体样式
		HSSFFont font2 = wb.createFont();
		// 设置字体加粗
		font2.setBold(true);
		font2.setFontName("宋体");
		font2.setFontHeight((short) 200);
		cellStyle2.setFont(font2);
		// 设置单元格边框为细线条
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		// 循环赋值
		for (int i = 0; i < results.size(); i++) {
			HSSFRow row2 = sheet.createRow(i + 1);
			Result result = results.get(i);
			HSSFCell cell0 = row2.createCell(0);
			HSSFCell cell1 = row2.createCell(1);
			HSSFCell cell2 = row2.createCell(2);
			HSSFCell cell3 = row2.createCell(3);
			HSSFCell cell4 = row2.createCell(4);
			HSSFCell cell5 = row2.createCell(5);
			HSSFCell cell6 = row2.createCell(6);
			HSSFCell cell7 = row2.createCell(7);
			HSSFCell cell8 = row2.createCell(8);
			HSSFCell cell9 = row2.createCell(9);
			HSSFCell cell10 = row2.createCell(10);
			HSSFCell cell11 = row2.createCell(11);
			HSSFCell cell12 = row2.createCell(12);
			HSSFCell cell13 = row2.createCell(13);


			cell0.setCellValue(result.iteration);
			cell1.setCellValue(result.edgeNum);
			cell2.setCellValue(result.vmNum);
			cell3.setCellValue(result.taskNum);
			cell4.setCellValue(result.Algorithm);
			cell5.setCellValue(result.OTSS);
			cell6.setCellValue(result.OD);
			cell7.setCellValue(result.RA);
//			cell8.setCellValue(Double.parseDouble(df.format(result.meanCompletionTime)));
//			cell9.setCellValue(Double.parseDouble(df.format(result.totalCompletionTime)));
			cell8.setCellValue(result.meanCompletionTime);
			cell9.setCellValue(result.totalCompletionTime);
			cell10.setCellValue(result.successTaskNum);
			cell11.setCellValue(result.successTaskRatio);
			cell12.setCellValue(result.RPD);
			cell13.setCellValue(result.runtime);

		}

		// 生成目录
		File fileDir = new File(Constant.PARAMS_CALIBRATION_DIR);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		Date dNow = new Date( );
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh_mm_ss");
		String fileName2 = fileDir.getAbsolutePath() + "/" + fileName + ft.format(dNow) + "_.xls";
//		String fileName2 = fileDir.getAbsolutePath() + "/" + fileName + ".xls";
		File file = new File(fileName2);
		if (!file.exists()) {
			file.createNewFile();
		}

		// 保存Excel文件
		FileOutputStream fileOut = new FileOutputStream(file);
		wb.write(fileOut);
		fileOut.close();
		wb.close();
	}


/*
	public void exportToExcel_Node(List<Result> results, String workflowName) throws IOException {		// **创建工作簿

		HSSFWorkbook wb = new HSSFWorkbook();

		// 1、创建工作表
		HSSFSheet sheet = wb.createSheet("执行数据");
		for (int i = 0; i < 9; i++) {
			// 设置列宽
			sheet.setColumnWidth(i, 3000);
		}

		// 2、标题样式

		// 创建单元格样式
		HSSFCellStyle cellStyle = wb.createCellStyle();
		// 设置单元格的背景颜色为green
		cellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		// 设置单元格居中对齐
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		// 设置单元格垂直居中对齐
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		// 创建单元格内容显示不下时自动换行
		cellStyle.setWrapText(true);

		// 3、设置单元格字体样式
		HSSFFont font = wb.createFont();
		// 设置字体加粗
		font.setBold(true);
		font.setFontName("宋体");
		font.setFontHeight((short) 200);
		cellStyle.setFont(font);
		// 设置单元格边框为细线条
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);

		HSSFRow row1 = sheet.createRow(0);
		// 标题信息
		String[] titles = { "Device Number","Average Task Number", "Task Ratio",
				" Algorithm", "Mean Completion Time","RPD","Runtime" };
		for (int i = 0; i < 8; i++) {
			HSSFCell cell1 = row1.createCell(i);
			cell1.setCellValue(titles[i]);
		}

		/// 4、内容样式
		// 创建单元格样式
		HSSFCellStyle cellStyle2 = wb.createCellStyle();
		// 设置单元格居中对齐
		cellStyle2.setAlignment(HorizontalAlignment.CENTER);
		// 设置单元格垂直居中对齐
		cellStyle2.setVerticalAlignment(VerticalAlignment.CENTER);
		// 创建单元格内容显示不下时自动换行
		cellStyle2.setWrapText(true);

		// 设置单元格字体样式
		HSSFFont font2 = wb.createFont();
		// 设置字体加粗
		font2.setBold(true);
		font2.setFontName("宋体");
		font2.setFontHeight((short) 200);
		cellStyle2.setFont(font2);
		// 设置单元格边框为细线条
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);

		// 循环赋值
		for (int i = 0; i < results.size(); i++) {
			HSSFRow row2 = sheet.createRow(i + 1);
			Result result = results.get(i);
			HSSFCell cell0 = row2.createCell(0);
			HSSFCell cell1 = row2.createCell(1);
			HSSFCell cell2 = row2.createCell(2);
			HSSFCell cell3 = row2.createCell(3);
			HSSFCell cell4 = row2.createCell(4);
			HSSFCell cell5 = row2.createCell(5);
			HSSFCell cell6 = row2.createCell(6);
			HSSFCell cell7 = row2.createCell(7);

			cell0.setCellValue(result.deviceNumber);

			cell1.setCellValue(result.averageTaskNumber);
			cell2.setCellValue(Arrays.toString(result.taskRatio));
			cell3.setCellValue(result.Algorithm);
			cell4.setCellValue(result.meanCompletionTime);
			cell5.setCellValue(result.RPD);
			cell6.setCellValue(Double.parseDouble(df.format(result.runtime)));
		}
		}

		// 生成目录
		File fileDir = new File(Constant.ALGORITHM_COMPARATION_DIR);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		String fileName2 = fileDir.getAbsolutePath() + "/算法比较"+workflowName+".xls";
		File file = new File(fileName2);
		if (!file.exists()) {
			file.createNewFile();
		}
		// 保存Excel文件
		FileOutputStream fileOut = new FileOutputStream(file);
		wb.write(fileOut);
		fileOut.close();
		wb.close();

	}
 */
}


