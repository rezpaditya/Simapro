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

public class WismaActivity extends AppCompatActivity {

    boolean canAddItem = true;

    private String tanah_lokasi;
    private String wisma_nama;
    private String wisma_harga;
    private String wisma_luas;
    private String wisma_imb;
    private String wisma_foto;
    private String wisma_jenis;
    private String wisma_status_terjual;
    private String wisma_status_layak;

    @Bind(R.id.lokasi_tanah_wisma) EditText lokasi_tanah_wisma;
    @Bind(R.id.nama_wisma) EditText nama_wisma;
    @Bind(R.id.harga_wisma) EditText harga_wisma;
    @Bind(R.id.luas_wisma) EditText luas_wisma;
    @Bind(R.id.imb_wisma) EditText imb_wisma;
    @Bind(R.id.foto_wisma) EditText foto_wisma;
    @Bind(R.id.jenis_rumah_wisma) EditText jenis_rumah_wisma;
    @Bind(R.id.status_terjual_wisma) EditText status_terjual_wisma;
    @Bind(R.id.status_layak_wisma) EditText status_layak_wisma;


    private final OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wisma);
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
            tanah_lokasi = lokasi_tanah_wisma.getText().toString();
            wisma_nama = nama_wisma.getText().toString();
            wisma_harga = harga_wisma.getText().toString();
            wisma_luas = luas_wisma.getText().toString();
            wisma_imb = imb_wisma.getText().toString();
            wisma_foto = foto_wisma.getText().toString();
            wisma_jenis = jenis_rumah_wisma.getText().toString();
            wisma_status_terjual = status_terjual_wisma.getText().toString();
            wisma_status_layak = status_layak_wisma.getText().toString();

            try {
                RequestBody formBody = new FormBody.Builder()
                        .add("tanah_lokasi", tanah_lokasi)
                        .add("wisma_nama", wisma_nama)
                        .add("wisma_harga", wisma_harga)
                        .add("wisma_luas", wisma_luas)
                        .add("wisma_imb", wisma_imb)
                        .add("wisma_foto", wisma_foto)
                        .add("wisma_jenis", wisma_jenis)
                        .add("wisma_status_terjual", wisma_status_terjual)
                        .add("wisma_status_terjual", wisma_status_layak)
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
                .url(Config.INPUT_WISMA)
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
