package com.framework.impl;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

import com.framework.Graphics;
import com.framework.Pixmap;
import com.totalannihilationroadrage.RotationTransformation;

public class AndroidGraphics implements Graphics {
    AssetManager assets;
    Bitmap frameBuffer;
    Canvas canvas;
    Paint paint;
    Rect srcRect = new Rect();
    Rect dstRect = new Rect();

    Matrix matrix;

    public AndroidGraphics(AssetManager assets, Bitmap frameBuffer) {
        this.assets = assets;
        this.frameBuffer = frameBuffer;
        this.canvas = new Canvas(frameBuffer);
        this.paint = new Paint();
        this.matrix = new Matrix();
    }

    public Pixmap newPixmap(String fileName, PixmapFormat format) {
        Config config = null;
        if (format == PixmapFormat.RGB565)
            config = Config.RGB_565;
        else if (format == PixmapFormat.ARGB4444)
            config = Config.ARGB_4444;
        else
            config = Config.ARGB_8888;

        Options options = new Options();
        options.inPreferredConfig = config;
        options.inPurgeable = true;
        options.inInputShareable = true;

        InputStream in = null;
        Bitmap bitmap = null;
        try {
            in = assets.open(fileName);
            bitmap = BitmapFactory.decodeStream(in);
            if (bitmap == null)
                throw new RuntimeException("Couldn't load bitmap from asset '"
                        + fileName + "'");
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load bitmap from asset '"
                    + fileName + "'");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        if (bitmap.getConfig() == Config.RGB_565)
            format = PixmapFormat.RGB565;
        else if (bitmap.getConfig() == Config.ARGB_4444)
            format = PixmapFormat.ARGB4444;
        else
            format = PixmapFormat.ARGB8888;

        return new AndroidPixmap(bitmap, format);
    }

    public void clear(int color) {
        canvas.drawRGB((color & 0xff0000) >> 16, (color & 0xff00) >> 8,
                (color & 0xff));
    }

    public void drawPixel(int x, int y, int color) {
        paint.setColor(color);
        canvas.drawPoint(x, y, paint);
    }

    public void drawLine(int x, int y, int x2, int y2, int color) {
        paint.setColor(color);
        canvas.drawLine(x, y, x2, y2, paint);
    }

    public void drawRect(int x, int y, int width, int height, int color) {
        paint.setColor(color);
        paint.setStyle(Style.FILL);
        canvas.drawRect(x, y, x + width - 1, y + width - 1, paint);
    }

    public void drawText (String text, int x, int y, int color, int size, Paint.Align alignment)
    {
        paint.setTextAlign(alignment);
        paint.setColor(color);
        paint.setTextSize(size);
        paint.setStyle(Style.FILL);
        canvas.drawText(text, x, y, paint);
    }

    public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY,
            int srcWidth, int srcHeight) {
        srcRect.left = srcX;
        srcRect.top = srcY;
        srcRect.right = srcX + srcWidth;
        srcRect.bottom = srcY + srcHeight;

        dstRect.left = x;
        dstRect.top = y;
        dstRect.right = x + srcWidth;
        dstRect.bottom = y + srcHeight;

        /*int wS = srcRect.width();
        int hS = srcRect.height();
        int wD = dstRect.width();
        int hD = dstRect.height();*/

        canvas.drawBitmap(((AndroidPixmap) pixmap).bitmap, srcRect, dstRect, null);
    }

    public void drawPixmap(Pixmap pixmap, int x, int y) {
        canvas.drawBitmap(((AndroidPixmap)pixmap).bitmap, x, y, null);
    }

    public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight, RotationTransformation rotTrans)
    {
        srcRect.left = srcX;
        srcRect.top = srcY;
        srcRect.right = srcX + srcWidth;
        srcRect.bottom = srcY + srcHeight;

        dstRect.left = x;
        dstRect.top = y;
        dstRect.right = x + srcWidth;
        dstRect.bottom = y + srcHeight;

        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        rotateCanvas(x + (int)(srcWidth * 0.5), y + (int)(srcHeight * 0.5), rotTrans);
        canvas.drawBitmap(((AndroidPixmap) pixmap).bitmap, srcRect, dstRect, null);
        canvas.restore();
    }

    public void drawPixmap(Bitmap bitmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight, int centreX, int centreY, RotationTransformation rotTrans)
    {
        srcRect.left = srcX;
        srcRect.top = srcY;
        srcRect.right = srcX + srcWidth;
        srcRect.bottom = srcY + srcHeight;

        dstRect.left = x;
        dstRect.top = y;
        dstRect.right = x + srcWidth;
        dstRect.bottom = y + srcHeight;

        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        rotateCanvas(centreX, centreY, rotTrans);
        canvas.drawBitmap(bitmap, srcRect, dstRect, null);
        canvas.restore();
    }

    public void rotateCanvas (int centreX, int centreY, RotationTransformation rotTrans)
    {
        if (rotTrans.isFlippedHorizontally)
        {
            /*if (angle < -180)
            {
                //canvas.rotate(angle + 540, x + (int)(srcWidth * 0.5), y + (int)(srcHeight * 0.5));
                canvas.rotate(angle + 540, centreX, centreY);
            }
            else
            {*/
                //canvas.rotate(angle + 180, x + (int)(srcWidth * 0.5), y + (int)(srcHeight * 0.5));
                canvas.rotate(rotTrans.angle - 180, centreX, centreY);
            //}
            //canvas.scale(-1.0f, 1.0f, x + (int)(srcWidth * 0.5), y + (int)(srcHeight * 0.5));
            canvas.scale(-1.0f, 1.0f, centreX, centreY);
        }
        else
        {
            //canvas.rotate(angle, x + (int)(srcWidth * 0.5), y + (int)(srcWidth * 0.5));
            canvas.rotate(rotTrans.angle, centreX, centreY);
        }
    }

    public int getWidth() {
        return frameBuffer.getWidth();
    }

    public int getHeight() {
        return frameBuffer.getHeight();
    }

    public Canvas getCanvas() { return canvas; }
}

