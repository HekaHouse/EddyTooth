package house.heka.eddytooth.highly.adapter;

import android.graphics.Typeface;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import house.heka.eddytooth.beacon.Beacon;
import house.heka.eddytooth.beacon.BeaconAdapter;
import house.heka.eddytooth.beacon.BeaconHolder;
import house.heka.eddytooth.highly.Contiguous;
import house.heka.eddytooth.highly.R;
import house.heka.eddytooth.highly.firebase.AppInstance;
import house.heka.eddytooth.highly.firebase.PeerMessage;


public class NearbyAdapter extends BeaconAdapter {
    private final Contiguous mActive;
    private boolean isSet = false;
    public NearbyAdapter(ArrayList<Beacon> beacons, Contiguous app) {
        super(beacons);
        mActive = app;
    }

    @Override
    public void onBindViewHolder(final BeaconHolder holder, int position) {
        final Beacon chosen = mBeacons.get(position);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference peerRef = database.getReference("appInstance/" + chosen.id);

        peerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final AppInstance peer = dataSnapshot.getValue(AppInstance.class);
                if (peer == null) {
                    return;
                } else {
                    if (!isSet) {
                        //mActive.setNearbyPhoto(holder.beaconImage, peer.instanceId);
                        isSet = true;
                    }
                    if (peer.tag != null && peer.tag.length() > 0)
                        holder.beaconName.setText(peer.tag);
                    else
                        holder.beaconName.setText(mActive.getString(R.string.anonymous));

                    holder.beaconCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            mActive.requestPeerSession(chosen);
//                            mActive.onUserChosen(chosen);
//                            mActive.buildMessageAdapter(peer);
                        }
                    });

                    final DatabaseReference messRef = database.getReference("messages/" + chosen.id);
                    messRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren() && dataSnapshot.getChildrenCount() >= 1) {
                                holder.beaconName.setTypeface(null, Typeface.BOLD_ITALIC);
                            } else {
                                holder.beaconName.setTypeface(null, Typeface.NORMAL);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


}
