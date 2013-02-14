package com.example.mylogger;

import java.util.List;

import com.example.mylogger.db.ColumnTuple;
import com.example.mylogger.db.DatabaseOpenHelper;
import com.example.mylogger.db.MyRecordDao;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SetColumnDialog extends Dialog implements OnItemClickListener{
	private ListView listView = null;
	private ArrayAdapter<ColumnTuple> arrayAdapter = null;
	private String tableName = null;
	Context context;

	public SetColumnDialog(Context context) {
		super(context);
		this.context = context;
		// TODO 自動生成されたコンストラクター・スタブ
		if(ListActivity.helper == null){
			ListActivity.helper = new DatabaseOpenHelper(context);
		}
		if(ListActivity.dao == null){
			ListActivity.dao = new MyRecordDao(context);
		}
		setContentView(R.layout.set_column_dialog);
		listView = (ListView)findViewById(R.id.columnList);
		
        arrayAdapter = new ArrayAdapter<ColumnTuple>(context,
                android.R.layout.simple_list_item_1);

        // アダプタを設定
        listView.setAdapter(arrayAdapter);
        
        // リスナの追加
        listView.setOnItemClickListener( this);

	}

	public SetColumnDialog(Context context, int theme) {
		super(context, theme);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public SetColumnDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
		DataLoadTask task = new DataLoadTask();
		task.execute();
	}
	/**
	 * 一覧データの取得と表示を行うタスク
	 */
	public class DataLoadTask extends AsyncTask<Object, Integer, List<ColumnTuple>> {
	        // 処理中ダイアログ
	        private ProgressDialog progressDialog = null;

	        @Override
	        protected void onPreExecute() {
	                // バックグラウンドの処理前にUIスレッドでダイアログ表示
	                progressDialog = new ProgressDialog(context);
	                progressDialog.setMessage(context.getResources().getText(
	                                R.string.data_loading));
	                progressDialog.setIndeterminate(true);
	                progressDialog.show();
	        }

	        @Override
	        protected List<ColumnTuple> doInBackground(Object... params) {
	                // 一覧データの取得をバックグラウンドで実行
	                return ListActivity.helper.listColumns(tableName);
	        }

	        @Override
	        protected void onPostExecute(List<ColumnTuple> result) {
	                // 処理中ダイアログをクローズ
	                progressDialog.dismiss();

	                // 表示データのクリア
	                arrayAdapter.clear();

	                // 表示データの設定
	                for (ColumnTuple tuple : result) {
	                        arrayAdapter.add(tuple);
	                }
	        }
	}
    /**
     * List要素クリック時の処理
     * 選択されたエンティティを詰めて参照画面へ遷移する
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 選択された要素を取得する
        final ColumnTuple tuple = (ColumnTuple)parent.getItemAtPosition( position);
        new AlertDialog.Builder(context)
        .setTitle("削除確認")
        .setMessage(tuple.name+"を削除しますか？")
        .setPositiveButton("OK", new DialogInterface.OnClickListener(){
        	public void onClick(DialogInterface dialog, int whichButton){
        		//OK選択でカラム削除
        		ListActivity.helper.dropColumn(tableName,tuple.name);
        	}
        	})
        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int whichButton){

        	}
        })
        .setNeutralButton("Rename", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int whichButton){
        		RenameDialogShow(tuple.name);
        	}
        })
	    .create()
        .show();
    };
    
    public void RenameDialogShow(final String columnName) {
    	final EditText afterNameText = new EditText(context);
		TextView beforeNameText = new TextView(context);
		TextView after = new TextView(context);

		after.setText("変更後");
		beforeNameText.setText(columnName);
		afterNameText.setText(columnName);
    	new AlertDialog.Builder(context)
    	.setView(beforeNameText)
		.setView(after)
		.setView(afterNameText)
		.setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO 自動生成されたメソッド・スタブ
				String name = afterNameText.getText().toString();
				if(name.equals("")){
         			Toast.makeText(context,
             				"新項目名は必須です",
             				Toast.LENGTH_LONG).show();         			
				}else{
					ListActivity.helper.renameColumn(tableName, columnName, name);
				}
				
			}
		})
		.show();
    }
}
