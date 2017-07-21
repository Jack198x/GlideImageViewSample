package cn.jack.glideimageview.transformation;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;

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
 * Created by Jack on 2017/7/20.
 */

public class RoundedTransformation extends BitmapTransformation {

    private static final int PAINT_FLAGS = Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG;
    private static final Paint DEFAULT_PAINT = new Paint(PAINT_FLAGS);

    private static final List<String> MODELS_REQUIRING_BITMAP_LOCK =
            Arrays.asList(
                    "XT1097",
                    "XT1085");

    private static final Lock BITMAP_DRAWABLE_LOCK =
            MODELS_REQUIRING_BITMAP_LOCK.contains(Build.MODEL)
                    && Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1
                    ? new ReentrantLock() : new NoLock();

    private static final int VERSION = 1;
    private static final String ID = "cn.jack.glideimageview.transformation.BlurTransformation.RoundedTransformation" + VERSION;
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);
    //private final Resources mResources;
    private final DisplayMetrics mDisplayMetrics;

    private float[] mCornerRadii = new float[]{0, 0, 0, 0};

    private boolean mOval = false;
    private float mBorderWidth = 0;
    private ColorStateList mBorderColor =
            ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
    private ImageView.ScaleType mScaleType = ImageView.ScaleType.FIT_CENTER;


    public RoundedTransformation() {
        mDisplayMetrics = Resources.getSystem().getDisplayMetrics();
    }


    public void setScaleType(ImageView.ScaleType scaleType) {
        mScaleType = scaleType;
    }

    /**
     * Set corner radius for all corners in px.
     *
     * @param radius the radius in px
     */
    public void setCornerRadius(float radius) {
        mCornerRadii[Corner.TOP_LEFT] = radius;
        mCornerRadii[Corner.TOP_RIGHT] = radius;
        mCornerRadii[Corner.BOTTOM_RIGHT] = radius;
        mCornerRadii[Corner.BOTTOM_LEFT] = radius;
    }

    /**
     * Set corner radius for a specific corner in px.
     *
     * @param corner the corner to set.
     * @param radius the radius in px.
     */
    public void setCornerRadius(@Corner int corner, float radius) {
        mCornerRadii[corner] = radius;
    }

    /**
     * Set corner radius for all corners in density independent pixels.
     *
     * @param radius the radius in density independent pixels.
     */
    public void setCornerRadiusDp(float radius) {
        setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radius, mDisplayMetrics));
    }

    /**
     * Set corner radius for a specific corner in density independent pixels.
     *
     * @param corner the corner to set
     * @param radius the radius in density independent pixels.
     */
    public void setCornerRadiusDp(int corner, float radius) {
        setCornerRadius(corner, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radius, mDisplayMetrics));
    }

    /**
     * Set the border width in pixels.
     *
     * @param width border width in pixels.
     */
    public void setBorderWidth(float width) {
        mBorderWidth = width;
    }

    /**
     * Set the border width in density independent pixels.
     *
     * @param width border width in density independent pixels.
     */
    public void setBorderWidthDp(float width) {
        mBorderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, mDisplayMetrics);
    }

    /**
     * Set the border color.
     *
     * @param color the color to set.
     */
    public void setBorderColor(int color) {
        mBorderColor = ColorStateList.valueOf(color);
    }

    /**
     * Set the border color as a {@link ColorStateList}.
     *
     * @param colors the {@link ColorStateList} to set.
     */
    public void setBorderColor(ColorStateList colors) {
        mBorderColor = colors;
    }

    /**
     * Sets whether the image should be oval or not.
     *
     * @param oval if the image should be oval.
     */
    public void oval(boolean oval) {
        mOval = oval;
    }


    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap inBitmap, int outWidth, int outHeight) {


        int srcWidth = inBitmap.getWidth();
        int srcHeight = inBitmap.getHeight();


        // Alpha is required for this transformation.
        Bitmap toTransform = RoundedDrawable.fromBitmap(inBitmap)
                .setScaleType(mScaleType)
                .setCornerRadius(mCornerRadii[0], mCornerRadii[1], mCornerRadii[2], mCornerRadii[3])
                .setBorderWidth(mBorderWidth)
                .setBorderColor(mBorderColor)
                .setOval(mOval)
                .toBitmap();

        Bitmap result = pool.get(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        result.setHasAlpha(true);

        BITMAP_DRAWABLE_LOCK.lock();
        try {
            Canvas canvas = new Canvas(result);
            canvas.drawBitmap(toTransform, new Matrix(), DEFAULT_PAINT);
            clear(canvas);
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
