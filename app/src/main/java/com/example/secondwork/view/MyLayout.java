package com.example.secondwork.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.secondwork.R;
import com.example.secondwork.util.DegreesUtil;

public class MyLayout extends ConstraintLayout implements View.OnTouchListener , View.OnClickListener {
    private ImageView delete_image;
    private ImageView change_image;
    private ImageView ticket_image;
    private float last_x;
    private float last_y;
    private int left;
    private int top;
    private int right;
    private int bottom;
    private boolean isFirst = false;
    public MyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        delete_image = findViewById(R.id.delete);
        change_image = findViewById(R.id.change);
        ticket_image = findViewById(R.id.ticket_image_view);
        change_image.setOnTouchListener(this);
        delete_image.setOnClickListener(this);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.left = getLeft();
        this.top = getTop();
        this.right = getRight();
        this.bottom = getBottom();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId()==R.id.change){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    if(isFirst==false) {
                        last_x = event.getRawX();
                        last_y = event.getRawY();
                    }
                    isFirst = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dx = event.getRawX() - last_x;
                    float dy = event.getRawY() - last_y;
                    int x = (left+right)/2;
                    int y = (top+bottom)/2;
                    float degrees = (float) DegreesUtil.getActionDegrees(x,y,event.getRawX(),event.getRawY(),last_x,last_y);
                    float scale = (event.getRawX()-(left+right)/2)/(last_x-(left+right)/2);
                    this.setScaleX(scale);
                    this.setScaleY(scale);
                    this.setRotation(-degrees);
//                    this.setTranslationX(dx);
//                    this.setTranslationY(dy);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                default:
                    break;
            }
            return true;
        }
       return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.delete:
                ticket_image.setImageBitmap(null);
                delete_image.setVisibility(View.INVISIBLE);
                change_image.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }
}
