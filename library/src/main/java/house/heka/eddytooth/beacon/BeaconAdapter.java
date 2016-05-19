package house.heka.eddytooth.beacon;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by aron on 5/18/16.
 */
public class BeaconAdapter extends ArrayAdapter<Beacon> {
    public BeaconAdapter(Context context, int resource, ArrayList<Beacon> objects) {
        super(context, resource, objects);
    }
}
