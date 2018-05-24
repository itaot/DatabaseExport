package me.itaot.write.word;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.poi.common.usermodel.fonts.FontFamily;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xwpf.model.XWPFParagraphDecorator;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

public class WriteWordFile {

	private static final String TABLE_HEAD_COLOR_STR = "6d9eeb";

	private static final XWPFDocument document = new XWPFDocument();

	private static FileOutputStream out = null;
	
	private static int headLineIndex = 0;

	public WriteWordFile(String fileName) throws FileNotFoundException {
		out = new FileOutputStream(new File(fileName));
	}

	public void writeTableStruct(String tableName, String comments, ResultSet rs)
			throws IOException, SQLException {
		headLineIndex++;
		//表名 2级标题
		addCustomHeadingStyle(document, "标题 2", 2);
		XWPFParagraph paragraph2 = document.createParagraph();  
        XWPFRun run2 = paragraph2.createRun();  
        run2.setText(headLineIndex + "、 " + tableName);  
        run2.setBold(true);
        run2.setFontSize(15);
        paragraph2.setStyle("标题 2");
        
        //表名注释
        XWPFParagraph paragraph = document.createParagraph();  
        XWPFRun run = paragraph.createRun();  
        run.setText(comments);

		// 表结构表格
		XWPFTable ComTable = document.createTable();

		// 表结构设置
		CTTblWidth comTableWidth = ComTable.getCTTbl().addNewTblPr().addNewTblW();
		comTableWidth.setType(STTblWidth.DXA);
		comTableWidth.setW(BigInteger.valueOf(8250));

		// 表头
		XWPFTableRow comTableRowOne = ComTable.getRow(0);
		XWPFTableCell cell = comTableRowOne.getCell(0);
		cell.setText("列名");
		cell.setColor(TABLE_HEAD_COLOR_STR);

		cell = comTableRowOne.addNewTableCell();

		cell.setText("数据类型");
		cell.setColor(TABLE_HEAD_COLOR_STR);

		cell = comTableRowOne.addNewTableCell();
		cell.setText("最大长度");
		cell.setColor(TABLE_HEAD_COLOR_STR);

		cell = comTableRowOne.addNewTableCell();
		cell.setColor(TABLE_HEAD_COLOR_STR);
		cell.setText("是否为空");

		cell = comTableRowOne.addNewTableCell();
		cell.setColor(TABLE_HEAD_COLOR_STR);
		cell.setText("注释");

		// 详细结构写入
		while (rs.next()) {
			XWPFTableRow comTableRowTwo = ComTable.createRow();
			comTableRowTwo.getCell(0).setText(rs.getString("column_name"));
			comTableRowTwo.getCell(1).setText(rs.getString("data_type"));
			comTableRowTwo.getCell(2).setText(rs.getString("data_length"));
			comTableRowTwo.getCell(3).setText(rs.getString("nullable"));
			comTableRowTwo.getCell(4).setText(rs.getString("comments"));
		}

		// 换行
		XWPFParagraph paragraph1 = document.createParagraph();
		XWPFRun paragraphRun1 = paragraph1.createRun();
		paragraphRun1.setText("\r\r");

	}

	public static void flush() throws IOException {
		document.write(out);
		out.close();
		System.out.println("create_table document written success.");
	}
	
	/** 
     * 增加自定义标题样式。这里用的是stackoverflow的源码 
     *  
     * @param docxDocument 目标文档 
     * @param strStyleId 样式名称 
     * @param headingLevel 样式级别 
     */  
    private static void addCustomHeadingStyle(XWPFDocument docxDocument, String strStyleId, int headingLevel) {  
  
        CTStyle ctStyle = CTStyle.Factory.newInstance();  
        ctStyle.setStyleId(strStyleId);  
  
        CTString styleName = CTString.Factory.newInstance();  
        styleName.setVal(strStyleId);  
        ctStyle.setName(styleName);  
  
        CTDecimalNumber indentNumber = CTDecimalNumber.Factory.newInstance();  
        indentNumber.setVal(BigInteger.valueOf(headingLevel));  
  
        // lower number > style is more prominent in the formats bar  
        ctStyle.setUiPriority(indentNumber);  
  
        CTOnOff onoffnull = CTOnOff.Factory.newInstance();  
        ctStyle.setUnhideWhenUsed(onoffnull);  
  
        // style shows up in the formats bar  
        ctStyle.setQFormat(onoffnull);  
  
        // style defines a heading of the given level  
        CTPPr ppr = CTPPr.Factory.newInstance();  
        ppr.setOutlineLvl(indentNumber);  
        ctStyle.setPPr(ppr);  
  
        XWPFStyle style = new XWPFStyle(ctStyle);  
  
        // is a null op if already defined  
        XWPFStyles styles = docxDocument.createStyles();  
  
        style.setType(STStyleType.PARAGRAPH);  
        styles.addStyle(style);  
  
    }  
}