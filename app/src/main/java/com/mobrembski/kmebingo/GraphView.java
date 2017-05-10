package com.mobrembski.kmebingo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class GraphView extends LinearLayout {
    private GraphicalView mChartView;
    private final RelativeLayout chartLayout;
    private XYSeries series;
    private double XMax = 0;
    private int chartPos = 0;
    private boolean isAnimating = false;
    private boolean graphEnabled = false;
    private String XTitle, YTitle;
    private XYMultipleSeriesRenderer mRenderer;

    public GraphView(Context context, AttributeSet set) {
        super(context, set);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.graph_row_layout, this);
        TypedArray attributesArray = context.obtainStyledAttributes(set, R.styleable.GraphRow);
        final int N = attributesArray.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = attributesArray.getIndex(i);
            switch (attr) {
                case R.styleable.GraphRow_XTitle:
                    XTitle = attributesArray.getString(attr);
                    break;
                case R.styleable.GraphRow_YTitle:
                    YTitle = attributesArray.getString(attr);
                    break;
            }
        }
        attributesArray.recycle();
        chartLayout = (RelativeLayout) findViewById(R.id.chartLayout);
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
        mRenderer = new XYMultipleSeriesRenderer();
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
        mRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        mRenderer.setXTitle(XTitle);
        mRenderer.setYTitle(YTitle);
        mRenderer.setLabelsColor(getResources().getColor(R.color.ChartLabelColors));
        mRenderer.setYLabelsColor(0, getResources().getColor(R.color.ChartLabelColors));
        mRenderer.setXLabelsColor(getResources().getColor(R.color.ChartLabelColors));
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
    }

    public void AddPoint(double val) {
        series.add(chartPos++, val);
        if (chartPos > XMax) {
            chartPos = 0;
            series.clear();
        }
        mChartView.repaint();
    }

    public void SetXTitle(String val) {
        mRenderer.setXTitle(val);
    }

    public void SetYTitle(String val) {
        mRenderer.setYTitle(val);
    }

}
