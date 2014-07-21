package com.clicky.liveshows.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceManager;

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
	
	public void tableVentas(Document document, List<Product> listProd, String[] headers, double totalVenta, float y){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Float priceUs = Float.parseFloat(prefs.getString("divisa", "0"));
		//list all the products sold to the customer
		float[] columnWidths = {1f, 0.25f, 1f, 1f, 0.5f, 1f, 1f, 0.7f, 0.65f, 1.1f, 0.6f, 0.6f, 1.1f, 0.75f, 0.8f, 0.9f, 0.9f};
		//create PDF table with the given widths
		PdfPTable table = new PdfPTable(columnWidths);
		// set table width a percentage of the page width
		table.setTotalWidth(980f);
		
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD,10);
		Font fontTexto = FontFactory.getFont(FontFactory.HELVETICA,8);
		font.setColor(BaseColor.WHITE);
		BaseColor azul = WebColors.getRGBColor("#347af0");
		
		for(int i = 0; i < headers.length; i++){
			PdfPCell cell = new PdfPCell(new Phrase(headers[i],font));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
			cell.setBackgroundColor(azul);
			table.addCell(cell);
		}
		table.setHeaderRows(1);
		
		DecimalFormat dinero = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.US);
		dinero.applyPattern("$#,###.00");
		DecimalFormat porcentaje = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.US);
		porcentaje.applyPattern("##.00%");
		for(int i = 0; i < listProd.size(); i++){
			Product prod = listProd.get(i);
			double precio = Double.parseDouble(prod.getPrecio());
			PdfPCell cell = new PdfPCell(new Phrase(dinero.format(precio / priceUs),fontTexto));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			
			cell.setPhrase(new Phrase(String.valueOf((i+1)),fontTexto));
			table.addCell(cell);
			cell.setPhrase(new Phrase(prod.getTipo(),fontTexto));
			table.addCell(cell);
			cell.setPhrase(new Phrase(prod.getNombre(),fontTexto));
			table.addCell(cell);
			if(!prod.getTalla().equals("") && prod.getTalla() != null){
				cell.setPhrase(new Phrase(prod.getTalla(),fontTexto));
			}else{
				cell.setPhrase(new Phrase("N/A",fontTexto));
			}
			table.addCell(cell);
			
			int cmv = 0, cmo = 0, cmd = 0, cmo1 = 0, cmo2 = 0;
			for(Cortesias cort : prod.getCortesias()){
				if(cort.getTipo().equals("DAMAGE")){
					cmd += cort.getAmount();
				}else if(cort.getTipo().equals("COMPS VENUE")){
					cmv += cort.getAmount();
				}else if(cort.getTipo().equals("COMPS OFFICE PRODUCTION")){
					cmo += cort.getAmount();
				}else if(cort.getTipo().equals(prefs.getString("op1", "OTHER"))){
					cmo1 += cort.getAmount();
				}else if(cort.getTipo().equals(prefs.getString("op2", "OTHER"))){
					cmo2 += cort.getAmount();
				}
			}
			
			int finalInventory = prod.getTotalCantidad()-(prod.getProdNo()+cmd+cmv+cmo+cmo1+cmo2);
			
			cell.setPhrase(new Phrase(String.valueOf(prod.getTotalCantidad()),fontTexto));
			table.addCell(cell);
			cell.setPhrase(new Phrase(dinero.format(Double.parseDouble(prod.getPrecio())),fontTexto));
			table.addCell(cell);
			
			cell.setPhrase(new Phrase(String.valueOf(cmd),fontTexto));
			table.addCell(cell);
			cell.setPhrase(new Phrase(String.valueOf(cmv),fontTexto));
			table.addCell(cell);
			cell.setPhrase(new Phrase(String.valueOf(cmo),fontTexto));
			table.addCell(cell);
			cell.setPhrase(new Phrase(String.valueOf(cmo1),fontTexto));
			table.addCell(cell);
			cell.setPhrase(new Phrase(String.valueOf(cmo2),fontTexto));
			table.addCell(cell);
			
			cell.setPhrase(new Phrase(String.valueOf(finalInventory),fontTexto));
			table.addCell(cell);
			
			double gross = Double.parseDouble(prod.getPrecio())* prod.getProdNo();
			cell.setPhrase(new Phrase(String.valueOf(prod.getProdNo()),fontTexto));
			table.addCell(cell);
			cell.setPhrase(new Phrase(dinero.format(gross),fontTexto));
			table.addCell(cell);
			cell.setPhrase(new Phrase(porcentaje.format(gross/totalVenta),fontTexto));
			table.addCell(cell);
			
			cell.setPhrase(new Phrase(dinero.format(gross/priceUs),fontTexto));
			table.addCell(cell);
			
		}
		
		table.writeSelectedRows(0, -1, 10, y, docWriter.getDirectContent());
	}
	
	public void tableStandVentas(Document document, List<Product> listProd, String[] headers, double total, float y){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Float priceUs = Float.parseFloat(prefs.getString("divisa", "0"));
		
		float[] columnWidths = {1f, 0.25f, 1f, 1f, 0.5f, 1f, 1f, 0.7f, 0.65f, 1.1f, 0.6f, 0.6f, 1.1f, 0.75f, 0.8f, 0.9f, 1.0f, 1.0f};
		PdfPTable table = new PdfPTable(columnWidths);
		// set table width a percentage of the page width
		table.setTotalWidth(940f);
		
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD,10);
		Font fontTexto = FontFactory.getFont(FontFactory.HELVETICA,8);
		font.setColor(BaseColor.WHITE);
		BaseColor azul = WebColors.getRGBColor("#347af0");
		
		for(int i = 0; i < headers.length; i++){
			PdfPCell cell = new PdfPCell(new Phrase(headers[i],font));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
			cell.setBackgroundColor(azul);
			table.addCell(cell);
		}
		table.setHeaderRows(1);
		
		DecimalFormat dinero = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.US);
		dinero.applyPattern("$#,###.00");
		DecimalFormat porcentaje = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.US);
		porcentaje.applyPattern("##.00%");
		
		for(int i = 0; i < listProd.size(); i++){
			Product prod = listProd.get(i);
			Comisiones vendedor = prod.getComisiones().get(0);
			double precio = Double.parseDouble(prod.getPrecio());
			PdfPCell cell = new PdfPCell(new Phrase(dinero.format(precio / priceUs),fontTexto));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			
			cell.setPhrase(new Phrase(String.valueOf(i),fontTexto));
			table.addCell(cell);
			cell.setPhrase(new Phrase(prod.getTipo(),fontTexto));
			table.addCell(cell);
			cell.setPhrase(new Phrase(prod.getNombre(),fontTexto));
			table.addCell(cell);
			if(!prod.getTalla().equals("") && prod.getTalla() != null){
				cell.setPhrase(new Phrase(prod.getTalla(),fontTexto));
			}else{
				cell.setPhrase(new Phrase("N/A",fontTexto));
			}
			table.addCell(cell);
			
			int cmv = 0, cmo = 0, cmd = 0, cmo1 = 0, cmo2 = 0;
			for(Cortesias cort : prod.getCortesias()){
				if(cort.getTipo().equals("DAMAGE")){
					cmd += cort.getAmount();
				}else if(cort.getTipo().equals("COMPS VENUE")){
					cmv += cort.getAmount();
				}else if(cort.getTipo().equals("COMPS OFFICE PRODUCTION")){
					cmo += cort.getAmount();
				}else if(cort.getTipo().equals(prefs.getString("op1", "OTHER"))){
					cmo1 += cort.getAmount();
				}else if(cort.getTipo().equals(prefs.getString("op2", "OTHER"))){
					cmo2 += cort.getAmount();
				}
			}
			
			int initInventory = prod.getCantidadStand()+cmd+cmv+cmo+cmo1+cmo2;
			
			cell.setPhrase(new Phrase(String.valueOf(initInventory),fontTexto));
			table.addCell(cell);
			cell.setPhrase(new Phrase(dinero.format(Double.parseDouble(prod.getPrecio())),fontTexto));
			table.addCell(cell);
			
			cell.setPhrase(new Phrase(String.valueOf(cmd),fontTexto));
			table.addCell(cell);
			cell.setPhrase(new Phrase(String.valueOf(cmv),fontTexto));
			table.addCell(cell);
			cell.setPhrase(new Phrase(String.valueOf(cmo),fontTexto));
			table.addCell(cell);
			cell.setPhrase(new Phrase(String.valueOf(cmo1),fontTexto));
			table.addCell(cell);
			cell.setPhrase(new Phrase(String.valueOf(cmo2),fontTexto));
			table.addCell(cell);
			
			int finalInventory = prod.getCantidadStand() - prod.getProdNo();
			
			cell.setPhrase(new Phrase(String.valueOf(finalInventory),fontTexto));
			table.addCell(cell);
			
			int vendidos = finalInventory - prod.getProdNo();
			double gross = Double.parseDouble(prod.getPrecio())*vendidos;
			cell.setPhrase(new Phrase(String.valueOf(vendidos),fontTexto));
			table.addCell(cell);
			cell.setPhrase(new Phrase(dinero.format(gross),fontTexto));
			table.addCell(cell);
			cell.setPhrase(new Phrase(porcentaje.format(gross/total),fontTexto));
			table.addCell(cell);
			
			if(vendedor.getIva().equals("$"))
				cell.setPhrase(new Phrase(vendedor.getIva()+""+vendedor.getCantidad()+"\n"+vendedor.getTipo(),fontTexto));
			else
				cell.setPhrase(new Phrase(vendedor.getCantidad()+""+vendedor.getIva()+"\n"+vendedor.getTipo(),fontTexto));
			table.addCell(cell);
			
			if(vendedor.getIva().equals("$")){
				double cant = finalInventory * vendedor.getCantidad();
				cell.setPhrase(new Phrase(dinero.format(cant),fontTexto));
				table.addCell(cell);
			}else{
				double totalStand = gross;
				double tax = 0;
				for(Taxes taxes : prod.getTaxes()){
					tax += gross * (taxes.getAmount() * 0.01);
				}
				if(vendedor.getTipo().equals("After taxes")){
					totalStand -= tax;
				}
				double cant = totalStand * (vendedor.getCantidad() * 0.01);
				cell.setPhrase(new Phrase(dinero.format(cant),fontTexto));
				table.addCell(cell);
			}
			
		}
		
		table.writeSelectedRows(0, -1, document.leftMargin(), y, docWriter.getDirectContent());
		
	}
	
	public double tableProducts(Document document, List<Product> listProd,String[] headers ,float y){
		//list all the products sold to the customer
		//float[] columnWidths = {1f, 1f, 1f, 1f, 1f};
		//create PDF table with the given widths
		PdfPTable table = new PdfPTable(headers.length);
		// set table width a percentage of the page width
		table.setTotalWidth(500f);
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD,10);
		font.setColor(BaseColor.WHITE);
		
		BaseColor azul = WebColors.getRGBColor("#347af0");
		
		for(int i = 0; i < headers.length; i++){
			PdfPCell cell = new PdfPCell(new Phrase(headers[i],font));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
			cell.setBackgroundColor(azul);
			table.addCell(cell);
		}
		table.setHeaderRows(1);
		 
		double amount = 0;
		DecimalFormat df = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.US);
		df.applyPattern("$#,###.00");
		for(Product prod : listProd){
			Comisiones vendedor = prod.getComisiones().get(0);
			PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(prod.getCantidadStand())));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			
			cell.setPhrase(new Phrase(prod.getNombre()));
			table.addCell(cell);
			cell.setPhrase(new Phrase(prod.getArtista()));
			table.addCell(cell);
			if(!prod.getTalla().equals("") && prod.getTalla() != null)
				cell.setPhrase(new Phrase(prod.getTalla()));
			else
				cell.setPhrase(new Phrase("N/A"));
			table.addCell(cell);
			cell.setPhrase(new Phrase(df.format(Double.parseDouble(prod.getPrecio()))));
			table.addCell(cell);
			cell.setPhrase(new Phrase(vendedor.getIva()+" "+vendedor.getCantidad()));
			table.addCell(cell);
			if(vendedor.getIva().equals("$")){
				double cant = prod.getCantidadStand() * vendedor.getCantidad();
				amount += cant;
				cell.setPhrase(new Phrase(df.format(cant)));
				table.addCell(cell);
			}else{
				double total = prod.getCantidadStand() * Double.parseDouble(prod.getPrecio());
				double tax = 0;
				for(Taxes taxes : prod.getTaxes()){
					tax += total * (taxes.getAmount() * 0.01);
				}
				if(vendedor.getTipo().equals("After taxes")){
					total -= tax;
				}
				double cant = total * (vendedor.getCantidad() * 0.01);
				amount += cant;
				cell.setPhrase(new Phrase(df.format(cant)));
				table.addCell(cell);
			}
		}
		//absolute location to print the PDF table from 
		table.writeSelectedRows(0, -1, document.leftMargin(), y, docWriter.getDirectContent());
		
		return amount;
	}
	
	public double tableIngresos(Document document, String title, String fin, String[] nombres, double[] cants, float x, float y){
		double amount = 0;
		PdfPTable table = new PdfPTable(2);
		// set table width a percentage of the page width
		table.setTotalWidth(230f);
		
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD,10);
		font.setColor(BaseColor.WHITE);
		
		BaseColor azul = WebColors.getRGBColor("#347af0");
		DecimalFormat df = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.US);
		df.applyPattern("$#,###.00");
		
		PdfPCell cell = new PdfPCell(new Phrase(title,font));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBackgroundColor(azul);
		cell.setColspan(2);
		table.addCell(cell);
		
		table.setHeaderRows(1);
		
		for(int i = 0; i < nombres.length; i++){
			cell = new PdfPCell(new Phrase(String.valueOf(nombres[i])));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			
			amount += cants[i];
			cell.setPhrase(new Phrase(df.format(cants[i])));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		
		cell.setPhrase(new Phrase(fin,font));
		cell.setBackgroundColor(azul);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		PdfPCell cellf = new PdfPCell(new Phrase(df.format(amount)));
		cellf.setHorizontalAlignment(Element.ALIGN_CENTER);
		cellf.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cellf);
		
		table.writeSelectedRows(0, -1, document.leftMargin()+x, y, docWriter.getDirectContent());
		
		return amount;
	}
	
	public void tableNum(Document document, String[] title, double[] amount, float x, float y){
		float[] columnWidths = {0.8f, 1f};
		PdfPTable table = new PdfPTable(columnWidths);
		// set table width a percentage of the page width
		table.setTotalWidth(230f);
		
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD,10);
		font.setColor(BaseColor.WHITE);
		
		BaseColor azul = WebColors.getRGBColor("#347af0");
		DecimalFormat df = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.US);
		df.applyPattern("$#,###.00");
		
		for(int i = 0; i < title.length; i++){
			PdfPCell cell = new PdfPCell(new Phrase(title[i],font));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPaddingLeft(5.0f);
			cell.setBackgroundColor(azul);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(df.format(amount[i])));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
		}
		
		table.writeSelectedRows(0, -1, document.leftMargin()+x, y, docWriter.getDirectContent());
	}
	
	public void tableDatos(Document document, String[] title, String[] datos, float x, float y){
		float[] columnWidths = {0.7f, 1.2f};
		PdfPTable table = new PdfPTable(columnWidths);
		// set table width a percentage of the page width
		table.setTotalWidth(230f);
		
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD,10);
		font.setColor(BaseColor.WHITE);
		
		BaseColor azul = WebColors.getRGBColor("#347af0");
		
		for(int i = 0; i < title.length; i++){
			PdfPCell cell = new PdfPCell(new Phrase(title[i],font));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPaddingLeft(5.0f);
			cell.setBackgroundColor(azul);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(datos[i]));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
		}
		
		table.writeSelectedRows(0, -1, document.leftMargin()+x, y, docWriter.getDirectContent());
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
			bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}