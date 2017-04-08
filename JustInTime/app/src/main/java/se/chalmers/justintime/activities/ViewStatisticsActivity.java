package se.chalmers.justintime.activities;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import se.chalmers.justintime.R;
import se.chalmers.justintime.database.DatabaseHelper;

public class ViewStatisticsActivity extends AppCompatActivity {
    DatabaseHelper db;
    Cursor cursor;
    Calendar start = Calendar.getInstance();
    Calendar stop = Calendar.getInstance();
    TableLayout tableLayout = (TableLayout) findViewById(R.id.table_main);
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    TextView textView_0, textView_1, textView_2 , textView2, textView00, textView01, textView02, textView03;
    TableRow row0, row00, row01, row02, tableRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_statistics);
        showEveryEntry();
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
        row00 = new TableRow(this);
        row00.setBackgroundColor(Color.WHITE);

        textView00 = new TextView(this);
        textView00.setText("SUMMARY");
        textView00.setGravity(Gravity.CENTER);
        row00.addView(textView00);
        tableLayout.addView(row00);

        row01 = new TableRow(this);
        row01.setBackgroundColor(Color.WHITE);

        textView01 = new TextView(this);
        String usedFor = " Used For  : " + db.getTotalDuration() + "";
        textView01.setText(usedFor);
        textView01.setGravity(Gravity.CENTER);
        row01.addView(textView01);
        tableLayout.addView(row01);

        row02 = new TableRow(this);
        row02.setBackgroundColor(Color.rgb(255,0,0));

        textView02 = new TextView(this);
        textView02.setText(" No ");
        row02.addView(textView02);

        textView03 = new TextView(this);
        textView03.setText(" Start Time ");
        row02.addView(textView03);

        textView2 = new TextView(this);
        textView2.setText(" Duration ");
        row02.addView(textView2);
        tableLayout.addView(row0);

        cursor = db.getData();
        cursor.moveToFirst();
        int max = cursor.getCount();

        for (int i = 1; i < max + 1; i++) {
            tableRow = new TableRow(this);
            if(i%2 == 0) tableRow.setBackgroundColor(Color.WHITE);
            else tableRow.setBackgroundColor(Color.rgb(255,0,0));
            textView_0 = new TextView(this);
            textView_1 = new TextView(this);
            textView_2 = new TextView(this);

            textView_0.setText(" " + i + " ");
            textView_0.setGravity(Gravity.CENTER);
            tableRow.addView(textView_0);

            start.setTimeInMillis(cursor.getLong(cursor.getColumnIndex("start_time")));
            stop.setTimeInMillis(cursor.getLong(cursor.getColumnIndex("duration")));
            String startText = formatter.format(start.getTime()) + "   ";
            textView_1.setText(startText);
            textView_1.setGravity(Gravity.CENTER);
            tableRow.addView(textView_1);

            textView_2.setText(formatter.format(stop.getTime()));
            textView_2.setGravity(Gravity.CENTER);
            tableRow.addView(textView_2);
            tableLayout.addView(tableRow);
            cursor.moveToNext();

        }
       cursor.close();

    }

 
}
