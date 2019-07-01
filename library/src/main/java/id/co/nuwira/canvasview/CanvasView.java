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
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CanvasView extends View {

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

        }

        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mCvStrokeColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mCvStrokeWidth);
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
        invalidate();
    }

    public Bitmap getBitmap() {
        setDrawingCacheEnabled(false);
        setDrawingCacheEnabled(true);

        return Bitmap.createBitmap(getDrawingCache());
    }
}
