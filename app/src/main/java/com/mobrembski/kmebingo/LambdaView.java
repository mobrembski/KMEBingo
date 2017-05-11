package com.mobrembski.kmebingo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class LambdaView extends View {
    private final Paint border_paint = new Paint();
    private final Paint line_paint = new Paint();
    private int borderWidth;
    private int rectPadding;
    private int rectSize;
    private int rectColor;
    private int viewWidth;
    private int viewHeight;
    private int rectWidth;
    private int startPoint = 10;
    private float oldValue = 0;
    private float maxValue = 1.0f;
    private int maxPoints;

    public LambdaView(Context ctx, AttributeSet set) {
        super(ctx, set);
        TypedArray attributesArray = ctx.obtainStyledAttributes(set, R.styleable.LambdaView);
        final int N = attributesArray.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = attributesArray.getIndex(i);
            switch (attr) {
                case R.styleable.LambdaView_LambdaViewBorderWidth:
                    borderWidth = attributesArray.getDimensionPixelSize(attr,
                            ctx.getResources().getDimensionPixelSize(R.dimen.LambdaView_border_width));
                    break;
                case R.styleable.LambdaView_LambdaViewRectPadding:
                    rectPadding = attributesArray.getDimensionPixelSize(attr,
                            ctx.getResources().getDimensionPixelSize(R.dimen.LambdaView_rect_padding));
                    break;
                case R.styleable.LambdaView_LambdaViewRectSize:
                    rectSize = attributesArray.getDimensionPixelSize(attr,
                            ctx.getResources().getDimensionPixelSize(R.dimen.LambdaView_rect_size));
                    break;
            }
        }
        attributesArray.recycle();
        rectColor = ctx.getResources().getColor(R.color.LambdaRectBorder);
        line_paint.setStrokeWidth(borderWidth);
        line_paint.setStyle(Paint.Style.STROKE);
        line_paint.setColor(rectColor);
        border_paint.setStyle(Paint.Style.FILL_AND_STROKE);
        border_paint.setColor(rectColor);
    }

    public void setLambdaMax(float max) {
        maxValue = max;
    }

    public void setLambdaValue(float value, int color) {
        // For performance, we don't invalidate view when value
        // wasn't changed.
        if (oldValue != value || rectColor != color) {
            rectColor = color;
            float percent = (value / maxValue);
            startPoint = (int) ((float) maxPoints * percent) + rectPadding;
            oldValue = value;
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(heightMeasureSpec == 0){
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(70, MeasureSpec.AT_MOST);
        }

        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), getDefaultSize(this.getSuggestedMinimumHeight(), heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        rectWidth = viewWidth / rectSize;
        maxPoints = viewWidth - rectWidth - borderWidth - rectPadding;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.drawRect(0, 0, viewWidth, viewHeight, line_paint);
        border_paint.setColor(rectColor);
        canvas.drawRect(startPoint + rectPadding, //Start X
                borderWidth, // Start Y, to be looking nice, we're omitting border
                startPoint + rectWidth, // End X
                viewHeight - borderWidth, // End Y
                border_paint);

    }
}