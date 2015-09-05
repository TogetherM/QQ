package com.m.dialog;

import com.m.utils.OnDrageChange;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.view.ViewHelper;

import android.animation.FloatEvaluator;
import android.animation.TypeEvaluator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.AvoidXfermode.Mode;
import android.graphics.Color;
import android.provider.Telephony.Mms;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

@SuppressLint("ClickableViewAccessibility")
public class Dialog extends FrameLayout{

	private ViewGroup mLeft;
	private ViewGroup mMain;

	private ViewDragHelper help;
	
	private OnDrageChange.OnDrageChangeListener dra;

	//初始状态
	private Status sta = Status.Close;
	
	public enum Status{
		Close,Open,Drage;
	}
	
	public Status getSta() {
		return sta;
	}

	public void setSta(Status sta) {
		this.sta = sta;
	}

	public void setOnDrageChangeListener(OnDrageChange.OnDrageChangeListener dra){
		this.dra = dra;
	}

	public Dialog(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		// TODO Auto-generated constructor stub
	}

	public Dialog(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}


	public Dialog(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		//初始化操作，通过静态方法
		help = ViewDragHelper.create(this, call);
	}
	
	ViewDragHelper.Callback call = new Callback() {
		//child:当前被拖拽的view
		//pointerId：区分多点触摸的ID
		//尝试捕获view
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			// TODO Auto-generated method stub
			return true;
		}
		
		//根据建议值修改将要移动的位置，此时没有发生真正的移动
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			/*
			 * child:当前拖拽的view
			 * left：新的位置
			 * dx:偏移量
			 * left = child.getLeft+dx
			 */
			if(child==mMain){
				left = fix(left);
			}
			return left;
		} 
		
		@Override
		public void onViewDragStateChanged(int state) {
			// TODO Auto-generated method stub
			super.onViewDragStateChanged(state);
		}
		
		//当View位置改变时，处理要做的事(更新状态，便随动画，重回界面)
		@Override
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
			// TODO Auto-generated method stub
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			
			int newLeft = left;
			if(changedView==mLeft){
				//初始化左边距
				newLeft = mMain.getLeft()+dx;
				//进行修正
				newLeft = fix(newLeft);
				//左边的ziView的位置
				mLeft.layout(0, 0, width, height);
				//Mian View的位置
				mMain.layout(newLeft, 0, newLeft+width, height);
			}
			
			//对动画进行修正
			dispactChange(newLeft);
			
			//为了兼容低版本，每次位置改编后重绘界面
			invalidate(); 
		}
		
		//view捕获成功时
		@Override
		public void onViewCaptured(View capturedChild, int activePointerId) {
			// TODO Auto-generated method stub
			super.onViewCaptured(capturedChild, activePointerId);
		}
		
		//当view被释放时，执行的动作
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			// TODO Auto-generated method stub
			super.onViewReleased(releasedChild, xvel, yvel);
			/*
			 * releasedChild:被释放时的子View
			 * xvel:向右时为+
			 * yvel：向下时为+
			 */
			
			if(xvel==0&&mMain.getLeft()>=mWidth/2){
				open(true);
			}else if(xvel>0){
				open(true);
			}else{
				close(true);
			}
		}
		
		//view横向的移动范围，不对拖拽的范围进行真正的限制，而是决定了动画的执行速度
		@Override
		public int getViewHorizontalDragRange(View child) {
			// TODO Auto-generated method stub
			return mWidth;
		}

		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			// TODO Auto-generated method stub
			return super.clampViewPositionVertical(child, top, dy);
		};
	};
	
	 void close(boolean isOK) {
		if(isOK){
			help.smoothSlideViewTo(mMain, 0, 0);
			ViewCompat.postInvalidateOnAnimation(this);
		}else{
			mMain.layout(0, 0, width, height);
		}
	}
	
	@SuppressWarnings("deprecation")
	protected void dispactChange(int newLeft) {
		float fraction = newLeft*1.0f/mWidth;
		
		/*
		 * 更新状态，执行回掉
		 */
		Status lastStatus = sta;	
		sta = updateStatus(fraction);
		if(lastStatus!=sta){
			//状态发生改变
			if(sta == Status.Close){
				//执行关闭
				if(dra!=null){
					dra.OnClose();
				}
			}else if(sta==Status.Open){
				if(dra!=null){
					dra.OnOpen();
				}
			}
		}
		
		if(dra!=null){
			dra.OnDrage(fraction);
		}
		//伴随动画
		aminView(fraction);
	}
	
	

	private Status updateStatus(float fraction) {
		// TODO Auto-generated method stub
		if(fraction==0){
			return Status.Close;
		}else if(fraction==1){
			return Status.Open;
		}
		
		return Status.Drage;
	}

	private void aminView(float fraction) {
		/*
		 * X，Y轴的缩放动画
		 */
		ViewHelper.setScaleX(mLeft, evaluate(fraction, 0.5f, 1.0f));
		ViewHelper.setScaleY(mLeft, evaluate(fraction, 0.5f, 1.0f));
		
		/*
		 * 平移动画
		 */
		ViewHelper.setTranslationX(mLeft, evaluate(fraction, -mWidth/2, 0));
		/*
		 * 透明动画
		 */
		ViewHelper.setAlpha(mLeft, evaluate(fraction, 0f, 1.0f));
		/*
		 * 主面板的平移动画
		 */
		ViewHelper.setScaleX(mMain, evaluate(fraction, 1.0f, 0.8f));
		ViewHelper.setScaleY(mMain, evaluate(fraction, 1.0f, 0.7f));
		/*
		 * 背景动画，颜色的变化
		 */
//		ArgbEvaluator A = new ArgbEvaluator();
//		getBackground().setColorFilter((Integer) A.evaluate(fraction, Color.BLACK, Color.bLA), android.graphics.PorterDuff.Mode.SRC_OVER);
	}
	
	/*
	 *估值器 
	 */
	 public Float evaluate(float fraction, Number startValue, Number endValue) {
	        float startFloat = startValue.floatValue();
	        return startFloat + fraction * (endValue.floatValue() - startFloat);
	    }

	public void open(boolean isOK){
		if(isOK){
			help.smoothSlideViewTo(mMain, mWidth, 0);
			ViewCompat.postInvalidateOnAnimation(this);
		}else{
			mMain.layout(mWidth,0,mWidth+width, height);
		}
	}
	
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		super.computeScroll();
		if(help.continueSettling(true)){
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}
	
	@Override
	public boolean dispatchDragEvent(DragEvent event) {
		// TODO Auto-generated method stub
		return super.dispatchDragEvent(event);
	}
	
	//修正变化量
			public int fix(int left){
				if(left<0){
					return 0;
				}else if(left>mWidth){
					return mWidth;
				}
				
				return left;
			}
	
	private int mWidth;
	private int width;
	private int height;
	
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = getMeasuredWidth();
		height = getMeasuredHeight();
		
		mWidth = (int) (width*0.6);
	};

	//传递触摸事件
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return help.shouldInterceptTouchEvent(ev);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			help.processTouchEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		
		if(getChildCount()<2){
			throw new IllegalArgumentException("ViewGroup 至少两个子View");
		}
		
		if(!(getChildAt(0) instanceof ViewGroup && getChildAt(1) instanceof ViewGroup)){
			throw new IllegalArgumentException("子View必须是ViewGroup的子View");
		}
		
		mLeft = (ViewGroup) getChildAt(0);
		mMain = (ViewGroup) getChildAt(1);
	}
}











