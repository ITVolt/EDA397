package se.chalmers.justintime.gui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import se.chalmers.justintime.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TempStatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TempStatisticsFragment extends Fragment {
    private static final String ARG_PAGE = "ARG_PAGE";

    private String fragName;


    public TempStatisticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fragmentName Tab name.
     * @return A new instance of fragment TempStatisticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TempStatisticsFragment newInstance(String fragmentName) {
        TempStatisticsFragment fragment = new TempStatisticsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PAGE, fragmentName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fragName = getArguments().getString(ARG_PAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temp_statistics, container, false);
        TextView textView = (TextView) view;
        textView.setText(fragName);
        return view;
    }

}
