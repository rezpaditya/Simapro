package id.co.pln.simapro.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import id.co.pln.simapro.Area;
import id.co.pln.simapro.Config;
import id.co.pln.simapro.R;
import id.co.pln.simapro.Unit;
import id.co.pln.simapro.helper.DatabaseConnector;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LandingActivity extends AppCompatActivity {
    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 5000;
    private ArrayList<Unit> list_unit;
    private ArrayList<Area> list_area;
    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        list_unit = new ArrayList<>();
        list_area = new ArrayList<>();

        okHttpClient = new OkHttpClient();

        try {
            getData();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
    * method getData() digunakan untuk mendapatkan nilai dari unit dan area untuk keperluan
    * dropdown list saat input lokasi
    * */

    public void getData() throws Exception{
        Request request_unit = new Request.Builder()
                .url(Config.GET_ALL_UNIT)
                .build();

        Log.d("get Unit", "jalan");
        okHttpClient.newCall(request_unit).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LandingActivity.this, "Koneksi ke server gagal!", Toast.LENGTH_LONG).show();
                        Intent mainIntent = new Intent(LandingActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("response", "unit dapat");

                String jsonData = response.body().string();
                JSONObject Jobject;
                try {
                    Jobject = new JSONObject(jsonData);
                    JSONArray Jarray = Jobject.getJSONArray("unit");
                    for (int i = 0; i < Jarray.length(); i++) {
                        Unit unit = new Unit();
                        JSONObject object = Jarray.getJSONObject(i);
                        unit.setID_UNIT(object.getString("ID_UNIT"));
                        unit.setNM_UNIT(object.getString("NM_UNIT"));
                        list_unit.add(unit);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("get Area", "jalan");
                Request request_area = new Request.Builder()
                        .url(Config.GET_ALL_AREA)
                        .build();

                okHttpClient.newCall(request_area).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LandingActivity.this, "Koneksi ke server gagal!", Toast.LENGTH_LONG).show();
                                Intent mainIntent = new Intent(LandingActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                            }
                        });
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d("response", " area dapat");

                        String jsonData = response.body().string();
                        JSONObject Jobject;
                        try {
                            Jobject = new JSONObject(jsonData);
                            JSONArray Jarray = Jobject.getJSONArray("area");
                            for (int i = 0; i < Jarray.length(); i++) {
                                Area area = new Area();
                                JSONObject object = Jarray.getJSONObject(i);
                                area.setID_UNITP(object.getString("ID_UNITP"));
                                area.setID_BURSARE_UNITP(object.getString("ID_BURSARE_UNITP"));
                                area.setNM_UNITP(object.getString("NM_UNITP"));
                                area.setID_UNIT_UNITP(object.getString("ID_UNIT_UNITP"));
                                list_area.add(area);
                            }

                            //insert data to db
                            new ConnectDB().execute();
                            Log.d("connectdb","jalan");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });
    }


    private class ConnectDB extends AsyncTask<Object, Object, Cursor> {

        DatabaseConnector databaseConnector = new DatabaseConnector(LandingActivity.this);

        @Override
        protected Cursor doInBackground(Object[] params) {
            //Cursor cursor = null;
            try {
                databaseConnector.open();
                //masukan unit ke db
                if(list_unit != null) {
                    for (Iterator<Unit> i = list_unit.iterator(); i.hasNext(); ) {
                        Unit item = i.next();
                        databaseConnector.insertUnit(item.getID_UNIT(), item.getNM_UNIT());
                        Log.d("unit", item.getID_UNIT() + item.getNM_UNIT());
                    }
                    //cursor = databaseConnector.getALlUnit();
                }
                //masukan area ke db
                if(list_area != null) {
                    for (Iterator<Area> i = list_area.iterator(); i.hasNext(); ) {
                        Area item = i.next();
                        databaseConnector.insertArea(item.getID_UNITP(), item.getID_BURSARE_UNITP(), item.getNM_UNITP(), item.getID_UNIT_UNITP());
                        Log.d("area", item.getID_BURSARE_UNITP() + item.getNM_UNITP());
                    }
                    //cursor = databaseConnector.getALlUnit();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            databaseConnector.close();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent mainIntent = new Intent(LandingActivity.this, LoginActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
    }

}
