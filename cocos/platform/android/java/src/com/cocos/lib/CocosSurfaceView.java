/****************************************************************************
 * Copyright (c) 2020 Xiamen Yaji Software Co., Ltd.
 *
 * http://www.cocos.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ****************************************************************************/

package com.cocos.lib;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

public class CocosSurfaceView extends SurfaceView {
    private CocosTouchHandler mTouchHandler;
    private FrameLayout mLayoutParent = null;
    private boolean mNeedRebuild = false;

    public CocosSurfaceView(Context context) {
        super(context);
        mTouchHandler = new CocosTouchHandler();
    }

    private native void nativeOnSizeChanged(final int width, final int height);

    public void enableRebuildOnSizeChanged() {
        mNeedRebuild = true;
    }

    public void disableRebuildOnSizeChanged() {
        mNeedRebuild = false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        CocosHelper.runOnGameThread(new Runnable() {
            @Override
            public void run() {
                nativeOnSizeChanged(w, h);
            }
        });

        if (!mNeedRebuild) {
            return;
        }

        this.post(new Runnable() {
            @Override
            public void run() {
                mLayoutParent = (FrameLayout) CocosSurfaceView.this.getParent();
                mLayoutParent.removeView(CocosSurfaceView.this);
            }
        });
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mLayoutParent.post(new Runnable() {
            @Override
            public void run() {
                mLayoutParent.addView(CocosSurfaceView.this);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mTouchHandler.onTouchEvent(event);
    }
}
