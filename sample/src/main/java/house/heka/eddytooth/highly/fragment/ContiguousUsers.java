package house.heka.eddytooth.highly.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import house.heka.eddytooth.beacon.Beacon;
import house.heka.eddytooth.highly.Contiguous;
import house.heka.eddytooth.highly.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnNearbyUserChosen} interface
 * to handle interaction events.
 * Use the {@link ContiguousUsers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContiguousUsers extends Fragment {

    private OnNearbyUserChosen mListener;

    public ContiguousUsers() {
        // Required empty public constructor
    }

    public static ContiguousUsers newInstance() {
        ContiguousUsers fragment = new ContiguousUsers();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.contiguous_user_fragment, container, false);

        RecyclerView recList = (RecyclerView) v.findViewById(R.id.discovery_results);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        //recList.setAdapter(((Contiguous)getActivity()).getBeaconAdapter());

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNearbyUserChosen) {
            mListener = (OnNearbyUserChosen) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNearbyUserChosen");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnNearbyUserChosen {
        // TODO: Update argument type and name
        void onUserChosen(Beacon chosen);
    }
}
