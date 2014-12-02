package nz.ac.waikato.fcms.catchacarp;

import nz.ac.waikato.fcms.catchacarp.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;

public class InfoActivity extends Activity {

	Button btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		btn = (Button)findViewById(R.id.btnNext);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);

			}
		});
	}


	public void onRadioButtonClickedPopulation(View view) {

		boolean checked = ((RadioButton) view).isChecked();


		switch(view.getId()) {
		case R.id.radio_small:
			if (checked)

				break;
		case R.id.radio_medium:
			if (checked)

				break;
		case R.id.radio_large:
			if (checked)

				break;
		}
	}

	public void onRadioButtonClickedActivity(View view) {

		boolean checked = ((RadioButton) view).isChecked();


		switch(view.getId()) {
		case R.id.radio_spawn:
			if (checked)

				break;
		case R.id.radio_swim:
			if (checked)

				break;
		case R.id.radio_unknown:
			if (checked)

				break;
		}
	}
}
