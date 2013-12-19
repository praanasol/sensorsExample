package com.example.sensorsexample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	static Context context;

	

	private static boolean isShakelow;

	private static SensorManager mSensorManager;
	private static float mAccel; // acceleration apart from gravity
	private static float mAccelCurrent; // current acceleration including
										// gravity
	private static float mAccelLast; // last acceleration including gravity

	private static SensorEventListener mSensorListener;

	private static String shakeVal = "3";
	private static String hiMsg = "Hi everybody!!";

	private static float mLastX;

	private static float mLastY;

	private static float mLastZ;
	private static boolean mInitialized;

	private static Sensor mAccelerometer;
	private static final float NOISE = (float) 2.0;

	private static LocationManager locationManager;
	private static LocationManager lm;
	private static LocationListener locationListener;

	private static String gpsloc;

	private static String provider;
	private static String latituteField;
	private static String longitudeField;
	private static Geocoder geocoder;
	private static Double lat = 0.0;
	private static Double lng = 0.0;
	private static String address;
	private static String addressgps;
	private static String country;
	private static String city;
	private static boolean statusOfLoc;
	private static boolean statusOfGPS;

	static TextView dummyTextView;
	static TextView dummyTextView2;
	// static GsmCellLocation location;

	private TelephonyManager telephony;
	private static Location location;
	private static GsmCellLocation location2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// final ActionBar actionBar = getActionBar();
		mInitialized = false;
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager.setOnPageChangeListener(mSectionsPagerAdapter);
		// mSensorManager = (SensorManager)
		// getSystemService(Context.SENSOR_SERVICE);
		// mSensorManager.registerListener(mSensorListener,
		// mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
		// SensorManager.SENSOR_DELAY_NORMAL);
		// mAccel = 0.00f;
		// mAccelCurrent = SensorManager.GRAVITY_EARTH;
		// mAccelLast = SensorManager.GRAVITY_EARTH;
		telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		mSensorManager.registerListener(mSensorListener, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the locatioin provider -> use
		// default
		geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
		statusOfLoc = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		statusOfGPS = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
			location2 = (GsmCellLocation) telephony.getCellLocation();

		}

		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);

		if (statusOfLoc) {

			location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		} else {
			location = null;
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);

		}

		/*
		 * if(statusOfGPS){
		 * 
		 * location =
		 * locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		 * 
		 * }else if(statusOfLoc){
		 * 
		 * location =
		 * locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER
		 * );
		 * 
		 * } else { location = null; Intent intent = new
		 * Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		 * startActivity(intent);
		 * 
		 * }
		 */

		// String adr2 = address.substring(pos1, address.length());
		// updateLocation();
		// String adr = address.substring(0, address.length());
		// dummyTextView.setText(latituteField +" "+longitudeField);
		// dummyTextView2.setText(address+" "+city+" "+country);

		locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location loc) {
				// TODO Auto-generated method stub
				location = loc;

				if (location != null) {

					lat = (Double) location.getLatitude();
					lng = (Double) location.getLongitude();
					latituteField = String.valueOf(lat);
					longitudeField = String.valueOf(lng);

					callAscycClass();
					/*
					 * try { List<Address> addresses;
					 * 
					 * addresses = geocoder.getFromLocation(lat, lng, 1);
					 * address = addresses.get(0).getAddressLine(0); city =
					 * addresses.get(0).getAddressLine(1); country =
					 * addresses.get(0).getAddressLine(2); } catch (IOException
					 * e) { // TODO Auto-generated catch block
					 * e.printStackTrace(); }
					 */

				} else {
					lat = 0.0;
					lng = 0.0;
					latituteField = String.valueOf(lat);
					longitudeField = String.valueOf(lng);

				}

				dummyTextView.setText(latituteField + " " + longitudeField);
				// dummyTextView2.setText(address+" "+city+" "+country);

			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub

			}

		};

		if (location != null) {

			lat = (Double) location.getLatitude();
			lng = (Double) location.getLongitude();
			latituteField = String.valueOf(lat);
			longitudeField = String.valueOf(lng);

			/*
			 * try { List<Address> addresses;
			 * 
			 * addresses = geocoder.getFromLocation(lat, lng, 1); address =
			 * addresses.get(0).getAddressLine(0); city =
			 * addresses.get(0).getAddressLine(1); country =
			 * addresses.get(0).getAddressLine(2); } catch (IOException e) { //
			 * TODO Auto-generated catch block e.printStackTrace(); }
			 */

		} else {
			lat = 0.0;
			lng = 0.0;
			latituteField = String.valueOf(lat);
			longitudeField = String.valueOf(lng);

		}
		
		locationManager.requestLocationUpdates(provider, 0, 0,
				locationListener);
		
		callAscycClass();

	}

	protected void onResume() {
		super.onResume();

		mSensorManager.registerListener(mSensorListener, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(mSensorListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter implements
			ViewPager.OnPageChangeListener {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);

			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {

			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			case 3:
				return getString(R.string.title_section4).toUpperCase(l);

			}
			return null;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

			if (arg0 == 0) {

				mSensorManager.registerListener(mSensorListener,
						mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

			} else {

				if (arg0 == 1) {

					callAscycClass();
				}

				mSensorManager.unregisterListener(mSensorListener);

			}
		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub

		}
	}

	public static void updateLocation() {

	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView = null;

			// dummyTextView.setText(Integer.toString(getArguments().getInt(
			// ARG_SECTION_NUMBER)));
			int varS = getArguments().getInt(ARG_SECTION_NUMBER);

			if (varS == 1) {
				rootView = inflater.inflate(R.layout.main, container, false);
				final TextView tvX = (TextView) rootView
						.findViewById(R.id.x_axis);
				final TextView tvY = (TextView) rootView
						.findViewById(R.id.y_axis);
				final TextView tvZ = (TextView) rootView
						.findViewById(R.id.z_axis);
				final ImageView iv = (ImageView) rootView
						.findViewById(R.id.image);

				rootView.setBackgroundColor(Color.CYAN);

				mSensorListener = new SensorEventListener() {

					public void onSensorChanged(SensorEvent se) {

						float x = se.values[0];
						float y = se.values[1];
						float z = se.values[2];
						if (!mInitialized) {
							mLastX = x;
							mLastY = y;
							mLastZ = z;
							tvX.setText("0.0");
							tvY.setText("0.0");
							tvZ.setText("0.0");
							mInitialized = true;
						} else {
							float deltaX = Math.abs(mLastX - x);
							float deltaY = Math.abs(mLastY - y);
							float deltaZ = Math.abs(mLastZ - z);
							if (deltaX < NOISE)
								deltaX = (float) 0.0;
							if (deltaY < NOISE)
								deltaY = (float) 0.0;
							if (deltaZ < NOISE)
								deltaZ = (float) 0.0;
							mLastX = x;
							mLastY = y;
							mLastZ = z;
							tvX.setText(Float.toString(deltaX));
							tvY.setText(Float.toString(deltaY));
							tvZ.setText(Float.toString(deltaZ));
							iv.setVisibility(View.VISIBLE);
							if (deltaX > deltaY) {
								iv.setImageResource(R.drawable.shaker_fig_1);
							} else if (deltaY > deltaX) {
								iv.setImageResource(R.drawable.shaker_fig_2);
							} else {
								iv.setVisibility(View.INVISIBLE);
							}
						}

					}

					public void onAccuracyChanged(Sensor sensor, int accuracy) {

					}
				};

				mSensorManager.registerListener(mSensorListener,
						mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

			} else if (varS == 2) {

				rootView = inflater.inflate(R.layout.fragment_main_dummy,
						container, false);
				dummyTextView = (TextView) rootView
						.findViewById(R.id.section_label);
				dummyTextView2 = (TextView) rootView
						.findViewById(R.id.section_label2);

				rootView.setBackgroundColor(Color.LTGRAY);

				dummyTextView.setText("Lat:" + latituteField + " long:"
						+ longitudeField);

				if (location2 != null) {
					dummyTextView2.setText("LAC: " + location2.getLac()
							+ " CID: " + location2.getCid());
				}
				// dummyTextView2.setText(address+" "+city+" "+country);

				

			} else if (varS == 3) {
				rootView = inflater.inflate(R.layout.fragment_main_dummy,
						container, false);
				TextView dummyTextView = (TextView) rootView
						.findViewById(R.id.section_label);
				TextView dummyTextView2 = (TextView) rootView
						.findViewById(R.id.section_label2);
				rootView.setBackgroundColor(Color.YELLOW);
				dummyTextView.setText("Compass.");
				dummyTextView2.setText("Hi!! How are you? 3");

			} else if (varS == 4) {
				rootView = inflater.inflate(R.layout.fragment_main_dummy,
						container, false);
				TextView dummyTextView = (TextView) rootView
						.findViewById(R.id.section_label);
				TextView dummyTextView2 = (TextView) rootView
						.findViewById(R.id.section_label2);
				rootView.setBackgroundColor(Color.WHITE);
				dummyTextView.setText("Proximity.");
				dummyTextView2.setText("Hi!! How are you? 4");

			}

			return rootView;
		}

	}

	// method to call location async class
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void callAscycClass() {

		if (location != null) {

			// Ensure that a Geocoder services is available
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
					&& Geocoder.isPresent()) {
				// Show the activity indicator

				/*
				 * Reverse geocoding is long-running and synchronous. Run it on
				 * a background thread. Pass the current location to the
				 * background task. When the task finishes, onPostExecute()
				 * displays the address.
				 */
				new GetAddressTask(this).execute(location);
				// GetAddressTask gad = new GetAddressTask();
				// gad.execute(location);
			}

		}
	}

	private class GetAddressTask extends AsyncTask<Location, Void, String> {
		Context mContext;

		public GetAddressTask(Context context) {
			super();
			mContext = context;
		}

		/**
		 * Get a Geocoder instance, get the latitude and longitude look up the
		 * address, and return it
		 * 
		 * @params params One or more Location objects
		 * @return A string containing the address of the current location, or
		 *         an empty string if no address can be found, or an error
		 *         message
		 */

		@Override
		protected String doInBackground(Location... params) {
			// Get the current location from the input parameter list
			Location loc = params[0];
			// Create a list to contain the result address

			String address = String.format(Locale.ENGLISH,
					"http://maps.googleapis.com/maps/api/geocode/json?latlng="
							+ loc.getLatitude() + "," + loc.getLongitude()
							+ "&sensor=true&language="
							+ Locale.getDefault().getCountry(),
					loc.getLatitude(), loc.getLongitude());
			HttpGet httpGet = new HttpGet(address);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			StringBuilder stringBuilder = new StringBuilder();

			List<Address> retList = null;

			try {
				response = client.execute(httpGet);
				HttpEntity entity = response.getEntity();
				InputStream stream = entity.getContent();
				int b;
				while ((b = stream.read()) != -1) {
					stringBuilder.append((char) b);
				}

				JSONObject jsonObject = new JSONObject();
				jsonObject = new JSONObject(stringBuilder.toString());

				retList = new ArrayList<Address>();

				if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
					JSONArray results = jsonObject.getJSONArray("results");
					for (int i = 0; i < results.length(); i++) {
						JSONObject result = results.getJSONObject(i);
						String indiStr = result.getString("formatted_address");
						Address addr = new Address(Locale.ENGLISH);
						addr.setAddressLine(0, indiStr);
						retList.add(addr);
					}
				}

			} catch (ClientProtocolException e) {

				Log.e(MainActivity.class.getName(),
						"Error calling Google geocode webservice.", e);
			} catch (IOException e) {

				Log.e(MainActivity.class.getName(),
						"Error calling Google geocode webservice.", e);
			} catch (JSONException e) {

				Log.e(MainActivity.class.getName(),
						"Error parsing Google geocode webservice response.", e);
			}

			if (retList != null && retList.size() > 0) {

				// Get the first address
				Address addressz = retList.get(0);
				/*
				 * Format the first line of address (if available), city, and
				 * country name.
				 */

				String s = "Address Line: " + addressz.getAddressLine(0);
					/*	+ addressz.getFeatureName() + "\n" + "Locality: "
						+ addressz.getLocality() + "\n"
						+ addressz.getPremises() + "\n" + "Admin Area: "
						+ addressz.getAdminArea() + "\n" + "Country code: "
						+ addressz.getCountryCode() + "\n" + "Country name: "
						+ addressz.getCountryName() + "\n" + "Phone: "
						+ addressz.getPhone() + "\n" + "Postbox: "
						+ addressz.getPostalCode() + "\n" + "SubLocality: "
						+ addressz.getSubLocality() + "\n" + "SubAdminArea: "
						+ addressz.getSubAdminArea() + "\n"
						+ "SubThoroughfare: " + addressz.getSubThoroughfare()
						+ "\n" + "Thoroughfare: " + addressz.getThoroughfare()
						+ "\n" + "URL: " + addressz.getUrl(); */
				String addressText = String.format(
						"%s, %s, %s",
						// If there's a street address, add it
						addressz.getMaxAddressLineIndex() > 0 ? addressz
								.getAddressLine(0) : ""
								+ addressz.getAddressLine(1),
						// Locality is usually a city
						addressz.getLocality(),
						// The country of the address
						addressz.getCountryName());
				// Return the text
				return s;
			} else {

				return "No address found";
			}
		}

		/**
		 * A method that's called once doInBackground() completes. Turn off the
		 * indeterminate activity indicator and set the text of the UI element
		 * that shows the address. If the lookup failed, display the error
		 * message.
		 */
		@Override
		protected void onPostExecute(String address) {

			// Display the results of the lookup.

			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			boolean connected;
			int type = 2;
			// int type = networkInfo.getType();
			// String typeName = networkInfo.getTypeName();
			if (networkInfo != null) {
				connected = networkInfo.isConnected();
				type = networkInfo.getType();
			} else {

				connected = false;
				type = 2;
			}
			// check if mobile data or wifi are available
			if (type == 0 || type == 1 && connected) {

				if (statusOfLoc || statusOfGPS) {

					if (address != null && address != "") {
						Toast.makeText(MainActivity.this, address, 2000).show();
					} else {
						Toast.makeText(MainActivity.this, "No lacation found",
								2000).show();

					}

				} else {
					// location = null;
					// to show location enabled activity
					// Intent intent = new
					// Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					// startActivity(intent);
					Toast.makeText(MainActivity.this,
							"Location is disabled. Please enable location",
							2000).show();
				}
			} else {
				Toast.makeText(
						MainActivity.this,
						"Network is disabled. Please enable network connection. Mobile data or wifi",
						2000).show();

			}

		}

		// Json geocoding implentation if geocoder throws exception

	}

}
