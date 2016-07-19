/**
 * _ooOoo_
 * o8888888o
 * 88" . "88
 * (| -_- |)
 * O\ = /O
 * ____/`---'\____
 * .   ' \\| |// `.
 * / \\||| : |||// \
 * / _||||| -:- |||||- \
 * | | \\\ - /// | |
 * | \_| ''\---/'' | |
 * \ .-\__ `-` ___/-. /
 * ___`. .' /--.--\ `. . __
 * ."" '< `.___\_<|>_/___.' >'"".
 * | | : `- \`.;`\ _ /`;.`/ - ` : | |
 * \ \ `-. \_ __\ /__ _/ .-` / /
 * ======`-.____`-.___\_____/___.-`____.-'======
 * `=---='
 *
 * .............................................
 * 佛祖保佑             永无BUG
 * 佛曰:
 * 写字楼里写字间，写字间里程序员；
 * 程序人员写程序，又拿程序换酒钱。
 * 酒醒只在网上坐，酒醉还来网下眠；
 * 酒醉酒醒日复日，网上网下年复年。
 * 但愿老死电脑间，不愿鞠躬老板前；
 * 奔驰宝马贵者趣，公交自行程序员。
 * 别人笑我忒疯癫，我笑自己命太贱；
 * 不见满街漂亮妹，哪个归得程序员？
 */

package com.go.sunzeping.movetest.widge;

import com.go.sunzeping.movetest.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by sunzeping on 2016/7/14.
 */
public class CircleView extends View {
    //内外环半径大小
    private final float RADIOOUT = 120.0f;
    private final float RADIOIN = 108.0f;
    //外环线体宽度
    private final float CIRCLE_WIDTH = 12.0f;
    //对号线粗
    private final float HOOKWIDTH = 6.0f;
    //左侧x轴坐标
    private final float LEFTPOINT = RADIOOUT / 2;
    private final float LEFTPOINT1 = RADIOOUT / 6;
    private final float RIGHTPOINT = RADIOOUT / 2;


    /**
     * 外环笔画
     */
    private Paint mCircleOutside;
    private float mRadioOutside;

    /**
     * 内环笔画
     */
    private Paint mCircleInside;
    private float mRadioInside;
    /**
     * 开始动画完标志
     */
    private boolean circleFlag = true;
    /**
     * 完成动画完标志
     */
    private boolean circleOkFlag = false;

    /**
     * 对勾笔刷
     */
    private Paint mHookLeft;
    private Paint mHookRight;
    private final float HOOK_WIDTH = 10.0f;
    private float leftPointTemp;
    private float rightPointTemp;


    private Context mContext;
    //view的宽高
    private float mWidth;
    private float mHeight;

    /******************************** 动画开始 ***********************************/
    private Paint mStart_Circle;
    private Paint mEnd_Circle;
    private int mStart_Speed;
    private int mTail_Speed;

    private int HEAD_ACCELERATION = 10;
    private int TAIL_ACCELERATION = 10;

    private int delteTime = 50;
    private final int OFFSET = 180;
    private boolean speed_change_flag = false;

