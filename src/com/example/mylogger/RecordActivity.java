package com.example.mylogger;

import com.example.mylogger.db.ColumnTuple;
import com.example.mylogger.db.DatabaseOpenHelper;
import com.example.mylogger.db.MyRecord;
import com.example.mylogger.db.MyRecordDao;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class RecordActivity extends Activity {
	String tableName;
	MyRecord record = null;
	TableLayout parentTableLayout;
	LinearLayout parentLinearLayout;
	
	TextView dateText;
	TextView timeText;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // �����������ꂽR.java�̒萔���w�肵��XML���烌�C�A�E�g�𐶐�
        setContentView(R.layout.record);

        // Intent����Ώۂ̃e�[�u�������擾
        tableName = (String)getIntent().getSerializableExtra( DatabaseOpenHelper.TABLE_NAME);
        record = (MyRecord)getIntent().getSerializableExtra( tableName);        
        parentTableLayout = (TableLayout)findViewById(R.id.parentTableLayout);
        parentLinearLayout = (LinearLayout)findViewById(R.id.parentLinearLayout);
        
        readView();
  
    }
	
	/**
	 * �A�N�e�B�r�e�B���O�ʂɗ��邽�тɃf�[�^���X�V
	 */
	@Override
	protected void onResume() {
	        super.onResume();

	}
    /**
     * �I�v�V�������j���[�̐���
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // XML�Œ�`����menu���w�肷��B
        inflater.inflate(R.menu.record, menu);
        return true;
    }
    /**
     * �I�v�V�������j���[�̑I��
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
    	MyRecordDao mrd = new MyRecordDao(this);
        switch (itemId) {
        case R.id.menu_save:
        	Integer num = record.COLUMNS.size();
        	ColumnTuple.TYPE type;        	
        	for(Integer i= 0;i<num;++i){
        		TableRow tr = (TableRow)parentTableLayout.getChildAt(i);
        		type = (ColumnTuple.TYPE)tr.getTag();
        		TextView tv = (TextView)tr.getChildAt(0);
        		EditText et = (EditText)tr.getChildAt(1);
        		record.COLUMNS.get(i).name = tv.getText().toString();
        		String value = et.getText().toString();
        		switch(type){
        		case INTEGER:
            		if(value.equals("")){
            			record.COLUMNS.get(i).intValue = null;
            		}else{
            			record.COLUMNS.get(i).intValue = Integer.valueOf(value);
            		}
        			break;
        		case REAL:
            		if(value.equals("")){
            			record.COLUMNS.get(i).doubleValue = null;
            		}else{
            			record.COLUMNS.get(i).doubleValue = Double.valueOf(value);
            		}
        			break;
        		case TEXT:
            		record.COLUMNS.get(i).stringValue = value;
        			break;
        		default:
        			break;
        		}        		
        	}
        	mrd.save(record);
        	finish();
            break;
        case R.id.menu_delete:
        	mrd.delete(record);
        	finish();
        	break;
        }
        return true;
    };
	
	void readView(){
		if(record.getYear() != null){
			if(dateText==null){
				dateText= new TextView(this);
				parentLinearLayout.addView(dateText);
			}
			Integer[] date = record.getDate();
			dateText.setText(date[0] + "/" + date[1]+1 + "/" + date[2]);
			dateText.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					// ���t���̓_�C�A���O�쐬
					new DatePickerDialog(RecordActivity.this,new OnDateSetListener(){
						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							try{
								record.setDate(year, monthOfYear, dayOfMonth);
							}catch(Exception ex){
								Toast.makeText(RecordActivity.this, "���t�ύX���s", Toast.LENGTH_LONG).show();
							}finally{
								readView();
							}							
						}
					},record.getYear(),record.getMonth(),record.getDay())
					.show();
				}
			});
		}
		if(record.getHour() != null){
			if(timeText==null){
				timeText= new TextView(this);
				parentLinearLayout.addView(timeText);
			}
			Integer[] time = record.getTime();
			timeText.setText(time[0] + ":" + time[1]);
			timeText.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					// �������̓_�C�A���O�쐬
					new TimePickerDialog(RecordActivity.this,new OnTimeSetListener(){
	
						@Override
						public void onTimeSet(TimePicker view, int hour,
								int minute) {
							try{
								record.setTime(hour,minute);
							}catch(Exception ex){
								Toast.makeText(RecordActivity.this, "�����ύX���s", Toast.LENGTH_LONG).show();
							}finally{
								readView();
							}															
						}
					},record.getHour(),record.getMinute(),true).show();
				}
			});
		}
		
		parentTableLayout.removeAllViews();
		for(ColumnTuple tuple : record.COLUMNS){
			TableRow tr = new TableRow(this);
			TextView tv = new TextView(this);
			EditText et = new EditText(this);
			tv.setText(tuple.name);
			tr.setTag(tuple.type);
			switch(tuple.type){
			case INTEGER:
				et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL );
				et.setHint("�����l");
				if(tuple.intValue!=null){
					et.setText(String.valueOf(tuple.intValue));
				}
				break;
			case TEXT:
				et.setHint("������");
				if(tuple.stringValue!=null){
					et.setText(tuple.stringValue);
				}
				break;
			case REAL:
				et.setInputType(InputType.TYPE_CLASS_NUMBER);
				et.setHint("�����l");
				if(tuple.doubleValue!=null){
					et.setText(String.valueOf(tuple.doubleValue));
				}
				break;
			default:
				break;				
			}
			tr.addView(tv);
			tr.addView(et);
			parentTableLayout.addView(tr);			
		}
	}

}
