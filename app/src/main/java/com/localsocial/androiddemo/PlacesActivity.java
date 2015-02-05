/*
 * Copyright (c) 2001 - 2015 Rococo Software Ltd., 3 Lincoln Place,
 * Dublin 2 Ireland. All Rights Reserved.
 *
 * This software is distributed under licenses restricting its use,
 * copying, distribution, and decompilation. No part of this
 * software may be reproduced in any form by any means without prior
 * written authorization of Rococo Software Ltd. and its licensors, if
 * any.
 *
 * This software is the confidential and proprietary information
 * of Rococo Software Ltd. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of
 * the license agreement you entered into with Rococo Software Ltd.
 * Use is subject to license terms.
 *
 * Rococo Software Ltd. has intellectual property rights relating
 * to the technology embodied in this software. In particular, and
 * without limitation, these intellectual property rights may include
 * one or more patents, or pending patent applications.
 */

package com.localsocial.androiddemo;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.localsocial.LocalSocial;
import com.localsocial.LocalSocialFactory;
import com.localsocial.LoggerFactory;
import com.localsocial.remote.RemoteFactory;
import com.localsocial.remote.exception.LocalSocialError;
import com.localsocial.v1.model.Location;
import com.localsocial.v1.remote.MerchantRemoteFactory;

import java.io.IOException;
import java.util.List;

public class PlacesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        m_locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPlacesNearby();
    }

    private void syncLocations(List<Location> locations) {
        //DO something with locations found here
        Log.d(TAG, locations.size() + " places found nearby");
        Log.d(TAG, "locations = " + locations);
    }

    private LocalSocial getLocalSocial() {
        if (null == m_localsocial) {
            try {
                m_localsocial = LocalSocialFactory.getLocalSocial();
            } catch (Exception e) {
                Log.e(TAG, "Exception :: " + e.getMessage());
            }
        }
        return m_localsocial;
    }

    private MerchantRemoteFactory getMerchantRemote() {
        if (null == m_merchantRemote) {
            m_merchantRemote = new MerchantRemoteFactory(new RemoteFactory(getLocalSocial()));
        }
        return m_merchantRemote;
    }

    private void getPlacesNearby() {
        if (null != getLastKnownLocation()) {
            new GetLocationsNearTask().execute(getLastKnownLocation());
        } else {
            Toast.makeText(getBaseContext(), "Location Unknown", Toast.LENGTH_SHORT).show();
        }
    }

    private android.location.Location getLastKnownLocation() {
        for (String provider : m_locationManager.getAllProviders()) {
            boolean enabled = m_locationManager.isProviderEnabled(provider);
            android.location.Location location = m_locationManager.getLastKnownLocation(provider);
            if (enabled && null != location) {
                return location;
            }
        }
        return null;
    }

    private class GetLocationsNearTask extends AsyncTask<android.location.Location, Void, Void> {

        @Override
        protected Void doInBackground(android.location.Location... location) {
            Log.d(TAG, "GetLocationsNearTask.doInBackground");
            try {
                double lat = location[0].getLatitude();
                double lon = location[0].getLongitude();
                int page = 0;
                int limit = 10;
                int range = 5000;
                List<com.localsocial.v1.model.Location> locations;
                do {
                    locations = getMerchantRemote().getLocationRemote().getNear(lat, lon, range, limit, page);
                    Log.d(TAG, "locations.size " + locations.size());
                    syncLocations(locations);
                    page++;
                } while (locations.size() == limit && page < 6);
            } catch (IOException e) {
                Log.d(TAG, "Unexpected error : " + e.getMessage());
            } catch (LocalSocialError lse) {
                Log.d(TAG, "Unexpected error : " + lse.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG, "GetLocationsNearTask.onPostExecute");
        }
    }

    private LocalSocial m_localsocial;
    private MerchantRemoteFactory m_merchantRemote;
    private LocationManager m_locationManager;
    private static final String TAG = "LocalSocial/" + LoggerFactory.getClassName(PlacesActivity.class);

}