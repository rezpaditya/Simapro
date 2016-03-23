package id.co.pln.simapro.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.co.pln.simapro.Config;
import id.co.pln.simapro.R;
import id.co.pln.simapro.Unit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TanahActivity extends AppCompatActivity {

    boolean canAddItem = true;

    private String tanah_lokasi;
    private String tanah_nama;
    private String tanah_harga;
    private String tanah_luas;
    private String tanah_alamat;
    private String tanah_longitude;
    private String tanah_latitude;
    private String tanah_altitude;
    private String tanah_pbb;
    private String tanah_biaya_pbb;
    private String tanah_no_sertifikat;
    private String tanah_periode_awal;
    private String tanah_periode_akhir;
    private String tanah_status_operasi;
    private String tanah_status_kepemilikan;
    private String tanah_status_terjual;

    @Bind(R.id.lokasi_tanah) EditText lokasi_tanah;
    @Bind(R.id.nama_tanah) EditText nama_tanah;
    @Bind(R.id.harga_tanah) EditText harga_tanah;
    @Bind(R.id.luas_tanah) EditText luas_tanah;
    @Bind(R.id.alamat_tanah) EditText alamat_tanah;
    @Bind(R.id.longitude_tanah) EditText longitude_tanah;
    @Bind(R.id.latitude_tanah) EditText latitude_tanah;
    @Bind(R.id.altitude_tanah) EditText altitude_tanah;
    @Bind(R.id.pbb_tanah) EditText pbb_tanah;
    @Bind(R.id.biaya_pbb_tanah) EditText biaya_pbb_tanah;
    @Bind(R.id.no_sertifikat_tanah) EditText no_sertifikat_tanah;
    @Bind(R.id.periode_awal) EditText periode_awal;
    @Bind(R.id.periode_akhir) EditText periode_akhir;
    @Bind(R.id.status_operasi_tanah) EditText status_operasi_tanah;
    @Bind(R.id.status_kepemilikan_tanah) EditText status_kepemilikan_tanah;
    @Bind(R.id.status_terjual_tanah) EditText status_terjual_tanah;

    private final OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanah);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //untuk mengahandle ketika tombol kirim ditekan
        if (id == R.id.action_kirim) {
            tanah_lokasi = lokasi_tanah.getText().toString();
            tanah_nama = nama_tanah.getText().toString();
            tanah_harga = harga_tanah.getText().toString();
            tanah_luas = luas_tanah.getText().toString();
            tanah_alamat = alamat_tanah.getText().toString();
            tanah_longitude = longitude_tanah.getText().toString();
            tanah_latitude = latitude_tanah.getText().toString();
            tanah_altitude = altitude_tanah.getText().toString();
            tanah_pbb = pbb_tanah.getText().toString();
            tanah_biaya_pbb = biaya_pbb_tanah.getText().toString();
            tanah_no_sertifikat = no_sertifikat_tanah.getText().toString();
            tanah_periode_awal = periode_awal.getText().toString();
            tanah_periode_akhir = periode_akhir.getText().toString();
            tanah_status_operasi = status_operasi_tanah.getText().toString();
            tanah_status_kepemilikan = status_kepemilikan_tanah.getText().toString();
            tanah_status_terjual = status_terjual_tanah.getText().toString();

            try {
                RequestBody formBody = new FormBody.Builder()
                        .add("tanah_lokasi", tanah_lokasi)
                        .add("tanah_nama", tanah_nama)
                        .add("tanah_harga", tanah_harga)
                        .add("tanah_luas", tanah_luas)
                        .add("tanah_alamat", tanah_alamat)
                        .add("tanah_longitude", tanah_longitude)
                        .add("tanah_latitude", tanah_latitude)
                        .add("tanah_altitude", tanah_altitude)
                        .add("tanah_pbb", tanah_pbb)
                        .add("tanah_biaya_pbb", tanah_biaya_pbb)
                        .add("tanah_no_sertifikat", tanah_no_sertifikat)
                        .add("tanah_periode_awal", tanah_periode_awal)
                        .add("tanah_periode_akhir", tanah_periode_akhir)
                        .add("tanah_status_operasi", tanah_status_operasi)
                        .add("tanah_status_kepemilikan", tanah_status_kepemilikan)
                        .add("tanah_status_terjual", tanah_status_terjual)
                        .build();
                runOkhttp3(formBody);
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*Toast.makeText(this, "joss",
                    .LENGTH_LONG).show();*/
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void runOkhttp3( RequestBody t) throws Exception {
        //String url = "http://10.1.37.136/simapro/masuk_tanah.php";


        Request request = new Request.Builder()
                .url(Config.INPUT_TANAH)
                .post(t)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("response", "input gagal");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                Log.d("response", "input berhasil");

            }
        });
    }
}
