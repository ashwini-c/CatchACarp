package nz.ac.waikato.fcms.catchacarp.activity;



import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


import nz.ac.waikato.fcms.catchacarp.R;
import nz.ac.waikato.fcms.catchacarp.helper.AndroidMultiPartEntity;
import nz.ac.waikato.fcms.catchacarp.helper.AndroidMultiPartEntity.ProgressListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class InfoActivity extends Activity {
	double lat,lng;
	Button btn;
	String population,activity;
	SharedPreferences pref;
	String username,email;
	// Progress Dialog
	private ProgressDialog pDialog;
	private String filePath = null;
	//ids
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	long totalSize = 0;
	private static final String ADDINFO_URL = "http://lernzdb-test.its.waikato.ac.nz/carpapp/addinfo.php";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		pref =  getSharedPreferences("registeractivity", MODE_PRIVATE);
		lat = getIntent().getDoubleExtra("lat", -3333);
		lng = getIntent().getDoubleExtra("lng", -3333);
		username = pref.getString("username", "");
		email = pref.getString("email", "");


		// image or video path that is captured in previous activity
		filePath = getIntent().getStringExtra("filePath");

		btn = (Button)findViewById(R.id.btnNext);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new UploadFileToServer().execute();

			}
		});
	}
	/**
	 * Uploading the file to server
	 * */
	private class UploadFileToServer extends AsyncTask<Void, Integer, String> {


		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(InfoActivity.this);
			pDialog.setMessage("Uploading data...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			return uploadFile();
		}


		@SuppressWarnings("deprecation")
		private String uploadFile() {
			String responseString = null;

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(ADDINFO_URL);
			InputStream is = null;
			JSONObject jObj = null;
			String json = "";
			int success;
			try {
				AndroidMultiPartEntity entity = new AndroidMultiPartEntity(new ProgressListener() {

					@Override
					public void transferred(long num) {
						publishProgress((int) ((num / (float) totalSize) * 100));
					}
				});

				File sourceFile = new File(filePath);

				// Adding file data to http body
				entity.addPart("fileToUpload", new FileBody(sourceFile));

				// Extra parameters if you want to pass to server
				entity.addPart("username",new StringBody(username));
				entity.addPart("email",new StringBody( email));
				entity.addPart("lat",new StringBody(Double.toString(lat)));
				entity.addPart("long",new StringBody(Double.toString(lng)));
				entity.addPart("activity",new StringBody(activity));
				entity.addPart("size",new StringBody(population));

				totalSize = entity.getContentLength();
				httppost.setEntity(entity);

				// Making server call
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity r_entity = response.getEntity();
				is = r_entity.getContent();


			} catch (ClientProtocolException e) {
				responseString = e.toString();
			} catch (IOException e) {
				responseString = e.toString();
			}


			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						is, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				json = sb.toString();
			} catch (Exception e) {
				Log.e("Buffer Error", "Error converting result " + e.toString());
			}
			Log.e("JSON Parser", "json string " +json);
			// try parse the string to a JSON object
			try {
				jObj = new JSONObject(json);
			} catch (JSONException e) {
				Log.e("JSON Parser", "Error parsing data " + e.toString());
			}
			try{
				success = jObj.getInt(TAG_SUCCESS);
				if (success == 1) {



					return jObj.getString(TAG_MESSAGE);
				}else{
					Log.d("Login Failure!", jObj.getString(TAG_MESSAGE));
					return jObj.getString(TAG_MESSAGE);

				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			Log.e("InfoActivity", "Response from server: " + result);

			pDialog.dismiss();
			if (result != null){
				Toast.makeText(InfoActivity.this, result, Toast.LENGTH_LONG).show();
			}
			finish();
			Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			super.onPostExecute(result);
		}


	}

	public void onRadioButtonClickedPopulation(View view) {

		boolean checked = ((RadioButton) view).isChecked();


		switch(view.getId()) {
		case R.id.radio_small:
			if (checked)
				population = "less_than_10";
			break;
		case R.id.radio_medium:
			if (checked)
				population = "10_to_50";
			break;
		case R.id.radio_large:
			if (checked)
				population = "more_than_50";
			break;
		}
	}

	public void onRadioButtonClickedActivity(View view) {

		boolean checked = ((RadioButton) view).isChecked();


		switch(view.getId()) {
		case R.id.radio_spawn:
			if (checked)
				activity = "Spawning";
			break;
		case R.id.radio_swim:
			if (checked)
				activity = "Swimming";
			break;
		case R.id.radio_unknown:
			if (checked)
				activity = "Unkown";
			break;
		}
	}
}
