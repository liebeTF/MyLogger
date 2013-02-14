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
 * �ꗗ�\���A�N�e�B�r�e�B
 */
public class TableListActivity extends Activity implements OnItemClickListener{
	final Calendar calendar = Calendar.getInstance();
	final Integer year = calendar.get(Calendar.YEAR);
	final Integer month = calendar.get(Calendar.MONTH);
	final Integer day = calendar.get(Calendar.DAY_OF_MONTH);
	static DatabaseOpenHelper helper;

	
	
    // �ꗗ�\���pListView
    private ListView listView = null;
    private ArrayAdapter<String> arrayAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // �����������ꂽR.java�̒萔���w�肵��XML���烌�C�A�E�g�𐶐�
        setContentView(R.layout.table_list);
        helper = new DatabaseOpenHelper(this);

        // XML�Œ�`����android:id�̒l���w�肵��ListView���擾���܂��B
        listView = (ListView) findViewById(R.id.list);

        // ListView�ɕ\������v�f��ێ�����A�_�v�^�𐶐����܂��B
        arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);

        // �A�_�v�^��ݒ�
        listView.setAdapter(arrayAdapter);
        
        // ���X�i�̒ǉ�
        listView.setOnItemClickListener( this);
  
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
	public class DataLoadTask extends AsyncTask<Object, Integer, List<String>> {
	        // �������_�C�A���O
	        private ProgressDialog progressDialog = null;

	        @Override
	        protected void onPreExecute() {
	                // �o�b�N�O���E���h�̏����O��UI�X���b�h�Ń_�C�A���O�\��
	                progressDialog = new ProgressDialog(TableListActivity.this);
	                progressDialog.setMessage(getResources().getText(
	                                R.string.data_loading));
	                progressDialog.setIndeterminate(true);
	                progressDialog.show();
	        }

	        @Override
	        protected List<String> doInBackground(Object... params) {
	                // �ꗗ�f�[�^�̎擾���o�b�N�O���E���h�Ŏ��s
	                return helper.list();
	        }

	        @Override
	        protected void onPostExecute(List<String> result) {
	                // �������_�C�A���O���N���[�Y
	                progressDialog.dismiss();

	                // �\���f�[�^�̃N���A
	                arrayAdapter.clear();

	                // �\���f�[�^�̐ݒ�
	                for (String table : result) {
	                        arrayAdapter.add(table);
	                }
	        }
	}
    /**
     * List�v�f�N���b�N���̏���
     * �I�����ꂽ�G���e�B�e�B���l�߂ĎQ�Ɖ�ʂ֑J�ڂ���
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // �I�����ꂽ�v�f���擾����
    	String table = (String)parent.getItemAtPosition( position);
        // �Q�Ɖ�ʂ֑J�ڂ��閾���I�C���e���g�𐶐�
        Intent listIntent = new Intent( this, ListActivity.class);
        // �I�����ꂽ�I�u�W�F�N�g���C���e���g�ɋl�߂�
        listIntent.putExtra(DatabaseOpenHelper.TABLE_NAME,table);
        // �A�N�e�B�r�e�B���J�n����
        startActivity( listIntent);
    }
    /**
     * �I�v�V�������j���[�̐���
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // XML�Œ�`����menu���w�肷��B
        inflater.inflate(R.menu.table_list, menu);
        return true;
    }
    /**
     * �I�v�V�������j���[�̑I��
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
        case R.id.menu_new:
        	   //�e�L�X�g���͂��󂯕t����r���[���쐬���܂��B
        	final EditText editView = new EditText(TableListActivity.this);
        	final CheckBox dateRecord = new CheckBox(TableListActivity.this);
        	final CheckBox timeRecord = new CheckBox(TableListActivity.this);
        	dateRecord.setChecked(true);
        	dateRecord.setText("���t");
        	timeRecord.setChecked(false);
        	timeRecord.setText("����");
        	LinearLayout layout = new LinearLayout(this);
        	layout.setOrientation(LinearLayout.VERTICAL);
        	LinearLayout row = new LinearLayout(this);
        	row.addView(dateRecord);
        	row.addView(timeRecord);
        	layout.addView(row);
        	layout.addView(editView);
        	new AlertDialog.Builder(TableListActivity.this)
        	.setIcon(android.R.drawable.ic_dialog_info)
        	.setTitle("�e�L�X�g���̓_�C�A���O")
        	//setView�ɂăr���[��ݒ肵�܂��B 
        	.setView(layout)
        	.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        		
        		public void onClick(DialogInterface dialog, int whichButton) {
        			//���͂����������g�[�X�g�o�͂���
        			String text = editView.getText().toString();
        			Toast.makeText(TableListActivity.this,
        				text,
        				Toast.LENGTH_LONG).show();
        			helper.createTable( text,dateRecord.isChecked(),timeRecord.isChecked());
        	        // �f�[�^�擾�^�X�N�̎��s
        	        DataLoadTask task = new DataLoadTask();
        	        task.execute();
       			}
        	})
        	.setNegativeButton("�L�����Z��", new DialogInterface.OnClickListener() {            
        		public void onClick(DialogInterface dialog, int whichButton) {
        			}        
        		})
        		.show();
            break;
        }
        return true;
    }
}