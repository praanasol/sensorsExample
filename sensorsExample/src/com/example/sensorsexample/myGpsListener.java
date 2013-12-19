package com.example.sensorsexample;


import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class myGpsListener implements LocationListener {
	public Double lat = 0.0;
	public Double lng = 0.0;
	public boolean isDisable = false;
	@Override
	public void onLocationChanged(Location loc) {
		// TODO Auto-generated method stub
		if (loc != null) {
	          lat =(Double) loc.getLatitude();
	         lng = (Double) loc.getLongitude();
	         
	        }
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		isDisable = true;
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	

}