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

    @Bind(R.id.lokasi_tanah) EditText lokasi_tanah;
    @Bind(R.id.nama_tanah) EditText nama_tanah;

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
            try {
                RequestBody formBody = new FormBody.Builder()
                        .add("tanah_lokasi", tanah_lokasi)
                        .add("tanah_nama", tanah_nama)
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
