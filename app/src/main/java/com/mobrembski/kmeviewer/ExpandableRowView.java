package com.mobrembski.kmeviewer;

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
import android.widget.TextView;


public class ExpandableRowView extends LinearLayout {
    protected final ImageView arrowImg;
    protected final LinearLayout hiddenLayout;
    protected final TextView DescriptionValue;
    protected final TextView DescriptionAdditionalValue;
    protected final TextView DescriptionLabel;
    private final Animation slideDown;
    private final Animation slideUp;
    protected boolean isAnimating = false;

    public ExpandableRowView(Context context, AttributeSet set) {
        super(context, set);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.settings_row_layout, this);
        DescriptionLabel = (TextView) findViewById(R.id.GraphRowDescription);
        DescriptionValue = (TextView) findViewById(R.id.GraphRowValue);
        DescriptionAdditionalValue = (TextView) findViewById(R.id.GraphRowAdditionalValue);
        TypedArray attributesArray = context.obtainStyledAttributes(set, R.styleable.GraphRow);
        final int N = attributesArray.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = attributesArray.getIndex(i);
            switch (attr) {
                case R.styleable.GraphRow_DescriptionText:
                    DescriptionLabel.setText(attributesArray.getString(attr));
                    break;
                case R.styleable.GraphRow_DescriptionColor:
                    DescriptionLabel.setTextColor(attributesArray.getColor(attr, Color.BLACK));
                    break;
                case R.styleable.GraphRow_DescriptionTextSize:
                    DescriptionLabel.setTextSize(attributesArray.getDimension(attr, 15));
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
        arrowImg.setVisibility(VISIBLE);
        //region Listeners
        mainLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hiddenLayout.getVisibility() != View.VISIBLE) {
                    hiddenLayout.setVisibility(View.VISIBLE);
                    hiddenLayout.startAnimation(slideDown);
                } else
                    hiddenLayout.startAnimation(slideUp);
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
    }

    public ViewGroup getInjectHiddenView() {
        return (ViewGroup) findViewById(R.id.injectHiddenLayout);
    }

    public ViewGroup getInjectVisibleView() {
        return (ViewGroup) findViewById(R.id.injectVisibleLayout);
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
        return hiddenLayout.getVisibility() == VISIBLE;
    }

}
