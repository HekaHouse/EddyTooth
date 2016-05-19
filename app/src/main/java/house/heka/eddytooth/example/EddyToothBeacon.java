package house.heka.eddytooth.example;

import android.Manifest;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import house.heka.eddytooth.scan.EddyScanActivity;
import house.heka.eddytooth.R;
import house.heka.eddytooth.advertise.EddyAdvertise;

public class EddyToothBeacon extends EddyScanActivity {

    private static final int BT_PERMISSION = 19;
    private static final String TAG = "EddyToothBeacon";
    private EddyAdvertise mEddyAdvert;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eddy_tooth_beacon);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, BT_PERMISSION);
        } else {
            constructEddy();
        }

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
        mScanner.getScanService();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mEddyAdvert.isStarted()) {
            mEddyAdvert.stopAdvertising();
        }
    }



    private void constructEddy() {
        final TextView statusText = (TextView) findViewById(R.id.advertising_status);
        assert statusText != null;
        mEddyAdvert = new EddyAdvertise(this, new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                statusText.setText(R.string.on);
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
                statusText.setText(R.string.off);
                EddyAdvertise.processFailureCallback(errorCode);
            }
        });
    }


    public void toggleAdvertising() {
        TextView statusText = (TextView) findViewById(R.id.advertising_status);
        assert statusText != null;
        if (mEddyAdvert.isStarted()) {
            mEddyAdvert.stopAdvertising();
            statusText.setText(R.string.off);
        } else {
            mEddyAdvert.startAdvertising(this);
            statusText.setText(R.string.on);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case BT_PERMISSION: {
                Log.i(TAG, "request result for bluetooth advertising returned");
            }
        }
    }
}
