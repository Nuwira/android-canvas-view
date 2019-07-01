package id.co.nuwira.canvasview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CanvasView extends View {
    public static final int VISIBLE = 1;
    public static final int INVISIBLE = 0;

    private static final float TOUCH_TOLERANCE = 4;
    private Path mPath;
    private Paint mPaint;
    private Bitmap mBitmap;
    private float startX = 0F;
    private float startY = 0F;
    private Boolean isClearingScreen = false;
    private float mCvStrokeWidth = 6f;
    private int mCvStrokeColor = Color.BLACK;
    private int mCvBackgroundColor = Color.WHITE;

    private TextPaint mTextPaint;
    private String mCvHint = null;
    private float mCvHintSize = 12f;
    private int mCvHintVisibility = VISIBLE;
    private int mCvHintColor = Color.GRAY;

    public CanvasView(Context context) {
        super(context);
        init(context, null);
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    protected void init(Context context, @Nullable AttributeSet attrs) {

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.CanvasView,
                    0, 0);

            mCvStrokeWidth = a.getFloat(R.styleable.CanvasView_cv_stroke_width, 6f);
            mCvStrokeColor = a.getColor(R.styleable.CanvasView_cv_stroke_color, Color.BLACK);
            mCvBackgroundColor = a.getColor(R.styleable.CanvasView_cv_background_color, Color.WHITE);
            mCvHint = a.getString(R.styleable.CanvasView_cv_hint);
            mCvHintSize = a.getDimension(R.styleable.CanvasView_cv_hint_size, 12);
            mCvHintVisibility = a.getInteger(R.styleable.CanvasView_cv_hint_visibility, VISIBLE);
            mCvHintColor = a.getColor(R.styleable.CanvasView_cv_hint_color, Color.GRAY);
        }

        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mCvStrokeColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mCvStrokeWidth);

        if (mCvHint != null) {
            mTextPaint = new TextPaint();
            mTextPaint.setTextSize(mCvHintSize);
            mTextPaint.setColor(mCvHintColor);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(mCvBackgroundColor);

        if (mCvHint != null && mCvHintVisibility == VISIBLE) {
            Rect textBounds = new Rect();
            mTextPaint.getTextBounds(mCvHint, 0, mCvHint.length(), textBounds);

            float centerX = (getWidth() / 2f) - (textBounds.width() / 2f);
            float centerY = (getHeight() / 2f) - (textBounds.height() / 2f);

            canvas.drawText(mCvHint, centerX, centerY, mTextPaint);
        }

        if (isClearingScreen) {
            mPath.reset();
            isClearingScreen = false;
        } else {
            canvas.drawBitmap(mBitmap, 0, 0, new Paint(Paint.DITHER_FLAG));
            canvas.drawPath(mPath, mPaint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float fingerX = event.getX();
        float fingerY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mCvHintVisibility = INVISIBLE;
                touchStart(fingerX, fingerY);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                touchMove(fingerX, fingerY);
                break;
            }
            case MotionEvent.ACTION_UP: {
                touchUp();
                break;
            }
        }

        invalidate();
        return true;
    }

    private void touchStart(float fingerX, float fingerY) {
        startX = fingerX;
        startY = fingerY;
        mPath.moveTo(startX, startY);
    }

    private void touchMove(float fingerX, float fingerY) {
        float dx = Math.abs(fingerX - startX);
        float dy = Math.abs(fingerY - startY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(startX, startY, (fingerX + startX) / 2, (fingerY + startY) / 2);
            startX = fingerX;
            startY = fingerY;
        }
    }

    private void touchUp() {
        mPath.lineTo(startX, startY);
        startX = 0F;
        startY = 0F;
    }

    public void clearCanvas() {
        isClearingScreen = true;
        if (mCvHint != null) {
            mCvHintVisibility = VISIBLE;
        }
        invalidate();
    }

    public Bitmap getBitmap() {
        setDrawingCacheEnabled(false);
        setDrawingCacheEnabled(true);

        return Bitmap.createBitmap(getDrawingCache());
    }

    public float getCvStrokeWidth() {
        return mCvStrokeWidth;
    }

    public void setCvStrokeWidth(float strokeWidth) {
        this.mCvStrokeWidth = strokeWidth;
        invalidate();
    }

    public int getCvStrokeColor() {
        return mCvStrokeColor;
    }

    public void setCvStrokeColor(int color) {
        this.mCvStrokeColor = color;
        invalidate();
    }

    public int getCvBackgroundColor() {
        return mCvBackgroundColor;
    }

    public void setCvBackgroundColor(int color) {
        this.mCvBackgroundColor = color;
        invalidate();
    }

    public String getCvHint() {
        return mCvHint;
    }

    public void setCvHint(String hint) {
        this.mCvHint = hint;
        invalidate();
    }

    public float getCvHintSize() {
        return mCvHintSize;
    }

    public void setCvHintSize(float size) {
        this.mCvHintSize = size;
        invalidate();
    }

    public int getCvHintVisibility() {
        return mCvHintVisibility;
    }

    public void setCvHintVisibility(int cvHintVisibility) {
        mCvHintVisibility = cvHintVisibility;
        invalidate();
    }

    public int getCvHintColor() {
        return mCvHintColor;
    }

    public void setCvHintColor(int color) {
        mCvHintColor = color;
        invalidate();
    }
}
