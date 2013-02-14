package com.example.mylogger.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class MyRecord implements Serializable {
		

	// テーブル名
	private String tableName;
	
	// カラム
	public final static String COLUMN_ID = "_id";
	public final static String COLUMN_YEAR = "year";
	public final static String COLUMN_MONTH = "month";
	public final static String COLUMN_DAY = "day";
	public final static String COLUMN_HOUR = "hour";
	public final static String COLUMN_MINUTE = "minute";
	private Long rowId;
	private Integer year;
	private Integer month;
	private Integer day;
	private Integer hour;
	private Integer minute;
	public List<ColumnTuple> COLUMNS = new ArrayList<ColumnTuple>();
	
	


	/**
	 * ListView表示の際に利用するので日付+熱量を返す
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if(year != null){
			builder.append(year + "/" + month+1 + "/" + day + ", ");
		}
		if(hour != null){
			builder.append(hour + ":" + minute + ", ");
		}
		if(COLUMNS.size()>0){
			builder.append(COLUMNS.get(0).name);
		}
		return builder.toString();
	}




	public Long getRowId() {
		return rowId;
	}
	public void setRowId(Long rowId) {
		this.rowId = rowId;
	}

	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}




	public Integer getYear() {
		return year;
	}




	public void setYear(Integer year) {
		this.year = year;
	}




	public Integer getMonth() {
		return month;
	}




	public void setMonth(Integer month) {
		this.month = month;
	}




	public Integer getDay() {
		return day;
	}




	public void setDay(Integer day) {
		this.day = day;
	}




	public Integer getHour() {
		return hour;
	}




	public void setHour(Integer hour) {
		this.hour = hour;
	}




	public Integer getMinute() {
		return minute;
	}




	public void setMinute(Integer minute) {
		this.minute = minute;
	}
	public void setDate(Integer _year,Integer _month,Integer _day)throws Exception{
		if(_year<0){
			throw new Exception();
		}
		if(_month<0 || _month >=12){
			throw new Exception();
		}
		if(_day<0 || _day>31){
			throw new Exception();
		}

		year = _year;
		month = _month;
		day = _day;
	}
	public Integer[] getDate(){
		return new Integer[]{year,month,day};
	}
	public void setTime(Integer _hour, Integer _minute)throws Exception{
		if(_hour<0 || _hour >=24){
			throw new Exception();
		}
		if(_minute<0 || _minute >60){
			throw new Exception();
		}
		hour =_hour;
		minute = _minute;
	}
	public Integer[] getTime(){
		return new Integer[]{hour,minute};
	}
}
