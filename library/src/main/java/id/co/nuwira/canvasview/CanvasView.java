package id.co.nuwira.canvasview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
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

    public CanvasView(Context context) {
        super(context);
        init();
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    protected void init() {
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(6f);
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

        canvas.drawColor(Color.WHITE);

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
