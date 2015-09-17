package com.murach.ch10_ex5;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener{

    private TextView messageTextView;
    private Timer timer;
    private Timer downloadTimer;
    private int startingSeconds;
    private long stoppedMillis;
    private Button startButton;
    private Button stopButton;
    private long startMillis;
    private long elapsedMillis;
    private long elapsedStoppedMillis;
    int countedElapsedSeconds;
    int feedDownloadCount = 0;
    private TextView feedDownloadTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startingSeconds = 0;
        stoppedMillis = 0;
        messageTextView = (TextView) findViewById(R.id.messageTextView);
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        feedDownloadTextView = (TextView) findViewById(R.id.feedDownloadTextView);
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        startTimer();
        downloadFile();
    }
    
    private void startTimer() {
        startMillis = System.currentTimeMillis();
        timer = new Timer(true);
        TimerTask task = new TimerTask() {
            
            @Override
            public void run() {
                elapsedMillis = System.currentTimeMillis() - startMillis;
                updateView(elapsedMillis);


            }
        };
        timer.schedule(task, 0, 10000);

    }

    private void updateView(final long elapsedMillis) {
        // UI changes need to be run on the UI thread
        messageTextView.post(new Runnable() {

            int elapsedSeconds = (int) elapsedMillis / 1000;

            @Override
            public void run() {
                messageTextView.setText("Seconds: " + elapsedSeconds);
            }
        });

        feedDownloadTextView.post(new Runnable() {

            @Override
            public void run() {
                feedDownloadTextView.setText("Feed downloaded " + feedDownloadCount + " time(s).");
            }
        });
    }

    @Override
    protected void onPause() {

        timer.cancel();
        super.onPause();
    }


     @Override
    public void onClick(View v) {

         switch(v.getId()){
             case R.id.startButton:
                 startupTimer();
                 break;
             case R.id.stopButton:
                 stopTimer();
                 break;
             default:

         }


    }

    public void startupTimer(){
        timer.cancel();
        timer = new Timer(true);
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                startMillis += elapsedStoppedMillis;
                elapsedMillis = (System.currentTimeMillis() - (startMillis));
                elapsedStoppedMillis = 0;
                updateView(elapsedMillis);
            }
        };
        timer.schedule(task, 0, 10000);

        if (downloadTimer != null){
            downloadTimer.cancel();
            downloadFile();
        }
        stopButton.setEnabled(true);
    }

    public void stopTimer() {
        timer.cancel();
        stoppedMillis = System.currentTimeMillis();
        timer = new Timer(true);

        TimerTask task = new TimerTask() {

            @Override
            public void run() {
               elapsedStoppedMillis = System.currentTimeMillis() - stoppedMillis;
            }
        };
        timer.schedule(task, 0, 1000);

        downloadTimer.cancel();
        stopButton.setEnabled(false);
        //startMillis = System.currentTimeMillis();

    }
    private final String URL_STRING = "http://rss.cnn.com/rss/cnn_tech.rss";
    private final String FILENAME = "news_feed.xml";
    private Context context = this;

    public void downloadFile() {

        downloadTimer = new Timer(true);
        TimerTask downloadTask = new TimerTask() {

            @Override
            public void run() {
                try {

                    // get the URL
                    URL url = new URL(URL_STRING);

                    // get the input stream
                    InputStream in = url.openStream();

                    // get the output stream
                    FileOutputStream out = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);

                    // read input and write output
                    byte[] buffer = new byte[1024];
                    int bytesRead = in.read(buffer);
                    while (bytesRead != -1) {
                        out.write(buffer, 0, bytesRead);
                        bytesRead = in.read(buffer);
                    }
                    feedDownloadCount ++;

                    out.close();
                    in.close();
                } catch (IOException e) {
                    Log.e("News reader", e.toString());
                }
            }
        };
        downloadTimer.schedule(downloadTask, 0, 10000);

    }
}