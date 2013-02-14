package com.example.mylogger;



import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
public class MainActivity extends Activity {

	  /** Called when the activity is first created. */
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        /* ���C�A�E�g���쐬����@*/

        /* �A�N�e�B�r�e�B�Ƀr���[�����C�A�E�g���Z�b�g����@*/
        
     }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    class ClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
		}
    }
 
	   /**
     * �I�v�V�������j���[�̑I��
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        
        switch (itemId) {
        case R.id.menu_settings:
        	Intent tableListIntent = new Intent(this,TableListActivity.class);
        	startActivity(tableListIntent);
	        
        default:
        	break;

        }
        return true;
    };
    
}
