package com.mobrembski.kmeviewer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class RPMView extends View {
    final Paint border_paint = new Paint();
    final Paint rect_paint = new Paint();
    final Paint marker_paint = new Paint();
    int borderWidth;
    int rectPadding;
    int rectColor;
    int borderColor;
    int markerLowColor;
    int markerHighColor;
    int markersPosTab[];
    int markersTextPadding;
    int markerTextSize;
    int markerTextWidth;
    int markerTop;
    int markerBottom;
    int viewWidth;
    int viewHeight;
    int rpmRectWidth;
    int oldValue = 0;
    int drawRectStartLeftMargin;
    int drawRectStartTopMargin;
    int drawRectEndMargin;
    float rpmsPerPixel;

    public RPMView(Context ctx, AttributeSet set) {
        super(ctx, set);
        TypedArray attributesArray = ctx.obtainStyledAttributes(set, R.styleable.RPMView);
        final int N = attributesArray.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = attributesArray.getIndex(i);
            switch (attr) {
                case R.styleable.RPMView_RPMViewBorderWidth:
                    borderWidth = attributesArray.getDimensionPixelSize(attr,
                            ctx.getResources().getDimensionPixelSize(R.dimen.RPMView_border_width));
                    break;
                case R.styleable.RPMView_RPMViewRectPadding:
                    rectPadding = attributesArray.getDimensionPixelSize(attr,
                            ctx.getResources().getDimensionPixelSize(R.dimen.RPMView_rect_padding));
                    break;
                case R.styleable.RPMView_RPMViewMarkerTextPadding:
                    markersTextPadding = attributesArray.getDimensionPixelSize(attr,
                            ctx.getResources().getDimensionPixelSize(
                                    R.dimen.RPMView_markers_text_top_padding));
                    break;
                case R.styleable.RPMView_RPMViewMarkerTextSize:
                    markerTextSize = attributesArray.getDimensionPixelSize(attr,
                            ctx.getResources().getDimensionPixelSize(
                                    R.dimen.RPMView_markers_text_size));
                    break;
            }
        }
        rectColor = ctx.getResources().getColor(R.color.RPMRect);
        borderColor = ctx.getResources().getColor(R.color.RPMBorder);
        markerLowColor = ctx.getResources().getColor(R.color.RPMMarkerLow);
        markerHighColor = ctx.getResources().getColor(R.color.RPMMarkerHigh);
        rect_paint.setStyle(Paint.Style.FILL);
        rect_paint.setColor(rectColor);
        border_paint.setStyle(Paint.Style.STROKE);
        border_paint.setColor(borderColor);
        border_paint.setStrokeWidth(borderWidth);
        marker_paint.setTextSize(markerTextSize);
    }

    public void setRpmValue(int value) {
        // For performance, we don't invalidate view when value
        // wasn't changed.
        if (oldValue != value) {
            rpmRectWidth = (int) Math.floor(value * rpmsPerPixel);
            oldValue = value;
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        rpmsPerPixel = (float) (viewWidth - rectPadding - borderWidth) / (float) 8000;
        drawRectStartLeftMargin = borderWidth + rectPadding;
        drawRectStartTopMargin = borderWidth + rectPadding + markersTextPadding;
        drawRectEndMargin = viewHeight - borderWidth - rectPadding;
        markerTop = borderWidth + markersTextPadding;
        markerBottom = viewHeight - borderWidth;
        markersPosTab = new int[8];
        // We're measuring only one segment of text. Rest is same.
        markerTextWidth = (int) marker_paint.measureText("1k") / 2;
        for (int i = 1; i < 9; i++) {
            markersPosTab[i - 1] = (int) (rpmsPerPixel * 1000 * i);
        }

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.drawRect(0, markersTextPadding, viewWidth, viewHeight, border_paint);
        canvas.drawRect(drawRectStartLeftMargin, //Start X
                drawRectStartTopMargin, // Start Y, to be looking nice, we're omitting border
                rpmRectWidth, // End X
                drawRectEndMargin, // End Y
                rect_paint);
        int i;
        marker_paint.setColor(markerLowColor);
        for (i = 0; i < 6; i++) {
            canvas.drawText(String.valueOf(i + 1) + "k",
                    markersPosTab[i] - markerTextWidth,
                    markerTextSize,
                    marker_paint);
            canvas.drawRect(markersPosTab[i],
                    markerTop, markersPosTab[i] + 3,
                    markerBottom,
                    marker_paint);
        }
        marker_paint.setColor(markerHighColor);
        for (i = 6; i < 8; i++) {
            canvas.drawText(String.valueOf(i + 1) + "k",
                    markersPosTab[i] - markerTextWidth,
                    markerTextSize,
                    marker_paint);
            canvas.drawRect(markersPosTab[i],
                    markerTop,
                    markersPosTab[i] + 3,
                    markerBottom,
                    marker_paint);
        }
    }
}