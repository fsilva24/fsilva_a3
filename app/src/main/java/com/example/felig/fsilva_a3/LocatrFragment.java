package com.example.felig.fsilva_a3;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Date;
import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * Created by felig on 4/10/2018.
 */

public class LocatrFragment extends SupportMapFragment {
    private GoogleApiClient mClient;
    private GoogleMap mMap;
    private JSONArray weatherJsonObject;
    private JSONObject currWeatherJsonObject;
    private JSONObject mainJsonObject;
    private String mTimeAndDate;
    private Bitmap mMapImage;
    private WeatherMarker mMapItem;
    private Location mCurrentLocation;
    private String mLatitude;
    private String mLongitude;
    private Date currentTime;

    private static final String OPEN_WEATHER_MAP_URL =
            "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric";

    private static final String OPEN_WEATHER_MAP_API = "cf23f5ce96c15df73c33f44760be0ec9";
    private static final String TAG = "LocatrFragment";
    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private static final int REQUEST_LOCATION_PERMISSIONS = 0;

    public static LocatrFragment newInstance() {
        return new LocatrFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //new FetchItemsTask().execute();

        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        getActivity().invalidateOptionsMenu();
                    }
                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                })
                .build();

        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                updateUI();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        getActivity().invalidateOptionsMenu();
        mClient.connect();
    }
    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_locatr, menu);
        MenuItem searchItem = menu.findItem(R.id.action_locate);
        searchItem.setEnabled(mClient.isConnected());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_locate:
                if (checkPermission()) {
                    findWeather();

                } else{
                    requestPermissions(LOCATION_PERMISSIONS,
                            REQUEST_LOCATION_PERMISSIONS);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS:
                if (checkPermission()) {
                    findWeather();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void findWeather() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);
        if (checkPermission()) {
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mClient, request, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            new SearchTask().execute(location);
                        }
                    });
        } else {
            requestPermissions(LOCATION_PERMISSIONS,
                    REQUEST_LOCATION_PERMISSIONS);
        }
    }


    private boolean checkPermission() {
        int result = ContextCompat
                .checkSelfPermission(getActivity(), LOCATION_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void updateUI() {
        if (mMap == null || mMapItem == null) {
            return;
        }
        LatLng itemPoint = new LatLng(Double.parseDouble(mMapItem.getLat()), Double.parseDouble(mMapItem.getLon()));
        LatLng myPoint = new LatLng(
                mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(itemPoint)
                .include(myPoint)
                .build();
        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, margin);
        mMap.animateCamera(update);
    }


    private class SearchTask extends AsyncTask<Location,Void,Void> {
        //private Bitmap mBitmap;
        private WeatherMarker item;
        private Location mLocation;
        @Override
        protected Void doInBackground(Location... params) {
            mLocation = params[0];
            item = new WeatherMarker();
            mLatitude = String.valueOf(mLocation.getLatitude());
            mLongitude = String.valueOf(mLocation.getLongitude());
            JSONObject jWeather = getWeatherJSON(mLatitude, mLongitude);
            JSONObject coordJsonObject = null;
            try {
                coordJsonObject = jWeather.getJSONObject("coord");
                weatherJsonObject = jWeather.getJSONArray("weather");
                currWeatherJsonObject = weatherJsonObject.getJSONObject(0);
                mainJsonObject = jWeather.getJSONObject("main");
                item.setLon(coordJsonObject.getString("lon"));
                item.setLat(coordJsonObject.getString("lat"));
                item.setWeather(currWeatherJsonObject.getString("description"));
                item.setTemperature(mainJsonObject.getString("temp"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            currentTime = Calendar.getInstance().getTime();
            item.setDateandTime(currentTime);
            Log.i(TAG, "Got a fix: " + mLocation.getLatitude() + " " +
                    mLocation.getLongitude() + " " + currentTime);
            String mLon = item.getLon();
            String mTem = item.getTemperature();
            String mWea = item.getWeather();
            String mLat = item.getLat();
            Log.i("wakitolo Frag", "Lon: " + mLon + " Temp : " + mTem + " Weather " + mWea);
            return null;


        }
        @Override
        protected void onPostExecute(Void result) {
           // mMapImage = mBitmap;
            mMapItem = item;
            mCurrentLocation = mLocation;
            updateUI();
        }
    }

    public static JSONObject getWeatherJSON(String lat, String lon){
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_URL, lat, lon));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();
            connection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();
            JSONObject data = new JSONObject(json.toString());
            // This value will be 404 if the request was not successful
            if(data.getInt("cod") != 200){
                return null;
            }
            return data;
        }catch(Exception e){
            return null;
        }
    }

}
