package house.heka.eddytooth.highly;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.security.GeneralSecurityException;

import house.heka.eddytooth.advertise.EddyAdvertise;
import house.heka.eddytooth.advertise.IAdvertise;
import house.heka.eddytooth.highly.firebase.AppInstance;
import house.heka.eddytooth.scan.EddyScanActivity;



public class Advertise implements IAdvertise {
    private static final String TAG = "Advertise";
    private final Contiguous mActive;

    private EddyAdvertise mEddyAdvert;
    private String mNamespace = "23721723721723721723";



    private String mInstance = "237217237217";

    public Advertise(Contiguous es) {
        mActive = es;
    }


    private void reconstruct(boolean andStart) {
        constructEddyAdvertise();
        if (mEddyAdvert.isStarted() && andStart) {
            stopAdvert();
            startAdvert();
        } else if (andStart) {
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
        Log.d(TAG,String.valueOf(mEddyAdvert.getUid_instance().equals(mInstance)));
        Log.d(TAG,"");
    }

    @Override
    public void setAdvertiseMode(int advertiseMode) {
        mEddyAdvert.setAdvertiseMode(advertiseMode);
        reconstruct(false);
    }

    @Override
    public void setTxPowerLevel(int txPowerLevel) {
        mEddyAdvert.setTxPowerLevel(txPowerLevel);
        reconstruct(false);
    }

    @Override
    public void setInstance(String newInstance) {
        while(newInstance.length() < 12)
            newInstance = newInstance + "0";
        this.mInstance = newInstance.toUpperCase().replaceAll("[^0-9A-F]", "0").replaceAll("0","F").substring(0, 12);
        reconstruct(false);
    }

    @Override
    public void setNamespace(String newNamespace) {
        while(newNamespace.length() < 20)
            newNamespace = newNamespace + "0";
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        this.mNamespace = newNamespace.toUpperCase().replaceAll("[^0-9A-F]", "0").substring(0, 20);


        final DatabaseReference ref = database.getReference("appInstance/" + mInstance);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AppInstance me = dataSnapshot.getValue(AppInstance.class);
                if (me == null) {
                    me = new AppInstance();
                    me.instanceId = mInstance;
                    ref.setValue(me);
                }


                    //EphemeralKeyPair ekp = Encrypt.generateEphemeralKeys(mActive);
                    ref.child("lastSeen").setValue(ServerValue.TIMESTAMP);



            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        reconstruct(true);
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

    @Override
    public String getNamespace() {
        return mNamespace;
    }

    public String getInstance() {
        return mInstance;
    }
}