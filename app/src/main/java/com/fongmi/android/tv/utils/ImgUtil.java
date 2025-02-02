package com.fongmi.android.tv.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.fongmi.android.tv.App;
import com.fongmi.android.tv.R;

import java.io.ByteArrayOutputStream;

public class ImgUtil {

    public static void load(String url, ImageView view) {
        view.setScaleType(ImageView.ScaleType.CENTER);
        if (TextUtils.isEmpty(url)) view.setImageResource(R.drawable.ic_img_error);
        else Glide.with(App.get()).asBitmap().load(getUrl(url)).skipMemoryCache(true).dontAnimate().sizeMultiplier(Prefers.getThumbnail()).signature(new ObjectKey(url + "_" + Prefers.getQuality())).placeholder(R.drawable.ic_img_loading).listener(getListener(view)).into(view);
    }

    public static void loadKeep(String url, ImageView view) {
        Glide.with(App.get()).load(url).error(R.drawable.ic_img_error).placeholder(R.drawable.ic_img_loading).into(view);
    }

    public static void loadHistory(String url, ImageView view) {
        Glide.with(App.get()).load(url).error(R.drawable.ic_img_error).placeholder(R.drawable.ic_img_loading).into(view);
    }

    public static void loadLive(String url, ImageView view) {
        Glide.with(App.get()).asBitmap().load(url).error(R.drawable.ic_img_error).placeholder(R.drawable.ic_img_loading).dontAnimate().into(view);
    }

    public static GlideUrl getUrl(String url) {
        LazyHeaders.Builder builder = new LazyHeaders.Builder();
        if (url.contains("@Cookie=")) builder.addHeader("Cookie", url.split("@Cookie=")[1].split("@")[0]);
        if (url.contains("@Referer=")) builder.addHeader("Referer", url.split("@Referer=")[1].split("@")[0]);
        if (url.contains("@User-Agent=")) builder.addHeader("User-Agent", url.split("@User-Agent=")[1].split("@")[0]);
        return new GlideUrl(url.split("@")[0], builder.build());
    }

    private static RequestListener<Bitmap> getListener(ImageView view) {
        return new RequestListener<>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                view.setScaleType(ImageView.ScaleType.CENTER);
                view.setImageResource(R.drawable.ic_img_error);
                return true;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                return false;
            }
        };
    }

    public static byte[] resize(byte[] bytes) {
        int width = ResUtil.getScreenWidthPx();
        int height = ResUtil.getScreenHeightPx();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        if (bitmap.getWidth() < width && bitmap.getHeight() < height) return bytes;
        Matrix matrix = new Matrix();
        matrix.postScale((float) width / bitmap.getWidth(), (float) height / bitmap.getHeight());
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }
}
