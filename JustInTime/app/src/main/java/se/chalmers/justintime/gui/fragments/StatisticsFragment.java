package se.chalmers.justintime.gui.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.chalmers.justintime.R;
import se.chalmers.justintime.gui.StatisticsFragmentPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticsFragment extends Fragment {

    private View view;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StatisticsFragment.
     */
    public static StatisticsFragment newInstance() {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_statistics, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.statistics_viewpager);
        viewPager.setAdapter(new StatisticsFragmentPagerAdapter(getChildFragmentManager(), view.getContext()));

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.statistics_sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
