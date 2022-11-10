package com.nkjy.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    //buttons
    Button fast_rewind_btn, play_btn, stop_btn, fast_forward_btn;

    //TextView
    TextView song_title, playing_time;

    //Seekbar
    SeekBar seek_bar;

    //handlers
    Handler handler = new Handler();

    //mediaPlayer
    MediaPlayer mediaPlayer = new MediaPlayer();

    //variables
    double start_time = 0;
    double final_time = 0;
    int forward_time = 10000;
    int backward_time = 10000;
    static int oneTimeOnly = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //buttons
        play_btn = findViewById(R.id.play_btn);
        stop_btn = findViewById(R.id.pause_btn);
        fast_forward_btn = findViewById(R.id.fast_forward_btn);
        fast_rewind_btn = findViewById(R.id.fast_rewind_btn);

        //TextView
        song_title = findViewById(R.id.playing_song_text);
        playing_time = findViewById(R.id.playing_time);

        //Seekbar
        seek_bar = findViewById(R.id.seek_bar);

        //define song title & relative path
        song_title.setText(getResources().getIdentifier(
                "ride",
                "raw",
                getPackageName()
        ));

        mediaPlayer = MediaPlayer.create(this, R.raw.ride);

        //the seek bar is not draggable
        seek_bar.setClickable(false);

        //Adding functionalities

        // PLAY
        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayMusic();
            }
        });

        // PAUSE
        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
            }
        });

        // FAST FORWARD
        fast_forward_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp = (int) start_time;
                if((temp + forward_time) <= final_time){
                    start_time += forward_time;
                    mediaPlayer.seekTo((int) start_time);
                }else{
                    Toast.makeText(MainActivity.this, "Can't Jump Forward", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // FAST REWIND
        fast_rewind_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp = (int) start_time;
                if((temp - backward_time) > 0){
                    start_time -= backward_time;
                    mediaPlayer.seekTo((int) start_time);
                } else{
                    Toast.makeText(MainActivity.this, "Can't Go Back", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void PlayMusic(){
        mediaPlayer.start();

        final_time = mediaPlayer.getDuration();
        start_time = mediaPlayer.getCurrentPosition();

        if(oneTimeOnly == 0){
            seek_bar.setMax((int) final_time);
            oneTimeOnly = 1;
        }

        playing_time.setText(String.format(
                "%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) final_time),
                TimeUnit.MILLISECONDS.toSeconds((long) final_time) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) final_time))
        ));

        seek_bar.setProgress((int) start_time);
        handler.postDelayed(UpdateSongTime, 100);
    }

    //creating the runnable
    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            start_time = mediaPlayer.getCurrentPosition();
            playing_time.setText(String.format(
                    "%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) start_time),
                    TimeUnit.MILLISECONDS.toSeconds((long) start_time) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) start_time))
            ));

            seek_bar.setProgress((int) start_time);
            handler.postDelayed(this, 100);
        }
    };
}