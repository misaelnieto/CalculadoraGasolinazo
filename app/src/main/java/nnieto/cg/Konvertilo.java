package nnieto.cg;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.Locale;


public class Konvertilo extends Activity {

    private double usd_mxn_rate = 20.41;
    private static final double gallon_litre = 3.78541;
    private EditText usd_input;
    private TextView usd_display;
    private EditText mxn_input;
    private TextView mxn_display;
    private TextView exch_rate_input;
    public static final String PREFS_NAME = "Konvertilo-Gasolinazo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konvertilo);

        // Configure tabs
        TabHost tabs=(TabHost)findViewById(R.id.tab_host);
        tabs.setup();

        String tab_title = getString(R.string.tab_usd_mxn);
        TabHost.TabSpec spec=tabs.newTabSpec(tab_title);
        spec.setIndicator(tab_title);
        spec.setContent(R.id.tab1);
        tabs.addTab(spec);

        tab_title = getResources().getString(R.string.tab_mxn_usd);
        spec=tabs.newTabSpec(tab_title);
        spec.setIndicator(tab_title);
        spec.setContent(R.id.tab2);
        tabs.addTab(spec);

        tab_title = "$";
        spec=tabs.newTabSpec(tab_title);
        spec.setIndicator(tab_title);
        spec.setContent(R.id.tab3);
        tabs.addTab(spec);

        // Configure textInputs
        usd_input = (EditText) findViewById(R.id.usd_input);
        usd_display = (TextView) findViewById(R.id.usd_display);
        mxn_input = (EditText) findViewById(R.id.mxn_input);
        mxn_display = (TextView) findViewById(R.id.mxn_display);
        exch_rate_input = (TextView) findViewById(R.id.exch_rate_input);

        //Look for the stored exchange rate
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        usd_mxn_rate = settings.getFloat("usd_mxn_rate", 0);
        if (usd_mxn_rate > 0) {
            exch_rate_input.setText(String.format("%.2f", usd_mxn_rate));
            tabs.setCurrentTab(0);
            findViewById(R.id.exch_rate_label).setVisibility(View.INVISIBLE);
        } else {
            tabs.setCurrentTab(2);
            tabs.getTabWidget().getChildTabViewAt(0).setEnabled(false);
            tabs.getTabWidget().getChildTabViewAt(1).setEnabled(false);
            findViewById(R.id.exch_rate_label).setVisibility(View.VISIBLE);
        }
    }

    public void saveExchangeRate(View v) {
        try {
            usd_mxn_rate = Double.parseDouble(exch_rate_input.getText().toString());
        } catch (NullPointerException | NumberFormatException ex) {
            exch_rate_input.setError(getString(R.string.exch_rate_missing));
            return;
        }
        if (usd_mxn_rate <= 0)
        {
            exch_rate_input.setError(getString(R.string.exch_rate_missing));
            return;
        }

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat("usd_mxn_rate", (float) usd_mxn_rate);

        // Commit the edits!
        editor.commit();

        findViewById(R.id.exch_rate_label).setVisibility(View.INVISIBLE);
        TabHost tabs=(TabHost)findViewById(R.id.tab_host);
        tabs.setCurrentTab(0);
        tabs.getTabWidget().getChildTabViewAt(0).setEnabled(true);
        tabs.getTabWidget().getChildTabViewAt(1).setEnabled(true);
    }

    public void convertUsdToMxn(View v) {
        double value = Double.parseDouble(usd_input.getText().toString());
        mxn_display.setText(String.format(getString(R.string.usd_result), value, usTomx(value)));
    }

    public void convertMxnToUsd(View v) {
        double value = Double.parseDouble(mxn_input.getText().toString());
        usd_display.setText(String.format(getString(R.string.mxn_result), value, mxTous(value)));
    }

    private double usTomx(double usd) { return usd*usd_mxn_rate/gallon_litre; }

    private double mxTous (double mxn) {
        return mxn/usd_mxn_rate*gallon_litre;
    }

}
