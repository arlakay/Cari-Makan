package com.carimakan;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TabHost;
import android.widget.TextView;

import com.carimakan.util.FontCache;

/**
 * Created by Ersa Rizki Dimitri on 24/09/2015.
 */
public class AboutActivity extends AppCompatActivity {
    private Typeface fontLemonMilk, fontHabibi;
    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec spec1 = tabHost.newTabSpec("T1");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("About");

        TabHost.TabSpec spec2 = tabHost.newTabSpec("T2");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Libraries");

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);

        fontLemonMilk       = FontCache.get(getApplicationContext(), "LemonMilk");
        fontHabibi          = FontCache.get(getApplicationContext(), "Habibi-Regular");

        TextView t2 = (TextView)findViewById(R.id.textView2);
        t2.setTypeface(fontLemonMilk);
        TextView t4 = (TextView)findViewById(R.id.textView4);
        t4.setTypeface(fontLemonMilk);
        TextView t5 = (TextView)findViewById(R.id.textView5);
        t5.setTypeface(fontLemonMilk);
        TextView t6 = (TextView)findViewById(R.id.textView6);
        t6.setTypeface(fontHabibi);
        TextView t7 = (TextView)findViewById(R.id.textView7);
        t7.setTypeface(fontHabibi);
        TextView t8 = (TextView)findViewById(R.id.textView8);
        t8.setTypeface(fontHabibi);
        TextView t9 = (TextView)findViewById(R.id.textView9);
        t9.setTypeface(fontHabibi);
        TextView t10 = (TextView)findViewById(R.id.textView10);
        t10.setTypeface(fontHabibi);
        TextView t11 = (TextView)findViewById(R.id.textView11);
        t11.setTypeface(fontHabibi);
        TextView t12 = (TextView)findViewById(R.id.textView12);
        t12.setTypeface(fontHabibi);

        TextView vName = (TextView)findViewById(R.id.versionName);

        String versionName = BuildConfig.VERSION_NAME;
        vName.setText("Ver. "+versionName);

    }
}
