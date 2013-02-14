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
 * �f�[�^�x�[�X�����N���X
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

	// �f�[�^�x�[�X���̒萔
	private static final String DB_NAME = "MY_LOGGER_DATABASE";
	public static final String TABLE_NAME = "TABLE_NAME";
	

	/**
	 * �R���X�g���N�^
	 */
	public DatabaseOpenHelper(Context context) {
		// �w�肵���f�[�^�x�[�X�������݂��Ȃ��ꍇ�́A�V���ɍ쐬����onCreate()���Ă΂��
		// �o�[�W������ύX�����onUpgrade()���Ă΂��
		super(context, DB_NAME, null, 1);
	}
	
	/**
	 * �f�[�^�x�[�X�̐����ɌĂяo�����̂ŁA �X�L�[�}�̐������s��
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.beginTransaction();
		
		try{
			// �e�[�u���̐���
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	/**
	 * �e�[�u���쐬
	 *@param db �f�[�^�x�[�X
	 *@param table_name �e�[�u���� 
	 *@param dateRecord ���t���L�^���邩�H
	 *@param timeRecord �������L�^���邩�H
	 */
	public void createTable(String table_name,Boolean dateRecord, Boolean timeRecord) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction();

		try {
			// �e�[�u���̐���
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
	 * �f�t�H���g�ł͓��t�K�{�Ŏ����͋L�^���Ȃ�
	 * @param table_name
	 */
	public void createTable(String table_name) {
		createTable(table_name,true,false);
	}
	public void dropTable(String table_name) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction();

		try {
			// �e�[�u���̐���
			String sql = "drop table " + table_name;
			db.execSQL(sql);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}


	/**
	 * �f�[�^�x�[�X�̍X�V �e�N���X�̃R���X�g���N�^�ɓn��version��ύX�����Ƃ��ɌĂяo�����
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// �w�肵���e�[�u���̃J�����\�����`�F�b�N���A
		// �����̃J�����ɂ��Ă̓A�b�v�O���[�h����f�[�^�������p���܂��B
		// �����̃J�����Ō^�Ɍ݊������Ȃ��ꍇ�̓G���[�ɂȂ�̂Œ��ӁB

		// �X�V�Ώۂ̃e�[�u��
		List<String> targetTables = list();
		db.beginTransaction();
		try {
			for(String targetTable: targetTables){
			// ���J�����ꗗ
			final List<String> columns = getColumns(targetTable,null);
			// ������
			db.execSQL("ALTER TABLE " + targetTable + " RENAME TO temp_"
					+ targetTable);
			onCreate(db);
			// �V�J�����ꗗ
			final List<String> newColumns = getColumns(targetTable,null);

			// �ω����Ȃ��J�����̂ݒ��o
			columns.retainAll(newColumns);

			// ���ʃf�[�^���ڂ��B(OLD�ɂ������݂��Ȃ����͎̂̂Ă��, NEW�ɂ������݂��Ȃ����̂�NULL�ɂȂ�)
			final String cols = join(columns, ",");
			db.execSQL(String.format(
					"INSERT INTO %s (%s) SELECT %s from temp_%s", targetTable,
					cols, cols, targetTable));
			// �I������
			db.execSQL("DROP TABLE temp_" + targetTable);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}	
	/**
	 * �w�肵���e�[�u���̃J���������X�g���擾����B
	 * @param db
	 * @param tableName
	 * @return �J�������̃��X�g
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
	 * �������C�ӂ̋�؂蕶���ŘA������B
	 * @param list
	 * ������̃��X�g
	 * @param delim
	 * ��؂蕶��
	 * @return �A����̕�����
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
	 * �e�[�u�����ꗗ���擾����
	 * @return ��������
	 */
	public List<String> list() {
		SQLiteDatabase db = getReadableDatabase();
		
		List<String> tableList;
		try {
			Cursor cursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table' ", null);
			Log.d("��", String.valueOf(cursor.getCount()));
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
	 * �J�����̖��O�A�^�A�l�̈ꗗ���擾����
	 * @return name,type,value��Tuple
	 */
	public List<ColumnTuple> listAllColumns(String tableName) {
		SQLiteDatabase db = getReadableDatabase();
		
		List<ColumnTuple> tupleList;
		try {
			String rquery = "PRAGMA table_info ('" + tableName +"')"; 
			Cursor cursor = db.rawQuery(rquery,null);
			Log.d("��", String.valueOf(cursor.getCount()));
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
	 * �J�����̖��O�A�^�A�l�̈ꗗ���擾����
	 * @return name,type,value��Tuple
	 */
	public List<ColumnTuple> listColumns(String tableName) {
		SQLiteDatabase db = getReadableDatabase();
		
		List<ColumnTuple> tupleList;
		try {
			String rquery = "PRAGMA table_info ('" + tableName +"')"; 
			Cursor cursor = db.rawQuery(rquery,null);
			Log.d("��", String.valueOf(cursor.getCount()));
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
	 * �J������ǉ�����
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
	 * �J�������폜
	 * @param tableName
	 * @param columnName
	 */
	public boolean dropColumn(String tableName, String columnName){
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try{
//			db.execSQL("ALTER TABLE " + tableName + " DROP " + columnName);
			// ���J�����ꗗ
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
			// �I������
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
	 *  �J�������ύX
	 * @param tableName
	 * @param beforeColumnName
	 * @param afterColumnName
	 */
	public boolean renameColumn(String tableName, String beforeColumnName,String afterColumnName){
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try{
			// ���J�����ꗗ
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
			// �I������
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