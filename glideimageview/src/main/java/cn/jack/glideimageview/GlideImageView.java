package cn.jack.glideimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

import cn.jack.glideimageview.transformation.BlurTransformation;
import cn.jack.glideimageview.transformation.RoundedTransformation;

/**
 * Created by Jack on 2017/7/20.
 */

public class GlideImageView extends ImageView {

    private RequestBuilder requestBuilder;

    private int holderResId = 0;
    private int errorResId = 0;
    private boolean noAnimation = false;
    private int transformationType = 0;
    private BitmapTransformation transformation = null;
    private int cornerRadius = 0;
    private int borderWidth = 0;
    private int borderColor = Color.parseColor("#FFFFFF");
    private int blurRadius = 1;
    private int blurSampling = 1;

    public GlideImageView(Context context) {
        super(context);
        init(context, null);
    }

    public GlideImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GlideImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GlideImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }


    private void init(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GlideImageView);
            try {
                if (typedArray.hasValue(R.styleable.GlideImageView_imageResource)) {
                    int resourceId = typedArray.getResourceId(R.styleable.GlideImageView_imageResource, 0);
                    requestBuilder = Glide.with(getContext()).load(resourceId);
                }
                if (typedArray.hasValue(R.styleable.GlideImageView_placeholderImageResource)) {
                    holderResId = typedArray.getResourceId(R.styleable.GlideImageView_placeholderImageResource, 0);
                }
                if (typedArray.hasValue(R.styleable.GlideImageView_errorImageResource)) {
                    errorResId = typedArray.getResourceId(R.styleable.GlideImageView_errorImageResource, 0);
                }
                if (typedArray.hasValue(R.styleable.GlideImageView_noAnimation)) {
                    noAnimation = typedArray.getBoolean(R.styleable.GlideImageView_noAnimation, false);
                }
                if (typedArray.hasValue(R.styleable.GlideImageView_transformation)) {
                    transformationType = typedArray.getInt(R.styleable.GlideImageView_transformation, 0);
                }
                if (transformationType == 0) {
                    //normal
                    transformation = null;
                }
                if (transformationType == 1) {
                    //round
                    if (typedArray.hasValue(R.styleable.GlideImageView_roundedCornerRadius)) {
                        cornerRadius = typedArray.getDimensionPixelSize(R.styleable.GlideImageView_roundedCornerRadius, 0);
                    }
                    if (typedArray.hasValue(R.styleable.GlideImageView_borderWidth)) {
                        borderWidth = typedArray.getDimensionPixelSize(R.styleable.GlideImageView_borderWidth, 0);
                    }
                    if (typedArray.hasValue(R.styleable.GlideImageView_borderColor)) {
                        borderColor = typedArray.getColor(R.styleable.GlideImageView_borderColor, Color.parseColor("#FFFFFF"));
                    }

                    RoundedTransformation roundedTransformation = new RoundedTransformation();
                    roundedTransformation.setBorderColor(borderColor);
                    roundedTransformation.setBorderWidth(borderWidth);
                    roundedTransformation.setCornerRadius(cornerRadius);
                    roundedTransformation.oval(false);
                    transformation = roundedTransformation;
                }
                if (transformationType == 2) {
                    //oval
                    if (typedArray.hasValue(R.styleable.GlideImageView_borderWidth)) {
                        borderWidth = typedArray.getDimensionPixelSize(R.styleable.GlideImageView_borderWidth, 0);
                    }
                    if (typedArray.hasValue(R.styleable.GlideImageView_borderColor)) {
                        borderColor = typedArray.getColor(R.styleable.GlideImageView_borderColor, Color.parseColor("#FFFFFF"));
                    }
                    RoundedTransformation roundedTransformation = new RoundedTransformation();
                    roundedTransformation.setBorderColor(borderColor);
                    roundedTransformation.setBorderWidth(borderWidth);
                    roundedTransformation.oval(true);
                    transformation = roundedTransformation;
                }
                if (transformationType == 3) {
                    //blur
                    if (typedArray.hasValue(R.styleable.GlideImageView_blurRadius)) {
                        blurRadius = typedArray.getInt(R.styleable.GlideImageView_blurRadius, 1);
                    }
                    if (typedArray.hasValue(R.styleable.GlideImageView_blurSampling)) {
                        blurSampling = typedArray.getInt(R.styleable.GlideImageView_blurSampling, 1);
                    }
                    transformation = new BlurTransformation(getContext(), blurRadius, blurSampling);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                typedArray.recycle();
            }
        }


    }


    public void setImageUrl(String imageUrl) {
        requestBuilder = Glide.with(getContext()).load(imageUrl);
        into();
    }

    public void setImageResource(@DrawableRes int resId) {
        requestBuilder = Glide.with(getContext()).load(resId);
        into();
    }

    public void setImageFile(File file) {
        requestBuilder = Glide.with(getContext()).load(file);
        into();
    }


    public void setImageUri(Uri uri) {
        requestBuilder = Glide.with(getContext()).load(uri);
        into();
    }


    private void into() {
        updateRequestBuilder(requestBuilder);
        requestBuilder.into(this);
    }

    private void updateRequestBuilder(RequestBuilder builder) {
        if (builder == null) {
            return;
        }
        if (noAnimation) {
            builder.apply(RequestOptions.noAnimation());
        }
        if (transformation != null) {
            builder.apply(RequestOptions.bitmapTransform(transformation));
        }
        if (holderResId > 0) {
            builder.apply(RequestOptions.placeholderOf(holderResId));
        }
        if (errorResId > 0) {
            builder.apply(RequestOptions.errorOf(holderResId));
        }
    }
}
