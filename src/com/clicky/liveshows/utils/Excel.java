package com.clicky.liveshows.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.CellValue;
import jxl.write.biff.RowsExceededException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class Excel {
	
	private Context context;
	
	public Excel(Context context){
		this.context = context;
	}
	
	/**
	 * 
	 * @param fileName - the name to give the new workbook file
	 * @return - a new WritableWorkbook with the given fileName 
	 */
	public WritableWorkbook createWorkbook(String fileName){
	    //exports must use a temp file while writing to avoid memory hogging
	    WorkbookSettings wbSettings = new WorkbookSettings(); 				
	    wbSettings.setUseTemporaryFileDuringWrite(true);   
	 
	    //get the sdcard's directory
	    File sdCard = Environment.getExternalStorageDirectory();
	    //add on the your app's path
	    File dir = new File(sdCard.getAbsolutePath() + "/MerchSys");
	    //make them in case they're not there
	    dir.mkdirs();
	    //create a standard java.io.File object for the Workbook to use
	    File wbfile = new File(dir,fileName);
	 
	    WritableWorkbook wb = null;
	 
	    try{
		//create a new WritableWorkbook using the java.io.File and
		//WorkbookSettings from above
		wb = Workbook.createWorkbook(wbfile,wbSettings); 
	    }catch(IOException ex){
	    	Log.e("EXCEL",ex.getStackTrace().toString());
	    	Log.e("EXCEL", ex.getMessage());
	    }
	 
	    return wb;	
	}
	
	/**
	 * 
	 * @param wb - WritableWorkbook to create new sheet in
	 * @param sheetName - name to be given to new sheet
	 * @param sheetIndex - position in sheet tabs at bottom of workbook
	 * @return - a new WritableSheet in given WritableWorkbook
	 */
	public WritableSheet createSheet(WritableWorkbook wb, 
	    String sheetName, int sheetIndex){
	    //create a new WritableSheet and return it
	    return wb.createSheet(sheetName, sheetIndex);
	}
	
	/**
	 * 
	 * @param columnPosition - column to place new cell in
	 * @param rowPosition - row to place new cell in
	 * @param contents - string value to place in cell
	 * @param headerCell - whether to give this cell special formatting
	 * @param sheet - WritableSheet to place cell in
	 * @throws RowsExceededException - thrown if adding cell exceeds .xls row limit
	 * @throws WriteException - Idunno, might be thrown
	 */
	public void writeCell(int columnPosition, int rowPosition, String contents, int headerCell,
	  	WritableSheet sheet) throws RowsExceededException, WriteException{
	    //create a new cell with contents at position
		CellValue newCell;
		if(headerCell != 5 && headerCell != 9)
			newCell = new Label(columnPosition,rowPosition,contents);
		else
			newCell = new Number(columnPosition, rowPosition, Double.parseDouble(contents));
	 
	    WritableFont headerFont;
	    WritableCellFormat headerFormat;
	    switch(headerCell){
	    	case 0:
		    	//give header cells size 10 Arial bolded 	
		    	headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
		    	headerFormat = new WritableCellFormat(headerFont);
		    	//center align the cells' contents
		    	headerFormat.setWrap(true);
		        newCell.setCellFormat(headerFormat);
		        break;
	    	case 1:
		    	//give header cells size 10 Arial bolded 	
		    	headerFont = new WritableFont(WritableFont.ARIAL, 8, WritableFont.NO_BOLD);
		    	headerFormat = new WritableCellFormat(headerFont);
		    	headerFormat.setWrap(true);
		        newCell.setCellFormat(headerFormat);
		        break;
	        case 2:
		    	//give header cells size 10 Arial bolded 	
		    	headerFont = new WritableFont(WritableFont.ARIAL, 8, WritableFont.NO_BOLD);
		    	headerFormat = new WritableCellFormat(headerFont);
		    	headerFormat.setWrap(true);
		    	headerFormat.setAlignment(Alignment.CENTRE);
		        newCell.setCellFormat(headerFormat);
		        break;  
	        case 3:
		    	//give header cells size 10 Arial bolded 	
		    	headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
		    	headerFormat = new WritableCellFormat(headerFont);
		    	//center align the cells' contents
		    	headerFormat.setWrap(true);
		    	headerFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
		        newCell.setCellFormat(headerFormat);
		        break;
	        case 4:
		    	//give header cells size 10 Arial bolded 	
		    	headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
		    	headerFormat = new WritableCellFormat(headerFont);
		    	//center align the cells' contents
		    	headerFormat.setWrap(true);
		    	headerFormat.setAlignment(Alignment.RIGHT);
		    	headerFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
		        newCell.setCellFormat(headerFormat);
		        break;
	        case 5:
		    	//give header cells size 10 Arial bolded 	
	        	NumberFormat currencyFormat = new NumberFormat(NumberFormat.CURRENCY_DOLLAR + " ###,###.00", NumberFormat.COMPLEX_FORMAT);
		    	headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
		    	headerFormat = new WritableCellFormat(currencyFormat);
		    	//center align the cells' contents
		    	headerFormat.setFont(headerFont);
		    	headerFormat.setWrap(true);
		    	headerFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
		    	headerFormat.setAlignment(Alignment.RIGHT);
		        newCell.setCellFormat(headerFormat);
		        break;
	        case 6:
		    	//give header cells size 10 Arial bolded 	
		    	headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
		    	headerFormat = new WritableCellFormat(headerFont);
		    	//center align the cells' contents
		    	headerFormat.setWrap(true);
		    	headerFormat.setBorder(Border.ALL, BorderLineStyle.THICK);
		        newCell.setCellFormat(headerFormat);
		        break; 
	        case 7:
		    	//give header cells size 10 Arial bolded 	
		    	headerFont = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
		    	headerFormat = new WritableCellFormat(headerFont);
		    	//center align the cells' contents
		    	headerFormat.setWrap(true);
		        newCell.setCellFormat(headerFormat);
		        break; 
	        case 8:
		    	//give header cells size 10 Arial bolded 	
		    	headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
		    	headerFormat = new WritableCellFormat(headerFont);
		    	headerFormat.setWrap(true);
		    	headerFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
		    	headerFormat.setAlignment(Alignment.CENTRE);
		        newCell.setCellFormat(headerFormat);
		        break;
	        case 9:
		    	//give header cells size 10 Arial bolded 	
		    	headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
		    	headerFormat = new WritableCellFormat(NumberFormats.PERCENT_FLOAT);
		    	//center align the cells' contents
		    	headerFormat.setFont(headerFont);
		    	headerFormat.setWrap(true);
		    	headerFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
		    	headerFormat.setAlignment(Alignment.RIGHT);
		        newCell.setCellFormat(headerFormat);
		        break;
	        case 10:
		    	//give header cells size 10 Arial bolded 	
		    	headerFont = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
		    	headerFormat = new WritableCellFormat(headerFont);
		    	headerFormat.setWrap(true);
		    	headerFormat.setAlignment(Alignment.CENTRE);
		        newCell.setCellFormat(headerFormat);
		        break;
		        
	    }
	 
	    sheet.addCell(newCell);
	}
	
	public void addImage(WritableSheet sheet,int imageResource){
		Bitmap bmp= BitmapFactory.decodeResource(context.getResources(),imageResource);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		sheet.addImage(new WritableImage(0, 0, 280, 116, byteArray));
	}
	
	public void sheetAutoFitColumns(WritableSheet sheet) {
	    for (int i = 0; i < sheet.getColumns(); i++) {
	        Cell[] cells = sheet.getColumn(i);
	        int longestStrLen = -1;

	        if (cells.length == 0)
	            continue;

	        /* Find the widest cell in the column. */
	        for (int j = 0; j < cells.length; j++) {
	            if ( cells[j].getContents().length() > longestStrLen ) {
	                String str = cells[j].getContents();
	                if (str == null || str.isEmpty())
	                    continue;
	                longestStrLen = str.trim().length();
	            }
	        }

	        /* If not found, skip the column. */
	        if (longestStrLen == -1) 
	            continue;

	        /* If wider than the max width, crop width */
	        if (longestStrLen > 255)
	            longestStrLen = 255;

	        CellView cv = sheet.getColumnView(i);
	        cv.setSize(longestStrLen * 256 + 100); /* Every character is 256 units wide, so scale it. */
	        sheet.setColumnView(i, cv);
	    }
	}
	
	public void sheetAutoFitRows(WritableSheet sheet) {
	    for (int i = 0; i < sheet.getRows(); i++) {
	    	sheet.getRowView(i).setAutosize(true);
	    }
	}

}
