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

	//��ʼ״̬
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
		//��ʼ��������ͨ����̬����
		help = ViewDragHelper.create(this, call);
	}
	
	ViewDragHelper.Callback call = new Callback() {
		//child:��ǰ����ק��view
		//pointerId�����ֶ�㴥����ID
		//���Բ���view
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			// TODO Auto-generated method stub
			return true;
		}
		
		//���ݽ���ֵ�޸Ľ�Ҫ�ƶ���λ�ã���ʱû�з����������ƶ�
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			/*
			 * child:��ǰ��ק��view
			 * left���µ�λ��
			 * dx:ƫ����
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
		
		//��Viewλ�øı�ʱ������Ҫ������(����״̬�����涯�����ػؽ���)
		@Override
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
			// TODO Auto-generated method stub
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			
			int newLeft = left;
			if(changedView==mLeft){
				//��ʼ����߾�
				newLeft = mMain.getLeft()+dx;
				//��������
				newLeft = fix(newLeft);
				//��ߵ�ziView��λ��
				mLeft.layout(0, 0, width, height);
				//Mian View��λ��
				mMain.layout(newLeft, 0, newLeft+width, height);
			}
			
			//�Զ�����������
			dispactChange(newLeft);
			
			//Ϊ�˼��ݵͰ汾��ÿ��λ�øı���ػ����
			invalidate(); 
		}
		
		//view����ɹ�ʱ
		@Override
		public void onViewCaptured(View capturedChild, int activePointerId) {
			// TODO Auto-generated method stub
			super.onViewCaptured(capturedChild, activePointerId);
		}
		
		//��view���ͷ�ʱ��ִ�еĶ���
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			// TODO Auto-generated method stub
			super.onViewReleased(releasedChild, xvel, yvel);
			/*
			 * releasedChild:���ͷ�ʱ����View
			 * xvel:����ʱΪ+
			 * yvel������ʱΪ+
			 */
			
			if(xvel==0&&mMain.getLeft()>=mWidth/2){
				open(true);
			}else if(xvel>0){
				open(true);
			}else{
				close(true);
			}
		}
		
		//view������ƶ���Χ��������ק�ķ�Χ�������������ƣ����Ǿ����˶�����ִ���ٶ�
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
		 * ����״̬��ִ�лص�
		 */
		Status lastStatus = sta;	
		sta = updateStatus(fraction);
		if(lastStatus!=sta){
			//״̬�����ı�
			if(sta == Status.Close){
				//ִ�йر�
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
		//���涯��
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
		 * X��Y������Ŷ���
		 */
		ViewHelper.setScaleX(mLeft, evaluate(fraction, 0.5f, 1.0f));
		ViewHelper.setScaleY(mLeft, evaluate(fraction, 0.5f, 1.0f));
		
		/*
		 * ƽ�ƶ���
		 */
		ViewHelper.setTranslationX(mLeft, evaluate(fraction, -mWidth/2, 0));
		/*
		 * ͸������
		 */
		ViewHelper.setAlpha(mLeft, evaluate(fraction, 0f, 1.0f));
		/*
		 * ������ƽ�ƶ���
		 */
		ViewHelper.setScaleX(mMain, evaluate(fraction, 1.0f, 0.8f));
		ViewHelper.setScaleY(mMain, evaluate(fraction, 1.0f, 0.7f));
		/*
		 * ������������ɫ�ı仯
		 */
//		ArgbEvaluator A = new ArgbEvaluator();
//		getBackground().setColorFilter((Integer) A.evaluate(fraction, Color.BLACK, Color.bLA), android.graphics.PorterDuff.Mode.SRC_OVER);
	}
	
	/*
	 *��ֵ�� 
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
	
	//�����仯��
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

	//���ݴ����¼�
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
			throw new IllegalArgumentException("ViewGroup ����������View");
		}
		
		if(!(getChildAt(0) instanceof ViewGroup && getChildAt(1) instanceof ViewGroup)){
			throw new IllegalArgumentException("��View������ViewGroup����View");
		}
		
		mLeft = (ViewGroup) getChildAt(0);
		mMain = (ViewGroup) getChildAt(1);
	}
}











