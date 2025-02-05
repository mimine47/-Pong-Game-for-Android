package com.qpp.pingpong;

import android.graphics.RectF;
import java.util.Random;

public class Ball {
    private RectF mRect;
    private float mXVelocity;
    private float mYVelocity;
    private float mBallWidth;
    private float mBallHeight;

    // Constructor to initialize ball properties based on screen size
    public Ball(int screenX, int screenY) {
        mBallWidth = screenX / 100; // Ball width is 1% of screen width
        mBallHeight = mBallWidth;   // Keeping the ball's width and height equal to make it a circle

        mYVelocity = screenY / 4;   // Initial vertical velocity
        mXVelocity = mYVelocity;    // Initial horizontal velocity (same as vertical)

        mRect = new RectF();        // Creating the ball's rectangle
    }

    // Returns the current RectF object of the ball
    public RectF getRect() {
        return mRect;
    }

    // Updates the position of the ball based on its velocity and frame rate
    public void update(long fps) {
        mRect.left = mRect.left + (mXVelocity / fps);   // Update left position
        mRect.top = mRect.top + (mYVelocity / fps);     // Update top position
        mRect.right = mRect.left + mBallWidth;          // Update right position based on left position
        mRect.bottom = mRect.top + mBallHeight;         // Update bottom position based on top position
    }

    // Reverse the vertical velocity (bounce off top/bottom walls)
    public void reverseYVelocity() {
        mYVelocity = -mYVelocity;
    }

    // Reverse the horizontal velocity (bounce off left/right walls)
    public void reverseXVelocity() {
        mXVelocity = -mXVelocity;
    }

    // Set random X velocity to either reverse it or keep it the same
    public void setRandomXVelocity() {
        Random generator = new Random();
        int answer = generator.nextInt(2);   // Random number (0 or 1)

        if (answer == 0) {
            reverseXVelocity();  // Reverse the X velocity with 50% chance
        }
    }

    // Increase the velocity of the ball by 10%
    public void increaseVelocity() {
        mXVelocity = mXVelocity + mXVelocity / 10;
        mYVelocity = mYVelocity + mYVelocity / 10;
    }

    // Clears the Y position when ball hits an obstacle
    public void clearObstacleY(float y) {
        mRect.bottom = y;
        mRect.top = y - mBallHeight;
    }

    // Clears the X position when ball hits an obstacle
    public void clearObstacleX(float x) {
        mRect.left = x;
        mRect.right = x + mBallWidth;
    }

    // Resets the ball's position to the center of the screen (with offset for Y)
    public void reset(int x, int y) {
        mRect.left = x / 2 - mBallWidth / 2;   // Position ball in the center horizontally
        mRect.top = y - 20;                    // Position ball a little above the bottom of the screen
        mRect.right = mRect.left + mBallWidth;
        mRect.bottom = mRect.top + mBallHeight;
    }
}
