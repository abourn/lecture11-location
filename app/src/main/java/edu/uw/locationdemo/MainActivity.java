package edu.uw.locationdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LOCATION";

    private GoogleApiClient mGoogleApiClient;

    private TextView textLat;
    private TextView textLng;

    private static final int LOC_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textLat = (TextView)findViewById(R.id.txt_lat);
        textLng = (TextView)findViewById(R.id.txt_lng);

        if (mGoogleApiClient == null) { // only instantiate if we don't already have a connection.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        // we want to connect to the API Client when the application becomes visible (hence why it's in onStart)=
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Ready to get location
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // can choose other Priorities, depending on how much battery/how accurate you want to use/be
        request.setInterval(10000); // every 10 seconds get an updated location
        request.setFastestInterval(5000);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        // make sure we have permission
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            // great, now send the request                                                      // make the Activity (this) the locationListener parameter
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this); // specify a listener to listen to changes in my location

        } else {
            // get the permission
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOC_REQUEST_CODE);
        }
    }

    // if we ask for permission, then we need to handle it as well.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOC_REQUEST_CODE) {
            // if gave us permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onConnected(null);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // What happens if I get disconnected in the middle. Will leave for blank now
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // this is where you'd do work with the location for Homework, like draw a line, etc.
    @Override
    public void onLocationChanged(Location location) {
        // called when the location changes (duh)
        if (location != null) {
            textLat.setText("" + location.getLatitude());
            textLng.setText("" + location.getLongitude());
        } else {
            Log.v(TAG, "Received null location");
        }
    }
}
