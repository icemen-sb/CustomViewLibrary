package ru.relastic.customviewlibrary.meet023customviewgroup;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import ru.relastic.customviewlibrary.R;

public class AlignLayout extends ViewGroup {
    private Rect mTmpContainerRect = new Rect();
    private Rect mTmpChildRect = new Rect();
    public static final int DEFAULT_ALIGN = LayoutParams.POSITION_MIDDLE;
    public static final boolean DEFAULT_MULTILINE = true;
    public static final int DEFAULT_GRAVITY = Gravity.TOP | Gravity.START;

    private boolean mMultiline;
    private int mAlign;
    private int mGravity;
    private final LayoutParams mParams = new LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);


    public AlignLayout(Context context) {
        super(context);
        mAlign = DEFAULT_ALIGN;
        mMultiline = DEFAULT_MULTILINE;
        mGravity = DEFAULT_GRAVITY;
    }
    public AlignLayout(Context context, AttributeSet attrs) {
        super(context, attrs,0);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.AlignLayout);
        mAlign = attributes.getInt(R.styleable.AlignLayout_layout_align, DEFAULT_ALIGN);
        mMultiline = attributes.getBoolean(R.styleable.AlignLayout_multiline,DEFAULT_MULTILINE);
        mGravity = attributes.getInt(R.styleable.AlignLayout_android_layout_gravity,DEFAULT_GRAVITY);
        attributes.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int leftPos = getPaddingLeft();
        final int rightPos = r - l - getPaddingRight();
        final int topPos = getPaddingTop();
        final int BottomPos = b - t - getPaddingBottom();

        int maxWidth = (rightPos-leftPos);  //ширина Layout c учетом padding контейнера
        int countAll = getChildCount();     //общее количество View в текущем Layout
        int count=0;                        //количество View, требующих выделения места на Layout
        int maxViewWidth=0;                 //вычисленная максимальная ширина элеиента View
        int maxViewHeight=0;                //вычисленная максимальная высота элемента View
        int countRow;                       //количество View, размещаемых в горизонтальном ряду

        int countRowDecrement = 0;          //Декремент текущего элемента в ряду
        int topPosCurrent = -1;             //Начальная координата по вертикали;
        int leftPosCurrent = leftPos;       //Начальная координата по горизонтали слева;
        int rightPosCurrent = rightPos;     //Начальная координата по горизонтали справа;
        int freeSpace = 0;                  //Свободное место по ширине. Только ля POSITION_MIDDLE
        for (int i = 0; i<countAll; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                count++;
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                maxViewWidth = Math.max(maxViewWidth,(child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin));
                maxViewHeight = Math.max(maxViewHeight,(child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin));
            }
        }
        if (mMultiline && maxViewWidth>0) {
            countRow = Math.min(maxWidth / maxViewWidth, count);
        } else {
            countRow = count;
            if((maxViewWidth*countRow)>maxWidth && countRow>0) {
                maxViewWidth = maxWidth / countRow;
            }
        }
        mTmpContainerRect.top = 0;
        mTmpContainerRect.bottom = 0;
        mTmpContainerRect.left = 0;
        mTmpContainerRect.right = 0;
        for (int i = 0; i<countAll; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                mTmpChildRect.top = 0;
                mTmpChildRect.bottom = 0;
                mTmpChildRect.left = 0;
                mTmpChildRect.right = 0;
                if (countRowDecrement == 0) {
                    if (count>=countRow) {
                        count -= countRow;
                    }else {
                        countRow = count;
                    }
                    countRowDecrement = countRow;
                    freeSpace = (maxWidth-countRow*maxViewWidth);
                    if (freeSpace<0) {freeSpace=0;}
                    leftPosCurrent = leftPos;
                    rightPosCurrent = rightPos;

                    if (topPosCurrent == -1) {
                        topPosCurrent = topPos;
                    } else {
                        topPosCurrent += maxViewHeight;
                    }
                }

                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();

                if(mAlign == LayoutParams.POSITION_LEFT) {
                    mTmpContainerRect.left = leftPosCurrent + lp.leftMargin;
                    mTmpContainerRect.right = leftPosCurrent + maxViewWidth - lp.rightMargin;
                    leftPosCurrent = leftPosCurrent + maxViewWidth;
                }else if(mAlign == LayoutParams.POSITION_RIGHT) {
                    mTmpContainerRect.right = rightPosCurrent - lp.rightMargin;
                    mTmpContainerRect.left = rightPosCurrent - maxViewWidth + lp.leftMargin;
                    rightPosCurrent = rightPosCurrent - maxViewWidth;
                }else {
                    mTmpContainerRect.left = leftPos
                            + (freeSpace / 2)
                            + (maxViewWidth * (countRow-countRowDecrement)) + lp.leftMargin;
                    mTmpContainerRect.right = mTmpContainerRect.left + maxViewWidth
                            - lp.leftMargin -lp.rightMargin;
                }
                mTmpContainerRect.top = topPosCurrent + lp.topMargin;
                mTmpContainerRect.bottom = topPosCurrent + maxViewHeight - lp.topMargin - lp.bottomMargin;
                mTmpChildRect.set(0, 0, 0, 0);
                Gravity.apply(mGravity, width, height, mTmpContainerRect, mTmpChildRect);
                child.layout(mTmpChildRect.left, mTmpChildRect.top, mTmpChildRect.right, mTmpChildRect.bottom);
                countRowDecrement--;
            }
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        int width = MeasureSpec.getSize(widthMeasureSpec)-getPaddingLeft()-getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec)-getPaddingTop()-getPaddingBottom();

        int countAll = getChildCount();     //общее количество View в текущем Layout
        int count=0;                        //количество View, требующих выделения места на Layout
        int maxViewWidth=0;                 //вычисленная максимальная ширина всех размещаяемых View
        int maxViewHeight=0;                //вычисленная максимальная высота всех размещаяемых View
        int countRow=0;                     //количество View, размещаемых в горизонтальном ряду
        for (int i = 0; i<countAll; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                count++;
                child.setLayoutParams(mParams);
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                maxViewWidth = Math.max(maxViewWidth,(child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin));
                maxViewHeight = Math.max(maxViewHeight,(child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin));
                childState = combineMeasuredStates(childState, child.getMeasuredState());
            }
        }

        int widthChildSpec = MeasureSpec.makeMeasureSpec(maxViewWidth,MeasureSpec.getMode(widthMeasureSpec));
        int heightChildSpec = MeasureSpec.makeMeasureSpec(maxViewHeight,MeasureSpec.getMode(heightMeasureSpec));
        for (int i = 0; i<countAll; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthChildSpec, 0, heightChildSpec, 0);
            }
        }

        if (maxViewWidth>0) {
            if (mMultiline) {
                countRow = Math.min(width / maxViewWidth, count);
                if (countRow>0) {
                    maxWidth = maxViewWidth*countRow;
                    maxHeight = ((count / countRow) + ((count % countRow == 0d) ? 0 : 1))*maxViewHeight;
                }
            } else {
                countRow = count;
                if((maxViewWidth*countRow)>maxWidth) {
                    maxViewWidth = maxWidth / countRow;
                }
                maxWidth = maxViewWidth*countRow;
                maxHeight = maxViewHeight;
            }
        }

        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    public static class LayoutParams extends MarginLayoutParams {
        //класс настроек для элементов данного ViewGroup
        public int gravity = Gravity.TOP | Gravity.START;
        public static int POSITION_MIDDLE = 0;
        public static int POSITION_LEFT = 1;
        public static int POSITION_RIGHT = 2;

        private int align = POSITION_MIDDLE;
        private boolean multiline = false;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.AlignLayout);
            //gravity = a.getInt(R.styleable., gravity);
            align = a.getInt(R.styleable.AlignLayout_layout_align, align);
            multiline = a.getBoolean(R.styleable.AlignLayout_multiline, multiline);
            a.recycle();
        }
        public LayoutParams(int width, int height) {
            super(width, height);
        }
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }
        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public int getAlign() {
            return align;
        }
        public void setAlign(int align, @Nullable AlignLayout layout) {
            this.align = align;
            refresh(layout);
        }
        public boolean getMultiline() {
            return multiline;
        }
        public void setMultiline(boolean multiline, @Nullable AlignLayout layout) {
            this.multiline = multiline;
            refresh(layout);
        }
        public int getGravity() {
            return gravity;
        }
        public void setGravity(int gravity, @Nullable AlignLayout layout) {
            this.gravity = gravity;
            refresh(layout);
        }
        private void refresh(@Nullable AlignLayout layout) {
            //if (layout != null && layout.getClass().equals(AlignLayout.class)) {
            if (layout != null) {
                layout.requestLayout();
                layout.invalidate();
            }
        }
    }
}
