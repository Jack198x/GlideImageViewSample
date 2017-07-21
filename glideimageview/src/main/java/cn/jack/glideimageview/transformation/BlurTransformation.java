package cn.jack.glideimageview.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.renderscript.RSRuntimeException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.util.Synthetic;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by Jack on 2016/9/21.
 */

public class BlurTransformation extends BitmapTransformation {

    private static final List<String> MODELS_REQUIRING_BITMAP_LOCK =
            Arrays.asList(
                    "XT1097",
                    "XT1085");

    private static final Lock BITMAP_DRAWABLE_LOCK =
            MODELS_REQUIRING_BITMAP_LOCK.contains(Build.MODEL)
                    && Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1
                    ? new ReentrantLock() : new NoLock();

    private static final int VERSION = 1;
    private static final String ID = "cn.jack.glideimageview.transformation.BlurTransformation." + VERSION;
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    private static int MAX_RADIUS = 25;
    private static int DEFAULT_DOWN_SAMPLING = 1;

    private Context mContext;

    private int mRadius;
    private int mSampling;

    public BlurTransformation(Context context) {
        this(context, MAX_RADIUS, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(Context context, int radius) {
        this(context, radius, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(Context context, int radius, int sampling) {
        mContext = context.getApplicationContext();
        mRadius = radius;
        mSampling = sampling;
    }


    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap inBitmap, int outWidth, int outHeight) {


        int scaledWidth = inBitmap.getWidth() / mSampling;
        int scaledHeight = inBitmap.getHeight() / mSampling;


        // Alpha is required for this transformation.
        Bitmap toTransform = getAlphaSafeBitmap(pool, inBitmap);

        Bitmap result = pool.get(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
        result.setHasAlpha(true);

        BITMAP_DRAWABLE_LOCK.lock();
        try {

            Canvas canvas = new Canvas(result);
            canvas.scale(1 / (float) mSampling, 1 / (float) mSampling);
            Paint paint = new Paint();
            paint.setFlags(Paint.FILTER_BITMAP_FLAG);
            canvas.drawBitmap(toTransform, 0, 0, paint);
            clear(canvas);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Log.e("Build.VERSION.SDK_INT", Build.VERSION.SDK_INT + "");
                try {
                    result = RSBlur.blur(mContext, result, mRadius);
                } catch (RSRuntimeException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    result = FastBlur.blur(result, mRadius, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } finally {
            BITMAP_DRAWABLE_LOCK.unlock();
        }

        if (!toTransform.equals(inBitmap)) {
            pool.put(toTransform);
        }
        return result;
    }


    private static void clear(Canvas canvas) {
        canvas.setBitmap(null);
    }


    private static Bitmap getAlphaSafeBitmap(@NonNull BitmapPool pool, @NonNull Bitmap maybeAlphaSafe) {
        if (Bitmap.Config.ARGB_8888.equals(maybeAlphaSafe.getConfig())) {
            return maybeAlphaSafe;
        }

        Bitmap argbBitmap = pool.get(maybeAlphaSafe.getWidth(), maybeAlphaSafe.getHeight(),
                Bitmap.Config.ARGB_8888);
        new Canvas(argbBitmap).drawBitmap(maybeAlphaSafe, 0 /*left*/, 0 /*top*/, null /*pain*/);

        // We now own this Bitmap. It's our responsibility to replace it in the pool outside this method
        // when we're finished with it.
        return argbBitmap;
    }


    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }


    private static final class NoLock implements Lock {

        @Synthetic
        NoLock() {
        }

        @Override
        public void lock() {
            // do nothing
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            // do nothing
        }

        @Override
        public boolean tryLock() {
            return true;
        }

        @Override
        public boolean tryLock(long time, @NonNull TimeUnit unit) throws InterruptedException {
            return true;
        }

        @Override
        public void unlock() {
            // do nothing
        }

        @NonNull
        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException("Should not be called");
        }
    }
}
