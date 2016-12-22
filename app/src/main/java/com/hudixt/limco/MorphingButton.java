package com.hudixt.limco;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by root on 12/19/2016.
 */

public class MorphingButton extends Button {

    private Padding mPadding;
    private int mHeight;
    private int mWidth;
    private int mColor;
    private int mCornerRadius;
    private int mStrokeWidth;
    private int mStrokeColor;

    protected boolean mAnimationInProgress;

    private StrokeGradientDrawable mDrawableNormal;
    private StrokeGradientDrawable mDrawablePressed;

    public MorphingButton(Context context) {
        super(context);
        initView();
    }

    public MorphingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MorphingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mHeight == 0 && mWidth == 0 && w != 0 && h != 0) {
            mHeight = getHeight();
            mWidth = getWidth();
        }
    }

    public StrokeGradientDrawable getDrawableNormal() {
        return mDrawableNormal;
    }

    public void morph(@NonNull Params params) {
        if (!mAnimationInProgress) {

            mDrawablePressed.setColor(params.colorPressed);
            mDrawablePressed.setCornerRadius(params.cornerRadius);
            mDrawablePressed.setStrokeColor(params.strokeColor);
            mDrawablePressed.setStrokeWidth(params.strokeWidth);

            if (params.duration == 0) {
                morphWithoutAnimation(params);
            } else {
                morphWithAnimation(params);
            }

            mColor = params.color;
            mCornerRadius = params.cornerRadius;
            mStrokeWidth = params.strokeWidth;
            mStrokeColor = params.strokeColor;
        }
    }

    private void morphWithAnimation(@NonNull final Params params) {
        mAnimationInProgress = true;
        setText(null);
        setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        setPadding(mPadding.left, mPadding.top, mPadding.right, mPadding.bottom);

        MorphingAnimation.Params animationParams = MorphingAnimation.Params.create(this)
                .color(mColor, params.color)
                .cornerRadius(mCornerRadius, params.cornerRadius)
                .strokeWidth(mStrokeWidth, params.strokeWidth)
                .strokeColor(mStrokeColor, params.strokeColor)
                .height(getHeight(), params.height)
                .width(getWidth(), params.width)
                .duration(params.duration)
                .listener(new MorphingAnimation.Listener() {
                    @Override
                    public void onAnimationEnd() {
                        finalizeMorphing(params);
                    }
                });

        MorphingAnimation animation = new MorphingAnimation(animationParams);
        animation.start();
    }

    private void morphWithoutAnimation(@NonNull Params params) {
        mDrawableNormal.setColor(params.color);
        mDrawableNormal.setCornerRadius(params.cornerRadius);
        mDrawableNormal.setStrokeColor(params.strokeColor);
        mDrawableNormal.setStrokeWidth(params.strokeWidth);

        if(params.width != 0 && params.height !=0) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = params.width;
            layoutParams.height = params.height;
            setLayoutParams(layoutParams);
        }

        finalizeMorphing(params);
    }

    private void finalizeMorphing(@NonNull Params params) {
        mAnimationInProgress = false;

        if (params.icon != 0 && params.text != null) {
            setIconLeft(params.icon);
            setText(params.text);
        } else if (params.icon != 0) {
            setIcon(params.icon);
        } else if(params.text != null) {
            setText(params.text);
        }

        if (params.animationListener != null) {
            params.animationListener.onAnimationEnd();
        }
    }

    public void blockTouch() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    public void unblockTouch() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    private void initView() {
        mPadding = new Padding();
        mPadding.left = getPaddingLeft();
        mPadding.right = getPaddingRight();
        mPadding.top = getPaddingTop();
        mPadding.bottom = getPaddingBottom();

        Resources resources = getResources();
        int cornerRadius = (int) 4;
        int primaryColor = resources.getColor(R.color.colorAccent);
        int secondaryColor = resources.getColor(R.color.colorPrimary);

        StateListDrawable background = new StateListDrawable();
        mDrawableNormal = createDrawable(primaryColor, cornerRadius, 0);
        mDrawablePressed = createDrawable(secondaryColor, cornerRadius, 0);

        mColor = primaryColor;
        mStrokeColor = primaryColor;
        mCornerRadius = cornerRadius;

        background.addState(new int[]{android.R.attr.state_pressed}, mDrawablePressed.getGradientDrawable());
        background.addState(StateSet.WILD_CARD, mDrawableNormal.getGradientDrawable());

        setBackgroundCompat(background);
    }

    private StrokeGradientDrawable createDrawable(int color, int cornerRadius, int strokeWidth) {
        StrokeGradientDrawable drawable = new StrokeGradientDrawable(new GradientDrawable());
        drawable.getGradientDrawable().setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(color);
        drawable.setCornerRadius(cornerRadius);
        drawable.setStrokeColor(color);
        drawable.setStrokeWidth(strokeWidth);

        return drawable;
    }

    @SuppressWarnings("deprecation")
    private void setBackgroundCompat(@Nullable Drawable drawable) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(drawable);
        } else {
            setBackground(drawable);
        }
    }

    public void setIcon(@DrawableRes final int icon) {
        // post is necessary, to make sure getWidth() doesn't return 0
        post(new Runnable() {
            @Override
            public void run() {
                Drawable drawable = getResources().getDrawable(icon);
                int padding = (getWidth() / 2) - (drawable.getIntrinsicWidth() / 2);
                setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
                setPadding(padding, 0, 0, 0);
            }
        });
    }

    public void setIconLeft(@DrawableRes int icon) {
        setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
    }

    private class Padding {
        public int left;
        public int right;
        public int top;
        public int bottom;
    }

    public static class Params {
        private int cornerRadius;
        private int width;
        private int height;
        private int color;
        private int colorPressed;
        private int duration;
        private int icon;
        private int strokeWidth;
        private int strokeColor;
        private String text;
        private MorphingAnimation.Listener animationListener;

        private Params() {

        }

        public static Params create() {
            return new Params();
        }

        public Params text(@NonNull String text) {
            this.text = text;
            return this;
        }

        public Params icon(@DrawableRes int icon) {
            this.icon = icon;
            return this;
        }

        public Params cornerRadius(int cornerRadius) {
            this.cornerRadius = cornerRadius;
            return this;
        }

        public Params width(int width) {
            this.width = width;
            return this;
        }

        public Params height(int height) {
            this.height = height;
            return this;
        }

        public Params color(int color) {
            this.color = color;
            return this;
        }

        public Params colorPressed(int colorPressed) {
            this.colorPressed = colorPressed;
            return this;
        }

        public Params duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Params strokeWidth(int strokeWidth) {
            this.strokeWidth = strokeWidth;
            return this;
        }

        public Params strokeColor(int strokeColor) {
            this.strokeColor = strokeColor;
            return this;
        }

        public Params animationListener(MorphingAnimation.Listener animationListener) {
            this.animationListener = animationListener;
            return this;
        }
    }
    public static class MorphingAnimation {

        public interface Listener {
            void onAnimationEnd();
        }

        public static class Params {

            private float fromCornerRadius;
            private float toCornerRadius;

            private int fromHeight;
            private int toHeight;

            private int fromWidth;
            private int toWidth;

            private int fromColor;
            private int toColor;

            private int duration;

            private int fromStrokeWidth;
            private int toStrokeWidth;

            private int fromStrokeColor;
            private int toStrokeColor;

            private MorphingButton button;
            private MorphingAnimation.Listener animationListener;

            private Params(@NonNull MorphingButton button) {
                this.button = button;
            }

            public static Params create(@NonNull MorphingButton button) {
                return new Params(button);
            }

            public Params duration(int duration) {
                this.duration = duration;
                return this;
            }

            public Params listener(@NonNull MorphingAnimation.Listener animationListener) {
                this.animationListener = animationListener;
                return this;
            }

            public Params color(int fromColor, int toColor) {
                this.fromColor = fromColor;
                this.toColor = toColor;
                return this;
            }

            public Params cornerRadius(int fromCornerRadius, int toCornerRadius) {
                this.fromCornerRadius = fromCornerRadius;
                this.toCornerRadius = toCornerRadius;
                return this;
            }

            public Params height(int fromHeight, int toHeight) {
                this.fromHeight = fromHeight;
                this.toHeight = toHeight;
                return this;
            }

            public Params width(int fromWidth, int toWidth) {
                this.fromWidth = fromWidth;
                this.toWidth = toWidth;
                return this;
            }

            public Params strokeWidth(int fromStrokeWidth, int toStrokeWidth) {
                this.fromStrokeWidth = fromStrokeWidth;
                this.toStrokeWidth = toStrokeWidth;
                return this;
            }

            public Params strokeColor(int fromStrokeColor, int toStrokeColor) {
                this.fromStrokeColor = fromStrokeColor;
                this.toStrokeColor = toStrokeColor;
                return this;
            }

        }

        private Params mParams;

        public MorphingAnimation(@NonNull Params params) {
            mParams = params;
        }

        public void start() {
            StrokeGradientDrawable background = mParams.button.getDrawableNormal();

            ObjectAnimator cornerAnimation =
                    ObjectAnimator.ofFloat(background, "cornerRadius", mParams.fromCornerRadius, mParams.toCornerRadius);

            ObjectAnimator strokeWidthAnimation =
                    ObjectAnimator.ofInt(background, "strokeWidth", mParams.fromStrokeWidth, mParams.toStrokeWidth);

            ObjectAnimator strokeColorAnimation = ObjectAnimator.ofInt(background, "strokeColor", mParams.fromStrokeColor, mParams.toStrokeColor);
            strokeColorAnimation.setEvaluator(new ArgbEvaluator());

            ObjectAnimator bgColorAnimation = ObjectAnimator.ofInt(background, "color", mParams.fromColor, mParams.toColor);
            bgColorAnimation.setEvaluator(new ArgbEvaluator());

            ValueAnimator heightAnimation = ValueAnimator.ofInt(mParams.fromHeight, mParams.toHeight);
            heightAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = mParams.button.getLayoutParams();
                    layoutParams.height = val;
                    mParams.button.setLayoutParams(layoutParams);
                }
            });

            ValueAnimator widthAnimation = ValueAnimator.ofInt(mParams.fromWidth, mParams.toWidth);
            widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = mParams.button.getLayoutParams();
                    layoutParams.width = val;
                    mParams.button.setLayoutParams(layoutParams);
                }
            });

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(mParams.duration);
            animatorSet.playTogether(strokeWidthAnimation, strokeColorAnimation, cornerAnimation, bgColorAnimation,
                    heightAnimation, widthAnimation);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mParams.animationListener != null) {
                        mParams.animationListener.onAnimationEnd();
                    }
                }
            });
            animatorSet.start();
        }

    }
    public class StrokeGradientDrawable {

        private int mStrokeWidth;
        private int mStrokeColor;

        private GradientDrawable mGradientDrawable;
        private float mRadius;
        private int mColor;

        public StrokeGradientDrawable(GradientDrawable drawable) {
            mGradientDrawable = drawable;
        }

        public int getStrokeWidth() {
            return mStrokeWidth;
        }

        public void setStrokeWidth(int strokeWidth) {
            mStrokeWidth = strokeWidth;
            mGradientDrawable.setStroke(strokeWidth, getStrokeColor());
        }

        public int getStrokeColor() {
            return mStrokeColor;
        }

        public void setStrokeColor(int strokeColor) {
            mStrokeColor = strokeColor;
            mGradientDrawable.setStroke(getStrokeWidth(), strokeColor);
        }

        public void setCornerRadius(float radius) {
            mRadius = radius;
            mGradientDrawable.setCornerRadius(radius);
        }

        public void setColor(int color) {
            mColor = color;
            mGradientDrawable.setColor(color);
        }

        public int getColor() {
            return mColor;
        }

        public float getRadius() {
            return mRadius;
        }

        public GradientDrawable getGradientDrawable() {
            return mGradientDrawable;
        }
    }
}