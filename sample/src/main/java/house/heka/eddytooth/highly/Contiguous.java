package house.heka.eddytooth.highly;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import house.heka.eddytooth.beacon.Beacon;
import house.heka.eddytooth.highly.firebase.AppInstance;
import house.heka.eddytooth.highly.firebase.Nearby;
import house.heka.eddytooth.scan.EddyScanActivity;



public class Contiguous extends EddyScanActivity {

    private static final int BT_PERMISSION = 19;
    private static final int LOCATION_PERMISSION = 23;
    private static final String TAG = "Contiguous";


    ArrayList<Advertise> ads = new ArrayList<>();

    private static final String DEFAULT_URL = "http://goo.gl/BhqDJb";
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FloatingActionButton fab;
    private FirebaseAuth mAuth;
    private String instance;


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

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }



    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    private Handler handler = new Handler();


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
      /* do what you need to do */
            purgeStaleBeacons();
      /* and here comes the "trick" */
            handler.postDelayed(this, 1000*60);
        }
    };

    private void purgeStaleBeacons() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference nearbyRef = database.getReference("nearby/" + instance);

        nearbyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Nearby neighbor = child.getValue(Nearby.class);
                    if ((System.currentTimeMillis() - neighbor.lastSeen) / 1000 > 60) {
                        nearbyRef.child(neighbor.instanceId).setValue(null);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final DatabaseReference contiguousRef = database.getReference("contiguous/" + instance);

        contiguousRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Nearby neighbor = child.getValue(Nearby.class);
                    if ((System.currentTimeMillis() - neighbor.lastSeen) / 1000 > 60) {
                        contiguousRef.child(neighbor.instanceId).setValue(null);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eddy_tooth_beacon);

        try {
            instance = SHA1(InstanceID.getInstance(Contiguous.this).getId()).substring(0,12).toUpperCase();
            namespace = SHA1(getString(R.string.heka_house)).replaceAll("[^0-9A-Fa-f]","0").substring(0,20);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in


                    if (ads.size() < 1) {
                        Advertise ad = new Advertise(Contiguous.this);
                        ad.setInstance(instance);
                        ad.setNamespace(namespace);

                        ads.add(ad);

                        RecyclerView recList = (RecyclerView) findViewById(R.id.discovery_results);
                        recList.setHasFixedSize(true);
                        LinearLayoutManager llm = new LinearLayoutManager(Contiguous.this);
                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                        recList.setLayoutManager(llm);

                        recList.setAdapter(mBeaconAdapter);


                        fab = (FloatingActionButton) findViewById(R.id.toggleEddy);
                        assert fab != null;

                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                toggleAdvertising();
                            }
                        });
                    }


                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                        handler.postDelayed(runnable, 1000*60);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInAnonymously", task.getException());
                            Toast.makeText(Contiguous.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPermissionGranted())
            mScanner.getScanService(namespace);
    }


    @Override
    protected void onPause() {
        super.onPause();
        for(Advertise advert: ads) {
            if (advert.isAdvertising())
                advert.stopAdvert();
        }

    }
    @Override
    public void removeAll(ArrayList<Beacon> expiredBeacons) {
        super.removeAll(expiredBeacons);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        for (Beacon toGo: expiredBeacons) {
           removeBeacon(toGo, database);
        }
    }

    private void removeBeacon(Beacon beacon, FirebaseDatabase database) {
        final DatabaseReference ref = database.getReference("nearby/" + instance + "/" + beacon.id);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Nearby near = dataSnapshot.getValue(Nearby.class);
                if (near == null) {
                    return;
                } else {
                    ref.setValue(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void addBeacon(final Beacon beacon) {
        if (!getBeacons().contains(beacon))
            super.addBeacon(beacon);


        FirebaseDatabase database = FirebaseDatabase.getInstance();

        establishNearby(beacon, database);

        establishContiguous(beacon, database);
    }

    private void establishContiguous(Beacon beacon, final FirebaseDatabase database) {
        final DatabaseReference nearbyRef = database.getReference("nearby/" + beacon.id);

        nearbyRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Nearby neighbor = dataSnapshot.getValue(Nearby.class);
                if (neighbor != null && neighbor.instanceId != null && !neighbor.instanceId.equals(instance)) {
                    addContiguous(neighbor,database);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }


            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Nearby neighbor = dataSnapshot.getValue(Nearby.class);
                if ((System.currentTimeMillis() - neighbor.lastSeen)/1000 > 45) {
                    nearbyRef.setValue(null);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
    private void addContiguous(final Nearby neighbor, FirebaseDatabase database) {
        final DatabaseReference contiguousRef = database.getReference("contiguous/" + instance + "/" + neighbor.instanceId);

        contiguousRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Nearby near = dataSnapshot.getValue(Nearby.class);
                if (near == null) {
                    near = neighbor;
                    contiguousRef.setValue(near);
                }
                if ((System.currentTimeMillis() - near.lastSeen)/1000 > 30) {
                    Log.d(TAG,String.valueOf((System.currentTimeMillis() - near.lastSeen)/1000));
                    contiguousRef.child("lastSeen").setValue(ServerValue.TIMESTAMP);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void establishNearby(final Beacon beacon, final FirebaseDatabase database) {
        final DatabaseReference nearbyRef = database.getReference("nearby/" + instance + "/" + beacon.id);

        nearbyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Nearby near = dataSnapshot.getValue(Nearby.class);
                if (near == null) {
                    near = new Nearby();
                    near.instanceId = beacon.id;
                    nearbyRef.setValue(near);
                }
                if ((System.currentTimeMillis() - near.lastSeen)/1000 > 30) {
                    Log.d(TAG,String.valueOf((System.currentTimeMillis() - near.lastSeen)/1000));
                    nearbyRef.child("lastSeen").setValue(ServerValue.TIMESTAMP);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                mScanner.getScanService(namespace);
            }
        }
    }
}
