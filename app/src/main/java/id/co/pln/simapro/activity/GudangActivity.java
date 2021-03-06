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

public class GudangActivity extends AppCompatActivity {

    boolean canAddItem = true;

    private String tanah_lokasi;
    private String gudang_nama;
    private String gudang_harga;
    private String gudang_luas;
    private String gudang_imb;
    private String gudang_foto;
    private String gudang_status_layer;
    private String gudang_status_terjual;

    @Bind(R.id.lokasi_tanah_gudang) EditText lokasi_tanah_gudang;
    @Bind(R.id.nama_gudang) EditText nama_gudang;
    @Bind(R.id.harga_gudang) EditText harga_gudang;
    @Bind(R.id.luas_gudang) EditText luas_gudang;
    @Bind(R.id.imb_gudang) EditText imb_gudang;
    @Bind(R.id.foto_gudang) EditText foto_gudang;
    @Bind(R.id.status_terjual_gudang) EditText status_terjual_gudang;


    private final OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gudang);
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
            tanah_lokasi = lokasi_tanah_gudang.getText().toString();
            gudang_nama = nama_gudang.getText().toString();
            gudang_harga = harga_gudang.getText().toString();
            gudang_luas = luas_gudang.getText().toString();
            gudang_imb = imb_gudang.getText().toString();
            gudang_foto = foto_gudang.getText().toString();
            gudang_status_terjual = status_terjual_gudang.getText().toString();

            try {
                RequestBody formBody = new FormBody.Builder()
                        .add("tanah_lokasi", tanah_lokasi)
                        .add("gudang_nama", gudang_nama)
                        .add("gudang_harga", gudang_harga)
                        .add("gudang_luas", gudang_luas)
                        .add("gudang_imb", gudang_imb)
                        .add("gudang_foto", gudang_foto)
                        .add("gudang_status_terjual", gudang_status_terjual)
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
                .url(Config.INPUT_GUDANG)
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
