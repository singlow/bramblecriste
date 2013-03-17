package com.iakob.bramblecriste;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.Button;

public class ResetButton extends Button{
    private Paint paint = new Paint();
    private int gap;
    private int canvasWidth;
    private int canvasHeight;
    private Rect block;
    private int resetTrigger = 5;
    private int resetCount;
    private int resetColor;
    
    public ResetButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        paint.setColor(Color.RED);
        paint.setStyle(Style.FILL_AND_STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	super.onSizeChanged(w, h, oldw, oldh);
    	canvasWidth = w;
    	canvasHeight = h;
    	calculateBlock();
    }

	private void calculateBlock() {
		gap = canvasHeight / 100;
		block = new Rect(
				gap, 
				gap, 
				canvasWidth - 2*gap, 
				(canvasHeight - (2*resetTrigger+1)*gap)/resetTrigger/2);
	}

    @Override
    public void onDraw(Canvas canvas) {
    	int start = resetTrigger - resetCount;
    	for (int i = start; i < start+resetCount*2; i++) {
    		block.offsetTo(gap, (canvasHeight-gap)*i/resetTrigger/2 + gap);
    		canvas.drawRect(block, paint);
		}
    }

	public int getResetTrigger() {
		return resetTrigger;
	}

	public void setResetTrigger(int resetTrigger) {
		this.resetTrigger = resetTrigger;
		calculateBlock();
	}

	public int getResetCount() {
		return resetCount;
	}

	public boolean increment() {
		if (resetCount < resetTrigger)
		{
			resetCount++;
			invalidate();
		}
		return resetCount >= resetTrigger;
	}
	
	public void clear() {
		invalidate();
		resetCount = 0;
	}

	public int getResetColor() {
		return resetColor;
	}

	public void setResetColor(int resetColor) {
		this.resetColor = resetColor;
	}
}
