package github.leelion96.piechartview.View;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


import java.text.DecimalFormat;
import java.util.ArrayList;

import github.leelion96.piechartview.Entity.PieEntry;

/**
 * @author LiCheng
 * @date 2019/2/22
 */
public class PieView extends View {
    private final int[] colors = {0xFFCCFF00, 0xFF6495ED, 0xFFE32636, 0xFF800000, 0xFF808000, 0xFFFF8C69, 0xFF808080,
            0xFFE6B800, 0xFF7CFC00};

    //是否显示中间的空洞
    private boolean mShowHole = true;
    //空洞的颜色
    private int holeColor = Color.WHITE;
    //饼图中间的空洞占据的比例
    private float holeRadiusProportion = 59;
    //饼图初始绘制角度
    private float startAngle = -90;
    //延长点和饼图边缘的间距
    private float distance = 14F;
    //延长点的大小
    private float smallCircleRadius = 3F;
    //延长点上的同心圆环的大小
    private float bigCircleRadius = 7F;
    //延长线转折点的横向偏移
    private float xOffset = 7F;
    //延长线转折点的纵向偏移
    private float yOffset = 14F;
    //延长线最长段部分的长度
    private float extend = 180F;


    private Paint mPaint;
    private RectF mRectF;
    //View的可用总宽高
    private float mTotalWidth;
    private float mTotalHeight;
    //饼图+延长线+文字 所占用长方形总空间的长宽比
    private float mScale;
    //饼图的半径
    private float mRadius;

    private ArrayList<Integer> mColorLists;
    private ArrayList<PieEntry> mPieLists;


    public PieView(Context context) {
        super(context);
    }

    public PieView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public PieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //高度为WrapContent时，设置默认高度
        if (mScale != 0 && MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            int height = (int) (mTotalWidth / mScale);
            setMeasuredDimension(widthMeasureSpec, height);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取实际View的宽高
        mTotalWidth = w - getPaddingStart() - getPaddingEnd();
        mTotalHeight = h - getPaddingTop() - getPaddingBottom();
        //绘制饼图所处的正方形RectF
        initRectF();

    }

