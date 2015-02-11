package nz.ac.waikato.fcms.catchacarp.activity;

import nz.ac.waikato.fcms.catchacarp.R;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ReportActivity extends Activity implements LocationListener{
	private GoogleMap mMap;
	private static final LatLng eg = new LatLng(42.093230818037,11.7971813678741);
	private LocationManager locationManager;
	private String provider;
	Marker curr ;
	private ProgressBar spinner;
	Button btn;
	double lat,lng;
	// Activity request codes
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	private static final String IMAGE_DIRECTORY_NAME = "CarpImages";


	private Uri fileUri; // file url to store image/video
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		spinner = (ProgressBar)findViewById(R.id.progressBar1);
		spinner.setVisibility(View.VISIBLE);
		btn = (Button)findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				captureImage();
			}
		});

		setUpMap();
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean enabledGPS = service
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean enabledWiFi = service
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


		if (!enabledGPS) {
			Toast.makeText(this, "GPS signal not found", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);


		if (location != null) {
			Toast.makeText(this, "Selected Provider " + provider,
					Toast.LENGTH_SHORT).show();
			onLocationChanged(location);
		} 
	}
	private void captureImage() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		fileUri = getOutputMediaFile();

		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		// start the image capture Intent
		startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
	}
	private static Uri getOutputMediaFile() {

		// External sdcard location
		File mediaStorageDir = new File(
				Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
						+ IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;

		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".jpg");


		return  Uri.fromFile(mediaFile);
	}
	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(provider, 400, 1, this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		lat =  location.getLatitude();
		lng = location.getLongitude();
		Log.d("ashwini", "Location " + lat+","+lng);
		LatLng coordinate = new LatLng(lat, lng);
		/*Toast.makeText(this, "Location " + coordinate.latitude+","+coordinate.longitude,
				Toast.LENGTH_LONG).show();*/
		spinner.setVisibility(View.GONE);
		CameraPosition cameraPosition = new CameraPosition.Builder().target(
				new LatLng(lat,lng)).zoom(18).build();

		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		if(curr!=null)
			curr.remove();
		curr = mMap.addMarker(new MarkerOptions()
		.position(coordinate)
		.snippet("Current Location")
		.draggable(true)
		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		curr.setDraggable(true);
		//mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter( location));
	}


	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();

	}


	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();

	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setUpMap()
	{
		if (mMap == null)
		{
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

			if (mMap != null)
			{
				mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
				mMap.getUiSettings().setCompassEnabled(true);
				mMap.getUiSettings().setRotateGesturesEnabled(true);
				mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
				{
					@Override
					public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker)
					{
						marker.showInfoWindow();
						return true;
					}
				});

				mMap.setOnMarkerDragListener(new OnMarkerDragListener() {

					@Override
					public void onMarkerDragStart(Marker marker) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onMarkerDragEnd(Marker marker) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onMarkerDrag(Marker marker) {
						// TODO Auto-generated method stub

					}
				});
			}
			else
				Toast.makeText(getApplicationContext(), "Unable to create Maps", Toast.LENGTH_SHORT).show();
		}
	}

	public class MarkerInfoWindowAdapter implements InfoWindowAdapter {


		Location lc;
		public MarkerInfoWindowAdapter(Location location) {
			// TODO Auto-generated constructor stub
			lc = location;
		}

		@Override
		public View getInfoContents(Marker arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			View v  = getLayoutInflater().inflate(R.layout.infowin, null);


			TextView markerLabel = (TextView)v.findViewById(R.id.marker_label);

			markerLabel.setText("LAT: "+lc.getLatitude()+" LONGITUDE: "+ lc.getLongitude());
			markerLabel = (TextView)v.findViewById(R.id.timezone);
			Date d = new Date(lc.getTime());
			markerLabel.setText("TimeZone: "+ TimeZone.getDefault().getID());
			markerLabel = (TextView)v.findViewById(R.id.utc);
			DateFormat df = DateFormat.getTimeInstance();
			//df.setTimeZone(TimeZone.getTimeZone("utc"));
			markerLabel.setText("Local time: "+df.format(d));
			df.setTimeZone(TimeZone.getTimeZone("utc"));
			markerLabel = (TextView)v.findViewById(R.id.local);
			markerLabel.setText("UTC time: "+df.format(d));
			Location dest = new Location("EROAD");
			dest.setLatitude(-36.722375);
			dest.setLongitude(174.707047);
			lc.distanceTo(dest);
			markerLabel = (TextView)v.findViewById(R.id.distance);
			markerLabel.setText("Distance to EROAD: "+ lc.distanceTo(dest) / 1000 + " KM");

			return v;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if the result is capturing Image
		if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Intent intent = new Intent(getApplicationContext(), TakePhotoActivity.class);
				intent.putExtra("lat", lat);
				intent.putExtra("lng", lng);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				intent.putExtra("imageuri", fileUri.toString());
				startActivity(intent);
			} else if (resultCode == RESULT_CANCELED) {
				// user cancelled Image capture
				Toast.makeText(getApplicationContext(),
						"User cancelled image capture", Toast.LENGTH_SHORT)
						.show();
			} else {
				// failed to capture image
				Toast.makeText(getApplicationContext(),
						"Sorry! Failed to capture image", Toast.LENGTH_SHORT)
						.show();
			}
		} 
	}


}
