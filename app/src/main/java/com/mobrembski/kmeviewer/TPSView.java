package com.mobrembski.kmeviewer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class TPSView extends View {
    final Paint line_paint = new Paint();
    final int colorTab[] = new int[4];
    final int borderWidth;
    private int viewWidth;
    private int viewHeight;
    private int rectWidth;
    private int rectFilled = -1;

    public TPSView(Context ctx, AttributeSet attr) {
        super(ctx,attr);
        borderWidth = (int)ctx.getResources().getDimension(R.dimen.TPSView_border_width);
        colorTab[0] = ctx.getResources().getColor(R.color.TPSRect1);
        colorTab[1] = ctx.getResources().getColor(R.color.TPSRect2);
        colorTab[2] = ctx.getResources().getColor(R.color.TPSRect3);
        colorTab[3] = ctx.getResources().getColor(R.color.TPSRect4);
        line_paint.setStrokeWidth(borderWidth);
        line_paint.setStyle(Paint.Style.STROKE);
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
            if(rectFilled == i)
                line_paint.setStyle(Paint.Style.FILL_AND_STROKE);
            else
                line_paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(startWidth,0,endWidth,viewHeight,line_paint);
            endWidth+=rectWidth;
            startWidth+=rectWidth;
            if(i==0)
                startWidth+=borderWidth;
        }


    }
}