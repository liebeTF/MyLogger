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
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
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

        // �A�_�v�^��ݒ�
        listView.setAdapter(arrayAdapter);
        
        // ���X�i�̒ǉ�
        listView.setOnItemClickListener( this);

	}

	public SetColumnDialog(Context context, int theme) {
		super(context, theme);
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
	}

	public SetColumnDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
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
	 * �ꗗ�f�[�^�̎擾�ƕ\�����s���^�X�N
	 */
	public class DataLoadTask extends AsyncTask<Object, Integer, List<ColumnTuple>> {
	        // �������_�C�A���O
	        private ProgressDialog progressDialog = null;

	        @Override
	        protected void onPreExecute() {
	                // �o�b�N�O���E���h�̏����O��UI�X���b�h�Ń_�C�A���O�\��
	                progressDialog = new ProgressDialog(context);
	                progressDialog.setMessage(context.getResources().getText(
	                                R.string.data_loading));
	                progressDialog.setIndeterminate(true);
	                progressDialog.show();
	        }

	        @Override
	        protected List<ColumnTuple> doInBackground(Object... params) {
	                // �ꗗ�f�[�^�̎擾���o�b�N�O���E���h�Ŏ��s
	                return ListActivity.helper.listColumns(tableName);
	        }

	        @Override
	        protected void onPostExecute(List<ColumnTuple> result) {
	                // �������_�C�A���O���N���[�Y
	                progressDialog.dismiss();

	                // �\���f�[�^�̃N���A
	                arrayAdapter.clear();

	                // �\���f�[�^�̐ݒ�
	                for (ColumnTuple tuple : result) {
	                        arrayAdapter.add(tuple);
	                }
	        }
	}
    /**
     * List�v�f�N���b�N���̏���
     * �I�����ꂽ�G���e�B�e�B���l�߂ĎQ�Ɖ�ʂ֑J�ڂ���
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // �I�����ꂽ�v�f���擾����
        final ColumnTuple tuple = (ColumnTuple)parent.getItemAtPosition( position);
        new AlertDialog.Builder(context)
        .setTitle("�폜�m�F")
        .setMessage(tuple.name+"���폜���܂����H")
        .setPositiveButton("OK", new DialogInterface.OnClickListener(){
        	public void onClick(DialogInterface dialog, int whichButton){
        		//OK�I���ŃJ�����폜
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

		after.setText("�ύX��");
		beforeNameText.setText(columnName);
		afterNameText.setText(columnName);
    	new AlertDialog.Builder(context)
    	.setView(beforeNameText)
		.setView(after)
		.setView(afterNameText)
		.setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO �����������ꂽ���\�b�h�E�X�^�u
				String name = afterNameText.getText().toString();
				if(name.equals("")){
         			Toast.makeText(context,
             				"�V���ږ��͕K�{�ł�",
             				Toast.LENGTH_LONG).show();         			
				}else{
					ListActivity.helper.renameColumn(tableName, columnName, name);
				}
				
			}
		})
		.show();
    }
}
