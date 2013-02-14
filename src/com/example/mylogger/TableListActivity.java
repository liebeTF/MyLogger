package com.example.mylogger;

import java.util.Calendar;
import java.util.List;
import com.example.mylogger.db.DatabaseOpenHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.Toast;

/**
 * 一覧表示アクティビティ
 */
public class TableListActivity extends Activity implements OnItemClickListener{
	final Calendar calendar = Calendar.getInstance();
	final Integer year = calendar.get(Calendar.YEAR);
	final Integer month = calendar.get(Calendar.MONTH);
	final Integer day = calendar.get(Calendar.DAY_OF_MONTH);
	static DatabaseOpenHelper helper;

	
	
    // 一覧表示用ListView
    private ListView listView = null;
    private ArrayAdapter<String> arrayAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 自動生成されたR.javaの定数を指定してXMLからレイアウトを生成
        setContentView(R.layout.table_list);
        helper = new DatabaseOpenHelper(this);

        // XMLで定義したandroid:idの値を指定してListViewを取得します。
        listView = (ListView) findViewById(R.id.list);

        // ListViewに表示する要素を保持するアダプタを生成します。
        arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);

        // アダプタを設定
        listView.setAdapter(arrayAdapter);
        
        // リスナの追加
        listView.setOnItemClickListener( this);
  
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
	public class DataLoadTask extends AsyncTask<Object, Integer, List<String>> {
	        // 処理中ダイアログ
	        private ProgressDialog progressDialog = null;

	        @Override
	        protected void onPreExecute() {
	                // バックグラウンドの処理前にUIスレッドでダイアログ表示
	                progressDialog = new ProgressDialog(TableListActivity.this);
	                progressDialog.setMessage(getResources().getText(
	                                R.string.data_loading));
	                progressDialog.setIndeterminate(true);
	                progressDialog.show();
	        }

	        @Override
	        protected List<String> doInBackground(Object... params) {
	                // 一覧データの取得をバックグラウンドで実行
	                return helper.list();
	        }

	        @Override
	        protected void onPostExecute(List<String> result) {
	                // 処理中ダイアログをクローズ
	                progressDialog.dismiss();

	                // 表示データのクリア
	                arrayAdapter.clear();

	                // 表示データの設定
	                for (String table : result) {
	                        arrayAdapter.add(table);
	                }
	        }
	}
    /**
     * List要素クリック時の処理
     * 選択されたエンティティを詰めて参照画面へ遷移する
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 選択された要素を取得する
    	String table = (String)parent.getItemAtPosition( position);
        // 参照画面へ遷移する明示的インテントを生成
        Intent listIntent = new Intent( this, ListActivity.class);
        // 選択されたオブジェクトをインテントに詰める
        listIntent.putExtra(DatabaseOpenHelper.TABLE_NAME,table);
        // アクティビティを開始する
        startActivity( listIntent);
    }
    /**
     * オプションメニューの生成
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // XMLで定義したmenuを指定する。
        inflater.inflate(R.menu.table_list, menu);
        return true;
    }
    /**
     * オプションメニューの選択
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
        case R.id.menu_new:
        	   //テキスト入力を受け付けるビューを作成します。
        	final EditText editView = new EditText(TableListActivity.this);
        	final CheckBox dateRecord = new CheckBox(TableListActivity.this);
        	final CheckBox timeRecord = new CheckBox(TableListActivity.this);
        	dateRecord.setChecked(true);
        	dateRecord.setText("日付");
        	timeRecord.setChecked(false);
        	timeRecord.setText("時刻");
        	LinearLayout layout = new LinearLayout(this);
        	layout.setOrientation(LinearLayout.VERTICAL);
        	LinearLayout row = new LinearLayout(this);
        	row.addView(dateRecord);
        	row.addView(timeRecord);
        	layout.addView(row);
        	layout.addView(editView);
        	new AlertDialog.Builder(TableListActivity.this)
        	.setIcon(android.R.drawable.ic_dialog_info)
        	.setTitle("テキスト入力ダイアログ")
        	//setViewにてビューを設定します。 
        	.setView(layout)
        	.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        		
        		public void onClick(DialogInterface dialog, int whichButton) {
        			//入力した文字をトースト出力する
        			String text = editView.getText().toString();
        			Toast.makeText(TableListActivity.this,
        				text,
        				Toast.LENGTH_LONG).show();
        			helper.createTable( text,dateRecord.isChecked(),timeRecord.isChecked());
        	        // データ取得タスクの実行
        	        DataLoadTask task = new DataLoadTask();
        	        task.execute();
       			}
        	})
        	.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {            
        		public void onClick(DialogInterface dialog, int whichButton) {
        			}        
        		})
        		.show();
            break;
        }
        return true;
    }
}