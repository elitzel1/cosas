package com.clicky.liveshows.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {
	//Campos de la BD
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

	//TABLA ADICIONAL
	static final String TABLE_ADICIONAL ="TAdicional";
	static final String colIdAdicional ="adicional_id";
	static final String colCantidadA="cantidad";
	static final String colNombreA="nombre";
	static final String colProductoIdA="producto_id";

	//TABLA STAND
	static final String TABLE_STAND = "TStand";
	static final String colIdStand ="stand_id";
	static final String colNombreStand="nombre_stand";
	static final String colNombreEmpleado="nombre_empleado";
	static final String colComisionStand="comision";
	static final String colIVAStand="iva";
	static final String colTipoCStand = "tipo_comision";
	
	static final String colCantidadVendedor = "cantidad_vendedor";
	static final String colAbiertoStand = "stand_abierto";
	
	static final String colCantidadEfectivo = "cantidad_efectivo";
	static final String colCantidadBanamex = "cantidad_banamex";
	static final String colCantidadBanorte = "cantidad_banorte";
	static final String colCantidadSantander = "cantidad_santander";
	static final String colCantidadAmex = "cantidad_amex";
	static final String colCantidadOtro1 = "cantidad_otro_uno";
	static final String colCantidadOtro2 = "cantidad_otro_dos";
	static final String colCantidadOtro3 = "cantidad_otro_tres";

	//TABLA STAND PRODUCTO
	static final String TABLE_STAND_PROD="TStand_Prod";
	static final String colIdStandProd="stand_prod_id";
	static final String colCantidadSP="cantidad";
	static final String colStandIdSP="stand_id";
	static final String colProductoIdSP="producto_id";
	static final String colFechaIdSP="fecha_id";
	static final String colImpuestoProdId ="taxes_id";

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

	private Context context;
	private SQLiteDatabase database;
	private DataBaseHelper dbHelper;

	public DBAdapter(Context context) {
		this.context = context;
	}

	public DBAdapter open() throws SQLException {
		dbHelper = new DataBaseHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * Crea una nueva tarea, si esto va bien retorna la
	 * rowId de la tarea, de lo contrario retorna -1
	 */
	public long createEvento(String nombre, String lugar, String capacidad) {
		ContentValues initialValues = createContentValues(nombre, lugar,
				capacidad);
		return database.insert(TABLE_EVENTO, null, initialValues);
	}

	public long createArtista(String nombre,int idEvento){
		ContentValues initialValues = createContentValuesArtist(nombre,idEvento);
		return database.insert(TABLE_ARTISTA, null, initialValues);
	}

	public long createFecha(String date,int idEvento){
		ContentValues initialValues = createContentValuesFecha(date,idEvento);
		return database.insert(TABLE_FECHA, null, initialValues);
	}

	public long createProducto(String nombre, String tipo, String foto, int cantidad,int cantidadTotal, String talla,int cortesia ,String precio, int evento, int artista){
		ContentValues initialValues = createContentValuesProducto(nombre,tipo,foto,cantidad,cantidadTotal,talla,cortesia,precio,evento,artista);
		return database.insert(TABLE_PRODCUT, null, initialValues);
	}

	public long createCortesia(String tipo, int cantidad, int producto_id, int stand_id){
		ContentValues initialValues = createContentValuesCortesia(tipo,cantidad,producto_id,stand_id);
		return database.insert(TABLE_CORTESIAS, null, initialValues);
	}

	public long createImpuesto(String nombre, String tipo, int porcentaje){
		ContentValues initialValues = createContentValuesImpuesto(nombre,porcentaje,tipo);
		return database.insert(TABLE_TAXES, null, initialValues);
	}

	public long createImpuesto(String nombre, String tipo, int porcentaje,String iva, String tipoPeso){
		ContentValues initialValues = createContentValuesImpuesto(nombre,porcentaje,tipo,iva, tipoPeso);
		return database.insert(TABLE_TAXES, null, initialValues);
	}

	public long createImpuestoProducto(int idProducto,int idTaxes){
		ContentValues initialValues = createContentValuesImpuestoProducto(idProducto,idTaxes);
		return database.insert(TABLE_TAXES_PRODUCT, null, initialValues);
	}

	public long createAdicional(String nombre,int cantidad,int id){
		ContentValues initialValues=createContentValuesAdicional(nombre,cantidad,id);
		return database.insert(TABLE_ADICIONAL, null, initialValues);
	}

	public long createStand(String nombre, int comision, String iva,String tipo, String empleado ){
		ContentValues initialValues = createContentValuesStand(nombre, empleado, comision,tipo, iva);
		return database.insert(TABLE_STAND, null, initialValues);
	}

	public long createStandProducto(int stand, int producto, int fecha, int cantidad, int comision){
		ContentValues initialValues=createContentValuesStandProd(producto, stand, fecha, cantidad,comision);
		return database.insert(TABLE_STAND_PROD, null, initialValues);
	}
	public long createVentaProducto(int stand, int producto_stand, int cantidad){
		ContentValues initialValues = createContentValuesVentaProd(stand, producto_stand,cantidad);
		return database.insert(TABLE_SALES_PRODUCT, null, initialValues);
	}

	//Actualiza la tarea
	/*	public boolean updateTodo(long rowId, String category, String summary,
				String description) {
			ContentValues updateValues = createContentValues(category, summary,
					description);

			return database.update(DATABASE_TABLE, updateValues, KEY_ROWID + "="
					+ rowId, null) > 0;
		}*/
	
	public boolean updateProducto(long rowId,String nombre, String tipo, String foto, int cantidad,int cantidadTotal,String precio, int artista){
		ContentValues updateValues = createContentValuesProducto(nombre, tipo, foto, cantidad, cantidadTotal, precio, artista);
		return database.update(TABLE_PRODCUT, updateValues, colIdProduct+" = "+rowId, null)>0;
	}
	
	public boolean updateComisiones(long rowId, String nombre, int porcentaje, String tipo){
		ContentValues updateValues = createContentValuesImpuesto(nombre,porcentaje,tipo);
		return database.update(TABLE_TAXES, updateValues, colIdTaxes+" = "+rowId, null)>0;
	}

	public boolean updateComisiones(long rowId,String nombre, String tipo, int porcentaje,String iva, String tipoPeso){
		ContentValues updateValues = createContentValuesImpuesto(nombre,porcentaje,tipo,iva, tipoPeso);
		return database.update(TABLE_TAXES, updateValues, colIdTaxes+" = "+rowId, null)>0;	
	}
	
	public boolean updateComision(long rowId,int porcentaje, String iva, String tipoPeso){
		ContentValues updateValues = createContentValuesImpuesto(porcentaje,iva,tipoPeso);
		return database.update(TABLE_TAXES, updateValues, colIdTaxes+" = "+rowId, null)>0;	
	}

	//Actualiza productos
	public boolean updateProducto(long rowId, int cantidadTotal, int cantidad){
		ContentValues updateValues = createContentValuesUpdate(cantidadTotal, cantidad);
		return database.update(TABLE_PRODCUT, updateValues, colIdProduct+" = "+rowId, null)>0;
	}
	public boolean updateProducto(long rowId,int cantidad){
		ContentValues updateValues = createContentValuesUpdateCantidad(cantidad);
		return database.update(TABLE_PRODCUT, updateValues, colIdProduct+" = "+rowId, null)>0;
	}
	
	public boolean updateStand(long rowId, String nombre, String encargado, int comision, String iva,String tipo){
		ContentValues updateValues = createContentValuesStand(nombre, encargado, comision,tipo, iva);
		return database.update(TABLE_STAND, updateValues, colIdStand+" = "+rowId, null)>0;
	}

	public boolean updateStandProducto(long rowId,long rowIdStand, int cantidadStand){
		ContentValues updateValues= createContentValuesUpdateCantidadStand(cantidadStand);
		return database.update(TABLE_STAND_PROD, updateValues, colIdStandProd + "=" + rowId +" AND "+ colStandIdSP + "=" + rowIdStand, null)>0;
	}

	public boolean updateCortesia(long rowId,int cantidadTotal,int cantidad){
		ContentValues updateValues = createContentValuesUpdateCantidadCortesia(cantidadTotal,cantidad);
		return database.update(TABLE_PRODCUT, updateValues, colIdProduct+" = "+rowId, null)>0;
	}

	public boolean updateAbrirStand(long rowId){
		ContentValues updateValues = createContentValuesUpdateStandAbrir();
		return database.update(TABLE_STAND, updateValues, colIdStand+" = "+rowId, null)>0;
	}
	public boolean updateStandCierre(long rowId,double efectivo,double banamex,double banorte, double santander, double amex,
			double otro1,double otro2,double otro3,double vendedor){
		ContentValues updateValues = createContentValuesUpdateStandCierre(efectivo,banamex,banorte,santander,amex,otro1,otro2,otro3,vendedor);
		return database.update(TABLE_STAND, updateValues, colIdStand+" = "+rowId, null)>0;
	}
	
	public boolean updateEvento(int rowId,String venue,int capacidad){
		ContentValues updateValues = createContentValuesUpdateEvento(venue,capacidad);
		return database.update(TABLE_EVENTO, updateValues, colIdEvento+" = "+rowId, null)>0;
	}
	
	public boolean updateEvento(int rowId,String event){
		ContentValues updateValues = createContentValuesUpdateEvento(event);
		return database.update(TABLE_EVENTO, updateValues, colIdEvento+" = "+rowId, null)>0;
	}
	//Borra la tarea
	public boolean deleteEvento(long rowId) {
		return database.delete(TABLE_EVENTO, colIdEvento + "=" + rowId, null) > 0;
	}

	public boolean deleteArtista(long rowId){
		return database.delete(TABLE_ARTISTA, colId + "="+rowId, null)>0;
	}

	public boolean deleteFecha(long rowId){
		return database.delete(TABLE_FECHA, colIdFecha + "="+rowId, null)>0;
	}

	public boolean deleteCortesiasStand(long rowId){
		return database.delete(TABLE_CORTESIAS, colIdCortesias + " = "+rowId, null)>0;
	}
	
	public boolean deleteAdicional(long rowId){
		return database.delete(TABLE_ADICIONAL, colIdAdicional + " = "+rowId, null)>0;
	}
	public boolean deleteProduct(long rowId){
		int band =database.delete(TABLE_PRODCUT, colIdProduct + " = "+rowId, null);
		database.delete(TABLE_ADICIONAL, colProductoIdA+" = "+rowId, null);
		deleteCortesias(rowId);
		deleteTaxesProd(rowId);
		deleteProductStand(rowId);
		return band>0;
	}
	
	public boolean deleteComision(long rowId){
		return database.delete(TABLE_TAXES, colIdTaxes + " = "+rowId, null)>0;
	}
	public boolean deleteComisiones(long rowId){
		return database.delete(TABLE_TAXES, colIdFecha + " = "+rowId, null)>0;
	}
	
	public boolean deleteInfoProdStand(long rowId){
		int band = 0;
		band += deleteTaxesProd(rowId);
		band += deleteVentas(rowId);
		
		return band > 0;
	}
	
	public boolean deleteStand(long rowId){
		return database.delete(TABLE_STAND, colIdStand+" = "+rowId, null) > 0;
	}
	
	public int deleteVentas(long rowId){
		return database.delete(TABLE_SALES_PRODUCT, colStandProdFK+" = "+rowId, null);
	}
	
	public int deleteTaxesProd(long rowId){
		return database.delete(TABLE_TAXES_PRODUCT, colIdProductCK+" = "+rowId, null);
	}
	
	public int deleteProductStand(long rowId){
		return database.delete(TABLE_STAND_PROD, colProductoIdSP+" = "+rowId, null);
	}
	
	private int deleteCortesias(long rowId){
		return database.delete(TABLE_CORTESIAS, colProductoCortesia+" = "+rowId, null);
	}

	public boolean deleteProductStand(long rowId,long rowIdP, int cantidad){
		updateProducto(rowIdP, cantidad);
		String args = colStandIdSP + "= "+rowId+" AND "+
				colProductoIdSP+" = "+rowIdP;
		int band = database.delete(TABLE_STAND_PROD, args, null);
		return band>0;
	}
	
	public void deleteTodo(){
		database.delete(TABLE_EVENTO, null, null);
		database.delete(TABLE_ARTISTA, null, null);
		database.delete(TABLE_FECHA, null, null);
		database.delete(TABLE_PRODCUT, null,null);
		database.delete(TABLE_STAND, null, null);
		database.delete(TABLE_STAND_PROD, null, null);
		database.delete(TABLE_ADICIONAL, null, null);
		database.delete(TABLE_TAXES, null, null);
		database.delete(TABLE_TAXES_PRODUCT, null, null);
		database.delete(TABLE_SALES_PRODUCT, null, null);
		database.delete(TABLE_CORTESIAS, null, null);
		
		database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_EVENTO + "'");
		database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_ARTISTA + "'");
		database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_FECHA + "'");
		database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_PRODCUT + "'");
		database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_STAND + "'");
		database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_STAND_PROD + "'");
		database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_ADICIONAL + "'");
		database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_TAXES + "'");
		database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_TAXES_PRODUCT + "'");
		database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_SALES_PRODUCT + "'");
		database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_CORTESIAS + "'");
	}
	
	public void deleteDia(){
		database.delete(TABLE_ADICIONAL, null, null);
		database.delete(TABLE_CORTESIAS, null, null);
	}
	
	//Returna un Cursor que contiene todos los items
	public Cursor fetchAllEvento() {
		return database.query(TABLE_EVENTO, new String[] { colIdEvento,
				colNombreEvento, colLugar, colCapacidad }, null, null, null,
				null, null);
	}

	public Cursor fetchAllArtistas(){
		return database.query(TABLE_ARTISTA, new String[] { colId,
				colNombre, colEvento}, null, null, null,
				null, null);
	}

	public Cursor fetchAllFechas(){
		return database.query(TABLE_FECHA, new String[] { colIdFecha,
				colFecha, colEventoF}, null, null, null,
				null, null);
	}

	public Cursor fetchAllProductos(){
		return database.query(TABLE_PRODCUT, new String[]{
				colIdProduct, colNombreP,colTipoP, colFoto, colCantidad, colCantidadTotal, colTalla,colPrecio, colEventoFK, colArtistaFK
		}, null, null, null, null, colArtistaFK+" DESC");
	}

	public Cursor fetchAllAdicional(){
		return database.query(TABLE_ADICIONAL, new String[] { colIdAdicional,colCantidadA,
				colNombreA, colProductoIdA}, null, null, null,
				null, null);
	}

	public Cursor fetchAllStand(){
		return database.query(TABLE_STAND, new String[] { colIdStand,colNombreStand,colNombreEmpleado,colComisionStand,colIVAStand,colTipoCStand,
				colCantidadEfectivo,colCantidadBanamex,colCantidadBanorte,colCantidadSantander,colCantidadAmex,
				colCantidadOtro1,colCantidadOtro2,colCantidadOtro3,colCantidadVendedor,colAbiertoStand
		}, null, null, null,
		null, null);
	}
	
	public Cursor fetchStandCierre(){
		return database.query(TABLE_STAND, new String[] { colIdStand,colCantidadEfectivo,colCantidadBanamex,colCantidadBanorte,
				colCantidadSantander,colCantidadAmex,colCantidadOtro1,colCantidadOtro2,colCantidadOtro3,colCantidadVendedor}, 
				null, null, null,null, null);
	}

	public Cursor fetchAllCortesias(){
		return database.query(TABLE_CORTESIAS, new String[] { colIdCortesias,colTipoCortesias,
				colCantidadCortesias,colProductoCortesia,colStandCortesias},
				null, null, null,null, null);
	}	

	public Cursor fetchProductosArtista(int idArtista){
		return database.query(TABLE_PRODCUT, new String[]{
				colIdProduct, colNombreP,colTipoP, colFoto, colCantidad, colCantidadTotal, colTalla,colPrecio, colEventoFK, colArtistaFK
		}, colArtistaFK+" = "+idArtista, null, null, null, null);
	}
	
	public Cursor fetchStand(int rowId){
		return database.query(TABLE_STAND, new String[] { colIdStand,colNombreStand,colNombreEmpleado,colComisionStand,colIVAStand,colTipoCStand
		}, colIdStand + "=" +rowId, null, null,null, null);
	}

	public Cursor fetchCortesias(long rowId){
		return database.query(TABLE_CORTESIAS, new String[] { colIdCortesias,colTipoCortesias,colCantidadCortesias,colProductoCortesia,colStandCortesias},
				colProductoCortesia + "=" +rowId, null, null,
				null, null);
	}
	
	public Cursor fetchCortesias(long rowId,long stand){
		return database.query(TABLE_CORTESIAS, new String[] { colIdCortesias,colTipoCortesias,colCantidadCortesias,colProductoCortesia,colStandCortesias},
				colProductoCortesia + "=" +rowId+" AND "+colStandCortesias+" = "+stand, null, null,
				null, null);
	}

	//Returna un Cursor que contiene la info del item
	public Cursor fetchEvento(long rowId) throws SQLException {
		Cursor mCursor = database.query(true, TABLE_EVENTO, new String[] {
				colIdEvento,
				colNombreEvento, colLugar, colCapacidad },
				colIdEvento + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor fetchArtista(long rowId) throws SQLException {
		Cursor mCursor = database.query(true, TABLE_ARTISTA, new String[] {
				colId, colNombre, colEvento },
				colId + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor fetchArtista(String name) throws SQLException {
		Cursor mCursor = database.query(true, TABLE_ARTISTA, new String[] {
				colId, colNombre, colEvento },
				colNombre + "=" + name, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor fetchFecha(long rowId) throws SQLException {
		Cursor mCursor = database.query(true, TABLE_FECHA, new String[] {
				colIdFecha,
				colFecha, colEventoF },
				colIdFecha + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}


	public Cursor fetchProducto(long rowId) throws SQLException {
		Cursor mCursor = database.query(true, TABLE_PRODCUT, new String[] {
				colIdProduct, colNombreP,colTipoP, colFoto, colCantidad,colCantidadTotal, colTalla, colPrecio, colEventoFK, colArtistaFK },
				colIdProduct + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}	

	public Cursor fetchAdicional(long rowId) throws SQLException{
		Cursor mCursor = database.query(true, TABLE_ADICIONAL, new String[] { colIdAdicional,colCantidadA,
				colNombreA, colProductoIdA}, colProductoIdA + "=" + rowId,null, null, null, null, null);
		return mCursor;
	}
	
	public Cursor fetchStandProduct(long rowId) throws SQLException{
		Cursor mCursor = database.query(true, TABLE_STAND_PROD, new String[] { colIdStandProd,colCantidadSP,
				colFechaIdSP, colProductoIdSP, colImpuestoProdId}, colStandIdSP + "=" + rowId,
				null, null, null, null, null);
		return mCursor;
	}
	
	public Cursor fetchStandProduct(long rowId,long idFecha) throws SQLException{
		Cursor mCursor = database.query(true, TABLE_STAND_PROD, new String[] { colIdStandProd,colCantidadSP,
				colFechaIdSP, colProductoIdSP, colImpuestoProdId}, colStandIdSP + "=" + rowId+" AND "+colFechaIdSP+" = "+idFecha,
				null, null, null, null, null);
		return mCursor;
	}
	
	public Cursor fetchStandProductDetail(long rowId,long idFecha) throws SQLException{
		Cursor mCursor = database.query(true, TABLE_STAND_PROD, new String[] { colIdStandProd,colCantidadSP,
				colFechaIdSP, colProductoIdSP, colImpuestoProdId,colStandIdSP}, 
				colProductoIdSP + "=" + rowId+" AND "+colFechaIdSP+" = "+idFecha
				,null, null, null, null, null);
		return mCursor;
	}

	public Cursor fetchStandProductAll(long rowId, long rowStandId) throws SQLException{
		String []args = {colProductoIdSP + "=" + rowId,colStandIdSP + "=" + rowStandId};
		Cursor mCursor = database.query(true, TABLE_STAND_PROD, new String[] { colIdStandProd,colCantidadSP,
				colFechaIdSP, colProductoIdSP, colImpuestoProdId},null,args, null, null, null, null);
		return mCursor;
	}
	
	public Cursor fetchProductoStand(long rowId){
		Cursor mCursor = database.query(true, TABLE_STAND_PROD, new String[] { colIdStandProd,colCantidadSP,
				colFechaIdSP, colProductoIdSP, colImpuestoProdId},colProductoIdSP+" = "+rowId,null, null, null, null, null);
		return mCursor;
	}
	
	public Cursor fetchStandProductAll(long rowId) throws SQLException{
		Cursor mCursor = database.query(true, TABLE_STAND_PROD, new String[] { colIdStandProd,colCantidadSP,
				colFechaIdSP, colProductoIdSP, colImpuestoProdId},colIdStandProd+" = "+rowId,null, null, null, null, null);
		return mCursor;
	}

	public Cursor fetchProductImpuestoProd(long rowId) throws SQLException{
		Cursor mCursor = database.query(true, TABLE_TAXES_PRODUCT, new String[] { colIdTaxesProductId,colIdTaxesCK,
				colIdProductCK}, colIdProductCK + "=" + rowId,null, null, null, null, null);
		return mCursor;
	}

	public Cursor fetchImpuestos(long rowId) throws SQLException{
		Cursor mCursor = database.query(true, TABLE_TAXES, new String[] { colIdTaxes,colNombreT,
				colPorcentajeT,colTipoImpuesto,colIVA,colTipoPorPeso}, colIdTaxes + "=" + rowId,null, null, null, null, null);
		return mCursor;
	}


	//	public Cursor fetchCortesias(long rowId) throws SQLException{
	//		Cursor mCursor = database.query(true, TABLE_PRODCUT, new String[] {colCortesia}, colIdProduct + "=" + rowId,null, null, null, null, null);
	//		return mCursor;
	//	}
	
	public Cursor fetchVentasProd(long rowId) throws SQLException{
		Cursor mCursor = database.query(true, TABLE_SALES_PRODUCT, new String[] { colIdSales,  colStandFK,  colStandProdFK,
				colCantidadVP}, colStandProdFK+" = "+rowId,null, null, null, null, null);
		return mCursor;
	}


	private ContentValues createContentValues(String name, String lugar,
			String capacidad) {
		ContentValues values = new ContentValues();
		values.put(colNombreEvento, name);
		values.put(colLugar, lugar);
		values.put(colCapacidad, capacidad);
		return values;
	}

	private ContentValues createContentValuesArtist(String name, int id_evento){
		ContentValues values = new ContentValues();
		values.put(colNombre, name);
		values.put(colEvento, id_evento);
		return values;
	}

	private ContentValues createContentValuesFecha(String date, int id_evento){
		ContentValues values = new ContentValues();
		values.put(colFecha, date);
		values.put(colEventoF, id_evento);
		return values;
	}
	private ContentValues createContentValuesProducto(String nombre, String tipo, String foto, int cantidad,int cantidadTotal, String talla,int cortesia, String precio, int evento, int artista){
		ContentValues values = new ContentValues();
		values.put(colNombreP, nombre);
		values.put(colTipoP, tipo);
		values.put(colFoto,foto);
		values.put(colCantidad,cantidad);
		values.put(colCantidadTotal, cantidadTotal);
		values.put(colTalla, talla);
		values.put(colPrecio, precio);
		values.put(colEventoFK, evento);
		values.put(colArtistaFK, artista);
		return values;
	}

	private ContentValues createContentValuesAdicional(String nombre,int cantidad ,int id){
		ContentValues values = new ContentValues();
		values.put(colCantidadA, cantidad);
		values.put(colNombreA, nombre);
		values.put(colProductoIdA, id);
		return values;
	}

	private ContentValues createContentValuesStand(String nombre,String empleado,int comision,String tipo ,String iva){
			//,double efectivo, double banamex, double banorte, double santander, double amex, double otro){
		ContentValues values = new ContentValues();
		values.put(colNombreStand, nombre);
		values.put(colNombreEmpleado, empleado);
		values.put(colComisionStand, comision);
		values.put(colTipoCStand, tipo);
		values.put(colIVAStand, iva);
		values.put(colAbiertoStand,1);
		/*
		values.put(colCantidadEfectivo, efectivo);
		values.put(colCantidadBanamex, banamex);
		values.put(colCantidadBanorte, banorte);
		values.put(colCantidadSantander, santander);
		values.put(colCantidadAmex, amex);
		values.put(colCantidadOtro, otro);
		*/
		return values;
	}


	private ContentValues createContentValuesStandProd(int producto,int stand, int fecha, int cantidad, int comision){
		ContentValues values = new ContentValues();
		values.put(colStandIdSP, stand);
		values.put(colProductoIdSP, producto);
		values.put(colFechaIdSP,fecha);
		values.put(colCantidadSP, cantidad);
		values.put(colImpuestoProdId,comision);
		return values;
	}

	private ContentValues createContentValuesUpdate(int cantidadTotal, int cantidad){
		ContentValues values = new ContentValues();
		values.put(colCantidadTotal, cantidadTotal);
		values.put(colCantidad, cantidad);
		return values;
	}

	private ContentValues createContentValuesUpdateEvento(String lugar,int capacidad) {
		ContentValues values = new ContentValues();
		values.put(colLugar, lugar);
		values.put(colCapacidad, capacidad);
		return values;
	}
	
	private ContentValues createContentValuesUpdateEvento(String evento) {
		ContentValues values = new ContentValues();
		values.put(colNombreEvento, evento);
		return values;
	}
	
	private ContentValues createContentValuesUpdateCantidad(int cantidad){
		ContentValues values= new ContentValues();
		values.put(colCantidad, cantidad);
		return values;
	}

	private ContentValues createContentValuesUpdateCantidadStand(int cantidadStand){
		ContentValues values= new ContentValues();
		values.put(colCantidadSP, cantidadStand);
		return values;
	}

	private ContentValues createContentValuesImpuesto(String nombre, int porcentaje, String tipo){
		ContentValues values = new ContentValues();
		values.put(colNombreT, nombre);
		values.put(colPorcentajeT, porcentaje);
		values.put(colTipoImpuesto, tipo);
		return values;
	}

	private ContentValues createContentValuesImpuesto(String nombre, int porcentaje, String tipo,String IVA, String pesoPor){
		ContentValues values = new ContentValues();
		values.put(colNombreT, nombre);
		values.put(colPorcentajeT, porcentaje);
		values.put(colTipoImpuesto, tipo);
		values.put(colIVA, IVA);
		values.put(colTipoPorPeso, pesoPor);
		return values;
	}

	private ContentValues createContentValuesImpuesto(int porcentaje,String IVA, String pesoPor){
		ContentValues values = new ContentValues();
		values.put(colPorcentajeT, porcentaje);
		values.put(colIVA, IVA);
		values.put(colTipoPorPeso, pesoPor);
		return values;
	}
	
	private ContentValues createContentValuesImpuestoProducto(int idProd,int idTaxes){
		ContentValues values = new ContentValues();
		values.put(colIdProductCK, idProd);
		values.put(colIdTaxesCK,idTaxes);
		return values;
	}

	private ContentValues createContentValuesUpdateCantidadCortesia(int cantidad,int cortesias){
		ContentValues values = new ContentValues();
		values.put(colCantidad, cantidad);
	//	values.put(colCortesia, cortesias);
		return values;
	}

	private ContentValues createContentValuesVentaProd(int stand, int producto_stand, int cantidad){
		ContentValues values = new ContentValues();
		values.put(colStandFK, stand);
		values.put(colStandProdFK, producto_stand);
		values.put(colCantidadVP, cantidad);
		return values;
	}


	private ContentValues createContentValuesUpdateStandCierre(double efectivo,double banamex,double banorte,double santander,
			double amex,double otro1,double otro2,double otro3,double vendedor){
		ContentValues values = new ContentValues();
		values.put(colCantidadEfectivo, efectivo);
		values.put(colCantidadBanamex, banamex);
		values.put(colCantidadBanorte, banorte);
		values.put(colCantidadSantander, santander);
		values.put(colCantidadAmex, amex);
		values.put(colCantidadOtro1, otro1);
		values.put(colCantidadOtro2, otro2);
		values.put(colCantidadOtro3, otro3);
		values.put(colCantidadVendedor, vendedor);
		values.put(colAbiertoStand, 0);
		return values;
	}
	
	private ContentValues createContentValuesUpdateStandAbrir(){
		ContentValues values = new ContentValues();
		values.put(colCantidadEfectivo, 0);
		values.put(colCantidadBanamex, 0);
		values.put(colCantidadBanorte, 0);
		values.put(colCantidadSantander, 0);
		values.put(colCantidadAmex, 0);
		values.put(colCantidadOtro1, 0);
		values.put(colCantidadOtro2, 0);
		values.put(colCantidadOtro3, 0);
		values.put(colCantidadVendedor, 0);
		values.put(colAbiertoStand, 1);
		return values;
	}

	private ContentValues createContentValuesCortesia(String tipo, int cantidad, int id, int stand){
		ContentValues values =new ContentValues();
		values.put(colTipoCortesias, tipo);
		values.put(colCantidadCortesias, cantidad);
		values.put(colProductoCortesia, id);
		values.put(colStandCortesias, stand);
		return values;
	}
	
	private ContentValues createContentValuesProducto(String nombre, String tipo, String foto, int cantidad,int cantidadTotal, String precio, int artista){
		ContentValues values = new ContentValues();
		values.put(colNombreP, nombre);
		values.put(colTipoP, tipo);
		values.put(colFoto,foto);
		values.put(colCantidad,cantidad);
		values.put(colCantidadTotal, cantidadTotal);
		values.put(colPrecio, precio);
		values.put(colArtistaFK, artista);
		return values;
	}
}