package com.game2011.tech.retrosquash;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

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
        //soundPool.play(sample3, 1.0f, 1.0f, 0, 0, 1.0f);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        while (true) {
            squashCourtView.pause();
            break;
        }

        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        squashCourtView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        squashCourtView.resume();
    }

    private class SquashCourtView extends SurfaceView implements Runnable{
        Thread ourThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playingSquash;
        Paint paint;

        public SquashCourtView(Context context) {
            super(context);

            ourHolder = getHolder();
            paint = new Paint();
            ballIsMovingDown = true;
            setBallRandomDirection(false);
        }

        @Override
        public void run() {
            while (playingSquash) {
                updateCourt();
                drawCourt();
                controlFPS();
            }
        }

        private void controlFPS() {
            long timeThisFrame = (System.currentTimeMillis() - lastFrameTime);
            long timeToSleep = 15 - timeThisFrame;

            if (timeThisFrame > 0) {
                fps = (int) (1000 / timeThisFrame);
            }

            if (timeToSleep > 0) {
                try {
                    ourThread.sleep(timeToSleep);
                } catch (InterruptedException e) {
                }
            }

            lastFrameTime = System.currentTimeMillis();
        }

        private void drawCourt() {
            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();
                //Paint paint = new Paint();
                canvas.drawColor(Color.BLACK);

                // Draw the background
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(45);
                canvas.drawText("Score: " + score + "Lives: " + lives + " fps:" + fps, 20, 40, paint);

                //Draw the squash racket
                canvas.drawRect(racketPosition.x -(racketWidth / 2), racketPosition.y - (racketHeight / 2), racketPosition.x + (racketWidth / 2), racketPosition.y + racketHeight, paint);

                //Draw the ball
                canvas.drawRect(ballPosition.x, ballPosition.y, ballPosition.x + ballWidth, ballPosition.y + ballWidth, paint) ;
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        private void updateCourt() {
            if (racketIsMovingRight) {
                racketPosition.x = racketPosition.x + 10;
            }

            if (racketIsMovingLeft) {
                racketPosition.x = racketPosition.x - 10;
            }

            // detect collisions
            // hit right of screen
            if (ballPosition.x + ballWidth > screenWidth) {
                ballIsMovingLeft = true;
                ballIsMovingRight = false;
                soundPool.play(sample1, 1.0f, 1.0f, 0, 0, 1.0F);
            }

            if ( ballPosition.x < 0 ) {
                ballIsMovingLeft = false;
                ballIsMovingRight = true;
                soundPool.play(sample1, 1.0f, 1.0f, 0, 0, 1.0F);
            }if ( ballPosition.y > screenHeight - ballWidth)
            {
                lives = lives - 1;

                if (lives == 0) {
                    lives = 3;
                    score = 0;
                    soundPool.play(sample4, 1.0f, 1.0f, 0, 0, 1.0F);
                }

                setBallRandomDirection(true);
            }

            // we hit the top of the screen
            if ( ballPosition.y <= 0) {
                ballIsMovingDown = true;
                ballIsMovingUp = false;
                ballPosition.y = 1;
                soundPool.play(sample2, 1.0f, 1.0f, 0, 0, 1.0F);
            }

            // Set new coordinates
            if (ballIsMovingDown) {
                ballPosition.y += 6;
            }

            if (ballIsMovingUp) {
                ballPosition.y -= 10;
            }

            if (ballIsMovingLeft) {
                ballPosition.x -= 12;
            }

            if (ballIsMovingRight) {
                ballPosition.x += 12;
            }

            // Has ball hit racket?
            if ( ballPosition.y + ballWidth >= (racketPosition.y - racketHeight/2)) {
                int halfRacket = racketWidth/2;
                if (ballPosition.x + ballWidth > (racketPosition.x-halfRacket) && ballPosition.x - ballWidth < (racketPosition.x + halfRacket)) {
                    // rebound the ball vertically and play a sound
                    soundPool.play(sample3, 1.0f, 1.0f, 0, 0, 1.0F);
                    score++;
                    ballIsMovingUp = true;
                    ballIsMovingDown = false;
                    // now decide how to rebound the ball horizontally
                    if ( ballPosition.x > racketPosition.x) {
                        ballIsMovingRight = true;
                        ballIsMovingLeft = false;
                    } else {
                        ballIsMovingRight = false;
                        ballIsMovingLeft = true;
                    }
                }
            }

        }

        private void setBallRandomDirection(boolean setStartX) {
            // Send the ball in random direction
            Random randomNumber = new Random();

            if ( setStartX ) {
                int startX = randomNumber.nextInt(screenWidth - ballWidth) + 1;
                ballPosition.x = startX + ballWidth;
            }

            int ballDirection = randomNumber.nextInt(3);
            switch (ballDirection) {
                case 0:
                    ballIsMovingLeft = true;
                    ballIsMovingRight = false;
                    break;
                case 1:
                    ballIsMovingLeft = false;
                    ballIsMovingRight = true;
                    break;
                case 2:
                    ballIsMovingLeft = false;
                    ballIsMovingRight = false;
                    break;
            }
        }

        public void pause() {
            playingSquash = false;
            try {
                ourThread.join();
            } catch (InterruptedException e) {

            }
        }

        public void resume () {
            playingSquash = true;
            ourThread = new Thread(this);
            ourThread.start();
        }
    }
}
