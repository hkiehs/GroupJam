package com.noextent.groupjam.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

public class GifDecoderView extends ImageView {

    private boolean mIsPlayingGif = false;

    private GifDecoder mGifDecoder;

    private Bitmap mTmpBitmap;

    final Handler mHandler = new Handler();

    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            if (mTmpBitmap != null && !mTmpBitmap.isRecycled()) {
                GifDecoderView.this.setImageBitmap(mTmpBitmap);
            }
        }
    };

    public GifDecoderView(Context context) {
        super(context);
    }

    public void playGif(GifDecoder gifDecoder) {
        mGifDecoder = gifDecoder;
        //mGifDecoder.read(stream);

        mIsPlayingGif = true;

        new Thread(new Runnable() {
            public void run() {
                final int n = mGifDecoder.getFrameCount();
                final int ntimes = mGifDecoder.getLoopCount();
                int repetitionCounter = 0;
                do {
                    for (int i = 0; i < n; i++) {
                        mTmpBitmap = mGifDecoder.getFrame(i);
                        int t = mGifDecoder.getDelay(i);
                        mHandler.post(mUpdateResults);
                        try {
                            Thread.sleep(t);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (ntimes != 0) {
                        repetitionCounter++;
                    }
                } while (mIsPlayingGif && (repetitionCounter <= ntimes));
            }
        }).start();
    }

    public void stopRendering() {
        mIsPlayingGif = true;
    }
}
