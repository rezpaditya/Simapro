package id.co.pln.simapro;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    private static AlertDialog alert;
    private LocationRequest mLocationRequest;
    private Marker marker;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private ActionBarDrawerToggle toggle;
    boolean canAddItem = false;

    private OkHttpClient okHttpClient;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private static final int MILLISECONDS_PER_SECOND = 1000;

    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //create okhttp client
        okHttpClient = new OkHttpClient();

        marker = null;
        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setTouchEnabled(false);

        FloatingActionButton cur_loc = (FloatingActionButton) findViewById(R.id.my_location);
        cur_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationUpdates();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(canAddItem) {
            //untuk menampilkan tombol kirim pada action bar
            this.getMenuInflater().inflate(R.menu.input_lokasi, menu);
            canAddItem = false;
        }else{
            //untuk menghapus tombol kirim pada action bar
            menu.removeItem(1);
            canAddItem = true;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //untuk mengahandle ketika tombol kirim ditekan
        if (id == R.id.action_kirim) {
            try {
                doPost();
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*Toast.makeText(this, "joss",
                    Toast.LENGTH_LONG).show();*/
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       /* if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnInfoWindowClickListener(this);
       /* Snackbar.make(this, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
    }

    private void latestLocation(Location location) {
        double currentLatitude;
        double currentLongitude;
        if (location != null) {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

            LatLng latLng = new LatLng(currentLatitude, currentLongitude);

            CameraPosition cameraPosition = new CameraPosition.Builder().
                    target(latLng).
                    tilt(45).
                    zoom(16).
                    build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        latestLocation(location);
    }

    @Override
    public void onConnected(Bundle bundle) {
        boolean gpsIsEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gpsIsEnabled) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            latestLocation(location);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(
                    "You need to activate location service to use this feature. Please turn on network or GPS mode in location settings")
                    .setTitle("Enable Location")
                    .setCancelable(false)
                    .setPositiveButton("Settings",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(
                                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent);
                                    alert.dismiss();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    alert.dismiss();
                                }
                            });
            alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latestLocation(location);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Toast.makeText(this, "Latitude: " + marker.getPosition().latitude + " Longitude: " + marker.getPosition().longitude,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (marker == null) {
            marker = mMap.addMarker(new MarkerOptions().
                    position(latLng).
                    title("Ambil Lokasi").
                    draggable(true));
        } else {
            marker.remove();
            marker = mMap.addMarker(new MarkerOptions().
                    position(latLng).
                    title("Ambil Lokasi").
                    draggable(true));
        }
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_icon));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //untuk menambah item kirim pada action bar
        canAddItem = true;
        invalidateOptionsMenu();
        //memunculkan panel input lokasi
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        //mengganti toggle drawer menjadi home up button
        toggle.setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //listener untuk home up button
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                toggle.setDrawerIndicatorEnabled(true);
                invalidateOptionsMenu();
            }
        });
    }

    protected void doPost() throws Exception{
        String url = "http://google.com";
        Request request = new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("response", response.body().string());
            }
        });
    }
}
