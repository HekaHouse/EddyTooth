package house.heka.eddytooth.highly.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import house.heka.eddytooth.beacon.Beacon;
import house.heka.eddytooth.highly.Contiguous;
import house.heka.eddytooth.highly.R;
import house.heka.eddytooth.highly.firebase.AppInstance;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SecureComm#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecureComm extends Fragment {
    private static final String REMOTE_BEACON = "remote";
    private static final String LOCAL_BEACON = "local";

    private AppInstance mRemote;
    private String mLocal;

    private TextInputEditText message;
    private ImageButton sender;

    public SecureComm() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mRemote remote app id.
     * @param mLocal local app id.
     * @return A new instance of fragment SecureComm.
     */
    public static SecureComm newInstance(Beacon mRemote, String mLocal) {
        SecureComm fragment = new SecureComm();
        Bundle args = new Bundle();
        args.putString(REMOTE_BEACON, mRemote.id);
        args.putString(LOCAL_BEACON, mLocal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference appRef = database.getReference("appInstance/" + getArguments().getString(REMOTE_BEACON));

            appRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mRemote = dataSnapshot.getValue(AppInstance.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            mLocal = getArguments().getString(LOCAL_BEACON);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.secure_comm_fragment, container, false);
        sender = (ImageButton) view.findViewById(R.id.send_message);
        message = (TextInputEditText) view.findViewById(R.id.input_message);
        sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendMessage(mRemote,message.getText().toString());
            }
        });
        RecyclerView recList = (RecyclerView) view.findViewById(R.id.message_list);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        //recList.setAdapter(((Contiguous)getActivity()).mMessageAdapter);
        return view;
    }


    public void onSendMessage(AppInstance chosen, String message) {
        //((Contiguous)getActivity()).sendPeerMessage(chosen,message);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
