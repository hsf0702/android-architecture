package com.klfront.control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.klfront.utils.R;


public class ProgressView extends View {
	
	private Paint mPaint;//画笔

	int width,height;
	
	Context context;
	
	int progress=0;

	public enum Direction{
		Toptobottm(0), Bottomtotop(1), non(2);

		int value;

		Direction(int value){
			this.value = value;
		}

		static Direction getDirection(int value){
			if(value==0){
				return Toptobottm;
			}else if(value==1){
				return Bottomtotop;
			}else{
				return non;
			}
		}

		public int getValue(){
			return this.value;
		}
	}

	Direction direction = Direction.Toptobottm;
	
	public ProgressView(Context context)
	{
		this(context, null);
	}

	public ProgressView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context=context;
		mPaint=new Paint();

		TypedArray typeedarr = context.obtainStyledAttributes(attrs, R.styleable.ProgressView);
		int directionValue = typeedarr.getInteger(R.styleable.ProgressView_direction, 0);
		direction = Direction.getDirection(directionValue);

		typeedarr.recycle();
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
        mPaint.setAntiAlias(true); // 消除锯齿
        mPaint.setStyle(Paint.Style.FILL); 

		if(direction== Direction.Bottomtotop){
			mPaint.setColor(Color.parseColor("#70000000"));//半透明
			canvas.drawRect(0, 0, getWidth(), getHeight() - getHeight() * progress / 100, mPaint);

			mPaint.setColor(Color.parseColor("#00000000"));//全透明
			canvas.drawRect(0, getHeight() - getHeight() * progress / 100, getWidth(), getHeight(), mPaint);
		}else if(direction== Direction.Toptobottm){
			mPaint.setColor(Color.parseColor("#70000000"));//半透明
			canvas.drawRect(0, getHeight() * progress / 100, getWidth(), getHeight(), mPaint);

			mPaint.setColor(Color.parseColor("#00000000"));//全透明
			canvas.drawRect(0, 0, getWidth(), getHeight() * progress / 100, mPaint);
		}else if(direction== Direction.non){
			//不画蒙版
		}

        mPaint.setTextSize(30);
        mPaint.setColor(Color.parseColor("#FFFFFF"));
		mPaint.setStrokeWidth(2);
		Rect rect=new Rect();
		mPaint.getTextBounds("100%", 0, "100%".length(), rect);//确定文字的宽度
		canvas.drawText(progress+"%", getWidth()/2-rect.width()/2,getHeight()/2, mPaint);
	}
	
	public void setProgress(int progress){
		this.progress=progress;
		postInvalidate();
	}
 }  

