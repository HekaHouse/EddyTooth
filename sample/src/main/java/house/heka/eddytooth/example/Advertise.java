package house.heka.eddytooth.example;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.net.Uri;

import house.heka.eddytooth.advertise.EddyAdvertise;
import house.heka.eddytooth.advertise.IAdvertise;
import house.heka.eddytooth.scan.EddyScanActivity;


public class Advertise implements IAdvertise {
    private static final String TAG = "Advertise";
    private final EddyScanActivity mActive;

    private EddyAdvertise mEddyAdvert;
    private String mNamespace = "23721723721723721723";



    private String mInstance = "237217237217";

    public Advertise(EddyScanActivity es) {
        mActive = es;

        constructEddyAdvertise();
    }


    private void reconstruct() {
        constructEddyAdvertise();
        if (mEddyAdvert.isStarted()) {
            stopAdvert();
            startAdvert();
        }
    }

    private void constructEddyAdvertise() {
        mEddyAdvert = new EddyAdvertise(mActive,
                mNamespace,
                mInstance,
                new AdvertiseCallback() {
                    @Override
                    public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                        super.onStartSuccess(settingsInEffect);
                        mActive.setBeaconStatus(true);
                    }

                    @Override
                    public void onStartFailure(int errorCode) {
                        super.onStartFailure(errorCode);
                        mActive.setBeaconStatus(true);
                        EddyAdvertise.processFailureCallback(errorCode);
                    }
                });
    }

    @Override
    public void setAdvertiseMode(int advertiseMode) {
        mEddyAdvert.setAdvertiseMode(advertiseMode);
        reconstruct();
    }

    @Override
    public void setTxPowerLevel(int txPowerLevel) {
        mEddyAdvert.setTxPowerLevel(txPowerLevel);
        reconstruct();
    }

    @Override
    public void setInstance(String newInstance) {
        while(newInstance.length() < 11)
            newInstance = newInstance + "0";
        this.mInstance = newInstance.toUpperCase().replaceAll("[^0-9A-Z]", "0").substring(0, 11);
        reconstruct();
    }

    @Override
    public void setNamespace(String newNamespace) {
        while(newNamespace.length() < 19)
            newNamespace = newNamespace + "0";
        this.mNamespace = newNamespace.toUpperCase().replaceAll("[^0-9A-Z]", "0").substring(0, 19);
        reconstruct();
    }

    @Override
    public void stopAdvert() {
        mEddyAdvert.stopAdvertising();
        mActive.setBeaconStatus(false);
    }

    @Override
    public void startAdvert() {
        mEddyAdvert.startAdvertising(mActive);
        mActive.setBeaconStatus(true);
    }

    @Override
    public void startAdvert(Uri uri) {
        mEddyAdvert.startAdvertising(uri, mActive);
    }

    @Override
    public boolean isAdvertising() {
        return mEddyAdvert.isStarted();
    }
}