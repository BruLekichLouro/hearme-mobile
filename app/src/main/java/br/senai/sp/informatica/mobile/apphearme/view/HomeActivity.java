package br.senai.sp.informatica.mobile.apphearme.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import br.senai.sp.informatica.mobile.apphearme.R;
import br.senai.sp.informatica.mobile.apphearme.domain.ApiResponse;
import br.senai.sp.informatica.mobile.apphearme.model.Historico;
import br.senai.sp.informatica.mobile.apphearme.service.HearmeRestService;

public class HomeActivity extends AppCompatActivity {
    private final String TAG = "HomeActivity";
    private ListView listView;
    private BaseAdapter itemLista;
    private Button map;
    private Button btBuscar;
    private Button btLigar;
    private final Context ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        HearmeRestService service = new HearmeRestService();
        service.listaHistorico(new ApiResponse<List<Historico>>() {

            @Override
            public void onSuccess(List<Historico> historicos) {
                itemLista = new HistoricoAdapter(historicos);
                listView = findViewById(R.id.lista);
                listView.setAdapter(itemLista);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "Erro ao listar históricos", t);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btLigar:
                Intent i = new Intent(ctx, BlueActivity.class);
                startActivity(new Intent(this, BlueActivity.class));
                return true;
            case R.id.btBuscar:
                startActivity(new Intent(this, BuscarDevices.class));
                return true;
            case R.id.mapa:
                startActivity(new Intent(this, MapsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
