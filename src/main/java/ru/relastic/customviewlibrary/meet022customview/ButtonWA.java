package ru.relastic.customviewlibrary.meet022customview;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.relastic.customviewlibrary.R;

public class ButtonWA extends View {
    private static final float DEFAULT_TEXT_SIZE_SP = 6;
    private static final float DEFAULT_IMAGE_SIZE_DP = 32;
    private static final int DEFAULT_CONNER_RAD_DP = 8;
    private static final float DEFAULT_VERTICAL_DISTANCE_DP = 0f;
    private static final int DEFAULT_RIPPLE_RADIUS_DP = 32;
    private static final boolean DEFAULT_TEXT_BOLD = true;
    private static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    private static final boolean DEFAULT_CLICKABLE = true;
    private static final boolean DEFAULT_FOCUSABLE = true;

    private final LinearLayout mLayout = new LinearLayout(this.getContext());
    private final ImageView mImage = new ImageView(this.getContext());
    private final TextView mText = new TextView(this.getContext());
    private final GestureDetector mGestures = new GestureDetector(this.getContext(), new MyGestureListener());
    private RippleDrawable mBackDrawable = null;
    private ShapeDrawable mBackShape = null;
    private static final ColorStateList RIPPLE_COLOR_DEFAULT = ColorStateList.valueOf(Color.LTGRAY);
    private static final ColorDrawable RIPPLE_MASK = null;

    private static float k_dp_px = 1f;
    private static float k_sp_px = 1f;
    private static float font_scale = 1f;

    public ButtonWA(Context context) {
        super(context);
        initViews();
        setClickable(DEFAULT_CLICKABLE);
        setFocusable(DEFAULT_FOCUSABLE);
        setBackground(null);
    }
    public ButtonWA(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ButtonWA);

