package com.example.task41p;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.PrintStream;

public class MainActivity extends AppCompatActivity {
    String lastTime, LASTTIME = "LAST TIME";
    String lastTask, LASTTASK = "LAST TASK";
    String TIMESTOPPED = "TIME STOPPED";
    String TIMERRUNNING = "TIME RUNNING";
    String TIMERPAUSED = "TIME PAUSED";

    long timeWhenStopped = 0;
    boolean timerRunning, timerPaused;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSharedPref();
        handleRotation(savedInstanceState);
    }

    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (timerRunning && !timerPaused) {
            Chronometer timer = findViewById(R.id.timer);
            timeWhenStopped = SystemClock.elapsedRealtime() - timer.getBase();
        }

        outState.putBoolean(TIMERRUNNING, timerRunning);
        outState.putBoolean(TIMERPAUSED, timerPaused);
        outState.putLong(TIMESTOPPED, timeWhenStopped);
    }

    public void startClick(View view) {

        EditText taskText = findViewById(R.id.nameInput);
        String inputString = taskText.getText().toString().trim();
        if (inputString.trim().length() == 0 || inputString == "" || inputString == null) {
            Toast.makeText(MainActivity.this, R.string.no_input_error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (timerRunning && !timerPaused) {
            Toast.makeText(MainActivity.this, R.string.isRunningError, Toast.LENGTH_SHORT).show();
            return;
        }

        Chronometer timer = findViewById(R.id.timer);
        timer.setBase(SystemClock.elapsedRealtime() - timeWhenStopped);
        timer.start();

        timerRunning = true;
        timerPaused = false;
    }

    public void pauseClick(View view) {
        Chronometer timer = findViewById(R.id.timer);

        if (!timerRunning || timerPaused) {
            Toast.makeText(MainActivity.this, R.string.isPausedError, Toast.LENGTH_SHORT).show();
            return;
        }

        timeWhenStopped = SystemClock.elapsedRealtime() - timer.getBase();
        timer.stop();

        timerPaused = true;
    }

    public void stopClick(View view) {
        if (!(timerRunning || timerPaused)) {
            Toast.makeText(MainActivity.this, R.string.isPausedError, Toast.LENGTH_SHORT).show();
            return;
        }

        Chronometer timer = findViewById(R.id.timer);
        EditText nameInput = findViewById(R.id.nameInput);
        TextView lastTimeText = findViewById(R.id.lastTimeText);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        lastTask = nameInput.getText().toString();
        lastTime = timer.getText().toString();
        editor.putString(LASTTASK, lastTask);
        editor.putString(LASTTIME, lastTime);
        lastTimeText.setText("You spent " + lastTime + " on " + lastTask + " last time.");

        editor.apply();

        timer.stop();
        timeWhenStopped = 0;
        timerRunning = false;
        timerPaused = false;
        nameInput.setText(null);
        timer.setBase(SystemClock.elapsedRealtime());
    }

    private void getSharedPref() {
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        lastTime = sharedPref.getString(LASTTIME, "00:00");
        lastTask = sharedPref.getString(LASTTASK, "...");
    }

    private void handleRotation(Bundle savedInstanceState) {
        TextView lastTimeText = findViewById(R.id.lastTimeText);
        if (savedInstanceState != null) {
            timerRunning = savedInstanceState.getBoolean(TIMERRUNNING);
            timerPaused = savedInstanceState.getBoolean(TIMERPAUSED);
            timeWhenStopped = savedInstanceState.getLong(TIMESTOPPED);
        }
        lastTimeText.setText("You spent " + lastTime + " on " + lastTask + " last time.");

        if (timerRunning) {
            Chronometer timer = findViewById(R.id.timer);

            timer.setBase(SystemClock.elapsedRealtime() - timeWhenStopped);

            if (timerPaused) { timer.stop(); }
            else { timer.start(); }
        }
    }

}