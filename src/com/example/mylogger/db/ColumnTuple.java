package com.example.mylogger.db;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ColumnTuple implements Serializable{
	public enum TYPE{
		NULL,
		INTEGER,
		TEXT,
		REAL,
		BLOB,		
	}
	public TYPE type;
	public Integer intValue;
	public String stringValue;
	public Double doubleValue;
	public byte[] byteValue;
	public String name;
	public Boolean NotNull;
	
	@Override
	public String toString() {
		// TODO 自動生成されたメソッド・スタブ
		return name + ": " + type;
	}
	
	/***
	 * nameが一致しているかどうか
	 * @param tuple
	 * @return
	 */
	public boolean equals(ColumnTuple tuple){
		return (name == tuple.name);
	}
}
