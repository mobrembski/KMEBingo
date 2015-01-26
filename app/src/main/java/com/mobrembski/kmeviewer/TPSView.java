package com.mobrembski.kmeviewer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class TPSView extends View {
    final Paint line_paint = new Paint();
    final int colorTab[] = new int[4];
    int borderWidth;
    int viewWidth;
    int viewHeight;
    int rectWidth;
    int rectFilled = -1;
    int rectPadding = 4;
    Paint fill_paint;

    public TPSView(Context ctx, AttributeSet set) {
        super(ctx,set);
        TypedArray attributesArray = ctx.obtainStyledAttributes(set, R.styleable.TPSView);
        final int N = attributesArray.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = attributesArray.getIndex(i);
            switch (attr) {
                case R.styleable.TPSView_TPSViewBorderWidth:
                    borderWidth = attributesArray.getDimensionPixelSize(attr,
                            ctx.getResources().getDimensionPixelSize(R.dimen.TPSView_border_width));
                    break;
                case R.styleable.TPSView_TPSViewRectPadding:
                    rectPadding = attributesArray.getDimensionPixelSize(attr,
                            ctx.getResources().getDimensionPixelSize(R.dimen.TPSView_rect_padding));
                    break;
            }
        }
        colorTab[0] = ctx.getResources().getColor(R.color.TPSRect1);
        colorTab[1] = ctx.getResources().getColor(R.color.TPSRect2);
        colorTab[2] = ctx.getResources().getColor(R.color.TPSRect3);
        colorTab[3] = ctx.getResources().getColor(R.color.TPSRect4);
        line_paint.setStrokeWidth(borderWidth);
        line_paint.setStyle(Paint.Style.STROKE);
        fill_paint = new Paint(line_paint);
        fill_paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void setRectFilled(int rectNum) {
        // For performance, we don't invalidate view when filled rect
        // wasn't changed.
        int tmp = rectNum - 1;
        if (this.rectFilled != tmp) {
            this.rectFilled = tmp;
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        rectWidth = viewWidth/4;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int startWidth = 0;
        int endWidth = rectWidth;
        for(int i=0;i<4;i++)
        {
            line_paint.setColor(colorTab[i]);
            line_paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(startWidth, 0, endWidth, viewHeight, line_paint);
            if(rectFilled == i) {
                fill_paint.setColor(colorTab[i]);
                canvas.drawRect(startWidth + rectPadding + borderWidth,
                        rectPadding + borderWidth,
                        endWidth - rectPadding - borderWidth,
                        viewHeight - rectPadding - borderWidth,
                        fill_paint);
            }
            endWidth+=rectWidth;
            startWidth+=rectWidth;
            if(i==0)
                startWidth+=borderWidth;
        }


    }
}