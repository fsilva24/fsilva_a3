package com.example.felig.fsilva_a3;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.support.design.widget.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Date;


/**
 * Created by felig on 4/10/2018.
 */

/*
    Most of the code in this fragment.
 */
public class LocatrFragment extends SupportMapFragment {
    private GoogleApiClient mClient;
    private GoogleMap mMap;
    private JSONArray weatherJsonObject;
    private JSONObject currWeatherJsonObject;
    private JSONObject mainJsonObject;
    private String mTimeAndDate;
    private WeatherMarker mMapItem;
    private Location mCurrentLocation;
    private String mLatitude;
    private String mLongitude;
    private Date currentTime;
    private List<WeatherMarker> weatherList;

    private static final String OPEN_WEATHER_MAP_URL =
            "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric";

    private static final String OPEN_WEATHER_MAP_API = "cf23f5ce96c15df73c33f44760be0ec9";
    private static final String TAG = "LocatrFragment";
    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private static final int REQUEST_LOCATION_PERMISSIONS = 0;
    private FloatingActionButton mFloatingActionButton;
    private CoordinatorLayout mContainer;

    public static LocatrFragment newInstance() {
        return new LocatrFragment();
    }

    /*
    This is where I load the map and the stored points from the database if there are any.
     */
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
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        marker.showInfoWindow();
                        Snackbar snack = Snackbar.make(getView(), marker.getTag().toString(), Snackbar.LENGTH_LONG);
                        snack.show();
                        return true;
                    }
                });
                weatherList = WeatherLab.get(getContext()).getMarkers();

                // Looping through list to get all previous markers
                if (weatherList.size() > 0){
                    for (int i = 0; i < weatherList.size(); i++){
                        LatLng myPoint = new LatLng(
                                weatherList.get(i).getLat(), weatherList.get(i).getLon());
                        MarkerOptions myMarker = new MarkerOptions()
                                .position(myPoint)
                                .title(myPoint.toString());

                        mMap.addMarker(myMarker).setTag("You were here: " + weatherList.get(i).getDateandTime() +
                                "\nTemp: " + weatherList.get(i).getTemperature() + "(" + weatherList.get(i).getWeather() + ")" );
                    }
                }

            }
        });
    }

    /*
    This is where I create the FAB and tell this button which things it is special to go to bottom right corner
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mapView = super.onCreateView(inflater, container, savedInstanceState);
        mContainer= new CoordinatorLayout(getActivity());
        mContainer.addView(mapView);
        mFloatingActionButton = new FloatingActionButton(getContext());
        mFloatingActionButton.setImageResource(R.drawable.ic_search_location);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    findWeather();
                    Log.d("wakitoli", String.valueOf(weatherList.size()));
                } else {
                    requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
                }
            }
        });

        mContainer.addView(mFloatingActionButton);
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) mFloatingActionButton.getLayoutParams();
        lp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp.setMargins(0,0,100,150);
        mFloatingActionButton.setLayoutParams(lp);
        return mContainer;
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

    /*
    Just menu creation
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_locatr, menu);
        MenuItem searchItem = menu.findItem(R.id.action_locate);
        searchItem.setEnabled(mClient.isConnected());
    }

    /*
    Listens for buttons being pressed on the menu. Gets information or makes requests based on permissions
     */
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
            case R.id.action_delete:
                WeatherLab.get(getContext()).clearDB();
                mMap.clear();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    Self explanatory
     */
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

    /*
    Calls search task
     */
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

    /*
    Self explanatory
     */
    private boolean checkPermission() {
        int result = ContextCompat
                .checkSelfPermission(getActivity(), LOCATION_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /*
    Adds markers to map.
    Zooms in to area of interest
     */
    private void updateUI() {
        if (mMap == null || mMapItem == null) {
            return;
        }
        LatLng itemPoint = new LatLng(mMapItem.getLat(), mMapItem.getLon());
        LatLng myPoint = new LatLng(
                mMapItem.getLat(), mMapItem.getLon());

        MarkerOptions myMarker = new MarkerOptions()
                .position(myPoint)
                .title(myPoint.toString());
        mMap.addMarker(myMarker).setTag("You were here: " + mMapItem.getDateandTime() + "\nTemp: " + mMapItem.getTemperature() + "(" + mMapItem.getWeather() + ")" );

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(itemPoint)
                .include(myPoint)
                .build();
        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, margin);
        mMap.animateCamera(update);
    }


    /*
    Does the heavy lifting. Sends location information to getWeatherJSON.
    Gets information and stores in weather object
     */
    private class SearchTask extends AsyncTask<Location,Void,Void> {
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
                item.setLon(mLocation.getLongitude());
                item.setLat(mLocation.getLatitude());
                item.setWeather(currWeatherJsonObject.getString("description"));
                item.setTemperature(mainJsonObject.getString("temp"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            currentTime = Calendar.getInstance().getTime();
            item.setDateandTime(currentTime.toString());
            Log.i(TAG, "Got a fix: " + mLocation.getLatitude() + " " +
                    mLocation.getLongitude() + " " + currentTime);
            double mLon = item.getLon();
            String mTem = item.getTemperature();
            String mWea = item.getWeather();
            double mLat = item.getLat();
            Log.i("wakitolo Frag", "Lon: " + mLon + " Temp : " + mTem + " Weather " + mWea);
            return null;
        }

        /*
        Adds weather to WeatherLab
         */
        @Override
        protected void onPostExecute(Void result) {
            mMapItem = item;
            mCurrentLocation = mLocation;
            WeatherLab.get(getContext()).addWeather(mMapItem);
            updateUI();
        }
    }

    /*
    Gets weather info from website
     */
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
