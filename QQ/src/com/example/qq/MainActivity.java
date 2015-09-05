package com.example.qq;

import com.m.dialog.Dialog;
import com.m.dialog.MyLinLayout;
import com.nineoldandroids.view.ViewHelper;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import util.Utils;

public class MainActivity extends Activity {
	Dialog dia;
	MyLinLayout lin;
	private ListView list_left;
	private ListView list_main;
	private ImageView image;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		dia = (Dialog) findViewById(R.id.dra);
		dia.setOnDrageChangeListener(new MyDra());
		image = (ImageView) findViewById(R.id.main_image);
		list_left = (ListView) findViewById(R.id.list);
		list_main = (ListView) findViewById(R.id.main_left);
		lin = (MyLinLayout) findViewById(R.id.lin);
		getList();
		lin.setDialog(dia);
	}
	
	public void getList(){
		String[] str = new String[100];
		String[] str2 = new String[26];
		for(int i=0;i<100;i++){
			str[i] = i+"";
			if(i<26){
				char c = (char) (i+65);
				str2[i] = c+"";
			}
		}
		list_main.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_expandable_list_item_1, str){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				View view =  super.getView(position, convertView, parent);
				TextView text = (TextView) view;
				text.setGravity(Gravity.CENTER_VERTICAL);
				text.setTextColor(Color.BLACK);
				return view;
			}
		});
		
		list_left.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_expandable_list_item_1, str2){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				View view =  super.getView(position, convertView, parent);
				TextView text = (TextView) view;
				text.setTextColor(Color.WHITE);
				return view;
			}
		});
	}
	
	class MyDra implements com.m.utils.OnDrageChange.OnDrageChangeListener{

		@Override
		public void OnClose() {
			// TODO Auto-generated method stub
			Utils.showToast(MainActivity.this, "Close");
			ObjectAnimator anim = ObjectAnimator.ofFloat(image, "translationX", 10.0f);
			anim.setInterpolator(new CycleInterpolator(3));
			anim.setDuration(500);
			anim.start();
		}

		@Override
		public void OnOpen() {
			// TODO Auto-generated method stub
			Utils.showToast(MainActivity.this, "Open");
		}

		@Override
		public void OnDrage(float faction) {
			// TODO Auto-generated method stub
			ViewHelper.setAlpha(image, 1-faction);
		}
		
	}
}
























