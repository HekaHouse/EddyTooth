package house.heka.eddytooth.highly.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import house.heka.eddytooth.highly.Contiguous;
import house.heka.eddytooth.highly.R;
import house.heka.eddytooth.highly.firebase.PeerMessage;



public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {
    protected final ArrayList<PeerMessage> peerMessages;
    protected final Contiguous mActive;
    public MessageAdapter(ArrayList<PeerMessage> messages, Contiguous active) {
        peerMessages = messages;
        mActive = active;
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.message_layout, parent, false);

        return new MessageHolder(itemView);
    }

    public void addMessage(PeerMessage pm) {
        peerMessages.add(pm);
    }

    @Override
    public void onBindViewHolder(MessageHolder holder, int position) {
        if (peerMessages.get(position).isLocal) {
            holder.remoteCard.setVisibility(View.GONE);
            //mActive.setNearbyPhoto(holder.localImage, LocalPref.getStringPref(mActive,"instanceID"));
            holder.localMessage.setText(peerMessages.get(position).decMessage);
        } else {
            holder.localCard.setVisibility(View.GONE);
            //mActive.setNearbyPhoto(holder.remoteImage, peerMessages.get(position).from);
            holder.remoteMessage.setText(peerMessages.get(position).decMessage);
            //messySnapshot.getRef().removeValue();
        }
        //holder.beaconName.setText(mBeacons.get(position).id);
    }

    @Override
    public int getItemCount() {
        return peerMessages.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder {

        public CardView localCard;
        public ImageView localImage;
        public TextView localMessage;
        public CardView remoteCard;
        public ImageView remoteImage;
        public TextView remoteMessage;
        public MessageHolder(View itemView) {
            super(itemView);
            localCard = (CardView) itemView.findViewById(R.id.local_message_card);
            localImage = (ImageView) itemView.findViewById(R.id.local_image);
            localMessage = (TextView) itemView.findViewById(R.id.local_message);

            remoteCard = (CardView) itemView.findViewById(R.id.remote_message_card);
            remoteImage = (ImageView) itemView.findViewById(R.id.remote_image);
            remoteMessage = (TextView) itemView.findViewById(R.id.remote_message);


        }
    }
}
