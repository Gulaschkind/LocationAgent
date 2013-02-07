package org.ligi.LocationAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import com.androidquery.AQuery;

public class MainActivity extends Activity {

    private EditText username_et;
    private EditText url_et;
    private SeekBar time_seek;
    private TextView act_time_tv;
    private SharedPreferences prefs;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final AQuery aq=new AQuery(this);

        prefs=getSharedPreferences("main", Context.MODE_WORLD_READABLE);

        username_et=aq.find(R.id.username_et).getEditText();
        username_et.setText(prefs.getString("username",""));

        url_et=aq.find(R.id.url_et).getEditText();
        url_et.setText(prefs.getString("url",""));

        time_seek=aq.find(R.id.update_time).getSeekBar();


        act_time_tv=aq.find(R.id.act_time_tv).getTextView();

        time_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                act_time_tv.setText(""+(progress+1)+"s");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        time_seek.setMax(60*60);
        time_seek.setProgress(prefs.getInt("time", 1));

        aq.find(R.id.start_btn).getButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this, UpdaterService.class);
                i.putExtra("username",username_et.getText().toString());
                i.putExtra("url",url_et.getText().toString());
                i.putExtra("time",time_seek.getProgress());

                SharedPreferences.Editor e=prefs.edit();

                e.putString("username",username_et.getText().toString());
                e.putString("url",url_et.getText().toString());
                e.putInt("time", time_seek.getProgress());

                e.commit();

                startService(i);
            }
        });

        aq.find(R.id.stop_btn).getButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, UpdaterService.class));
            }
        });


    }
}
