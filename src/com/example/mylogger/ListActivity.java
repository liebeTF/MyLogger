package com.example.mylogger;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.example.mylogger.db.ColumnTuple;
import com.example.mylogger.db.ColumnTuple.TYPE;
import com.example.mylogger.db.DatabaseOpenHelper;
import com.example.mylogger.db.MyRecord;
import com.example.mylogger.db.MyRecordDao;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * �ꗗ�\���A�N�e�B�r�e�B
 */
public class ListActivity extends Activity implements OnItemClickListener{
	static DatabaseOpenHelper helper ;
	static MyRecordDao dao;
	private String tableName;
	ColumnTuple.TYPE type = null;
	Boolean dateRecord = false;
	Boolean timeRecord = false;
	
	List<String> columnList = null;
	String columnsString;
	
	
    // �ꗗ�\���pListView
    private ListView listView = null;
    private ArrayAdapter<MyRecord> arrayAdapter = null;
    private TextView columnsText = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new DatabaseOpenHelper(this);
        // �����������ꂽR.java�̒萔���w�肵��XML���烌�C�A�E�g�𐶐�
        setContentView(R.layout.list);

        // Intent����Ώۂ̃e�[�u�������擾
        tableName = (String)getIntent().getSerializableExtra( DatabaseOpenHelper.TABLE_NAME);

        // XML�Œ�`����android:id�̒l���w�肵��ListView���擾���܂��B
        listView = (ListView) findViewById(R.id.list);
        columnsText = (TextView)findViewById(R.id.columnsText);

        // ListView�ɕ\������v�f��ێ�����A�_�v�^�𐶐����܂��B
        arrayAdapter = new ArrayAdapter<MyRecord>(this,
                android.R.layout.simple_list_item_1);

        // �A�_�v�^��ݒ�
        listView.setAdapter(arrayAdapter);
        
