package house.heka.eddytooth.scan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import house.heka.eddytooth.beacon.Beacon;
import house.heka.eddytooth.beacon.BeaconAdapter;

/**
 * Created by aron on 5/18/16.
 */
public abstract class EddyScanActivity extends AppCompatActivity {
    protected BeaconAdapter mBeaconAdapter;
    protected ArrayList<Beacon> mBeacons;
    protected EddyScan mScanner;
    protected boolean beaconBroadcastIsActive;
    protected String namespace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildBeaconAdapter();
        mScanner = new EddyScan(this);
    }

    public void notifyChanges() {
        mBeaconAdapter.notifyDataSetChanged();
    }


    public void buildBeaconAdapter() {
        mBeacons = new ArrayList<>();
        mBeaconAdapter = new BeaconAdapter(mBeacons);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScanner.getScanService(namespace);
    }


    @Override
    protected void onPause() {
        super.onPause();

        mScanner.disconnect();
    }


    public void removeAll(ArrayList<Beacon> expiredBeacons) {
        mBeacons.removeAll(expiredBeacons);
    }

    public ArrayList<Beacon> getBeacons() {
        return mBeacons;
    }

    public void addBeacon(Beacon beacon) {
        if (!mBeacons.contains(beacon))
            mBeacons.add(beacon);
    }

    public void setBeaconStatus(boolean b) {
        beaconBroadcastIsActive = b;
    }
}
