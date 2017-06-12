package com.ebanx.swipebtn;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by leandroferreira on 07/03/17.
 *
 */

public class SwipeButton extends RelativeLayout {

    private ImageView swipeButton;
    private float initialX;
    private boolean active;
    private TextView centerText;
    private ViewGroup background;

    private Drawable disabledDrawable;
    private Drawable enabledDrawable;

    private OnStateChangeListener onStateChangeListener;

    private static final int ENABLED = 0;
    private static final int DISABLED = 1;

    public SwipeButton(Context context) {
        super(context);

        init(context, null, -1, -1);
    }

    public SwipeButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs, -1, -1);
    }

    public SwipeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr, -1);
    }

    @TargetApi(21)
    public SwipeButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs, defStyleAttr, defStyleRes);
    }
    
    public boolean isActive() {
        return active;
    }

    public void setText(String text) {
        centerText.setText(text);
    }

    public void setBackground(Drawable drawable) {
        background.setBackground(drawable);
    }

    public void setSlidingButtonBackground(Drawable drawable) {
        background.setBackground(drawable);
    }

    public void setDisabledDrawable(Drawable drawable) {
        disabledDrawable = drawable;

        if (!active) {
            swipeButton.setImageDrawable(drawable);
        }
    }

    public void setEnabledDrawable(Drawable drawable) {
        enabledDrawable = drawable;

        if (active) {
            swipeButton.setImageDrawable(drawable);
        }
    }

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
    }

    public void setInnerTextPadding(int left, int top, int right, int bottom) {
        centerText.setPadding(left, top, right, bottom);
    }

    public void setSwipeButtonPadding(int left, int top, int right, int bottom) {
        swipeButton.setPadding(left, top, right, bottom);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        background = new RelativeLayout(context);

        LayoutParams layoutParamsView = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParamsView.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        addView(background, layoutParamsView);

        final TextView centerText = new TextView(context);
        this.centerText = centerText;
        centerText.setGravity(Gravity.CENTER);

        LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        background.addView(centerText, layoutParams);

        final ImageView swipeButton = new ImageView(context);
        this.swipeButton = swipeButton;

        if (attrs != null && defStyleAttr == -1 && defStyleRes == -1) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwipeButton,
                    defStyleAttr, defStyleRes);

            Drawable drawable = typedArray.getDrawable(R.styleable.SwipeButton_inner_text_background);

            if (drawable != null) {
                background.setBackground(drawable);
            } else {
                background.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_rounded));
            }

            centerText.setText(typedArray.getText(R.styleable.SwipeButton_inner_text));
            centerText.setTextColor(typedArray.getColor(R.styleable.SwipeButton_inner_text_color,
                    Color.WHITE));

            float textSize = DimentionUtils.converPixelsToSp(
                    typedArray.getDimension(R.styleable.SwipeButton_inner_text_size, 0), context);

            if (textSize != 0) {
                centerText.setTextSize(textSize);
            } else {
                centerText.setTextSize(12);
            }

            disabledDrawable = typedArray.getDrawable(R.styleable.SwipeButton_button_image_disabled);
            enabledDrawable = typedArray.getDrawable(R.styleable.SwipeButton_button_image_enabled);
            float innerTextLeftPadding = typedArray.getDimension(
                    R.styleable.SwipeButton_inner_text_left_padding, 0);
            float innerTextTopPadding = typedArray.getDimension(
                    R.styleable.SwipeButton_inner_text_top_padding, 0);
            float innerTextRightPadding = typedArray.getDimension(
                    R.styleable.SwipeButton_inner_text_right_padding, 0);
            float innerTextBottomPadding = typedArray.getDimension(
                    R.styleable.SwipeButton_inner_text_bottom_padding, 0);

            int initialState = typedArray.getInt(R.styleable.SwipeButton_initial_state, DISABLED);

            if (initialState == ENABLED) {
                LayoutParams layoutParamsButton = new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                layoutParamsButton.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                layoutParamsButton.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

                addView(swipeButton, layoutParamsButton);

                active = true;
            } else {
                LayoutParams layoutParamsButton = new LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                layoutParamsButton.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                layoutParamsButton.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

                addView(swipeButton, layoutParamsButton);

                active = false;
            }

            centerText.setPadding((int) innerTextLeftPadding,
                    (int) innerTextTopPadding,
                    (int) innerTextRightPadding,
                    (int) innerTextBottomPadding);

            Drawable buttonBackground =
                    typedArray.getDrawable(R.styleable.SwipeButton_button_background);

            if (buttonBackground != null) {
                swipeButton.setBackground(buttonBackground);
            } else {
                swipeButton.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button));
            }

            swipeButton.setImageDrawable(disabledDrawable);

            float buttonLeftPadding = typedArray.getDimension(
                    R.styleable.SwipeButton_button_left_padding, 0);
            float buttonTopPadding = typedArray.getDimension(
                    R.styleable.SwipeButton_button_top_padding, 0);
            float buttonRightPadding = typedArray.getDimension(
                    R.styleable.SwipeButton_button_right_padding, 0);
            float buttonBottomPadding = typedArray.getDimension(
                    R.styleable.SwipeButton_button_bottom_padding, 0);

            swipeButton.setPadding((int) buttonLeftPadding,
                    (int) buttonTopPadding,
                    (int) buttonRightPadding,
                    (int) buttonBottomPadding);

            typedArray.recycle();
        }

        setOnTouchListener(getButtonTouchListener());
    }

    private OnTouchListener getButtonTouchListener() {
        return new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (initialX == 0) {
                        initialX = swipeButton.getX();
                    }

                    if (event.getX() > initialX + swipeButton.getWidth() / 2 &&
                            event.getX() + swipeButton.getWidth() / 2 < getWidth()) {
                        swipeButton.setX(event.getX() - swipeButton.getWidth() / 2);
                        centerText.setAlpha(1 - 1.3f * (swipeButton.getX() + swipeButton.getWidth()) / getWidth());
                    }

                    if  (event.getX() + swipeButton.getWidth() / 2 > getWidth() &&
                            swipeButton.getX() + swipeButton.getWidth() / 2 < getWidth()) {
                        swipeButton.setX(getWidth() - swipeButton.getWidth());
                    }

                    if  (event.getX() < swipeButton.getWidth() / 2 &&
                            swipeButton.getX() > 0) {
                        swipeButton.setX(0);
                    }

                    return true;
                case MotionEvent.ACTION_UP:
                    if (active) {
                        collapseButton();
                    } else {
                        if (swipeButton.getX() + swipeButton.getWidth() > getWidth() * 0.85) {
                            expandButton();
                        } else {
                            moveButtonBack();
                        }
                    }

                    return true;
                }

            return false;
            }
        };
    }

    private void expandButton() {
        final ValueAnimator positionAnimator =
                ValueAnimator.ofFloat(swipeButton.getX(), 0);
        positionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (Float) positionAnimator.getAnimatedValue();
                swipeButton.setX(x);
            }
        });


        final ValueAnimator widthAnimator = ValueAnimator.ofInt(
                swipeButton.getWidth(),
                getWidth());

        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams params = swipeButton.getLayoutParams();
                params.width = (Integer) widthAnimator.getAnimatedValue();
                swipeButton.setLayoutParams(params);
            }
        });


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                active = true;
                swipeButton.setImageDrawable(enabledDrawable);

                if (onStateChangeListener != null) {
                    onStateChangeListener.onStateChange(active);
                }
            }
        });

        animatorSet.playTogether(positionAnimator, widthAnimator);
        animatorSet.start();
    }

    private void moveButtonBack() {
        final ValueAnimator positionAnimator =
                ValueAnimator.ofFloat(swipeButton.getX(), 0);
        positionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        positionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (Float) positionAnimator.getAnimatedValue();
                swipeButton.setX(x);
            }
        });

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
                centerText, "alpha", 1);

        positionAnimator.setDuration(200);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator, positionAnimator);
        animatorSet.start();
    }

    private void collapseButton() {
        final ValueAnimator widthAnimator = ValueAnimator.ofInt(
                swipeButton.getWidth(),
                swipeButton.getHeight());

        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams params =  swipeButton.getLayoutParams();
                params.width = (Integer) widthAnimator.getAnimatedValue();
                swipeButton.setLayoutParams(params);
            }
        });

        widthAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                active = false;
                swipeButton.setImageDrawable(disabledDrawable);

                if (onStateChangeListener != null) {
                    onStateChangeListener.onStateChange(active);
                }
            }
        });

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
                centerText, "alpha", 1);

        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.playTogether(objectAnimator, widthAnimator);
        animatorSet.start();
    }


}
