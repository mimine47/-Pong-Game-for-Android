package com.qpp.pingpong;
import android.graphics.RectF;

public class Bat {
    private RectF mRect;

    // How long and high our mBat will be
    private float mLength;
    private float mHeight;

    // X is the far left of the rectangle which forms our mBat
    private float mXCoord;

    // Y is the top coordinate
    private float mYCoord;

    // This will hold the pixels per second speed that the bat will move
    private float mBatSpeed;

    // Movement directions
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    private int mBatMoving = STOPPED;

    // Screen dimensions
    private int mScreenX;
    private int mScreenY;

    public Bat(int x, int y){
        mScreenX = x;
        mScreenY = y;

        // Bat dimensions (relative to screen size)
        mLength = mScreenX / 8;
        mHeight = mScreenY / 25;

        // Start bat in roughly the screen center
        mXCoord = mScreenX / 2;
        mYCoord = mScreenY - 20;

        // Create a rectangle for the bat
        mRect = new RectF(mXCoord, mYCoord, mXCoord + mLength, mYCoord + mHeight);

        // Speed: Cover entire screen width in 1 second
        mBatSpeed =500;
    }

    public RectF getRect() {
        return mRect;
    }

    public void setMovementState(int state){
        mBatMoving = state;
    }

    public void update(long fps) {
        // Move the bat left
        if (mBatMoving == LEFT) {
            mXCoord -= mBatSpeed / fps; // Move to the left
        }

        // Move the bat right
        if (mBatMoving == RIGHT) {
            mXCoord += mBatSpeed / fps; // Move to the right
        }

        // Boundaries check for left and right
        if (mXCoord < 0) {
            mXCoord = 0;  // Prevent moving past the left boundary
        }
        if (mXCoord + mLength > mScreenX) {
            mXCoord = mScreenX - mLength;  // Prevent moving past the right boundary
        }

        // Update the mRect position to match the new mXCoord
        mRect.left = mXCoord;
        mRect.right = mXCoord + mLength;
    }
}
