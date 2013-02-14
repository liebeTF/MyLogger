package com.example.mylogger.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.mylogger.db.ColumnTuple.TYPE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * MyRecord�p�f�[�^�A�N�Z�X�N���X
 */
public class MyRecordDao {
	
	private DatabaseOpenHelper helper = null;
	private static SQLiteDatabase db;
	
	public MyRecordDao(Context context) {
		helper = new DatabaseOpenHelper(context);
//		db = helper.getWritableDatabase();
	}
	
	/**
	 * Myrecord�̕ۑ�
	 * rowid��null�̏ꍇ��insert�Arowid��!null�̏ꍇ��update
	 * @param MyRecord �ۑ��Ώۂ̃I�u�W�F�N�g
	 * @return �ۑ�����
	 */
	public MyRecord save( MyRecord record){
		db = helper.getWritableDatabase();
		MyRecord result = null;
		try {
			ContentValues values = new ContentValues();
			for(ColumnTuple tuple:record.COLUMNS){
				switch (tuple.type){
				case INTEGER:
					values.put(tuple.name, tuple.intValue);
					break;
				case TEXT:
					values.put(tuple.name, tuple.stringValue);
					break;
				case REAL:
					values.put(tuple.name, tuple.doubleValue);
					break;
				default:
					break;				
				}
			}			
			Long rowId = record.getRowId();
			if(record.getYear() != null){
				Integer date[] = record.getDate();
				values.put(MyRecord.COLUMN_YEAR, date[0]);
				values.put(MyRecord.COLUMN_MONTH, date[1]);
				values.put(MyRecord.COLUMN_DAY, date[2]);				
			}
			if(record.getHour() != null){
				Integer time[] = record.getDate();
				values.put(MyRecord.COLUMN_HOUR, time[0]);
				values.put(MyRecord.COLUMN_MINUTE, time[1]);
			}
			
			// ID��null�̏ꍇ��insert
			if( rowId == null){
				rowId = db.insert( record.getTableName(), null, values);
			}
			else{
				db.update( record.getTableName(), values, MyRecord.COLUMN_ID + "=?", new String[]{ String.valueOf( rowId)});
			}
			result = load(record.getTableName(), rowId);
		} finally {
//			db.close();
		}
		return result;
	}
	
	/**
	 * ���R�[�h�̍폜
	 * @param MyRecord �폜�Ώۂ̃I�u�W�F�N�g
	 */
	public void delete(MyRecord record) {
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			db.delete( record.getTableName(), MyRecord.COLUMN_ID + "=?", new String[]{ String.valueOf( record.getRowId())});
		} finally {
//			db.close();
		}
	}
	
	/**
	 * id��MyRecord�����[�h����
	 * @param rowId PK
	 * @return ���[�h����
	 */
	public MyRecord load(String tableName,Long rowId) {
		SQLiteDatabase db = helper.getReadableDatabase();		
		MyRecord record = null;
		try {
			Cursor cursor = db.query( tableName, null, MyRecord.COLUMN_ID + "=?", new String[]{ String.valueOf( rowId)}, null, null, null);
			cursor.moveToFirst();
			record = getRecord(tableName, cursor);
		} finally {
//			db.close();
		}
		return record;
	}
	
	/**
	 * �ꗗ���擾����
	 * @return ��������
	 */
	public List<MyRecord> list(String tableName) {
		SQLiteDatabase db = helper.getReadableDatabase();
		
		List<MyRecord> RecordList = null;
		try {
			Cursor cursor = db.query( tableName, null, null, null, null, null, MyRecord.COLUMN_ID);
			RecordList = new ArrayList<MyRecord>();
			cursor.moveToFirst();
			while( !cursor.isAfterLast()){
				RecordList.add( getRecord(tableName, cursor));
				cursor.moveToNext();
			}
		} catch(Exception ex){
			Log.d(ex.toString(), ex.getMessage());
		}finally {
//			db.close();
		}
		return RecordList;
	}
	/**
	 * �e�[�u�����ꗗ���擾����
	 * @return ��������
	 */
	public List<String> list() {		
		return helper.list();
	}
	/**
	 * �J��������delim�ŘA�����Ď擾����
	 * @param tableName �e�[�u����
	 * @param delim �A������
	 * @return ��������
	 */
	public String getColumnsString(String tableName,String delim) {
		return DatabaseOpenHelper.join(helper.getColumns(tableName),delim);
	}
	public String getColumnsString(String tableName,String[] exception,String delim) {
		return DatabaseOpenHelper.join(helper.getColumns(tableName,exception),delim);
	}
	
	/**
	 * �J�[�\������I�u�W�F�N�g�ւ̕ϊ�
	 * @param cursor �J�[�\��
	 * @return �ϊ�����
	 */
	private MyRecord getRecord(String tableName,Cursor cursor){
		MyRecord record = new MyRecord();
		record.setTableName(tableName);
		List<ColumnTuple> columnsList =  helper.listAllColumns(tableName);
		Iterator<ColumnTuple> itr = columnsList.iterator();
		while(itr.hasNext()){
			ColumnTuple tuple = itr.next();			
			if(tuple.name.equals(MyRecord.COLUMN_ID)){
				record.setRowId(cursor.getLong(cursor.getColumnIndex(MyRecord.COLUMN_ID)));
				itr.remove();
				continue;
			}else if(tuple.name.equals(MyRecord.COLUMN_YEAR)){
				record.setYear(cursor.getInt(cursor.getColumnIndex(MyRecord.COLUMN_YEAR)));
				itr.remove();
				continue;
			}else if(tuple.name.equals(MyRecord.COLUMN_MONTH)){
				record.setMonth(cursor.getInt(cursor.getColumnIndex(MyRecord.COLUMN_MONTH)));
				itr.remove();
				continue;
			}else if(tuple.name.equals(MyRecord.COLUMN_DAY)){
				record.setDay(cursor.getInt(cursor.getColumnIndex(MyRecord.COLUMN_DAY)));
				itr.remove();
				continue;
			}else if(tuple.name.equals(MyRecord.COLUMN_HOUR)){
				record.setHour(cursor.getInt(cursor.getColumnIndex(MyRecord.COLUMN_HOUR)));
				itr.remove();
				continue;
			}else if(tuple.name.equals(MyRecord.COLUMN_MINUTE)){
				record.setMinute(cursor.getInt(cursor.getColumnIndex(MyRecord.COLUMN_MINUTE)));
				itr.remove();
				continue;
			}

			switch(tuple.type){
			case INTEGER:
				tuple.intValue=cursor.getInt(cursor.getColumnIndex(tuple.name));
				break;
			case TEXT:
				tuple.stringValue = cursor.getString(cursor.getColumnIndex(tuple.name));
				break;
			case REAL:
				tuple.doubleValue = cursor.getDouble(cursor.getColumnIndex(tuple.name));;
				break;
			case BLOB:
				tuple.byteValue = cursor.getBlob((cursor.getColumnIndex(tuple.name)));
				break;
			default:
				tuple.type = TYPE.NULL;
				break;
			}
			record.COLUMNS.add(tuple);
		}
		return record;
	}
}