    private RectF drawArc;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) invalidate();
        }
    };


    public CircleView(Context context) {
        super(context);
        this.mContext = context;
        inteView();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        inteView();
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        inteView();
    }

    /**
     * 初始化画笔
     */
    private void inteView() {
        //头部
        mStart_Circle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStart_Circle.setColor(getResources().getColor(R.color.green));
        mStart_Circle.setStyle(Paint.Style.STROKE);
        mStart_Circle.setStrokeWidth(CIRCLE_WIDTH);
        //尾部
        mEnd_Circle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mEnd_Circle.setColor(getResources().getColor(R.color.green));
        mEnd_Circle.setStyle(Paint.Style.STROKE);
        mEnd_Circle.setStrokeWidth(CIRCLE_WIDTH);
        //外环
        mRadioOutside = RADIOOUT;
        mCircleOutside = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleOutside.setColor(getResources().getColor(R.color.green));
        mCircleOutside.setStyle(Paint.Style.FILL);
        //内环
        mRadioInside = RADIOIN;
        mCircleInside = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleInside.setColor(getResources().getColor(R.color.white));
        mCircleInside.setStyle(Paint.Style.FILL);
        //左侧对号
        mHookLeft = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHookLeft.setColor(getResources().getColor(R.color.white));
        mHookLeft.setStyle(Paint.Style.FILL);
        mHookLeft.setStrokeWidth(HOOK_WIDTH);
        //右侧对号
        mHookRight = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHookRight.setColor(getResources().getColor(R.color.white));
        mHookRight.setStyle(Paint.Style.FILL);
        mHookRight.setStrokeWidth(HOOK_WIDTH);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (circleFlag) {
            drawStart(canvas);

        } else {
            drawOk(canvas);
        }
    }

    /**
     * 绘制开始动画
     */
    private void drawStart(Canvas canvas) {
//        if(mStart_Speed == 360){
//            mStart_Speed =0;
//            mTail_Speed = 0;
//            delteTime = 0;
//        }
        canvas.drawArc(drawArc,mTail_Speed + OFFSET,mStart_Speed,false,mStart_Circle);
        mStart_Speed = calculateGrade(true);
        mTail_Speed = calculateGrade(false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 0;
                mHandler.sendMessage(message);
            }
        }, 20);
    }

    /**
     * 完成动画
     */
    private void drawOk(Canvas canvas) {
        //绘制外层圆环动画
        canvas.drawCircle(mWidth / 2, mHeight / 2, mRadioOutside, mCircleOutside);
        canvas.drawCircle(mWidth / 2, mHeight / 2, mRadioInside, mCircleInside);
        if (!circleOkFlag) {
            mRadioInside -= 4.0;
            if (mRadioInside >= 0) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 0;
                        mHandler.sendMessage(message);
                    }
                }, 20);
            } else {
                circleOkFlag = true;
                Message message = new Message();
                message.what = 0;
                mHandler.sendMessage(message);
            }
        } else {//绘制内部对号动画
            float[] point1 = getLeftHookPoint(mWidth / 2 - LEFTPOINT);
            float[] point2 = getLeftHookPoint(leftPointTemp);
            float[] point3 = getRightHookPoint(mWidth / 2 - LEFTPOINT1 - 3);
            float[] point4 = getRightHookPoint(rightPointTemp);
            canvas.drawLine(point1[0], point1[1], point2[0], point2[1], mHookLeft);
            canvas.drawLine(point3[0], point3[1], point4[0], point4[1], mHookRight);
            if (leftPointTemp <= mWidth / 2 - LEFTPOINT1) {
                leftPointTemp += 4;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 0;
                        mHandler.sendMessage(message);
                    }
                }, 20);
            } else {
                if (rightPointTemp <= mWidth / 2 + RIGHTPOINT) {
                    rightPointTemp += 4;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = 0;
                            mHandler.sendMessage(message);
                        }
                    }, 20);
                }
            }

        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = 0;
        int height = 0;
        mWidth = getRight() - getLeft();
        mHeight = getBottom() - getTop();
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            int desired = (int) (getPaddingLeft() + getPaddingRight());
            width = desired;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            int desired = (int) (getPaddingTop() + getPaddingBottom());
            height = desired;
        }
        leftPointTemp = mWidth / 2 - LEFTPOINT;
        rightPointTemp = mWidth / 2 - LEFTPOINT1 - 3;
        drawArc = new RectF();
        drawArc.left = mWidth / 2 - RADIOOUT;
        drawArc.top = mHeight / 2 - RADIOOUT;
        drawArc.right = mWidth /2 + RADIOOUT;
        drawArc.bottom = mHeight / 2  + RADIOOUT;

        setMeasuredDimension(width, height);
    }

    /**
     * 计算左侧对号相对坐标
     */
    private float[] getLeftHookPoint(float x) {
        float[] point = new float[2];
        float y = (mHeight - mWidth) / 2 + LEFTPOINT + x;
        point[0] = x;
        point[1] = y;
        return point;
    }

    /**
     * 计算右侧对号相对坐标
     */
    private float[] getRightHookPoint(float x) {
        float[] point = new float[2];
        float y = (mHeight + mWidth) / 2 + LEFTPOINT - LEFTPOINT1 * 2 - x;
        point[0] = x;
        point[1] = y;
        return point;
    }

    public int calculateGrade(boolean isHead) {
        int speed = 0;
        if (isHead) {
            if(mStart_Speed + mTail_Speed>=360){
                speed_change_flag = true;
                HEAD_ACCELERATION = -10;
                delteTime = 50;
            }
            if(mStart_Speed <= 0){
                speed_change_flag = false;
                HEAD_ACCELERATION = 10;
            }
            if(speed_change_flag){
                mStart_Speed = 360 + HEAD_ACCELERATION * delteTime / 1000;
            }else{
                mStart_Speed = HEAD_ACCELERATION * delteTime / 1000;
            }
            speed = mStart_Speed;

        } else {
            mTail_Speed = TAIL_ACCELERATION * delteTime / 1000;
            speed = mTail_Speed;
        }
        delteTime += 500;
        return speed;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        circleFlag = false;
        return true;
    }
}