package com.beanthere.listeners;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by staccie on 9/21/15.
 */
public class BeanLocationListener implements LocationListener {


    private LocationReceiver mLocationListener;
    private String mTag;
    private Long mFieldId;

    public interface LocationReceiver {
        void onLocationReceive(String tag, Long fieldId, Location location);
    }

    public BeanLocationListener(Context context, String tag, Long fieldId) {
        mLocationListener = (LocationReceiver) context;
        mTag = tag;
        mFieldId = fieldId;
    }

    public BeanLocationListener(Context context, String tag) {
        mLocationListener = (LocationReceiver) context;
        mTag = tag;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocationListener.onLocationReceive(mTag, mFieldId, location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }
}
