package com.framework.impl;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.TextView;

import com.framework.Audio;
import com.framework.FileIO;
import com.framework.Game;
import com.framework.Graphics;
import com.framework.Input;
import com.framework.Screen;

public abstract class AndroidGame extends Activity implements Game {
    AndroidFastRenderView renderView;
    Graphics graphics;
    Audio audio;
    Input input;
    FileIO fileIO;
    Screen screen;
    WakeLock wakeLock;
    TMXParse tmxParse;
    GestureDetector myG;
    TextView gestureEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        //int frameBufferWidth = isLandscape ? 480 : 320;
        //int frameBufferHeight = isLandscape ? 320 : 480;
        int frameBufferWidth = isLandscape ? 1920 : 1080;
        int frameBufferHeight = isLandscape ? 1080 : 1920;
        Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth,
                frameBufferHeight, Config.RGB_565);
        
        float scaleX = (float) frameBufferWidth
                / getWindowManager().getDefaultDisplay().getWidth();
        float scaleY = (float) frameBufferHeight
                / getWindowManager().getDefaultDisplay().getHeight();

        renderView = new AndroidFastRenderView(this, frameBuffer);
        graphics = new AndroidGraphics(getAssets(), frameBuffer);
        fileIO = new AndroidFileIO(this);
        audio = new AndroidAudio(this);
        input = new AndroidInput(this, renderView, scaleX, scaleY);
        screen = getStartScreen();
        
        tmxParse = new TMXParse (getAssets());

        //myG = new GestureDetector(this,this);

        setContentView(renderView);
        //gestureEvent = (TextView)findViewById(renderView.id.GestureEvent);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "GLGame");
    }

    @Override
    public void onResume() {
        super.onResume();
        wakeLock.acquire();
        screen.resume();
        renderView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        wakeLock.release();
        renderView.pause();
        screen.pause();

        if (isFinishing())
            screen.dispose();
    }

    public Input getInput() {
        return input;
    }

    public FileIO getFileIO() {
        return fileIO;
    }

    public Graphics getGraphics() {
        return graphics;
    }

    public Audio getAudio() {
        return audio;
    }

    public TMXParse getTMXParse() {
        return tmxParse;
    }

    public void setScreen(Screen screen) {
        if (screen == null)
            throw new IllegalArgumentException("Screen must not be null");

        this.screen.pause();
        this.screen.dispose();
        screen.resume();
        screen.update(0);
        this.screen = screen;
    }

    public Screen getCurrentScreen() {
        return screen;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return gestureDetector.onTouchEvent(event);
    }

    SimpleOnGestureListener simpleOnGestureListener
            = new SimpleOnGestureListener()
    {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            String swipe = "";
            float sensitivity = 50;

            // TODO Auto-generated method stub
            if((e1.getX() - e2.getX()) > sensitivity){
                swipe += "Swipe Left\n";
            }else if((e2.getX() - e1.getX()) > sensitivity){
                swipe += "Swipe Right\n";
            }else{
                swipe += "\n";
            }

            if((e1.getY() - e2.getY()) > sensitivity){
                swipe += "Swipe Up\n";
            }else if((e2.getY() - e1.getY()) > sensitivity){
                swipe += "Swipe Down\n";
            }else{
                swipe += "\n";
            }

            gestureEvent.setText(swipe);

            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            // TODO Auto-generated method stub
            gestureEvent.setText("onLongPress: \n" + e.toString());
            super.onLongPress(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // TODO Auto-generated method stub
            gestureEvent.setText("onSingleTapConfirmed: \n" + e.toString());
            return super.onSingleTapConfirmed(e);
        }
    };

    GestureDetector gestureDetector
            = new GestureDetector(simpleOnGestureListener);
}
