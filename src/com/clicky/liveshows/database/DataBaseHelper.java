package com.clicky.liveshows.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "BDliveshows";
	private static final int DATABASE_VERSION = 8;

	//TABLA ARTISTA
	static final String TABLE_ARTISTA = "TArtista";
	static final String colId = "artista_id";
	static final String colNombre = "artista_nombre";
	static final String colEvento="evento";

	//TABLA EVENTO
	static final String TABLE_EVENTO = "TEvento";
	static final String colIdEvento = "evento_id";
	static final String colNombreEvento = "evento_nombre";
	static final String colLugar = "lugar";
	static final String colCapacidad = "capacidad";

	//TABLA ARTISTA
	static final String TABLE_FECHA = "TFecha";
	static final String colIdFecha = "fecha_id";
	static final String colFecha = "fecha";
	static final String colEventoF = "evento";

	//TABLA PRODUCTO
	static final String TABLE_PRODCUT ="TProduct";
	static final String colIdProduct = "product_id";
	static final String colNombreP ="nombre";
	static final String colTipoP="tipo";
	static final String colFoto="foto";
	static final String colCantidad="cantidad";
	static final String colCantidadTotal="cantidad_total";
	static final String colTalla ="talla";
	static final String colPrecio ="precio";
	static final String colEventoFK ="evento_id";
	static final String colArtistaFK="artista_id";

	//TABLA IMPUESTO
	static final String TABLE_TAXES ="TTaxes";
	static final String colIdTaxes="taxes_id";
	static final String colNombreT="nombre";
	static final String colPorcentajeT="porcentaje";
	static final String colTipoImpuesto="tipo_impuesto"; //Saber si es comision o impuesto
	static final String colIVA="iva";
	static final String colTipoPorPeso="tipo_porcentaje_peso";

	//TABLA IMPUESTO PRODUCTO
	static final String TABLE_TAXES_PRODUCT ="TTaxesProduct";
	static final String colIdTaxesCK ="taxes_id";
	static final String colIdProductCK ="product_id";
	static final String colIdTaxesProductId="taxes_product_id";

	//TABLA STAND
	static final String TABLE_STAND = "TStand";
	static final String colIdStand ="stand_id";
	static final String colNombreStand="nombre_stand";
	static final String colNombreEmpleado="nombre_empleado";
	static final String colComisionStand="comision";
	static final String colIVAStand="iva";
	static final String colTipoCStand = "tipo_comision";
	
	static final String colCantidadEfectivo = "cantidad_efectivo";
	static final String colCantidadBanamex = "cantidad_banamex";
	static final String colCantidadBanorte = "cantidad_banorte";
	static final String colCantidadSantander = "cantidad_santander";
	static final String colCantidadAmex = "cantidad_amex";
	static final String colCantidadOtro = "cantidad_otro";

	//TABLA STAND PRODUCTO
	static final String TABLE_STAND_PROD="TStand_Prod";
	static final String colIdStandProd="stand_prod_id";
	static final String colCantidadSP="cantidad";
	static final String colStandIdSP="stand_id";
	static final String colProductoIdSP="producto_id";
	static final String colFechaIdSP="fecha_id";
	static final String colImpuestoProdId ="taxes_id";


	//TABLA ADICIONAL
	static final String TABLE_ADICIONAL ="TAdicional";
	static final String colIdAdicional ="adicional_id";
	static final String colCantidadA="cantidad";
	static final String colNombreA="nombre";
	static final String colProductoIdA="producto_id";

	//TABLA VENTA PRODUCTO
	static final String TABLE_SALES_PRODUCT = "TSalesProduct";
	static final String colIdSales = "sales_id";
	static final String colCantidadVP = "cantidad";
	static final String colStandFK = "stand_id";
	static final String colStandProdFK = "stand_prod_id";
	
	//TABLA CORTESIAS
	static final String TABLE_CORTESIAS = "TCortesias";
	static final String colIdCortesias ="cortesias_id";
	static final String colTipoCortesias="tipo";
	static final String colCantidadCortesias="cantidad";
	static final String colProductoCortesia="producto_id";
	static final String colStandCortesias = "stand_id";
	
	

	public DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null,DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		// TODO Auto-generated method stub
		String SQL_EVENTO ="CREATE TABLE "+TABLE_EVENTO+" ( "+colIdEvento+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
		colNombreEvento+" TEXT NOT NULL, "+colLugar+" TEXT NOT NULL, "+colCapacidad+" TEXT NOT NULL)";
		database.execSQL(SQL_EVENTO);

		String SQL_ARTISTA ="CREATE TABLE "+TABLE_ARTISTA+" ("+colId+" INTEGER PRIMARY KEY AUTOINCREMENT," +
				colNombre+" TEXT NOT NULL, "+colEvento+" INTEGER NOT NULL, foreign KEY ("+colEvento+") REFERENCES "+
				TABLE_EVENTO+"("+colIdEvento+"))";
		database.execSQL(SQL_ARTISTA);

		String SQL_FECHA="CREATE TABLE "+TABLE_FECHA+" ("+colIdFecha+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
		colFecha+" TEXT NOT NULL, "+colEventoF+" INTEGER NOT NULL, foreign KEY ("+colEventoF+") REFERENCES "+
				TABLE_EVENTO+"("+colIdEvento+"))";
		database.execSQL(SQL_FECHA);

		String SQL_PRODUCTO ="CREATE TABLE "+TABLE_PRODCUT+" ("+colIdProduct+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
				colNombreP+" TEXT NOT NULL, "+colTipoP+" TEXT NOT NULL, "+colFoto+" TEXT NULL, "+colCantidad+
				" INTEGER NOT NULL, "+colCantidadTotal+
				" INTEGER NOT NULL, "+colTalla+" TEXT NULL, "+colPrecio+" TEXT NOT NULL, "+colEventoFK+" INTEGER NOT NULL, "+
				colArtistaFK+" INTEGER NOT NULL, "+
						"foreign KEY ("+colArtistaFK+") REFERENCES "+TABLE_ARTISTA+" ("+colId+")) ";
		database.execSQL(SQL_PRODUCTO);

		String SQL_CORTESIAS ="CREATE TABLE "+TABLE_CORTESIAS+" ("+colIdCortesias+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
				colTipoCortesias+" TEXT NOT NULL, "+colCantidadCortesias+" INTEGER NOT NULL, "
				+colProductoCortesia+" INTEGER NOT NULL, "+colStandCortesias+" INTEGER NOT NULL, "+
						"foreign KEY ("+colProductoCortesia+") REFERENCES "+TABLE_PRODCUT+" ("+colIdProduct+")) ";
		database.execSQL(SQL_CORTESIAS);
		
		String SQL_IMPUESTOS ="CREATE TABLE "+TABLE_TAXES+" ("+colIdTaxes+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
		colNombreT+" TEXT NOT NULL,"+colPorcentajeT+" INTEGER NOT NULL, "+colTipoImpuesto+" TEXT NULL, "+colIVA+" TEXT NULL, "+
				colTipoPorPeso+" TEXT NULL)";
		database.execSQL(SQL_IMPUESTOS);

		String SQL_IMPUESTOS_PRODUCTOS ="CREATE TABLE "+TABLE_TAXES_PRODUCT+" ("+colIdTaxesProductId+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
				colIdTaxesCK+" INTEGER NOT NULL, "+
		colIdProductCK+" INTEGER NOT NULL,  foreign KEY ("+colIdProductCK+") REFERENCES "+TABLE_PRODCUT+" ("+colIdProduct+"))";
		database.execSQL(SQL_IMPUESTOS_PRODUCTOS);

		String SQL_ADICIONALES ="CREATE TABLE "+TABLE_ADICIONAL+ " ("+colIdAdicional+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
		colCantidadA+" INTEGER NOT NULL, "+colNombreA+" TEXT NOT NULL, "+colProductoIdA+" INTEGER NOT NULL, foreign KEY ("+colProductoIdA+") REFERENCES "+
				TABLE_PRODCUT+" ("+colIdProduct+"))";
		database.execSQL(SQL_ADICIONALES);

		String SQL_STAND ="CREATE TABLE "+TABLE_STAND+ " ("+colIdStand+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
		colNombreStand+" TEXT NOT NULL, "+colNombreEmpleado+" TEXT NOT NULL, "+colComisionStand+" INTEGER NOT NULL, "
				+colIVAStand+" TEXT NOT NULL, "+colTipoCStand+" TEXT NOT NULL, "+colCantidadEfectivo+" REAL, "
				+colCantidadBanamex+" REAL, "+colCantidadBanorte+" REAL, "+colCantidadSantander+" REAL, "
				+colCantidadAmex+" REAL, "+colCantidadOtro+" REAL)";
		database.execSQL(SQL_STAND);

		String SQL_PRODUCTOS_STAND ="CREATE TABLE "+TABLE_STAND_PROD+ " ("+colIdStandProd+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
				colStandIdSP+" INTEGER NOT NULL, "+
		colCantidadSP+" INTEGER NOT NULL, "+colFechaIdSP+" INTEGER NOT NULL, "+colProductoIdSP+" INTEGER NOT NULL, "
				+colImpuestoProdId+" INTEGER NOT NULL, foreign KEY ("
				+colProductoIdSP+") REFERENCES "+TABLE_PRODCUT+" ("+colIdProduct+"))";
		database.execSQL(SQL_PRODUCTOS_STAND);

		String SQL_VENTAS_PRODUCTOS ="CREATE TABLE "+TABLE_SALES_PRODUCT+ " ("+colIdSales+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
				colCantidadVP+" INTEGER NOT NULL, "+colStandFK+" INTEGER NOT NULL, "
						+colStandProdFK+" INTEGER NOT NULL, foreign KEY ("
						+colStandProdFK+") REFERENCES "+TABLE_STAND_PROD+" ("+colIdStandProd+"))";
				database.execSQL(SQL_VENTAS_PRODUCTOS);


	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_EVENTO);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_ARTISTA);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_FECHA);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_PRODCUT);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_TAXES);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_TAXES_PRODUCT);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_ADICIONAL);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_STAND);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_STAND_PROD);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_SALES_PRODUCT);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_CORTESIAS);

		onCreate(db);
	}

}