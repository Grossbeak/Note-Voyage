package com.grossbeak.mypianoapp;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class LeftGravityImageView extends AppCompatImageView {
    private int leftSvgPaddingPx = 0;

    public LeftGravityImageView(Context context) {
        super(context);
        init(context, null);
    }
    public LeftGravityImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    public LeftGravityImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LeftGravityImageView);
            leftSvgPaddingPx = a.getDimensionPixelSize(R.styleable.LeftGravityImageView_leftSvgPadding, 0);
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) return;

        int dWidth = drawable.getIntrinsicWidth();
        int dHeight = drawable.getIntrinsicHeight();
        int vWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int vHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        // Масштабируем по высоте, чтобы SVG всегда был прижат к левому краю
        float scale = (float) vHeight / dHeight;
        int width = (int) (dWidth * scale);
        int height = vHeight;

        int left = getPaddingLeft() + leftSvgPaddingPx; // Прижимаем к левому краю с кастомным отступом
        int top = getPaddingTop();

        canvas.save();
        canvas.translate(left, top);
        canvas.scale(scale, scale);
        drawable.setBounds(0, 0, dWidth, dHeight);
        drawable.draw(canvas);
        canvas.restore();
    }
} 