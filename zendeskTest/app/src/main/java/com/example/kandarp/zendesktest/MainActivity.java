package com.example.kandarp.zendesktest;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    AlertDialog.Builder builder;
    int timerValue = 10;
    int commentDisplayOrder = 0; //0 = newest to oldest, 1 = random
    TimerTask getReviewTask;
    Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        reScheduleTimer(timerValue);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            builder = new AlertDialog.Builder(this)
                    .setTitle("Settings")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            EditText txt = (EditText) ((AlertDialog) dialog).findViewById(R.id.timerValueTxt);
                            if(!txt.getText().toString().isEmpty())
                                timerValue = Integer.valueOf(txt.getText().toString());
                            Spinner spn = (Spinner)((AlertDialog) dialog).findViewById(R.id.spinnerCommentOrder);
                            commentDisplayOrder = spn.getSelectedItemPosition();
                            Toast.makeText(MainActivity.this,"Settings changed.", Toast.LENGTH_SHORT).show();
                            reScheduleTimer(timerValue);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                        }
                    });
            builder.setView(R.layout.settings_layout);
            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void reScheduleTimer(int duration) {
        if(timer != null)
            timer.cancel();
        timer = new Timer("alertTimer",true);
        getReviewTask = new MyTimerTask();
        timer.schedule(getReviewTask,0, duration * 1000L);
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    ((TextView)findViewById(R.id.ticketReviewBodyTxt)).setText(System.nanoTime()+"");
                }});

        }
    }

}
