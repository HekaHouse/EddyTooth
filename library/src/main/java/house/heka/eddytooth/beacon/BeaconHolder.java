package house.heka.eddytooth.beacon;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import house.heka.eddytooth.R;

/**
 * Created by aron on 7/18/16.
 */
public class BeaconHolder extends RecyclerView.ViewHolder {
    public TextView beaconName;
    public BeaconHolder(View itemView) {
        super(itemView);
        beaconName = (TextView)itemView.findViewById(R.id.beacon_name);
    }
}
