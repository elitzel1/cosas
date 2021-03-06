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
		static final String colCortesia="cortesias";
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
		
		//TABLA STAND PRODUCTO
		static final String TABLE_STAND_PROD="TStand_Prod";
		static final String colIdStandProd="stand_prod_id";
		static final String colCantidadSP="cantidad";
		static final String colStandIdSP="stand_id";
		static final String colProductoIdSP="producto_id";
		static final String colFechaIdSP="fecha_id";
		
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
		
		public long createArtista(String nombre){
			ContentValues initialValues = createContentValuesArtist(nombre,0);
			return database.insert(TABLE_ARTISTA, null, initialValues);
		}
		
		public long createFecha(String date){
			ContentValues initialValues = createContentValuesFecha(date,0);
			return database.insert(TABLE_FECHA, null, initialValues);
		}
		
		public long createProducto(String nombre, String tipo, String foto, int cantidad,int cantidadTotal, String talla,int cortesia ,String precio, int evento, int artista){
			ContentValues initialValues = createContentValuesProducto(nombre,tipo,foto,cantidad,cantidadTotal,talla,cortesia,precio,evento,artista);
			return database.insert(TABLE_PRODCUT, null, initialValues);
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
		
		public long createStandProducto(int stand, int producto, int fecha, int cantidad){
			ContentValues initialValues=createContentValuesStandProd(producto, stand, fecha, cantidad);
			return database.insert(TABLE_STAND_PROD, null, initialValues);
		}
		
		
		//Actualiza la tarea
	/*	public boolean updateTodo(long rowId, String category, String summary,
				String description) {
			ContentValues updateValues = createContentValues(category, summary,
					description);

			return database.update(DATABASE_TABLE, updateValues, KEY_ROWID + "="
					+ rowId, null) > 0;
		}*/

		
		//Actualiza productos
		public boolean updateProducto(long rowId, int cantidadTotal, int cantidad){
			ContentValues updateValues = createContentValuesUpdate(cantidadTotal, cantidad);
			return database.update(TABLE_PRODCUT, updateValues, colIdProduct+" = "+rowId, null)>0;
		}
		public boolean updateProducto(long rowId,int cantidad){
			ContentValues updateValues = createContentValuesUpdateCantidad(cantidad);
			return database.update(TABLE_PRODCUT, updateValues, colIdProduct+" = "+rowId, null)>0;
		}
		
		public boolean updateStandProducto(long rowId,long rowIdStand, int cantidadStand){
			ContentValues updateValues= createContentValuesUpdateCantidadStand(cantidadStand);
			return database.update(TABLE_STAND_PROD, updateValues, colProductoIdSP + "=" + rowId +" AND "+ colStandIdSP + "=" + rowIdStand, null)>0;
		}
		
		public boolean updateCortesia(long rowId,int cantidadTotal,int cantidad){
			ContentValues updateValues = createContentValuesUpdateCantidadCortesia(cantidadTotal,cantidad);
			return database.update(TABLE_PRODCUT, updateValues, colIdProduct+" = "+rowId, null)>0;
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

		public boolean deleteProduct(long rowId){
			int band =database.delete(TABLE_PRODCUT, colIdProduct + "="+rowId, null);
			database.delete(TABLE_ADICIONAL, colProductoIdA+" ="+rowId, null);
			return band>0;
		}
		
		public boolean deleteProductStand(long rowId,long rowIdP, int cantidad){
			updateProducto(rowIdP, cantidad);
			String []args = {colStandIdSP + "= "+rowId,
					colProductoIdSP+" = "+rowIdP};
			int band = database.delete(TABLE_STAND_PROD, null, args);
			return band>0;
			
		}
		public void deleteTodo(){
			database.delete(TABLE_EVENTO, null, null);
		//	database.execSQL("delete from sqlite_sequence where name="+TABLE_EVENTO);
			database.delete(TABLE_ARTISTA, null, null);
			database.delete(TABLE_FECHA, null, null);
			database.delete(TABLE_PRODCUT, null,null);
			database.delete(TABLE_STAND, null, null);
			database.delete(TABLE_STAND_PROD, null, null);
			database.delete(TABLE_ADICIONAL, null, null);
			database.delete(TABLE_TAXES, null, null);
			database.delete(TABLE_TAXES_PRODUCT, null, null);
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
					colIdProduct, colNombreP,colTipoP, colFoto, colCantidad, colCantidadTotal, colTalla, colCortesia,colPrecio, colEventoFK, colArtistaFK
			}, null, null, null, null, null);
		}
		
		public Cursor fetchAllAdicional(){
			return database.query(TABLE_ADICIONAL, new String[] { colIdAdicional,colCantidadA,
					colNombreA, colProductoIdA}, null, null, null,
					null, null);
		}
		
		public Cursor fetchAllStand(){
			return database.query(TABLE_STAND, new String[] { colIdStand,colNombreStand,colNombreEmpleado,colComisionStand,colIVAStand,colTipoCStand
					}, null, null, null,
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
					colEvento + "=" + rowId, null, null, null, null, null);
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
					colFechaIdSP, colProductoIdSP}, colStandIdSP + "=" + rowId,null, null, null, null, null);
			return mCursor;
		}
		
		public Cursor fetchStandProductAll(long rowId, long rowStandId) throws SQLException{
			String []args = {colProductoIdSP + "=" + rowId,colStandIdSP + "=" + rowStandId};
			Cursor mCursor = database.query(true, TABLE_STAND_PROD, new String[] { colIdStandProd,colCantidadSP,
					colFechaIdSP, colProductoIdSP},null,args, null, null, null, null);
			return mCursor;
		}
		
		public Cursor fetchProductImpuestoProd(long rowId) throws SQLException{
			Cursor mCursor = database.query(true, TABLE_TAXES_PRODUCT, new String[] { colIdTaxesProductId,colIdTaxesCK,
					colIdProductCK}, colIdProductCK + "=" + rowId,null, null, null, null, null);
			return mCursor;
		}
		
		public Cursor fetchImpuestos(long rowId) throws SQLException{
			Cursor mCursor = database.query(true, TABLE_TAXES_PRODUCT, new String[] { colIdTaxes,colNombreT,
					colPorcentajeT,colTipoImpuesto,colIVA,colTipoPorPeso}, colIdTaxes + "=" + rowId,null, null, null, null, null);
			return mCursor;
		}
		
		public Cursor fetchCortesias(long rowId) throws SQLException{
			Cursor mCursor = database.query(true, TABLE_PRODCUT, new String[] {colCortesia}, colIdProduct + "=" + rowId,null, null, null, null, null);
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
			values.put(colCortesia, cortesia);
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
			ContentValues values = new ContentValues();
			values.put(colNombreStand, nombre);
			values.put(colNombreEmpleado, empleado);
			values.put(colComisionStand, comision);
			values.put(colTipoCStand, tipo);
			values.put(colIVAStand, iva);
			return values;
		}
		
		
		private ContentValues createContentValuesStandProd(int producto,int stand, int fecha, int cantidad){
			ContentValues values = new ContentValues();
			values.put(colStandIdSP, stand);
			values.put(colProductoIdSP, producto);
			values.put(colFechaIdSP,fecha);
			values.put(colCantidadSP, cantidad);
			return values;
		}
		
		private ContentValues createContentValuesUpdate(int cantidadTotal, int cantidad){
			ContentValues values = new ContentValues();
			values.put(colCantidadTotal, cantidadTotal);
			values.put(colCantidad, cantidad);
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
		
		private ContentValues createContentValuesImpuestoProducto(int idProd,int idTaxes){
			ContentValues values = new ContentValues();
			values.put(colIdProductCK, idProd);
			values.put(colIdTaxesCK,idTaxes);
			return values;
		}
		
		private ContentValues createContentValuesUpdateCantidadCortesia(int cantidad,int cortesias){
			ContentValues values = new ContentValues();
			values.put(colCantidad, cantidad);
			values.put(colCortesia, cortesias);
			return values;
		}
}
