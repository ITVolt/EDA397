package se.chalmers.justintime.fragments;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import se.chalmers.justintime.R;
import se.chalmers.justintime.database.DatabaseHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListStatisticsFragment extends Fragment {

    DatabaseHelper db;
    Cursor cursor;
    Calendar start = Calendar.getInstance();
    //Calendar stop = Calendar.getInstance();
    TableLayout tableLayout;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    TextView textView_0, textView_1, textView_2 , textView2, textView00, textView01, textView02, textView03;
    TableRow row0, row00, row01, row02, tableRow;
    View view;
    private OnFragmentInteractionListener mListener;

    public ListStatisticsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ListStatisticsFragment newInstance() {
        ListStatisticsFragment fragment = new ListStatisticsFragment();
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
        view = inflater.inflate(R.layout.fragment_list_statistics, container, false);
        showEveryEntry();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

   /* @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public void showEveryEntry() {
        /*               Table Summary
                ----------------------------------
               |            SUMMARY               |
               |   USed For : 10:15:20 Hours      |
                ----------------------------------
               | No   | Start Time     | Duration |
                ----------------------------------
         */
        db = new DatabaseHelper(this.getContext());
        String totalDuration = db.getTotalDuration();
        if(totalDuration==null)
        {
            totalDuration = "0 Hr";
        }
        tableLayout = (TableLayout)view.findViewById(R.id.table_main_);
        row00 = new TableRow(getContext());
        row00.setBackgroundColor(Color.WHITE);

        textView00 = new TextView(getContext());
        textView00.setText("SUMMARY");
        textView00.setGravity(Gravity.CENTER);
        row00.addView(textView00);
        tableLayout.addView(row00);

        row01 = new TableRow(getContext());
        row01.setBackgroundColor(Color.WHITE);

        textView01 = new TextView(getContext());
        String usedFor = " Used For  : " +  totalDuration;
        textView01.setText(usedFor);
        textView01.setGravity(Gravity.CENTER);
        row01.addView(textView01);
        tableLayout.addView(row01);

        row02 = new TableRow(getContext());
        row02.setBackgroundColor(Color.rgb(255,0,0));

        textView02 = new TextView(getContext());
        textView02.setText(" No ");
        row02.addView(textView02);

        textView03 = new TextView(getContext());
        textView03.setText(" Start Time ");
        row02.addView(textView03);

        textView2 = new TextView(getContext());
        textView2.setText(" Duration ");
        row02.addView(textView2);
        tableLayout.addView(row02);

        cursor = db.getData();
        cursor.moveToFirst();
        int max = cursor.getCount();

        for (int i = 1; i < max + 1; i++) {
            tableRow = new TableRow(getContext());
            if(i%2 == 0) tableRow.setBackgroundColor(Color.rgb(255,0,0));
            else tableRow.setBackgroundColor(Color.WHITE);
            textView_0 = new TextView(getContext());
            textView_1 = new TextView(getContext());
            textView_2 = new TextView(getContext());

            textView_0.setText(" " + i + " ");
            textView_0.setGravity(Gravity.CENTER);
            tableRow.addView(textView_0);

            LocalDateTime start = LocalDateTime.ofEpochSecond(cursor.getLong(cursor.getColumnIndex("start_time")), 0, ZoneOffset.UTC);
            textView_1.setText(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            textView_1.setGravity(Gravity.CENTER);
            tableRow.addView(textView_1);

            String duration = " " + cursor.getLong(cursor.getColumnIndex("duration"))+ "";
            textView_2.setText(duration);
            textView_2.setGravity(Gravity.CENTER);
            tableRow.addView(textView_2);
            tableLayout.addView(tableRow);
            cursor.moveToNext();

        }
        cursor.close();

    }
}
