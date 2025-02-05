package com.qpp.pingpong;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

class PongView extends SurfaceView implements Runnable{
    Thread mGameThread = null;
    SurfaceHolder mOurHolder;
    volatile boolean mPlaying;
    boolean mPaused = true;
    Canvas mCanvas;
    Paint mPaint;
    long mFPS;
    int mScreenX;
    int mScreenY;
    Bat mBat;
    Ball mBall;
    SoundPool sp;
    int explodeID = -1;
    int beep1ID = -1;
    int beep2ID = -1;
    int beep3ID = -1;
    int loseLifeID = -1;
    int mScore = 0;
    int mLives = 3;
public PongView (Context context, int x , int y){
    super ( context);
    mScreenX = x;
    mScreenY = y;
    mOurHolder = getHolder();
    mPaint = new Paint();
    mBat =  new Bat(mScreenX, mScreenY);
    mBall = new Ball(mScreenX, mScreenY);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        sp = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build();

    } else {
        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    }

    try{
        // Create objects of the 2 required classes
        AssetManager assetManager = context.getAssets();
        AssetFileDescriptor descriptor;

        // Load our fx in memory ready for use
        descriptor = assetManager.openFd("beep1.ogg");
        beep1ID = sp.load(descriptor, 0);

        descriptor = assetManager.openFd("beep2.ogg");
        beep2ID = sp.load(descriptor, 0);

        descriptor = assetManager.openFd("beep3.ogg");
        beep3ID = sp.load(descriptor, 0);

        descriptor = assetManager.openFd("loseLife.ogg");
        loseLifeID = sp.load(descriptor, 0);

        descriptor = assetManager.openFd("explode.ogg");
        explodeID = sp.load(descriptor, 0);

    }catch(IOException e){
        // Print an error message to the console
        Log.e("error", "failed to load sound files");
    }

    setupAndRestart();
 }
 public void setupAndRestart(){
     mBall.reset(mScreenX, mScreenY);
     if(mLives == 0) {
         mScore = 0;
         mLives = 3;
     }
 }
    @Override
    public void run() {
        while (mPlaying) {


            long startFrameTime = System.currentTimeMillis();


            if(!mPaused){
                update();
            }

            // Draw the frame
            draw();


            long timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                mFPS = 1000 / timeThisFrame;
            }

        }

    }
    public void update(){
        mBat.update(mFPS);

        mBall.update(mFPS);
        if(RectF.intersects(mBat.getRect(), mBall.getRect())) {
            mBall.setRandomXVelocity();
            mBall.reverseYVelocity();
            mBall.clearObstacleY(mBat.getRect().top - 2);

            mScore++;
            mBall.increaseVelocity();

            sp.play(beep1ID, 1, 1, 0, 0, 1);
        }
        if(mBall.getRect().bottom > mScreenY){
            mBall.reverseYVelocity();
            mBall.clearObstacleY(mScreenY - 2);


            mLives--;
            sp.play(loseLifeID, 1, 1, 0, 0, 1);

            if(mLives == 0){
                mPaused = true;
                setupAndRestart();
            }
        }

        if(mBall.getRect().top < 0){
            mBall.reverseYVelocity();
            mBall.clearObstacleY(12);

            sp.play(beep2ID, 1, 1, 0, 0, 1);
        }
        if(mBall.getRect().left < 0){
            mBall.reverseXVelocity();
            mBall.clearObstacleX(2);

            sp.play(beep3ID, 1, 1, 0, 0, 1);
        }
        if(mBall.getRect().right > mScreenX){
            mBall.reverseXVelocity();
            mBall.clearObstacleX(mScreenX - 22);

            sp.play(beep3ID, 1, 1, 0, 0, 1);
        }


    }
    public void draw (){
        if(mOurHolder.getSurface().isValid()){
            mCanvas = mOurHolder.lockCanvas();
            mCanvas.drawColor(Color.argb(255, 120, 197, 87));
            mPaint.setColor(Color.argb(255, 255, 255, 255));
            mCanvas.drawRect(mBat.getRect(), mPaint);
            mCanvas.drawRect(mBall.getRect(), mPaint);
            mPaint.setColor(Color.argb(255, 255, 255, 255));
            mPaint.setTextSize(40);
            mCanvas.drawText("Score: " + mScore + "   Lives: " + mLives, 10, 50, mPaint);
            mOurHolder.unlockCanvasAndPost(mCanvas);
        }
    }
    public void pause() {
        mPlaying = false;
        try {
            mGameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }
    public void resume() {
        mPlaying = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:

                mPaused = false; // Unpauses the game if it was paused

                // Is the touch on the right or left side of the screen?
                if (motionEvent.getX() > mScreenX / 2) {
                    // If it's on the right, move the Bat right
                    mBat.setMovementState(mBat.RIGHT);
                } else {
                    // If it's on the left, move the Bat left
                    mBat.setMovementState(mBat.LEFT);
                }

                break;

            // Player has removed their finger from the screen
            case MotionEvent.ACTION_UP:

                // Stop the Bat from moving
                mBat.setMovementState(mBat.STOPPED);
                break;
        }
        return true;
    }
}
