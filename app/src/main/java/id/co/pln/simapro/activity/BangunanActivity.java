package id.co.pln.simapro.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.co.pln.simapro.Config;
import id.co.pln.simapro.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BangunanActivity extends AppCompatActivity {

    boolean canAddItem = true;

    private String tanah_lokasi;
    private String bangunan_nama;
    private String bangunan_harga;
    private String bangunan_luas;
    private String bangunan_imb;
    private String bangunan_foto;
    private String bangunan_status_layer;
    private String bangunan_status_terjual;

    @Bind(R.id.lokasi_tanah_bangunan) EditText lokasi_tanah_bangunan;
    @Bind(R.id.nama_bangunan) EditText nama_bangunan;
    @Bind(R.id.harga_bangunan) EditText harga_bangunan;
    @Bind(R.id.luas_bangunan) EditText luas_bangunan;
    @Bind(R.id.imb_bangunan) EditText imb_bangunan;
    @Bind(R.id.foto_bangunan) EditText foto_bangunan;
    @Bind(R.id.status_layer_bangunan) EditText status_layer_bangunan;
    @Bind(R.id.status_terjual_bangunan) EditText status_terjual_bangunan;


    private final OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bangunan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
            tanah_lokasi = lokasi_tanah_bangunan.getText().toString();
            bangunan_nama = nama_bangunan.getText().toString();
            bangunan_harga = harga_bangunan.getText().toString();
            bangunan_luas = luas_bangunan.getText().toString();
            bangunan_imb = imb_bangunan.getText().toString();
            bangunan_foto = foto_bangunan.getText().toString();
            bangunan_status_layer = status_layer_bangunan.getText().toString();
            bangunan_status_terjual = status_terjual_bangunan.getText().toString();

            try {
                RequestBody formBody = new FormBody.Builder()
                        .add("tanah_lokasi", tanah_lokasi)
                        .add("bangunan_nama", bangunan_nama)
                        .add("bangunan_harga", bangunan_harga)
                        .add("bangunan_luas", bangunan_luas)
                        .add("bangunan_imb", bangunan_imb)
                        .add("bangunan_foto", bangunan_foto)
                        .add("bangunan_status_layer", bangunan_status_layer)
                        .add("bangunan_status_terjual", bangunan_status_terjual)
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
                .url(Config.INPUT_BANGUNAN)
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
