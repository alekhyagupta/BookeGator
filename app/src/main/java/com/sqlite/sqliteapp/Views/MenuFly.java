package com.sqlite.sqliteapp.Views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.content.Intent;
import android.widget.Scroller;
import android.os.Handler;
import com.sqlite.sqliteapp.Main2Activity;
import com.sqlite.sqliteapp.MainActivity;

import java.util.logging.LogRecord;

public class MenuFly extends LinearLayout {
    private View menu;
    private View content;

    // Constants
    protected static final int menuMargin = 400;

    public enum MenuState {
        CLOSED, OPEN, CLOSING, OPENING
    }

    // Position information attributes
    protected int currentContentOffset = 0;
    protected MenuState menuCurrentState = MenuState.CLOSED;

    public MenuFly(Context context, AttributeSet attributeSet, int defStyle){
        super(context,attributeSet,defStyle);
    }

    public MenuFly(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    public MenuFly(Context context){
        super(context);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        this.menu = this.getChildAt(0);
        this.content = this.getChildAt(1);

        this.menu.setVisibility(View.GONE);
    }

    private int getMenuHeight(){
        return this.menu.getLayoutParams().height;
    }

    private void getMenuDimensions(){
        this.content.getLayoutParams().height = this.getHeight();
        this.content.getLayoutParams().width = this.getWidth();

        this.menu.getLayoutParams().width = this.getWidth();
        this.menu.getLayoutParams().height = this.getHeight() - menuMargin;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,int bottom) {
        if (changed)
            this.getMenuDimensions();

        this.menu.layout(left, top, right, bottom - menuMargin);

        this.content.layout(left, top + this.currentContentOffset, right, bottom + this.currentContentOffset);

    }

    protected Scroller menuScroller = new Scroller(this.getContext(), new smoothInterpolator());
    protected Runnable menuRunnable = new AnimationRunnable();
    protected Handler menuHandler = new Handler();

    private static final int menuAnimationCount = 1000;
    private static final int menuPollarInterval = 16;

    public void toggleMenu() {
        switch (this.menuCurrentState) {

            case CLOSED:
                this.menuCurrentState = MenuState.OPENING;
                this.menu.setVisibility(View.VISIBLE);
                this.menuScroller.startScroll(0,0,0, this.getMenuHeight(), menuAnimationCount);
                //this.currentContentOffset = this.getMenuHeight();
                //this.content.offsetTopAndBottom(currentContentOffset);
                //this.menuCurrentState = MenuState.OPEN;
                break;
            case OPEN:
                this.menuCurrentState = MenuState.CLOSING;
                this.menuScroller.startScroll(0, this.currentContentOffset, 0, -this.currentContentOffset, menuAnimationCount);
                //this.content.offsetTopAndBottom(-currentContentOffset);
                //this.currentContentOffset = 0;
                //this.menuCurrentState = MenuState.CLOSED;
                //this.menu.setVisibility(View.GONE);
                break;
            default:
                return;
        }
        //this.invalidate();
        this.menuHandler.postDelayed(this.menuRunnable, menuPollarInterval);
    }

    public class smoothInterpolator implements Interpolator{

        @Override
        public float getInterpolation(float input) {
            return (float)Math.pow(input-1,5)+1;
        }
    }

    protected class AnimationRunnable implements Runnable{

        public void run(){
            boolean isAnimationOngoing = MenuFly.this.menuScroller.computeScrollOffset();
            MenuFly.this.adjustContextPosition(isAnimationOngoing);
        }
    }

    private void adjustContextPosition(boolean isAnimationOngoing) {
        int scrollerOffest = this.menuScroller.getCurrY();

        this.content.offsetTopAndBottom(scrollerOffest-this.currentContentOffset);
        this.currentContentOffset = scrollerOffest;

        this.invalidate();

        if(isAnimationOngoing)
            this.menuHandler.postDelayed(this.menuRunnable, menuPollarInterval);
        else
            onThisMenuTransitionComplete();
    }

    private void onThisMenuTransitionComplete() {
        switch (this.menuCurrentState) {

            case OPENING:
                this.menuCurrentState = MenuState.OPEN;
                break;
            case CLOSING:
                this.menuCurrentState = MenuState.CLOSED;
                this.menu.setVisibility(View.GONE);
                break;
            default:
                return;
        }
    }

}
