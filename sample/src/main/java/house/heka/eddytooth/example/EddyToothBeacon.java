package house.heka.eddytooth.example;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import house.heka.eddytooth.scan.EddyScanActivity;



public class EddyToothBeacon extends EddyScanActivity {

    private static final int BT_PERMISSION = 19;
    private static final int LOCATION_PERMISSION = 23;
    private static final String TAG = "EddyToothBeacon";


    ArrayList<Advertise> ads = new ArrayList<>();

    private static final String DEFAULT_URL = "http://goo.gl/BhqDJb";


    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eddy_tooth_beacon);

        Advertise ad = new Advertise(this);
        ad.setInstance("1234567891");
        ad.setNamespace("12345678910111213141");
        ads.add(ad);

        ad = new Advertise(this);
        ad.setInstance("1987654321");
        ad.setNamespace("01112131411987654321");
        ads.add(ad);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.toggleEddy);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleAdvertising();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPermissionGranted())
            mScanner.getScanService();
    }


    @Override
    protected void onPause() {
        super.onPause();
        for(Advertise advert: ads) {
            if (advert.isAdvertising())
                advert.stopAdvert();
        }

    }






    public void toggleAdvertising() {
        TextView statusText = (TextView) findViewById(R.id.advertising_status);
        assert statusText != null;
        for(Advertise advert: ads) {
            if (advert.isAdvertising()) {
                advert.stopAdvert();
                statusText.setText(R.string.off);
            } else {
                Uri uri = Uri.parse(DEFAULT_URL);
                advert.startAdvert();
                //advert.startAdvert(uri);
                statusText.setText(R.string.on);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION: {
                mScanner.getScanService();
            }
        }
    }
}
