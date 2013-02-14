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
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return name + ": " + type;
	}
	
	/***
	 * name����v���Ă��邩�ǂ���
	 * @param tuple
	 * @return
	 */
	public boolean equals(ColumnTuple tuple){
		return (name == tuple.name);
	}
}
