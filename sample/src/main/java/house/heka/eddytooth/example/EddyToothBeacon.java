package house.heka.eddytooth.example;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import house.heka.eddytooth.scan.EddyScanActivity;



public class EddyToothBeacon extends EddyScanActivity {

    private static final int BT_PERMISSION = 19;
    private static final String TAG = "EddyToothBeacon";
    private Advertise advert;

    private static final String DEFAULT_URL = "http://goo.gl/BhqDJb";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eddy_tooth_beacon);



        advert = new Advertise(this);

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
        if (advert.isAdvertising())
            advert.stopAdvert();
    }






    public void toggleAdvertising() {
        TextView statusText = (TextView) findViewById(R.id.advertising_status);
        assert statusText != null;
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
