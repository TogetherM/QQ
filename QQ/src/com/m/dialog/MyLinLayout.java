package com.m.dialog;

import com.m.dialog.Dialog.Status;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class MyLinLayout extends LinearLayout{
	Dialog dia;

	public MyLinLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyLinLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void setDialog(Dialog dia){
		this.dia = dia;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(dia.getSta()==Status.Close){
			return super.onInterceptTouchEvent(ev);
		}else{
			return true;
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(dia.getSta()==Status.Close){
			return super.onTouchEvent(event);
		}else{
			if(event.getAction()==MotionEvent.ACTION_UP){
				dia.close(true);
			}
			return true;
		}
	}
}





























