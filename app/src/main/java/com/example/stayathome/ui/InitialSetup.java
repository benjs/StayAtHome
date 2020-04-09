package com.example.stayathome.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.stayathome.R;
import com.example.stayathome.helper.SharedPreferencesHelper;

import java.util.HashSet;

public class InitialSetup extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;
    SharedPreferencesHelper prefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_setup);

        // Assign SharedPreferencesHelper when the Activity is created
        prefHelper = new SharedPreferencesHelper(this);

        //PROPERLY REQUEST/MANAGE PERMISSION FOR ANDROID 8.1+

        // Request permission for location in order to receive the name of the SSID and BSSID
        if(!locationPermissionGranted()){
            requestLocationPermission();
            //
        }

        // Save SSID when launching for first time
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        HashSet<String> wifis = new HashSet<String>();
        wifis.add(wifiManager.getConnectionInfo().getSSID());
        prefHelper.storeSet("wifi", wifis);

        Button startBtn = (Button) findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private boolean locationPermissionGranted(){
        // Return true if access to the location permission has been granted
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    break;
                } else {
                    // Permission denied
                    break;
                }
            }
            // Add more cases to the switch to check for other permission that might have been requested
        }
    }
} // End class InitialSetup
