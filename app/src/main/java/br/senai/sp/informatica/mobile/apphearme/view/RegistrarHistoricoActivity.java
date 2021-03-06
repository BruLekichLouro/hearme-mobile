package br.senai.sp.informatica.mobile.apphearme.view;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import br.senai.sp.informatica.mobile.apphearme.domain.ApiResponse;
import br.senai.sp.informatica.mobile.apphearme.model.Historico;
import br.senai.sp.informatica.mobile.apphearme.model.MyLocationListener;
import br.senai.sp.informatica.mobile.apphearme.service.HearmeRestService;

//TODO: TESTAR GPS E DATE

public class RegistrarHistoricoActivity extends Activity {
    public final String TAG = "RegistrarHistorico";

    int msgCount = 0;
    private HearmeRestService hearmeRestService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        hearmeRestService = new HearmeRestService();

        super.onCreate(savedInstanceState);
        BluetoothDevice device = (BluetoothDevice) getIntent().getExtras().get(BuscarDevices.EXTRA_DEVICE);

        BluetoothConfiguration config = new BluetoothConfiguration();
        config.context = getApplicationContext();
        config.bluetoothServiceClass = BluetoothClassicService.class;
        config.bufferSize = 1024;
        config.characterDelimiter = '\n';
        config.deviceName = "NYMERIA";
        config.callListenersInMainThread = true;

        config.uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        BluetoothService.init(config);

        final BluetoothService service = BluetoothService.getDefaultInstance();

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "entrou no if", Toast.LENGTH_SHORT).show();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

        final Date currentTime = Calendar.getInstance().getTime();

        service.setOnEventCallback(new BluetoothService.OnBluetoothEventCallback() {
            @Override
            public void onDataRead(byte[] buffer, int length) {
                Log.d(TAG, String.format("onDataRead: %d", msgCount));
                Historico novoHistorico = new Historico();
                novoHistorico.setClienteId(1);
                novoHistorico.setDataHorarioAlerta(currentTime);
                novoHistorico.setLat(120);
                novoHistorico.setLon(80);
                getHearmeRestService().enviarDadoHistorico(novoHistorico, new ApiResponse<Historico>() {
                    @Override
                    public void onSuccess(Historico data) {
                        Toast.makeText(RegistrarHistoricoActivity.this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "Erro ao enviar historico", t);
                    }
                });
            }

            @Override
            public void onStatusChange(BluetoothStatus status) {
                Toast.makeText(getBaseContext(), "onStatusChange", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceName(String deviceName) {
                Toast.makeText(getBaseContext(), "onDeviceName", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onToast(String message) {
                Toast.makeText(getBaseContext(), "onToast", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDataWrite(byte[] buffer) {
                Toast.makeText(getBaseContext(), "onDataWrite", Toast.LENGTH_SHORT).show();
            }
        });

        service.connect(device);
    }

    @NonNull
    private HearmeRestService getHearmeRestService() {
        return hearmeRestService;
    }
}
