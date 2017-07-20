package cn.jack.glideimageview.transformation;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * Created by Jack on 2017/7/20.
 */

public class RoundedTransformation extends BitmapTransformation {

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
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return RoundedDrawable.fromBitmap(toTransform)
                .setScaleType(mScaleType)
                .setCornerRadius(mCornerRadii[0], mCornerRadii[1], mCornerRadii[2], mCornerRadii[3])
                .setBorderWidth(mBorderWidth)
                .setBorderColor(mBorderColor)
                .setOval(mOval)
                .toBitmap();
    }


    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }
}
