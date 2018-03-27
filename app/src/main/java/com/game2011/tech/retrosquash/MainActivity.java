package com.game2011.tech.retrosquash;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class MainActivity extends Activity {
    Canvas canvas;
    SquashCourtView squashCourtView;

    // Sound
    // Initialize sound variables
    private SoundPool soundPool;
    int sample1 = -1;
    int sample2 = -1;
    int sample3 = -1;
    int sample4 = -1;

    // For getting display details like the number of pixels
    Display display;
    Point size;
    int screenWidth;
    int screenHeight;

    // Game Objects
    int racketWidth;
    int racketHeight;
    Point racketPosition;

    Point ballPosition;
    int ballWidth;

    // For Ball Movement
    boolean ballIsMovingLeft;
    boolean ballIsMovingRight;
    boolean ballIsMovingUp;
    boolean ballIsMovingDown;

    // For racket movement
    boolean racketIsMovingLeft;
    boolean racketIsMovingRight;

    // Stats
    long lastFrameTime;
    int fps;
    int score;
    int lives;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        squashCourtView = new SquashCourtView(this);
        setContentView(squashCourtView);

        // Setup the sound
        soundPool = (new SoundPool.Builder()).setMaxStreams(2).build();

        sample1 = soundPool.load(this, R.raw.sample1, 1);
        sample2 = soundPool.load(this, R.raw.sample2, 1);
        sample3 = soundPool.load(this, R.raw.sample3, 1);
        sample4 = soundPool.load(this, R.raw.sample4, 1);

        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        // The game objects
        racketPosition = new Point();
        racketPosition.x = screenWidth/2;
        racketPosition.y = screenHeight - 20;
        racketWidth = screenWidth/8;
        racketHeight = 10;

        ballWidth = screenWidth/35;
        ballPosition = new Point();
        ballPosition.x = screenWidth/2;
        ballPosition.y = 1 + ballWidth;

        lives = 3;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        soundPool.play(sample3, 1.0f, 1.0f, 0, 0, 1.0f);
        return super.onTouchEvent(event);
    }

    private class SquashCourtView extends SurfaceView{
        public SquashCourtView(Context context) {
            super(context);
        }
    }
}
