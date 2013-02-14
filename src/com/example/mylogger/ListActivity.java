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
 * 一覧表示アクティビティ
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
	
	
    // 一覧表示用ListView
    private ListView listView = null;
    private ArrayAdapter<MyRecord> arrayAdapter = null;
    private TextView columnsText = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new DatabaseOpenHelper(this);
        // 自動生成されたR.javaの定数を指定してXMLからレイアウトを生成
        setContentView(R.layout.list);

        // Intentから対象のテーブル名を取得
        tableName = (String)getIntent().getSerializableExtra( DatabaseOpenHelper.TABLE_NAME);

        // XMLで定義したandroid:idの値を指定してListViewを取得します。
        listView = (ListView) findViewById(R.id.list);
        columnsText = (TextView)findViewById(R.id.columnsText);

        // ListViewに表示する要素を保持するアダプタを生成します。
        arrayAdapter = new ArrayAdapter<MyRecord>(this,
                android.R.layout.simple_list_item_1);

        // アダプタを設定
        listView.setAdapter(arrayAdapter);
        
        // リスナの追加
        listView.setOnItemClickListener( this);
        columnsText.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				SetColumnDialog d = new SetColumnDialog(ListActivity.this);
				d.setTableName(tableName);
				d.show();
			}
		});
		
        dao = new MyRecordDao(ListActivity.this);
    }
	
	/**
	 * アクティビティが前面に来るたびにデータを更新
	 */
	@Override
	protected void onResume() {
	        super.onResume();

	        // データ取得タスクの実行
	        DataLoadTask task = new DataLoadTask();
	        task.execute();
	}

	/**
	 * 一覧データの取得と表示を行うタスク
	 */
	public class DataLoadTask extends AsyncTask<Object, Integer, List<MyRecord>> {
	        // 処理中ダイアログ
	        private ProgressDialog progressDialog = null;

	        @Override
	        protected void onPreExecute() {
	                // バックグラウンドの処理前にUIスレッドでダイアログ表示
	                progressDialog = new ProgressDialog(ListActivity.this);
	                progressDialog.setMessage(getResources().getText(
	                                R.string.data_loading));
	                progressDialog.setIndeterminate(true);
	                progressDialog.show();
	        }

	        @Override
	        protected List<MyRecord> doInBackground(Object... params) {
	                // 一覧データの取得をバックグラウンドで実行
	        	if(dao == null){
	                dao = new MyRecordDao(ListActivity.this);
	        	}
	        	return dao.list(tableName);
	        }

	        @Override
	        protected void onPostExecute(List<MyRecord> result) {
	                // 処理中ダイアログをクローズ
	                progressDialog.dismiss();

	                // 表示データのクリア
	                arrayAdapter.clear();

	                //カラム一覧の読み込み
	                readColumnlist();
	                

	                // 表示データの設定
	                for (MyRecord record : result) {
	                        arrayAdapter.add(record);
	                }
	        }
	}
    /**
     * List要素クリック時の処理
     * 選択されたエンティティを詰めて参照画面へ遷移する
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 選択された要素を取得する
        MyRecord record = (MyRecord)parent.getItemAtPosition( position);
        // 参照画面へ遷移する明示的インテントを生成
        Intent recordIntent = new Intent( this, RecordActivity.class);
        // 選択されたオブジェクトをインテントに詰める
        recordIntent.putExtra( DatabaseOpenHelper.TABLE_NAME, tableName);
        recordIntent.putExtra( tableName, record);
        // アクティビティを開始する
        startActivity( recordIntent);
    }
    /**
     * オプションメニューの生成
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // XMLで定義したmenuを指定する。
        inflater.inflate(R.menu.list, menu);
        return true;
    }
    /**
     * オプションメニューの選択
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
        case R.id.menu_add_column:
     	   //テキスト入力を受け付けるビューを作成します。
     	final EditText editView = new EditText(ListActivity.this);
     	editView.setHint("項目名");
     	final String items[] = {"文字列","整数","実数"};
     	type = TYPE.TEXT;
     	
     	new AlertDialog.Builder(ListActivity.this)
     	.setIcon(android.R.drawable.ic_dialog_info)
     	.setTitle("テキスト入力ダイアログ")
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
     			//入力した文字をトースト出力する
     			
     			String text = editView.getText().toString();
     			if(text.equals("")){
         			Toast.makeText(ListActivity.this,
             				"項目名は必須です",
             				Toast.LENGTH_LONG).show();
         			
     			}else{
     				Toast.makeText(ListActivity.this,
     						text,Toast.LENGTH_LONG).show();
     				helper.addColumn(tableName, text, type);
    			}
     		}
     	})
     	.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {            
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
							Toast.makeText(ListActivity.this, "日付変更失敗", Toast.LENGTH_LONG)
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
									Toast.makeText(ListActivity.this, "時刻設定失敗",
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
                     "画面遷移に失敗しました。", Toast.LENGTH_LONG)
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
        	columnsString += "日付, ";
        }
        if(timeRecord){
        	columnsString += "時刻, ";
        }
        columnsString += DatabaseOpenHelper.join(columnList, ",");
        columnsText.setText(columnsString);

    }
}