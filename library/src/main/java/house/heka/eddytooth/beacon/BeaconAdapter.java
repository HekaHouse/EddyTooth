package house.heka.eddytooth.beacon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import house.heka.eddytooth.R;

/**
 * Created by aron on 5/18/16.
 */
public class BeaconAdapter extends RecyclerView.Adapter<BeaconHolder> {
    protected final ArrayList<Beacon> mBeacons;

    public BeaconAdapter(ArrayList<Beacon> beacons) {
        mBeacons = beacons;
    }

    @Override
    public BeaconHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.beacon_layout, parent, false);

        return new BeaconHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BeaconHolder holder, int position) {
        holder.beaconName.setText(mBeacons.get(position).id);
    }

    @Override
    public int getItemCount() {
        return mBeacons.size();
    }
}
