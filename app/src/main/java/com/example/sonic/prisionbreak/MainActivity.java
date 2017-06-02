package com.example.sonic.prisionbreak;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener {
    Button binicio;
    TextView centro, actual, estado;
    LocationManager manager;
    Location locInicial, locActual;
    float distancia;
    boolean activo = false;
    Context a;
    SmsManager sms;
    String numero = "5564166955";
    String mensaje = "OBJETIVO A MAS DE 50 METROS ALERTA";
    EditText txtRadio;
    float radio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        centro = (TextView) findViewById(R.id.centro);
        actual = (TextView) findViewById(R.id.actual);
        estado = (TextView) findViewById(R.id.estado);
        txtRadio = (EditText) findViewById(R.id.radio);
        activo = false;
        binicio = (Button) findViewById(R.id.activo);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        a = this;
        sms = SmsManager.getDefault();
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        }catch(Exception e){
            Toast toast = Toast.makeText(this, "error----"+e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }

        binicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activo){
                    binicio.setText("FIJARCENTRO");
                    activo = false;
                }else{
                    try {
                        locInicial = locActual;
                        binicio.setText("DETENER");
                        radio = Float.parseFloat(String.valueOf(txtRadio.getText()));//Integer.parseInt(String.valueOf(txtRadio.getText()));
                        activo = true;
                        centro.setText("Latitud: "+locInicial.getLatitude()+"\nLongitud: "+locInicial.getLongitude());
                    }catch (Exception e){
                        Toast toast1 = Toast.makeText(a,"SIN SEÑAL GPS",Toast.LENGTH_LONG);
                        toast1.show();
                    }

                }

            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {
        locActual = location;
        actual.setText("Latitud: "+locActual.getLatitude()+"\nLongitud: "+locActual.getLongitude());
        if(activo){
            distancia = locInicial.distanceTo(locActual);
            estado.setText("DISTANCIA: "+distancia);
            if(distancia>radio){
                estado.append("\nALERTA!!");
                activo = false;
                sms.sendTextMessage(numero,null,mensaje,null,null);
                Toast.makeText(a, "MENSAJE ENVIADO", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast toast = Toast.makeText(this, "ENCIENDE EL GPS", Toast.LENGTH_LONG);
        toast.show();
    }

    private double convertirLat(double ilatitud){
        return ilatitud * 1000000 / 90;
    }
    private double convertirLong(double ilongitud){
        return ilongitud * 40000000 / 360;
    }

}
