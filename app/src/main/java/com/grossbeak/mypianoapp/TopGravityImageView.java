package com.grossbeak.mypianoapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class TopGravityImageView extends AppCompatImageView {
    public TopGravityImageView(Context context) {
        super(context);
    }
    public TopGravityImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public TopGravityImageView(Context context, AttributeSet attrs, int defStyle) {
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

        // Масштабируем по ширине, но не больше высоты
        float scale = Math.min((float) vWidth / dWidth, (float) vHeight / dHeight);
        int width = (int) (dWidth * scale);
        int height = (int) (dHeight * scale);

        int left = getPaddingLeft() + (vWidth - width) / 2;
        int top = getPaddingTop(); // Прижимаем к верху

        canvas.save();
        canvas.translate(left, top);
        canvas.scale(scale, scale);
        drawable.setBounds(0, 0, dWidth, dHeight);
        drawable.draw(canvas);
        canvas.restore();
    }

    // Проверка, находится ли точка внутри drawable (SVG) внутри view
    public boolean isPointInsideDrawable(float x, float y) {
        Drawable drawable = getDrawable();
        if (drawable == null) return false;
        int dWidth = drawable.getIntrinsicWidth();
        int dHeight = drawable.getIntrinsicHeight();
        int vWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int vHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        float scale = Math.min((float) vWidth / dWidth, (float) vHeight / dHeight);
        int width = (int) (dWidth * scale);
        int height = (int) (dHeight * scale);
        int left = getPaddingLeft() + (vWidth - width) / 2;
        int top = getPaddingTop();
        // Проверяем, попадает ли точка (x, y) внутрь прямоугольника drawable
        return x >= left && x <= (left + width) && y >= top && y <= (top + height);
    }
} 