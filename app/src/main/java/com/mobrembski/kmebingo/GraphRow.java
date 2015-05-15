package com.mobrembski.kmebingo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class GraphRow extends LinearLayout {
    private final Animation slideDown;
    private final Animation slideUp;
    private GraphicalView mChartView;
    private final ImageView arrowImg;
    private final LinearLayout hiddenLayout;
    private final RelativeLayout chartLayout;
    private final TextView DescriptionValue;
    private final TextView DescriptionAdditionalValue;
    private XYSeries series;
    private double XMax = 0;
    private int chartPos = 0;
    private boolean isAnimating = false;
    private boolean graphEnabled = false;

    public GraphRow(Context context, AttributeSet set) {
        super(context, set);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.graph_row_layout, this);
        TextView descriptionLabel = (TextView) findViewById(R.id.GraphRowDescription);
        DescriptionValue = (TextView) findViewById(R.id.GraphRowValue);
        DescriptionAdditionalValue = (TextView) findViewById(R.id.GraphRowAdditionalValue);
        TypedArray attributesArray = context.obtainStyledAttributes(set, R.styleable.GraphRow);
        final int N = attributesArray.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = attributesArray.getIndex(i);
            switch (attr) {
                case R.styleable.GraphRow_DescriptionText:
                    descriptionLabel.setText(attributesArray.getString(attr));
                    break;
                case R.styleable.GraphRow_DescriptionColor:
                    descriptionLabel.setTextColor(attributesArray.getColor(attr, Color.BLACK));
                    break;
                case R.styleable.GraphRow_DescriptionTextSize:
                    descriptionLabel.setTextSize(attributesArray.getDimension(attr, 15));
                    break;
                case R.styleable.GraphRow_DescriptionValueTextSize:
                    DescriptionValue.setTextSize(attributesArray.getDimension(attr, 15));
                    break;
                case R.styleable.GraphRow_DescriptionAdditionalValueTextSize:
                    DescriptionAdditionalValue.setTextSize(attributesArray.getDimension(attr, 3));
                    break;
            }
        }
        attributesArray.recycle();
        slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        hiddenLayout = (LinearLayout) findViewById(R.id.HiddenLayout);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.RootLayout);
        arrowImg = (ImageView) findViewById(R.id.GraphRowArrowImage);
        arrowImg.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down_float));
        //region Listeners
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (graphEnabled) {
                    if (hiddenLayout.getVisibility() != View.VISIBLE) {
                        hiddenLayout.setVisibility(View.VISIBLE);
                        hiddenLayout.startAnimation(slideDown);
                    } else
                        hiddenLayout.startAnimation(slideUp);
                }
            }
        });
        slideUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                hiddenLayout.setVisibility(View.GONE);
                isAnimating = false;
                arrowImg.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down_float));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                hiddenLayout.setVisibility(View.VISIBLE);
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                hiddenLayout.setVisibility(View.VISIBLE);
                isAnimating = false;
                arrowImg.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up_float));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                hiddenLayout.setVisibility(View.VISIBLE);
            }
        });
        //endregion
        chartLayout = (RelativeLayout) findViewById(R.id.chartLayout);
    }

    public ViewGroup getInjectHiddenView() {
        return (ViewGroup) findViewById(R.id.injectHiddenLayout);
    }

    public ViewGroup getInjectVisibleView() {
        return (ViewGroup) findViewById(R.id.injectVisibleLayout);
    }

    public void CreateRenderer(double YMax, double XMax) {
        CreateRenderer(YMax, 0, XMax, 0);
    }

    public void CreateRenderer(double YMax, double YMin, double XMax, double XMin) {
        series = new XYSeries("");
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(2);
        renderer.setColor(Color.RED);
        renderer.setDisplayBoundingPoints(false);
        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);
        mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
        mRenderer.setPanEnabled(false, false);
        mRenderer.setYAxisMin(YMin);
        mRenderer.setShowGrid(true);
        mRenderer.setZoomEnabled(false, false);
        mRenderer.setClickEnabled(false);
        mRenderer.setPanEnabled(false, false);
        mRenderer.setXAxisMax(XMax);
        mRenderer.setXAxisMin(XMin);
        mRenderer.setYAxisMax(YMax);
        mRenderer.setShowLabels(true);
        mRenderer.setShowLegend(false);
        mRenderer.setBackgroundColor(Color.WHITE);
        mChartView = ChartFactory.getLineChartView(getContext(), dataset, mRenderer);
        mChartView.setClickable(false);
        mChartView.setBackgroundColor(Color.WHITE);
        chartLayout.addView(mChartView);
        this.XMax = XMax;
        graphEnabled = true;
        arrowImg.setVisibility(VISIBLE);
    }

    public void AddPoint(double val) {
        series.add(chartPos++, val);
        if (chartPos > XMax) {
            chartPos = 0;
            series.clear();
        }
        if (hiddenLayout.getVisibility() == View.VISIBLE && !isAnimating)
            mChartView.repaint();
    }

    public void SetValueText(String value) {
        DescriptionValue.setText(value);
    }

    public void SetValueColor(int color) {
        DescriptionValue.setTextColor(color);
    }

    public void SetAdditionalValueText(String value) {
        DescriptionAdditionalValue.setVisibility(View.VISIBLE);
        DescriptionAdditionalValue.setText(value);
    }

    public boolean GetHiddenVisibility() {
        return graphEnabled && (hiddenLayout.getVisibility() == VISIBLE);
    }

}
