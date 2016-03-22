package id.co.pln.simapro.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.Duration;

import id.co.pln.simapro.Area;
import id.co.pln.simapro.Config;
import id.co.pln.simapro.R;
import id.co.pln.simapro.Unit;
import id.co.pln.simapro.helper.DatabaseConnector;
import id.co.pln.simapro.helper.SessionManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapClickListener, AdapterView.OnItemSelectedListener {

    private SessionManager sessionManager;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    private static AlertDialog alert;
    private LocationRequest mLocationRequest;
    private Marker marker;
    private Marker userMarker;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private ActionBarDrawerToggle toggle;
    boolean canAddItem = false;
    boolean isCollapsed = true;

    //untuk spinner unit & area
    ArrayList<Unit> list_unit;
    ArrayList<Area> list_area;
    ArrayAdapter unit_adapter, area_adapter;

    private OkHttpClient okHttpClient = new OkHttpClient();

    private ProgressDialog pDialog;

    private static final int MILLISECONDS_PER_SECOND = 1000;

    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    DatabaseConnector databaseConnector;

    /*
    * Variable untuk input lokasi
    * */
    private String id_area;
    private String nama_lokasi;
    private String alamat;
    private String provinsi;
    private String kabupaten;
    private String kecamatan;
    private String kodepos;
    private String latitude;
    private String longitude;
    private String altitude;

    /*
    * Variable of the view using butterknife library
    * */
    @Bind(R.id.unit_pln)
    Spinner unit_pln;
    @Bind(R.id.area_pln)
    Spinner area_pln;

    @Bind(R.id.input_nama_lokasi)
    EditText input_nama_lokasi;
    @Bind(R.id.input_alamat)
    EditText input_alamat;
    @Bind(R.id.input_provinsi)
    EditText input_provinsi;
    @Bind(R.id.input_kabupaten)
    EditText input_kabupaten;
    @Bind(R.id.input_kecamatan)
    EditText input_kecamatan;
    @Bind(R.id.input_latitude)
    EditText input_latitude;
    @Bind(R.id.input_longitude)
    EditText input_longitude;
    @Bind(R.id.input_altitude)
    EditText input_altitude;
    @Bind(R.id.input_kodepos)
    EditText input_kodepos;

    @Bind(R.id.lokasi_aset)
    FloatingActionButton btn_show_lokasi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //bind variable using butterknife
        ButterKnife.bind(this);

        sessionManager = new SessionManager(this);

        if (!sessionManager.checkLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        HashMap<String, String> user = sessionManager.getUserDetails();
        Log.d("id user at home", user.get(SessionManager.NM_USER));

        databaseConnector = new DatabaseConnector(MainActivity.this);

        list_unit = new ArrayList<>();
        unit_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list_unit);
        unit_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unit_pln.setAdapter(unit_adapter);
        unit_pln.setOnItemSelectedListener(this);

        list_area = new ArrayList<>();
        area_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list_area);
        area_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        area_pln.setAdapter(area_adapter);
        area_pln.setOnItemSelectedListener(this);

        marker = null;
        userMarker = null;

        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setTouchEnabled(false);

        FloatingActionButton btn_current_location = (FloatingActionButton) findViewById(R.id.my_location);
        btn_current_location.setOnClickListener(new View.OnClickListener() {
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

    public void collapsePanel() {
        //set field to null
        input_nama_lokasi.setText("");
        input_alamat.setText("");
        input_provinsi.setText("");
        input_kabupaten.setText("");
        input_kecamatan.setText("");
        input_kodepos.setText("");
        input_latitude.setText("");
        input_longitude.setText("");
        input_altitude.setText("");

        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toggle.setDrawerIndicatorEnabled(true);
        invalidateOptionsMenu();
        isCollapsed = true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            Log.d("onBackPress", "drawer close");
        } else if (!isCollapsed) {
            collapsePanel();
            Log.d("onBackPress", "collapse panel");
        } else {
            super.onBackPressed();
            Log.d("onBackPress", "close");
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
        if (canAddItem) {
            //untuk menampilkan tombol kirim pada action bar
            this.getMenuInflater().inflate(R.menu.input_lokasi, menu);
            canAddItem = false;
        } else {
            //untuk menghapus tombol kirim pada action bar
            menu.removeItem(1);
            canAddItem = true;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //untuk mengahandle ketika tombol kirim ditekan
        if (id == R.id.action_kirim) {
            nama_lokasi = input_nama_lokasi.getText().toString();
            try {
                RequestBody formBody = new FormBody.Builder()
                        .add("id_area", id_area)
                        .add("nama_lokasi", nama_lokasi)
                        .add("alamat_lokasi", alamat)
                        .add("propinsi", provinsi)
                        .add("kabupaten", kabupaten)
                        .add("kecamatan", kecamatan)
                        .add("kodepos", kodepos)
                        .add("longitude", longitude)
                        .add("latitude", latitude)
                        .add("altitude", altitude)
                        .add("nm_user", "admin_ojt")
                        .build();
                postLokasi(Config.INPUT_LOKASI, formBody);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_lokasi) {
            collapsePanel();
        } else if (id == R.id.nav_tanah) {
            Intent intent = new Intent(this, TanahActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_wisma) {

        } else if (id == R.id.nav_gudang) {

        } else if (id == R.id.nav_kantor) {

        } else if (id == R.id.nav_logout) {
            sessionManager.logoutUser();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

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
        mMap.setOnMapClickListener(this);
       /* Snackbar.make(this, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
    }

    private void latestLocation(Location location) {
        if (location != null) {
            final double currentLatitude = location.getLatitude();
            final double currentLongitude = location.getLongitude();


            LatLng latLng = new LatLng(currentLatitude, currentLongitude);

            CameraPosition cameraPosition = new CameraPosition.Builder().
                    target(latLng).
                    tilt(45).
                    zoom(16).
                    build();
            //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            if (userMarker == null) {
                userMarker = mMap.addMarker(new MarkerOptions().
                        position(latLng));
            } else {
                userMarker.remove();
                userMarker = mMap.addMarker(new MarkerOptions().
                        position(latLng));
            }
            userMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_user_2));

            //mengambil nilai altitude
            altitude = Double.toString(location.getAltitude());

            btn_show_lokasi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getLokasiAset(currentLatitude, currentLongitude);
                }
            });
        }
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
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
            //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            if (userMarker == null) {
                userMarker = mMap.addMarker(new MarkerOptions().
                        position(latLng));
            } else {
                userMarker.remove();
                userMarker = mMap.addMarker(new MarkerOptions().
                        position(latLng));
            }
            userMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_user_2));

            //mengambil nilai altitude
            altitude = Double.toString(location.getAltitude());
        }
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
        /*Toast.makeText(this, "Latitude: " + marker.getPosition().latitude + "\nLongitude: " + marker.getPosition().longitude,
                Toast.LENGTH_LONG).show();*/
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, "Latitude: " + marker.getPosition().latitude + " Longitude: " + marker.getPosition().longitude, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
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
    public void onMapClick(LatLng latLng) {
        if (marker != null) {
            marker.remove();
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //untuk menambah item kirim pada action bar
        canAddItem = true;
        invalidateOptionsMenu();

        //memunculkan panel input lokasi
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        isCollapsed = false;

        //mengganti toggle drawer menjadi home up button
        toggle.setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //listener untuk home up button
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapsePanel();
            }
        });

        //get data unit
        try {
            getALlUnit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //set nilai latitude, langitude dan altidude
        latitude = Double.toString(marker.getPosition().latitude);
        longitude = Double.toString(marker.getPosition().longitude);

        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        new AsyncTask<Void, Void, Void>() {
            List<Address> addresses;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (geocoder != null) {
                        addresses = geocoder.getFromLocation(Double.valueOf(latitude), Double.valueOf(longitude), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if (addresses != null) {
                    alamat = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    provinsi = addresses.get(0).getAdminArea();
                    kabupaten = addresses.get(0).getSubAdminArea();
                    kecamatan = addresses.get(0).getSubLocality();
                    kodepos = addresses.get(0).getPostalCode();
                }
            }
        }.execute();


        //apply nilai ke dalam edittext
        input_alamat.setText(alamat);
        input_provinsi.setText(provinsi);
        input_kabupaten.setText(kabupaten);
        input_kecamatan.setText(kecamatan);
        input_kodepos.setText(kodepos);
        input_latitude.setText(latitude);
        input_longitude.setText(longitude);
        input_altitude.setText(altitude);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.unit_pln:
                Unit unit = (Unit) unit_pln.getSelectedItem();
                area_pln.setVisibility(View.VISIBLE);
                list_area.clear();
                try {
                    getAreaByUnit(unit.getID_UNIT());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.area_pln:
                Area area = (Area) area_pln.getSelectedItem();
                //set nilai id_area
                id_area = area.getID_BURSARE_UNITP();
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //get all unit
    protected void getALlUnit() throws Exception {
        databaseConnector.open();
        Cursor cursor = databaseConnector.getALlUnit();
        if (cursor.moveToFirst()) {
            do {
                Unit unit = new Unit();
                unit.setID_UNIT(cursor.getString(cursor.getColumnIndex("id_unit")));   //hanya mengambil id dan nama unit saja
                unit.setNM_UNIT(cursor.getString(cursor.getColumnIndex("nm_unit")));
                list_unit.add(unit);
                Log.d("cursor item", "added to list");
            } while (cursor.moveToNext());
        }
        cursor.close();
        databaseConnector.close();

        unit_adapter.notifyDataSetChanged();

        /*String url = Config.GET_ALL_UNIT;
        Request request = new Request.Builder()
                .url(url)
                .build();

        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //wait until list has been updated then update the spinner
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pDialog.dismiss();
                        collapsePanel();
                        Toast.makeText(MainActivity.this, "Koneksi ke server gagal!", Toast.LENGTH_LONG).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                Log.d("response", "dapat");

                String jsonData = response.body().string();
                JSONObject Jobject;
                try {
                    Jobject = new JSONObject(jsonData);
                    JSONArray Jarray = Jobject.getJSONArray("unit");
                    for (int i = 0; i < Jarray.length(); i++) {
                        Unit unit = new Unit();
                        JSONObject object = Jarray.getJSONObject(i);
                        unit.setID_UNIT(object.getString("ID_UNIT"));   //hanya mengambil id dan nama unit saja
                        unit.setNM_UNIT(object.getString("NM_UNIT"));
                        list_unit.add(unit);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //wait until list has been updated then update the spinner
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        unit_adapter.notifyDataSetChanged();
                        pDialog.dismiss();
                    }
                });
            }


        });*/
    }

    //get all area by unit id
    protected void getAreaByUnit(String id) throws Exception {
        databaseConnector.open();
        Cursor cursor = databaseConnector.getAreaByUnit(id);
        if (cursor.moveToFirst()) {
            do {
                Area area = new Area();
                area.setID_BURSARE_UNITP(cursor.getString(cursor.getColumnIndex("id_bursare_unitp")));
                area.setNM_UNITP(cursor.getString(cursor.getColumnIndex("nm_unitp")));
                list_area.add(area);

                Log.d("cursor item", "added to list");
            } while (cursor.moveToNext());
        }
        cursor.close();
        databaseConnector.close();

        area_adapter.notifyDataSetChanged();
        /*Request request = new Request.Builder()
                .url(url)
                .build();

        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pDialog.dismiss();
                        collapsePanel();
                        Toast.makeText(MainActivity.this, "Koneksi ke server gagal!", Toast.LENGTH_LONG).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                Log.d("response", "dapat");

                String jsonData = response.body().string();
                JSONObject Jobject;
                try {
                    Jobject = new JSONObject(jsonData);
                    JSONArray Jarray = Jobject.getJSONArray("area");
                    for (int i = 0; i < Jarray.length(); i++) {
                        Area area = new Area();
                        JSONObject object = Jarray.getJSONObject(i);
                        area.setID_BURSARE_UNITP(object.getString("ID_BURSARE_UNITP"));   //hanya mengambil id dan nama area saja
                        area.setNM_UNITP(object.getString("NM_UNITP"));
                        list_area.add(area);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //wait until list has been updated then update the spinner
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        area_adapter.notifyDataSetChanged();
                        pDialog.dismiss();
                    }
                });
            }
        });*/
    }

    //get lokasi aset
    public void getLokasiAset(double Lat, double Long) {
        Request request = new Request.Builder()
                .url(Config.GET_LOKASI_SEKITAR + "latitude=" + Double.toString(Lat) + "&longitude=" + Double.toString(Long) + "&distance=5")
                .build();

        Log.d("url", Config.GET_LOKASI_SEKITAR + "latitude=" + Double.toString(Lat) + "&longitude=" + Double.toString(Long) + "&distance=5");

        OkHttpClient copy = okHttpClient.newBuilder()
                .readTimeout(999999999, TimeUnit.MILLISECONDS)
                .build();

        copy.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("lokasi sekitar", "gagal pak");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("lokasi sekitar", "dapat");

                String jsonData = response.body().string();
                JSONObject Jobject;
                try {
                    Jobject = new JSONObject(jsonData);
                    Log.d("lokasi sekitar", "json object created");

                    JSONArray Jarray = Jobject.getJSONArray("lokasi");
                    Log.d("lokasi sekitar", "json array created");
                    for (int i = 0; i < Jarray.length(); i++) {
                        JSONObject object = Jarray.getJSONObject(i);

                        final double currentLatitude = Double.valueOf(object.getString("LAT_LOKASI"));
                        final double currentLongitude = Double.valueOf(object.getString("LONG_LOKASI"));
                        final String nama_lokasi = object.getString("NM_LOKASI");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LatLng latLng = new LatLng(currentLatitude, currentLongitude);
                                marker = mMap.addMarker(new MarkerOptions().
                                        position(latLng).
                                        title(nama_lokasi).
                                        draggable(true));
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_lokasi));
                            }
                        });
                        Log.d("lokasi sekitar", object.getString("NM_LOKASI"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //input lokasi
    protected void postLokasi(String url, RequestBody formBody) throws Exception {

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this, "Koneksi ke server gagal!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                Log.d("response", "input berhasil");

                //wait until form has been poested then update the view
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pDialog.dismiss();
                        collapsePanel();
                    }
                });
            }
        });
    }
}
