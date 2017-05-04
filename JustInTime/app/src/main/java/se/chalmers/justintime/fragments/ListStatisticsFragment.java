package se.chalmers.justintime.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import se.chalmers.justintime.R;
import se.chalmers.justintime.alert.SharedPreference;
import se.chalmers.justintime.database.DatabaseHelper;

public class ListStatisticsFragment extends Fragment {

    View view;
    Switch timerInfo,generalInfo;
    TextView timerInfoText, appInfoText;
    SharedPreference mPreferences;
    DatabaseHelper db;
    private OnFragmentInteractionListener mListener;

    public ListStatisticsFragment() {
        // Required empty public constructor
    }


    public static ListStatisticsFragment newInstance() {
        ListStatisticsFragment fragment = new ListStatisticsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = new SharedPreference(getContext());
        db = new DatabaseHelper(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_statistics, container, false);
        timerInfo = (Switch) view.findViewById(R.id.timerInfoSwitch);
        generalInfo = (Switch) view.findViewById(R.id.generalInfoSwitch);
        timerInfoText = (TextView) view.findViewById(R.id.timerInfoText);
        appInfoText = (TextView) view.findViewById(R.id.appInfoText);
        timerInfoText.setVisibility(View.GONE);
        appInfoText.setVisibility(View.GONE);
        setOnSwitchChangeListener();
        return view;
    }

    private void setOnSwitchChangeListener() {
        timerInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String totalDuration = db.getTotalDuration();
                    if(totalDuration==null)
                    {
                        totalDuration = "0 Hour";
                    }
                    timerInfoText.setText("Used for : " + totalDuration );
                    appInfoText.setText("Used : " + mPreferences.getAppUsageCount() + " times");
                    timerInfoText.setVisibility(View.VISIBLE);
                    appInfoText.setVisibility(View.VISIBLE);
                }
                else{
                    timerInfoText.setVisibility(View.GONE);
                    appInfoText.setVisibility(View.GONE);
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
