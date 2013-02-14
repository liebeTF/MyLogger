package com.example.mylogger.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.mylogger.db.ColumnTuple.TYPE;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * データベース処理クラス
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

	// データベース名の定数
	private static final String DB_NAME = "MY_LOGGER_DATABASE";
	public static final String TABLE_NAME = "TABLE_NAME";
	

	/**
	 * コンストラクタ
	 */
	public DatabaseOpenHelper(Context context) {
		// 指定したデータベース名が存在しない場合は、新たに作成されonCreate()が呼ばれる
		// バージョンを変更するとonUpgrade()が呼ばれる
		super(context, DB_NAME, null, 1);
	}
	
	/**
	 * データベースの生成に呼び出されるので、 スキーマの生成を行う
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.beginTransaction();
		
		try{
			// テーブルの生成
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	/**
	 * テーブル作成
	 *@param db データベース
	 *@param table_name テーブル名 
	 *@param dateRecord 日付を記録するか？
	 *@param timeRecord 時刻を記録するか？
	 */
	public void createTable(String table_name,Boolean dateRecord, Boolean timeRecord) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction();

		try {
			// テーブルの生成
			String sql = "create table " + table_name+"(" + MyRecord.COLUMN_ID + " integer primary key autoincrement";
			if(dateRecord){
				sql += "," + MyRecord.COLUMN_YEAR +" integer"
						+ "," + MyRecord.COLUMN_MONTH +" integer"
						+ "," + MyRecord.COLUMN_DAY +" integer";						
			}
			if(timeRecord){
				sql += "," + MyRecord.COLUMN_HOUR +" integer"
						+ "," + MyRecord.COLUMN_MINUTE +" integer";						
			}
			sql += ")";
			db.execSQL(sql);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	/***
	 * デフォルトでは日付必須で時刻は記録しない
	 * @param table_name
	 */
	public void createTable(String table_name) {
		createTable(table_name,true,false);
	}
	public void dropTable(String table_name) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction();

		try {
			// テーブルの生成
			String sql = "drop table " + table_name;
			db.execSQL(sql);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}


	/**
	 * データベースの更新 親クラスのコンストラクタに渡すversionを変更したときに呼び出される
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 指定したテーブルのカラム構成をチェックし、
		// 同名のカラムについてはアップグレード後もデータを引き継ぎます。
		// 同名のカラムで型に互換性がない場合はエラーになるので注意。

		// 更新対象のテーブル
		List<String> targetTables = list();
		db.beginTransaction();
		try {
			for(String targetTable: targetTables){
			// 元カラム一覧
			final List<String> columns = getColumns(targetTable,null);
			// 初期化
			db.execSQL("ALTER TABLE " + targetTable + " RENAME TO temp_"
					+ targetTable);
			onCreate(db);
			// 新カラム一覧
			final List<String> newColumns = getColumns(targetTable,null);

			// 変化しないカラムのみ抽出
			columns.retainAll(newColumns);

			// 共通データを移す。(OLDにしか存在しないものは捨てられ, NEWにしか存在しないものはNULLになる)
			final String cols = join(columns, ",");
			db.execSQL(String.format(
					"INSERT INTO %s (%s) SELECT %s from temp_%s", targetTable,
					cols, cols, targetTable));
			// 終了処理
			db.execSQL("DROP TABLE temp_" + targetTable);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}	
	/**
	 * 指定したテーブルのカラム名リストを取得する。
	 * @param db
	 * @param tableName
	 * @return カラム名のリスト
	 */
	public List<String> getColumns( String tableName) {
		String[] exception = new String[]{
				MyRecord.COLUMN_ID, 
				MyRecord.COLUMN_YEAR, 
				MyRecord.COLUMN_MONTH, 
				MyRecord.COLUMN_DAY,
				MyRecord.COLUMN_HOUR,
				MyRecord.COLUMN_MINUTE
				};
		return getColumns(tableName,exception);
	}
	public List<String> getColumns( String tableName,String[] exception) {
		SQLiteDatabase db = getReadableDatabase();
		List<String> ar = null;
		Cursor c = null;
		try {
			c = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);
			if (c != null) {
				ar = new ArrayList<String>(Arrays.asList(c.getColumnNames()));
				if(exception!=null){
					for (String s : exception) {
						ar.remove(s);
					}
				}
			}
		} catch(Exception ex){
			Log.d("Exception:", ex.getMessage());
		}finally {
			if (c != null)
				c.close();
		}
		return ar;
	}
	 
	/**
	 * 文字列を任意の区切り文字で連結する。
	 * @param list
	 * 文字列のリスト
	 * @param delim
	 * 区切り文字
	 * @return 連結後の文字列
	 */
	public static String join(List<String> list, String delim) {
		final StringBuilder buf = new StringBuilder();
		final int num = list.size();
		for (int i = 0; i < num; i++) {
			if (i != 0)
				buf.append(delim);
			buf.append((String) list.get(i));
		}
		return buf.toString();
	}
	/**
	 * テーブル名一覧を取得する
	 * @return 検索結果
	 */
	public List<String> list() {
		SQLiteDatabase db = getReadableDatabase();
		
		List<String> tableList;
		try {
			Cursor cursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table' ", null);
			Log.d("個数", String.valueOf(cursor.getCount()));
			tableList = new ArrayList<String>();
			cursor.moveToFirst();
			while( !cursor.isAfterLast()){
				String name = cursor.getString(cursor.getColumnIndex("name"));
				if(!name.equals("android_metadata") &&
						!name.equals("sqlite_sequence")){
					tableList.add( name);
				}
				cursor.moveToNext();
			}
		} finally {
		}
		return tableList;
	}
	/**
	 * カラムの名前、型、値の一覧を取得する
	 * @return name,type,valueのTuple
	 */
	public List<ColumnTuple> listAllColumns(String tableName) {
		SQLiteDatabase db = getReadableDatabase();
		
		List<ColumnTuple> tupleList;
		try {
			String rquery = "PRAGMA table_info ('" + tableName +"')"; 
			Cursor cursor = db.rawQuery(rquery,null);
			Log.d("個数", String.valueOf(cursor.getCount()));
			tupleList = new ArrayList<ColumnTuple>();
			cursor.moveToFirst();
			while( !cursor.isAfterLast()){
				ColumnTuple tuple = new ColumnTuple();
				tuple.name = cursor.getString(cursor.getColumnIndex("name"));
				String type = cursor.getString(cursor.getColumnIndex("type"));
				tuple.NotNull = cursor.getString((cursor.getColumnIndex("type"))).equals("0") ? false:true;
				if(type.equals("integer")){
					tuple.type = TYPE.INTEGER;					
				}else if(type.equals("text")){
					tuple.type = TYPE.TEXT;	
				}else if(type.equals("real")){
					tuple.type = TYPE.REAL;	
				}else if(type.equals("blob")){
					tuple.type = TYPE.BLOB;	
				}else{
					tuple.type = TYPE.NULL;	
				}								
				tupleList.add( tuple);
				cursor.moveToNext();
			}
		} finally {
			
		}
		return tupleList;
	}
	/**
	 * カラムの名前、型、値の一覧を取得する
	 * @return name,type,valueのTuple
	 */
	public List<ColumnTuple> listColumns(String tableName) {
		SQLiteDatabase db = getReadableDatabase();
		
		List<ColumnTuple> tupleList;
		try {
			String rquery = "PRAGMA table_info ('" + tableName +"')"; 
			Cursor cursor = db.rawQuery(rquery,null);
			Log.d("個数", String.valueOf(cursor.getCount()));
			tupleList = new ArrayList<ColumnTuple>();
			cursor.moveToFirst();
			while( !cursor.isAfterLast()){
				ColumnTuple tuple = new ColumnTuple();
				tuple.name = cursor.getString(cursor.getColumnIndex("name"));
				if(tuple.name.equals(MyRecord.COLUMN_ID)
						||tuple.name.equals(MyRecord.COLUMN_YEAR)
						||tuple.name.equals(MyRecord.COLUMN_MONTH)
						||tuple.name.equals(MyRecord.COLUMN_DAY)
						||tuple.name.equals(MyRecord.COLUMN_HOUR)
						||tuple.name.equals(MyRecord.COLUMN_MINUTE)
						){
					cursor.moveToNext();
					continue;
				}
				String type = cursor.getString(cursor.getColumnIndex("type"));
				if(type.equals("integer")){
					tuple.type = TYPE.INTEGER;					
				}else if(type.equals("text")){
					tuple.type = TYPE.TEXT;	
				}else if(type.equals("real")){
					tuple.type = TYPE.REAL;	
				}else{
					tuple.type = TYPE.NULL;	
				}								
				tupleList.add( tuple);
				cursor.moveToNext();
			}
		} finally {
			
		}
		return tupleList;
	}

	/**
	 * カラムを追加する
	 * @param tableName
	 * @param columnName
	 */
	public boolean addColumn(String tableName, String columnName, ColumnTuple.TYPE type){
		String columnType = null;
		switch(type){
		case INTEGER:
			columnType = "integer";
			break;
		case REAL:
			columnType = "real";
			break;
		case TEXT:
			columnType = "text";
			break;
			
		}
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try{
			db.execSQL("ALTER TABLE " + tableName + " ADD " + columnName + " " + columnType);
			db.setTransactionSuccessful();
		}catch(Exception e){
			return false;
		}finally{
			db.endTransaction();
		}
		return true;
	}
	/**
	 * カラムを削除
	 * @param tableName
	 * @param columnName
	 */
	public boolean dropColumn(String tableName, String columnName){
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try{
//			db.execSQL("ALTER TABLE " + tableName + " DROP " + columnName);
			// 元カラム一覧
			List<String> columns = getColumns(tableName,null);
			String sql = String.format("ALTER TABLE %s RENAME TO tmp_%s", tableName,tableName); 
			db.execSQL(sql);
			columns.remove(columnName);
			final String cols = join(columns, ",");
			
			sql = String.format("CREATE TABLE %s (%s)", tableName, cols); 
			db.execSQL(sql);
			sql = String.format(
					"INSERT INTO %s (%s) SELECT %s FROM temp_%s", tableName,
					cols, cols, tableName); 
			db.execSQL(sql);
			// 終了処理
			db.execSQL("DROP TABLE temp_" + tableName);

			db.setTransactionSuccessful();
		}catch(Exception ex){
			Log.d(ex.toString(), ex.getMessage());
		}finally{
			db.endTransaction();
		}
		return true;
	}
	 /**
	 *  カラム名変更
	 * @param tableName
	 * @param beforeColumnName
	 * @param afterColumnName
	 */
	public boolean renameColumn(String tableName, String beforeColumnName,String afterColumnName){
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try{
			// 元カラム一覧
			List<String> columns = getColumns(tableName,null);
			String tmpTableName = "tmp_"+tableName;
			String sql = String.format("ALTER TABLE %s RENAME TO %s", tableName, tmpTableName); 

			db.execSQL("ALTER TABLE " + tableName + " RENAME TO " + tmpTableName);
			final String oldCols = join(columns, ",");
			for(Integer i=0;i<columns.size();++i){				
				if(columns.get(i).equals(beforeColumnName)){
					columns.set(i,afterColumnName);
					break;
				}
			}
			final String newCols = join(columns, ",");
			
			sql = String.format("CREATE TABLE %s (%s)", tableName, newCols); 
			db.execSQL(sql);
			sql = String.format(
					"INSERT INTO %s (%s) SELECT %s FROM %s", tableName,
					newCols, oldCols, tmpTableName); 
			db.execSQL(sql);
			// 終了処理
			db.execSQL("DROP TABLE " + tmpTableName);

			db.setTransactionSuccessful();
		}catch(Exception ex){
			Log.d(ex.toString(), ex.getMessage());
		}finally{
			db.endTransaction();
		}
		return true;
	}

}