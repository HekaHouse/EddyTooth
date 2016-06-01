package house.heka.eddytooth.scan;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import house.heka.eddytooth.beacon.Beacon;

/**
 * Created by aron on 5/18/16.
 */
public class EddyScan implements
        ServiceConnection, EddyScanService.OnBeaconEventListener {

    private static final String TAG = "EddyScan";
    private final EddyScanActivity mContext;

    private EddyScanService mService;



    private static final int EXPIRE_TIMEOUT = 5000;
    private static final int EXPIRE_TASK_PERIOD = 1000;
    private boolean isBinding=false;

    public EddyScan(EddyScanActivity a) {
        mContext = a;
    }


    public void getScanService() {
        if (checkBluetoothStatus()) {
            Intent intent = new Intent(mContext, EddyScanService.class);
            mContext.bindService(intent, this, Activity.BIND_AUTO_CREATE);

            mHandler.post(mPruneTask);
        }
    }

    public void disconnect() {
        mHandler.removeCallbacks(mPruneTask);
        if (mService != null &! isBinding) {
            try {
                mService.setBeaconEventListener(null);
                mContext.unbindService(this);
            } catch(IllegalArgumentException e) {
                Log.d(TAG,"EddyScan Service is not bound");
            }
        }
    }

    /* Verify Bluetooth Support */
    public boolean checkBluetoothStatus() {
        BluetoothManager manager =
                (BluetoothManager) mContext.getSystemService(Activity.BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = manager.getAdapter();
        /*
         * We need to enforce that Bluetooth is first enabled, and take the
         * user to settings to enable it if they have not done so.
         */
        if (adapter == null || !adapter.isEnabled()) {
            //Bluetooth is disabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mContext.startActivity(enableBtIntent);
            mContext.finish();
            return false;
        }

        /*
         * Check for Bluetooth LE Support.  In production, our manifest entry will keep this
         * from installing on these devices, but this will allow test devices or other
         * sideloads to report whether or not the feature exists.
         */
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(mContext, "No LE Support.", Toast.LENGTH_SHORT).show();
            mContext.finish();
            return false;
        }

        return true;
    }

    /* This task checks for beacons we haven't seen in awhile */
    private Handler mHandler = new Handler();
    private Runnable mPruneTask = new Runnable() {
        @Override
        public void run() {
            final ArrayList<Beacon> expiredBeacons = new ArrayList<>();
            final long now = System.currentTimeMillis();
            for (house.heka.eddytooth.beacon.Beacon beacon : mContext.getBeacons()) {
                long delta = now - beacon.lastDetectedTimestamp;
                if (delta >= EXPIRE_TIMEOUT) {
                    expiredBeacons.add(beacon);
                }
            }

            if (!expiredBeacons.isEmpty()) {
                Log.d(TAG, "Found " + expiredBeacons.size() + " expired");
                mContext.removeAll(expiredBeacons);
                mContext.notifyChanges();
            }

            mHandler.postDelayed(this, EXPIRE_TASK_PERIOD);
        }
    };

    /* Handle connection events to the discovery service */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "Connected to scanner service");
        isBinding = true;
        mService = ((EddyScanService.LocalBinder) service).getService();
        mService.setBeaconEventListener(this);
        isBinding = false;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "Disconnected from scanner service");
        mService = null;
    }

    /* Handle callback events from the discovery service */
    @Override
    public void onBeaconIdentifier(String deviceAddress, int rssi, String instanceId) {
        final long now = System.currentTimeMillis();
        for (Beacon item : mContext.getBeacons()) {
            if (instanceId.equals(item.id)) {
                //Already have this one, make sure device info is up to date
                item.update(deviceAddress, rssi, now);
                mContext.notifyChanges();
                return;
            }
        }

        //New beacon, add it
        Beacon beacon = new Beacon(deviceAddress, rssi, instanceId, now);
        mContext.addBeacon(beacon);
        mContext.notifyChanges();
    }

    @Override
    public void onBeaconTelemetry(String deviceAddress, float battery, float temperature) {
        for (Beacon item : mContext.getBeacons()) {
            if (deviceAddress.equals(item.deviceAddress)) {
                //Found it, update voltage
                item.battery = battery;
                item.temperature = temperature;
                mContext.notifyChanges();
                return;
            }
        }
    }
}
