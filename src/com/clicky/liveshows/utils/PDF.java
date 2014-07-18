package com.clicky.liveshows.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class PDF{
	
	Context context;
	private BaseFont bfBold;
	PdfWriter docWriter;
	PdfContentByte cb;
	
	public PDF(Context context) {
		this.context = context;
		initializeFonts();
	}
	
	public void addImage(float x, float y, Document document){
		//the company logo is stored in the assets which is read only
		//get the logo and print on the document
		try {
			InputStream inputStream = context.getAssets().open("live_shows_logo.png");
			Bitmap bmp = BitmapFactory.decodeStream(inputStream);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
			Image companyLogo = Image.getInstance(stream.toByteArray());
			companyLogo.setAbsolutePosition(x,y);
			companyLogo.scalePercent(50);
			document.add(companyLogo); 
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BadElementException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	public void createHeadings(float x, float y, int size, String text){
		cb.beginText();
		cb.setFontAndSize(bfBold, size);
		cb.setTextMatrix(x,y);
		cb.showText(text.trim());
		cb.endText(); 
	}
	
	public void addLine(float x, float y){
		cb.moveTo(x, y);
		cb.lineTo(x+200, y);
		cb.stroke();
	}
	
	public void tableVentas(Document document, List<Product> listProd, float y){
		//list all the products sold to the customer
		float[] columnWidths = {1f, 0.25f, 1f, 1f, 0.5f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f};
		//create PDF table with the given widths
		PdfPTable table = new PdfPTable(columnWidths);
		// set table width a percentage of the page width
		table.setTotalWidth(980f);
		
		Font font = FontFactory.getFont(FontFactory.HELVETICA,8);
		font.setColor(BaseColor.WHITE);
		
		BaseColor azul = WebColors.getRGBColor("#347af0");
		
		PdfPCell cell = new PdfPCell(new Phrase("PRICE SALES IN US",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("#",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("ITEM",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("STYLE",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("SIZE",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("INITIAL\nINVENTORY",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("ADDING 1",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("ADDING 2",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("ADDING 3",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("ADDING 4",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("ADDING 5",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("TOTAL\nINVENTORY",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("PRICE SALE",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("PRICE SALE",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("PRICE SALE",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("PRICE SALE",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("PRICE SALE",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("PRICE SALE",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("PRICE SALE",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("PRICE SALE",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("PRICE SALE",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("PRICE SALE",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("PRICE SALE",font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBackgroundColor(azul);
		table.addCell(cell);
		table.setHeaderRows(1);
		
		table.writeSelectedRows(0, -1, 10, y, docWriter.getDirectContent());
	}
	
	public void tableProducts(Document document, List<Product> listProd, float y){
		//list all the products sold to the customer
		float[] columnWidths = {1f, 1f, 1f, 1f, 1f};
		//create PDF table with the given widths
		PdfPTable table = new PdfPTable(columnWidths);
		// set table width a percentage of the page width
		table.setTotalWidth(500f);
		
		PdfPCell cell = new PdfPCell(new Phrase("Cantidad"));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Producto"));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Artista"));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Talla"));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Precio"));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		table.setHeaderRows(1);
		 
		DecimalFormat df = new DecimalFormat("$#.00");
		for(Product prod : listProd){
			table.addCell(String.valueOf(prod.getCantidadStand()));
			table.addCell(prod.getNombre());
			table.addCell(prod.getArtista());
			if(!prod.getTalla().equals("") && prod.getTalla() != null)
				table.addCell(prod.getTalla());
			else
				table.addCell("N/A");
			table.addCell(df.format(Double.parseDouble(prod.getPrecio())));
		}
		//absolute location to print the PDF table from 
		table.writeSelectedRows(0, -1, document.leftMargin(), y, docWriter.getDirectContent());
	}
	
	public Document createPDF(String fileName){
		
		Document document = new Document(PageSize.A4);
		//get the sdcard's directory
	    File sdCard = Environment.getExternalStorageDirectory();
	    //add on the your app's path
	    File dir = new File(sdCard.getAbsolutePath() + "/MerchSys");
	    //make them in case they're not there
	    dir.mkdirs();
	    //create a standard java.io.File object for the Workbook to use
	    File pdffile = new File(dir,fileName);
		  try {
		    
		   docWriter = PdfWriter.getInstance(document, new FileOutputStream(pdffile));
		   document.open();
		   
		   cb = docWriter.getDirectContent();
		 
		  } 
		  catch(Exception e){
			  e.printStackTrace();
		  }
		  return document;
	}
	
	public Document createPDFHorizontal(String fileName){
		
		Document document = new Document(PageSize.LEGAL.rotate());
		//get the sdcard's directory
	    File sdCard = Environment.getExternalStorageDirectory();
	    //add on the your app's path
	    File dir = new File(sdCard.getAbsolutePath() + "/MerchSys");
	    //make them in case they're not there
	    dir.mkdirs();
	    //create a standard java.io.File object for the Workbook to use
	    File pdffile = new File(dir,fileName); 
		  try {
		    
		   docWriter = PdfWriter.getInstance(document, new FileOutputStream(pdffile));
		   document.open();
		   
		   cb = docWriter.getDirectContent();
		 
		  } 
		  catch(Exception e){
			  e.printStackTrace();
		  }
		  return document;
	}
	
	private void initializeFonts(){
		try {
			bfBold = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
