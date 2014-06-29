package com.framework;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.totalannihilationroadrage.RotationTransformation;

public interface Graphics {
    public static enum PixmapFormat {
        ARGB8888, ARGB4444, RGB565
    }

    public Pixmap newPixmap(String fileName, PixmapFormat format);

    public void clear(int color);

    public void drawPixel(int x, int y, int color);

    public void drawLine(int x, int y, int x2, int y2, int color);

    public void drawRect(int x, int y, int width, int height, int color);

    public void drawText (String text, int x, int y, int color, int size, Paint.Align alignment);

    public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY,
            int srcWidth, int srcHeight);

    public void drawPixmap(Pixmap pixmap, int x, int y);

    public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY,
                           int srcWidth, int srcHeight, RotationTransformation rotTrans);

    public void drawPixmap(Bitmap bitmap, int x, int y, int srcX, int srcY,
                           int srcWidth, int srcHeight, int centreX, int centreY, RotationTransformation rotTrans);

    public void rotateCanvas (int centreX, int centreY, RotationTransformation rotTrans);

    public int getWidth();

    public int getHeight();

    public Canvas getCanvas();
}
