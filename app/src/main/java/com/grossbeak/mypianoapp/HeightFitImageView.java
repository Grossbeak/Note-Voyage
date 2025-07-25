package com.grossbeak.mypianoapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

public class HeightFitImageView extends AppCompatImageView {
    public HeightFitImageView(Context context) {
        super(context);
    }
    public HeightFitImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public HeightFitImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) return;

        int dWidth = drawable.getIntrinsicWidth();
        int dHeight = drawable.getIntrinsicHeight();
        int vWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int vHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        // Масштабируем по высоте, с сохранением пропорций
        float scale = (float) vHeight / dHeight;
        int width = (int) (dWidth * scale);
        int height = vHeight;

        int left = getPaddingLeft() + (vWidth - width) / 2;
        int top = getPaddingTop();

        canvas.save();
        canvas.translate(left, top);
        canvas.scale(scale, scale);
        drawable.setBounds(0, 0, dWidth, dHeight);
        drawable.draw(canvas);
        canvas.restore();
    }
} 