        setImage(attributes.getResourceId(R.styleable.ButtonWA_image_source,-1));
        setText(attributes.getResourceId(R.styleable.ButtonWA_annotation_source,-1));
        setFrontColor(attributes.getResourceId(R.styleable.ButtonWA_front_color,-1));
        setBackColor(attributes.getResourceId(R.styleable.ButtonWA_back_color,-1));
        setImageSize(attributes.getDimension(R.styleable.ButtonWA_image_size, -1));
        setTextSize(attributes.getDimension(R.styleable.ButtonWA_text_size, -1));
        setTextBold(attributes.getBoolean(R.styleable.ButtonWA_text_bold,DEFAULT_TEXT_BOLD));
        setDistance(attributes.getDimension(R.styleable.ButtonWA_vertical_distance,-1));
        if (attrs != null) {
            setClickable(attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/android",
                    "clickable", DEFAULT_CLICKABLE));
            setFocusable(attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/android",
                    "focusable", DEFAULT_FOCUSABLE));
        }
        attributes.recycle();

        //setBackground(getBackground());
    }

    private void initViews() {
        k_dp_px = getResources().getDisplayMetrics().density;
        k_sp_px = getResources().getDisplayMetrics().scaledDensity;
        font_scale = getResources().getConfiguration().fontScale;

        final LinearLayout.LayoutParams imageParam;
        final LinearLayout.LayoutParams textParam;

        imageParam = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageParam.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        imageParam.width = (int)(DEFAULT_IMAGE_SIZE_DP * k_dp_px);
        imageParam.height = (int)(DEFAULT_IMAGE_SIZE_DP * k_dp_px);
        mImage.setLayoutParams(imageParam);

        textParam = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textParam.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        //textParam.topMargin = (int)(DEFAULT_VERTICAL_DISTANCE_DP * k_dp_px);
        mText.setLayoutParams(textParam);
        //mText.setTextSize(DEFAULT_TEXT_SIZE_DP * font_scale);
        mText.setTypeface(mText.getTypeface(), Typeface.BOLD);

        mLayout.setOrientation(LinearLayout.VERTICAL);
        mLayout.addView(mImage);
        mLayout.addView(mText);
        mLayout.setGravity(Gravity.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mLayout.measure(resolveSizeAndState(MeasureSpec.getSize(widthMeasureSpec)-getPaddingStart()-getPaddingEnd(),
                widthMeasureSpec,
                mLayout.getMeasuredWidthAndState()),
                resolveSizeAndState(MeasureSpec.getSize(heightMeasureSpec)-getPaddingTop()-getPaddingBottom(),
                        heightMeasureSpec,
                        mLayout.getMeasuredHeightAndState()));

        int desiredWidth = getPaddingStart() + mLayout.getMeasuredWidth() + getPaddingEnd();
        int desiredHeight = getPaddingTop() + mLayout.getMeasuredHeight()+ getPaddingBottom();
        setMeasuredDimension(measuredDimension(desiredWidth,widthMeasureSpec),
                measuredDimension(desiredHeight,heightMeasureSpec));
    }
    private static int measuredDimension(int desiredSize, int measuredSpec){
        int retVal = desiredSize;
        int specMode = MeasureSpec.getMode(measuredSpec);
        int specSize = MeasureSpec.getSize(measuredSpec);
        if (specMode==MeasureSpec.EXACTLY) {
            retVal = specSize;
        }else if (specMode==MeasureSpec.AT_MOST) {
            retVal = Math.min(desiredSize,specSize);
        }
        return retVal;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int maxWidth = (right-left);
        int maxHeight = (bottom-top);
        mLayout.layout(0,0,maxWidth,maxHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mLayout.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestures.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void setBackground(Drawable background) {
        RippleDrawable rd = prepareBackground(background);
        super.setBackground(rd);
    }

    public ImageView getImage() {
        return mImage;
    }
    public void setImage(int imageID) {
        if (imageID==-1) {
            Bitmap bitmap = Bitmap.createBitmap((int) (DEFAULT_IMAGE_SIZE_DP * k_dp_px),
                    (int) (DEFAULT_IMAGE_SIZE_DP * k_dp_px),
                    Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.BLACK);
            this.mImage.setImageBitmap(bitmap);
        } else {
            this.mImage.setImageDrawable(getResources().getDrawable(imageID));
        }
        update();
    }
    public void setImage(Bitmap bitmap) {
        this.mImage.setImageBitmap(bitmap);
        update();
    }
    public TextView getText() {
        return mText;
    }
    public void setText(int textID) {
        if (textID==-1) {
            this.mText.setText("<annotation>");
        }else {
            this.mText.setText(getResources().getString(textID));
        }
        update();
    }
    public void setText(String text) {
        this.mText.setText(text);
        update();
    }
    public void setFrontColor(int colorID) {
        if (colorID==-1) {
            mImage.setImageTintList(null);
            mText.setTextColor(DEFAULT_TEXT_COLOR);
        }else {
            ColorStateList myColorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{}
                    },
                    new int[] {
                            getResources().getColor(colorID),
                    }
            );
            mImage.setImageTintList(myColorStateList);
            mText.setTextColor(myColorStateList);
        }
        invalidate();
    }
    public void setBackColor(@ColorRes int colorID) {
        if (colorID==-1) {
            setBackground(getBackground());
        } else {
            setBackColorInt(getResources().getColor(colorID));
        }
        invalidate();
    }
    private void setBackColorInt(@ColorInt int color) {
        setBackground(prepareBackShapeByColor(color));
    }
    public void setImageSize(float size) {
        //by px
        if (size<0) {
            size = DEFAULT_IMAGE_SIZE_DP * k_dp_px;
        }
        mImage.getLayoutParams().width = (int)(size);
        mImage.getLayoutParams().height = (int)(size);
        update();
    }
    public void setTextSize(float size) {
        //by px
        /*
        if (size<1) {
            size = DEFAULT_TEXT_SIZE_SP * k_sp_px * font_scale;
        }
        mText.setTextSize(size);
        update();
        */
    }
    public void setTextBold(boolean bold) {
        mText.setTypeface(mText.getTypeface(), bold ? Typeface.BOLD : Typeface.NORMAL);
        update();
    }
    public float getDistance() {
        return ((LinearLayout.LayoutParams)mText.getLayoutParams()).topMargin;
    }
    public void setDistance(float distance) {
        if (distance<0) {
            distance = DEFAULT_VERTICAL_DISTANCE_DP * k_dp_px;
        }
        if (getDistance()!=distance) {
            ((LinearLayout.LayoutParams)mText.getLayoutParams()).topMargin = (int)distance;
            update();
        }
    }

    private void update() {
        invalidate();
        requestLayout();
    }

    private RippleDrawable prepareBackground(@Nullable Drawable backDrawable){
        if (backDrawable==null) {
            //создаем Ripple по умолчанию
            //обнуляем Shape
            mBackDrawable = createRippleDrawable(RIPPLE_COLOR_DEFAULT, null, RIPPLE_MASK);
            mBackShape = null;
        } else if (isColorDrawable(backDrawable)) {
            if (mBackShape==null) {
                //создаем Shape
                //пересоздаем Ripple на основе Shape
                mBackDrawable = createRippleDrawable(RIPPLE_COLOR_DEFAULT,
                        prepareBackShapeByColor(((ColorDrawable) backDrawable).getColor()), RIPPLE_MASK);
            } else if (mBackShape.getPaint().getColor() != ((ColorDrawable) backDrawable).getColor()){
                //меняем цвет
                mBackShape.getPaint().setColor(((ColorDrawable) backDrawable).getColor());
            }
        } else if (mBackDrawable==null || !mBackDrawable.equals(backDrawable)) {
                //Пересоздаем Ripple
                //обнуляем Shape
                mBackDrawable = createRippleDrawable(RIPPLE_COLOR_DEFAULT, backDrawable, RIPPLE_MASK);
                mBackShape = null;
        }
        return mBackDrawable;
    }
    private ShapeDrawable prepareBackShapeByColor(@ColorInt int color){
        if (mBackShape == null) {
            float rad = DEFAULT_CONNER_RAD_DP * k_dp_px;
            mLayout.setPadding((int)rad/3,(int)rad/3,(int)rad/3,(int)rad/3);
            float [] outR = new float [] {rad, rad, rad, rad, rad, rad, rad, rad};
            //RectF rectF = new RectF(8, 8, 8, 8);
            //float [] inR = new float [] { 6, 6, 6, 6, 6, 6, 6, 6};
            mBackShape = new ShapeDrawable(new RoundRectShape(outR, null, null));
        }
        if(mBackShape.getPaint().getColor()!=color) {
            mBackShape.getPaint().setColor(color);
        }

        return mBackShape;
    }
    private RippleDrawable createRippleDrawable(ColorStateList colors, Drawable content, Drawable mask) {
        //RippleDrawable retVal = new RippleDrawable(colors, content, mBackDrawable); - в релизе 1.1
        RippleDrawable retVal = new RippleDrawable(colors, content, mask);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal.setRadius((int)(DEFAULT_RIPPLE_RADIUS_DP * k_dp_px));
        }
        return retVal;
    }

    private static boolean isColorDrawable(@Nullable  Drawable target) {
        boolean retVal = false;
        if ((target != null) && (target.getClass().equals(ColorDrawable.class))) {
            retVal = true;
        }
        return retVal;
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        MyGestureListener() {
            super();
        }

        @Override
        public boolean onDown(MotionEvent e) {
            //ButtonWA.this.callOnClick();
            return super.onDown(e);
        }
    }
}
