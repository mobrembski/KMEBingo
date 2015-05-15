package com.mobrembski.kmebingo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.mobrembski.kmebingo.SerialFrames.KMEDataConfig;

public class ActuatorView extends View {
    private final Paint border_paint = new Paint();
    private final Paint idle_rect_paint = new Paint();
    private final Paint actual_rect_paint = new Paint();
    private final Paint load_rect_paint = new Paint();
    private final Paint PWA_marker_paint = new Paint();
    private int viewWidth;
    private int viewHeight;
    private int borderWidth;
    private int stepsBarHeight;
    private int configBarHeight;
    private int rectPadding;
    private int barBottomPadding;
    private float stepsPerPixel;
    private int PWAval;
    private int barVerticalMargin;
    private int barHorizontalMargin;
    private int actuatorSteps;
    private KMEDataConfig actualConfig = null;

    public ActuatorView(Context ctx, AttributeSet set) {
        super(ctx, set);
        TypedArray attributesArray = ctx.obtainStyledAttributes(set, R.styleable.ActuatorView);
        final int N = attributesArray.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = attributesArray.getIndex(i);
            switch (attr) {
                case R.styleable.ActuatorView_ActuatorViewBorderWidth:
                    borderWidth = attributesArray.getDimensionPixelSize(attr,
                            ctx.getResources().getDimensionPixelSize(
                                    R.dimen.ActuatorView_border_width));
                    break;
                case R.styleable.ActuatorView_ActuatorViewRectPadding:
                    rectPadding = attributesArray.getDimensionPixelSize(attr,
                            ctx.getResources().getDimensionPixelSize(
                                    R.dimen.ActuatorView_rect_padding));
                    break;
               case R.styleable.ActuatorView_ActuatorViewBarBottomPadding:
                    barBottomPadding = attributesArray.getDimensionPixelSize(attr,
                            ctx.getResources().getDimensionPixelSize(
                                    R.dimen.ActuatorView_bar_bottom_padding));
                    break;
            }
        }
        attributesArray.recycle();
        int borderColor = ctx.getResources().getColor(R.color.ActuatorRowBorder);
        int idleRectColor = ctx.getResources().getColor(R.color.ActuatorRowIdleRect);
        int loadRectColor = ctx.getResources().getColor(R.color.ActuatorRowLoadRect);
        int actualRectColor = ctx.getResources().getColor(R.color.ActuatorRowActual);
        int markerColor = ctx.getResources().getColor(R.color.ActuatorRowMarker);
        idle_rect_paint.setStyle(Paint.Style.FILL);
        idle_rect_paint.setColor(idleRectColor);
        load_rect_paint.setStyle(Paint.Style.FILL);
        load_rect_paint.setColor(loadRectColor);
        actual_rect_paint.setStyle(Paint.Style.FILL);
        actual_rect_paint.setColor(actualRectColor);
        border_paint.setStyle(Paint.Style.STROKE);
        border_paint.setColor(borderColor);
        border_paint.setStrokeWidth(borderWidth);
        PWA_marker_paint.setStyle(Paint.Style.STROKE);
        PWA_marker_paint.setStrokeWidth(borderWidth);
        PWA_marker_paint.setColor(markerColor);
        PWAval = 100;
        actuatorSteps = 155;
        actualConfig = new KMEDataConfig(new int[]{0x65,0x32, 0x32, 0xC8, 0x1E, 0x10, 0x27, 0x07, 0x10, 0x27, 0x24, 0xC4, 0x09, 0x37, 0x2D});
    }

    public void setDataConfigFrame(KMEDataConfig config) {
        if (config != actualConfig) {
            actualConfig = config;
            invalidate();
        }
    }

    public void setPWAValue(int PWAval) {
        if (PWAval != this.PWAval) {
            this.PWAval = PWAval;
            invalidate();
        }
    }

    public void setActuatorSteps(int steps) {
        if (actuatorSteps != steps) {
            actuatorSteps = steps;
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        stepsPerPixel = (viewWidth - 2 * rectPadding - 2 * borderWidth) / 256;
        int tmp = (viewWidth - 2 * rectPadding - 2 * borderWidth) % 256;
        barVerticalMargin = borderWidth + rectPadding;
        barHorizontalMargin = barVerticalMargin + tmp/2;
        stepsBarHeight = (int)((float)(viewHeight - 2 * barVerticalMargin) * 0.6f);
        configBarHeight = (viewHeight - stepsBarHeight - 2 * barBottomPadding) / 2;

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (actualConfig == null)
            return;
        super.dispatchDraw(canvas);
        float steps = actuatorSteps * stepsPerPixel;
        int idlemin = ((int) stepsPerPixel * PWAval) - ((int) stepsPerPixel * actualConfig.ActuatorMinOpenOnIdle.GetValue());
        int idlemax = ((int) stepsPerPixel * PWAval) + ((int) stepsPerPixel * actualConfig.ActuatorMaxOpenOnIdle.GetValue());
        int loadmin = ((int) stepsPerPixel * PWAval) - ((int) stepsPerPixel * actualConfig.ActuatorMinOpenOnLoad.GetValue());
        int loadmax = ((int) stepsPerPixel * PWAval) + ((int) stepsPerPixel * actualConfig.ActuatorMaxOpenOnLoad.GetValue());
        canvas.drawRect(0, 0 , viewWidth, viewHeight, border_paint);
        canvas.drawRect(
                barHorizontalMargin,
                barVerticalMargin,
                steps + barHorizontalMargin,
                stepsBarHeight + barVerticalMargin,
                actual_rect_paint);
        canvas.drawRect(
                idlemin + barHorizontalMargin,
                stepsBarHeight + barVerticalMargin + barBottomPadding,
                idlemax + barHorizontalMargin,
                configBarHeight + stepsBarHeight + barVerticalMargin,
                idle_rect_paint);
        canvas.drawRect(
                loadmin + barHorizontalMargin,
                configBarHeight + stepsBarHeight + barVerticalMargin,
                loadmax + barHorizontalMargin,
                viewHeight - rectPadding - borderWidth,
                load_rect_paint);
        canvas.drawRect(
                PWAval * stepsPerPixel + barHorizontalMargin,
                stepsBarHeight + barVerticalMargin + barBottomPadding,
                PWAval * stepsPerPixel + barHorizontalMargin + 1,
                viewHeight - rectPadding - borderWidth,
                PWA_marker_paint);
    }
}