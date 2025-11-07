package com.example.mensura.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.myapplication.R;

public class AngleGaugeView extends View {

    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF arcBounds = new RectF();

    private float ringWidthPx;
    private int trackColor = 0xFFE6E9F0;      // cinza claro
    private int progressColor = 0xFF5A73FF;   // azul
    private int maxAngle = 180;               // você pode mudar para 360 se quiser
    private float angle = 0f;                 // valor atual
    private boolean startAtTop = true;        // começa às 12h (−90°)

    public AngleGaugeView(Context c) { this(c, null); }
    public AngleGaugeView(Context c, @Nullable AttributeSet a) { this(c, a, 0); }
    public AngleGaugeView(Context c, @Nullable AttributeSet a, int defStyleAttr) {
        super(c, a, defStyleAttr);

        float defaultRing = dp(18);

        if (a != null) {
            TypedArray ta = c.obtainStyledAttributes(a, R.styleable.AngleGaugeView);
            maxAngle     = ta.getInt(R.styleable.AngleGaugeView_maxAngle, maxAngle);
            ringWidthPx  = ta.getDimension(R.styleable.AngleGaugeView_ringWidth, defaultRing);
            trackColor   = ta.getColor(R.styleable.AngleGaugeView_trackColor, trackColor);
            progressColor= ta.getColor(R.styleable.AngleGaugeView_progressColor, progressColor);
            startAtTop   = ta.getBoolean(R.styleable.AngleGaugeView_startAtTop, true);
            ta.recycle();
        } else {
            ringWidthPx = defaultRing;
        }

        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(ringWidthPx);
        trackPaint.setStrokeCap(Paint.Cap.ROUND);
        trackPaint.setColor(trackColor);

        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(ringWidthPx);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setColor(progressColor);

        thumbPaint.setStyle(Paint.Style.FILL);
        thumbPaint.setColor(progressColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        float pad = ringWidthPx / 2f + getPaddingLeft();
        float right = w - (ringWidthPx / 2f) - getPaddingRight();
        float bottom = h - (ringWidthPx / 2f) - getPaddingBottom();
        arcBounds.set(pad, ringWidthPx/2f + getPaddingTop(), right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 1) trilha completa
        canvas.drawArc(arcBounds, -90, 360, false, trackPaint);

        // 2) progresso proporcional ao ângulo
        float sweep = (maxAngle <= 0) ? 0 : (angle / maxAngle) * 360f;
        float start = startAtTop ? -90f : 0f;
        if (sweep > 0) {
            canvas.drawArc(arcBounds, start, sweep, false, progressPaint);

            // 3) "thumb" na ponta do arco
            double rad = Math.toRadians(start + sweep);
            float cx = arcBounds.centerX();
            float cy = arcBounds.centerY();
            float rx = arcBounds.width() / 2f;
            float ry = arcBounds.height() / 2f;
            float x = (float) (cx + rx * Math.cos(rad));
            float y = (float) (cy + ry * Math.sin(rad));
            canvas.drawCircle(x, y, ringWidthPx / 2.6f, thumbPaint);
        }
    }

    public void setAngle(float value) {
        if (value < 0) value = 0;
        if (value > maxAngle) value = maxAngle;
        if (this.angle != value) {
            this.angle = value;
            invalidate();
        }
    }

    public float getAngle() { return angle; }

    public void setMaxAngle(int maxAngle) {
        this.maxAngle = Math.max(1, maxAngle);
        invalidate();
    }

    private float dp(float v) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
    }
}