    //绘制饼图所处的正方形RectF
    private void initRectF() {

        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        //文字的高度
        float textHeight = fontMetrics.bottom - fontMetrics.top + fontMetrics.leading;
        //延长线的纵向长度
        float lineHeight = distance + bigCircleRadius + yOffset;
        //延长线的横向长度
        float lineWidth = distance + bigCircleRadius + xOffset + extend;
        //求出饼状图加延长线和文字 所有内容需要的长方形空间的长宽比
        mScale = mTotalWidth / (mTotalWidth + lineHeight * 2 + textHeight * 2 - lineWidth * 2);


        //长方形空间其短边的长度
        float shortSideLength;
        //通过宽高比选择短边
        if (mTotalWidth / mTotalHeight >= mScale) {
            shortSideLength = mTotalHeight;
        } else {
            shortSideLength = mTotalWidth / mScale;
        }
        //饼图所在的区域为正方形，处于长方形空间的中心
        //空间的高度减去上下两部分文字显示需要的高度，除以2即为饼图的半径
        mRadius = shortSideLength / 2 - lineHeight - textHeight;
        //设置RectF的坐标
        mRectF = new RectF(-mRadius, -mRadius, mRadius, mRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将坐标中心设到View的中心
        canvas.translate(mTotalWidth / 2, mTotalHeight / 2);

        if (isPieListsNull()) {
            mPaint.setColor(Color.BLACK);
            canvas.drawText("请通过setData添加数据", -120, 0, mPaint);
        } else {
            //绘制饼状图
            drawPie(canvas);
            //绘制中心空洞
            drawHole(canvas);
            //绘制延长线和文字
            drawLineAndText(canvas);
        }

    }

    //绘制饼图
    private void drawPie(Canvas canvas) {
        //当前起始角度
        for (PieEntry pie : mPieLists) {
            mPaint.setColor(pie.getColor());
            canvas.drawArc(mRectF,
                    pie.getCurrentStartAngle(),
                    pie.getSweepAngle(),
                    true, mPaint);
        }
    }

    //绘制中心空洞
    private void drawHole(Canvas canvas) {
        if (mShowHole) {
            mPaint.setColor(holeColor);
            canvas.drawCircle(0, 0, mRadius * holeRadiusProportion / 100, mPaint);
        }
    }

    //延长线分为 延长点、同心圆环和线三个部分
    private void drawLineAndText(Canvas canvas) {

        //算出延长线转折点相对起点的正余弦值
        double offsetRadians = Math.atan(yOffset / xOffset);
        float cosOffset = (float) Math.cos(offsetRadians);
        float sinOffset = (float) Math.sin(offsetRadians);

        for (PieEntry pie : mPieLists) {

            //延长点的位置处于扇形的中间
            float halfAngle = pie.getCurrentStartAngle() + pie.getSweepAngle() / 2;
            float cos = (float) Math.cos(Math.toRadians(halfAngle));
            float sin = (float) Math.sin(Math.toRadians(halfAngle));
            //通过正余弦算出延长点的位置
            float xCirclePoint = (mRadius + distance) * cos;
            float yCirclePoint = (mRadius + distance) * sin;

            mPaint.setColor(pie.getColor());
            //绘制延长点
            canvas.drawCircle(xCirclePoint, yCirclePoint, smallCircleRadius, mPaint);
            //绘制同心圆环
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(xCirclePoint, yCirclePoint, bigCircleRadius, mPaint);
            mPaint.setStyle(Paint.Style.FILL);

            //将饼图分为4个象限，从右上角开始顺时针，每90度分为一个象限
            int quadrant = (int) (halfAngle + 90) / 90;
            //初始化 延长线的起点、转折点、终点
            float xLineStartPoint = 0;
            float yLineStartPoint = 0;
            float xLineTurningPoint = 0;
            float yLineTurningPoint = 0;
            float xLineEndPoint = 0;
            float yLineEndPoint = 0;

            String text = pie.getLabel() + " " +
                    new DecimalFormat("#.#").format(pie.getPercentage()) + "%";


            //延长点、起点、转折点在同一条线上
            //不同象限转折的方向不同
            float cosLength = bigCircleRadius * cosOffset;
            float sinLength = bigCircleRadius * sinOffset;
            switch (quadrant) {
                case 0:
                    xLineStartPoint = xCirclePoint + cosLength;
                    yLineStartPoint = yCirclePoint - sinLength;
                    xLineTurningPoint = xLineStartPoint + xOffset;
                    yLineTurningPoint = yLineStartPoint - yOffset;
                    xLineEndPoint = xLineTurningPoint + extend;
                    yLineEndPoint = yLineTurningPoint;
                    mPaint.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText(text, xLineEndPoint, yLineEndPoint - 5, mPaint);
                    break;
                case 1:
                    xLineStartPoint = xCirclePoint + cosLength;
                    yLineStartPoint = yCirclePoint + sinLength;
                    xLineTurningPoint = xLineStartPoint + xOffset;
                    yLineTurningPoint = yLineStartPoint + yOffset;
                    xLineEndPoint = xLineTurningPoint + extend;
                    yLineEndPoint = yLineTurningPoint;
                    mPaint.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText(text, xLineEndPoint, yLineEndPoint - 5, mPaint);
                    break;
                case 2:
                    xLineStartPoint = xCirclePoint - cosLength;
                    yLineStartPoint = yCirclePoint + sinLength;
                    xLineTurningPoint = xLineStartPoint - xOffset;
                    yLineTurningPoint = yLineStartPoint + yOffset;
                    xLineEndPoint = xLineTurningPoint - extend;
                    yLineEndPoint = yLineTurningPoint;
                    mPaint.setTextAlign(Paint.Align.LEFT);
                    canvas.drawText(text, xLineEndPoint, yLineEndPoint - 5, mPaint);
                    break;
                case 3:
                    xLineStartPoint = xCirclePoint - cosLength;
                    yLineStartPoint = yCirclePoint - sinLength;
                    xLineTurningPoint = xLineStartPoint - xOffset;
                    yLineTurningPoint = yLineStartPoint - yOffset;
                    xLineEndPoint = xLineTurningPoint - extend;
                    yLineEndPoint = yLineTurningPoint;
                    mPaint.setTextAlign(Paint.Align.LEFT);
                    canvas.drawText(text, xLineEndPoint, yLineEndPoint - 5, mPaint);
                    break;
                default:
            }
            //绘制延长线
            canvas.drawLine(xLineStartPoint, yLineStartPoint, xLineTurningPoint, yLineTurningPoint, mPaint);
            canvas.drawLine(xLineTurningPoint, yLineTurningPoint, xLineEndPoint, yLineEndPoint, mPaint);
        }
    }


    //初始化画笔
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(sp2px(12));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1f);
    }

    //初始化数据
    private void initData() {
        if (isPieListsNull()) {
            return;
        }
        //当前起始角度
        float currentStartAngle = startAngle;
        for (int i = 0; i < mPieLists.size(); i++) {
            PieEntry pie = mPieLists.get(i);
            pie.setCurrentStartAngle(currentStartAngle);
            //每个数据百分比对应的角度
            float sweepAngle = pie.getPercentage() / 100 * 360;
            pie.setSweepAngle(sweepAngle);
            //起始角度不断增加
            currentStartAngle += sweepAngle;


            //未传入颜色时 以默认的颜色表作为颜色
            if (mColorLists == null || mColorLists.size() == 0) {
                int j = i % colors.length;
                pie.setColor(colors[j]);
            } else {
                pie.setColor(mColorLists.get(i));
            }
        }
    }

    //初始化颜色
    private void initColors() {
        if (isPieListsNull()) {
            return;
        }
        for (int i = 0; i < mPieLists.size(); i++) {
            mPieLists.get(i).setColor(mColorLists.get(i));
        }
    }

    //判断数据是否为空
    private boolean isPieListsNull() {
        return mPieLists == null || mPieLists.size() == 0;
    }

    //添加数据
    public void setData(ArrayList<PieEntry> pieLists) {
        this.mPieLists = pieLists;
        initData();
        invalidate();
    }

    //添加颜色
    public void setColors(ArrayList<Integer> colorLists) {
        this.mColorLists = colorLists;
        initColors();
        invalidate();
    }

    //是否显示空洞
    public void setShowHole(boolean isShowHole) {
        this.mShowHole = isShowHole;
        invalidate();
    }

    /**
     * Value of sp to value of px.
     *
     * @param spValue The value of sp.
     * @return value of px
     */
    public static int sp2px(final float spValue) {
        final float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