        // ���X�i�̒ǉ�
        listView.setOnItemClickListener( this);
        columnsText.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO �����������ꂽ���\�b�h�E�X�^�u
				SetColumnDialog d = new SetColumnDialog(ListActivity.this);
				d.setTableName(tableName);
				d.show();
			}
		});
		
        dao = new MyRecordDao(ListActivity.this);
    }
	
	/**
	 * �A�N�e�B�r�e�B���O�ʂɗ��邽�тɃf�[�^���X�V
	 */
	@Override
	protected void onResume() {
	        super.onResume();

	        // �f�[�^�擾�^�X�N�̎��s
	        DataLoadTask task = new DataLoadTask();
	        task.execute();
	}

	/**
	 * �ꗗ�f�[�^�̎擾�ƕ\�����s���^�X�N
	 */
	public class DataLoadTask extends AsyncTask<Object, Integer, List<MyRecord>> {
	        // �������_�C�A���O
	        private ProgressDialog progressDialog = null;

	        @Override
	        protected void onPreExecute() {
	                // �o�b�N�O���E���h�̏����O��UI�X���b�h�Ń_�C�A���O�\��
	                progressDialog = new ProgressDialog(ListActivity.this);
	                progressDialog.setMessage(getResources().getText(
	                                R.string.data_loading));
	                progressDialog.setIndeterminate(true);
	                progressDialog.show();
	        }

	        @Override
	        protected List<MyRecord> doInBackground(Object... params) {
	                // �ꗗ�f�[�^�̎擾���o�b�N�O���E���h�Ŏ��s
	        	if(dao == null){
	                dao = new MyRecordDao(ListActivity.this);
	        	}
	        	return dao.list(tableName);
	        }

	        @Override
	        protected void onPostExecute(List<MyRecord> result) {
	                // �������_�C�A���O���N���[�Y
	                progressDialog.dismiss();

	                // �\���f�[�^�̃N���A
	                arrayAdapter.clear();

	                //�J�����ꗗ�̓ǂݍ���
	                readColumnlist();
	                

	                // �\���f�[�^�̐ݒ�
	                for (MyRecord record : result) {
	                        arrayAdapter.add(record);
	                }
	        }
	}
    /**
     * List�v�f�N���b�N���̏���
     * �I�����ꂽ�G���e�B�e�B���l�߂ĎQ�Ɖ�ʂ֑J�ڂ���
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // �I�����ꂽ�v�f���擾����
        MyRecord record = (MyRecord)parent.getItemAtPosition( position);
        // �Q�Ɖ�ʂ֑J�ڂ��閾���I�C���e���g�𐶐�
        Intent recordIntent = new Intent( this, RecordActivity.class);
        // �I�����ꂽ�I�u�W�F�N�g���C���e���g�ɋl�߂�
        recordIntent.putExtra( DatabaseOpenHelper.TABLE_NAME, tableName);
        recordIntent.putExtra( tableName, record);
        // �A�N�e�B�r�e�B���J�n����
        startActivity( recordIntent);
    }
    /**
     * �I�v�V�������j���[�̐���
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // XML�Œ�`����menu���w�肷��B
        inflater.inflate(R.menu.list, menu);
        return true;
    }
    /**
     * �I�v�V�������j���[�̑I��
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
        case R.id.menu_add_column:
     	   //�e�L�X�g���͂��󂯕t����r���[���쐬���܂��B
     	final EditText editView = new EditText(ListActivity.this);
     	editView.setHint("���ږ�");
     	final String items[] = {"������","����","����"};
     	type = TYPE.TEXT;
     	
     	new AlertDialog.Builder(ListActivity.this)
     	.setIcon(android.R.drawable.ic_dialog_info)
     	.setTitle("�e�L�X�g���̓_�C�A���O")
     	.setView(editView)
     	.setSingleChoiceItems(items, 0,
     			new DialogInterface.OnClickListener() {
            @Override
            public void onClick(
              DialogInterface dialog, 
              int which) {
              Log.d("ListActivity", items[which] + " Selected");
              switch(which){
              case 0:
            	  type = TYPE.TEXT;
            	  break;
              case 1:
            	  type = TYPE.INTEGER;
            	  break;
              case 2:
            	  type = TYPE.REAL;
            	  break;
              default:
            	  break;
              }
            }
          })
     	.setPositiveButton("OK", new DialogInterface.OnClickListener() {
     		
     		public void onClick(DialogInterface dialog, int whichButton) {
     			//���͂����������g�[�X�g�o�͂���
     			
     			String text = editView.getText().toString();
     			if(text.equals("")){
         			Toast.makeText(ListActivity.this,
             				"���ږ��͕K�{�ł�",
             				Toast.LENGTH_LONG).show();
         			
     			}else{
     				Toast.makeText(ListActivity.this,
     						text,Toast.LENGTH_LONG).show();
     				helper.addColumn(tableName, text, type);
    			}
     		}
     	})
     	.setNegativeButton("�L�����Z��", new DialogInterface.OnClickListener() {            
     		public void onClick(DialogInterface dialog, int whichButton) {
     			}        
     		})
     		.show();
            break;
        case R.id.menu_new:
        	Calendar cal = Calendar.getInstance();
        	
        	final MyRecord record = new MyRecord();
        	record.setTableName(tableName);
        	record.COLUMNS = helper.listColumns(tableName);
        	Iterator<ColumnTuple> itr = record.COLUMNS.iterator();
        	while(itr.hasNext()){
        		ColumnTuple tuple = itr.next();
        		if(tuple.name.equals(MyRecord.COLUMN_YEAR)
        				|| tuple.name.equals(MyRecord.COLUMN_MONTH)
        				|| tuple.name.equals(MyRecord.COLUMN_DAY)){
        			dateRecord = true;
        			itr.remove();
        		}else if(tuple.name.equals(MyRecord.COLUMN_HOUR)
        				|| tuple.name.equals(MyRecord.COLUMN_MINUTE)){
        			timeRecord = true;
        			itr.remove();
        		}
        	}        	
        	if(dateRecord){
            	try{
            		record.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            	}catch(Exception ex){
            		
            	}
				new DatePickerDialog(ListActivity.this,new OnDateSetListener(){
					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						try{
							record.setDate(year, monthOfYear, dayOfMonth);
							if(!timeRecord){
								toRecordActivity(record);
							}
						}catch(Exception ex){
							Toast.makeText(ListActivity.this, "���t�ύX���s", Toast.LENGTH_LONG)
							.show();
						}finally{
						}							
					}
				},record.getYear(),record.getMonth(),record.getDay())
				.show();
        	}        	
        	if(timeRecord){
            	try{
            		record.setTime(cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));
            	}catch(Exception ex){
            		
            	}
				new TimePickerDialog(ListActivity.this,
						new OnTimeSetListener() {
							@Override
							public void onTimeSet(TimePicker view, int hour,
									int minute) {
								try {
									record.setTime(hour, minute);
									toRecordActivity(record);
								} catch (Exception ex) {
									Toast.makeText(ListActivity.this, "�����ݒ莸�s",
											Toast.LENGTH_LONG)
											.show();
								} finally {
								}
							}
						}, record.getHour(), record.getMinute(), true)
				.show();
        	}else{
        		toRecordActivity(record);
        	}
        	break;
        case R.id.menu_table_delete:
        	helper.dropTable(tableName);
        	break;
        default:
        	break;
        }
        return true;
    };
    private void toRecordActivity(MyRecord record){
    	Intent recordIntent = new Intent(this,RecordActivity.class);
        recordIntent.putExtra( DatabaseOpenHelper.TABLE_NAME, tableName);
        recordIntent.putExtra( tableName, record);
        try {
        	startActivity(recordIntent);
         } catch (Exception ex) {
             Toast.makeText(ListActivity.this,
                     "��ʑJ�ڂɎ��s���܂����B", Toast.LENGTH_LONG)
                     .show();
         }

    }
    
    private void readColumnlist(){
        columnList = helper.getColumns(tableName,null);
        Iterator<String> itr = columnList.iterator();
        while(itr.hasNext()){
        	String column = itr.next();
        	 if(column.equals(MyRecord.COLUMN_ID)){
        		 itr.remove();
        		 continue;
        	 }else if(column.equals(MyRecord.COLUMN_YEAR)){
        		 dateRecord = true;
        		 itr.remove();
        		 continue;
        	 }else if(column.equals(MyRecord.COLUMN_MONTH)){
        		 dateRecord = true;
        		 itr.remove();
        		 continue;
        	 }else if(column.equals(MyRecord.COLUMN_DAY)){
        		 dateRecord = true;
        		 itr.remove();
        		 continue;
        	 }else if(column.equals(MyRecord.COLUMN_HOUR)){
        		 timeRecord = true;
        		 itr.remove();
        		 continue;
        	 }else if(column.equals(MyRecord.COLUMN_MINUTE)){
        		 timeRecord = true;
        		 itr.remove();
        		 continue;
        	 }
        }//end-of-while    	
        
        columnsString = "";
        if(dateRecord){
        	columnsString += "���t, ";
        }
        if(timeRecord){
        	columnsString += "����, ";
        }
        columnsString += DatabaseOpenHelper.join(columnList, ",");
        columnsText.setText(columnsString);

    }
}