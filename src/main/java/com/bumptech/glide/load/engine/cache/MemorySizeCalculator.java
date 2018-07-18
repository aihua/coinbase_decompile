package com.bumptech.glide.load.engine.cache;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build.VERSION;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;

public final class MemorySizeCalculator {
    private final int arrayPoolSize;
    private final int bitmapPoolSize;
    private final Context context;
    private final int memoryCacheSize;

    public static final class Builder {
        private ActivityManager activityManager;
        private int arrayPoolSizeBytes = 4194304;
        private float bitmapPoolScreens = 4.0f;
        private final Context context;
        private float lowMemoryMaxSizeMultiplier = 0.33f;
        private float maxSizeMultiplier = 0.4f;
        private float memoryCacheScreens = 2.0f;
        private ScreenDimensions screenDimensions;

        public Builder(Context context) {
            this.context = context;
            this.activityManager = (ActivityManager) context.getSystemService("activity");
            this.screenDimensions = new DisplayMetricsScreenDimensions(context.getResources().getDisplayMetrics());
        }

        public MemorySizeCalculator build() {
            return new MemorySizeCalculator(this.context, this.activityManager, this.screenDimensions, this.memoryCacheScreens, this.bitmapPoolScreens, this.arrayPoolSizeBytes, this.maxSizeMultiplier, this.lowMemoryMaxSizeMultiplier);
        }
    }

    interface ScreenDimensions {
        int getHeightPixels();

        int getWidthPixels();
    }

    private static final class DisplayMetricsScreenDimensions implements ScreenDimensions {
        private final DisplayMetrics displayMetrics;

        public DisplayMetricsScreenDimensions(DisplayMetrics displayMetrics) {
            this.displayMetrics = displayMetrics;
        }

        public int getWidthPixels() {
            return this.displayMetrics.widthPixels;
        }

        public int getHeightPixels() {
            return this.displayMetrics.heightPixels;
        }
    }

    MemorySizeCalculator(Context context, ActivityManager activityManager, ScreenDimensions screenDimensions, float memoryCacheScreens, float bitmapPoolScreens, int targetArrayPoolSize, float maxSizeMultiplier, float lowMemoryMaxSizeMultiplier) {
        this.context = context;
        if (isLowMemoryDevice(activityManager)) {
            targetArrayPoolSize /= 2;
        }
        this.arrayPoolSize = targetArrayPoolSize;
        int maxSize = getMaxSize(activityManager, maxSizeMultiplier, lowMemoryMaxSizeMultiplier);
        int screenSize = (screenDimensions.getWidthPixels() * screenDimensions.getHeightPixels()) * 4;
        int targetPoolSize = Math.round(((float) screenSize) * bitmapPoolScreens);
        int targetMemoryCacheSize = Math.round(((float) screenSize) * memoryCacheScreens);
        int availableSize = maxSize - this.arrayPoolSize;
        if (targetMemoryCacheSize + targetPoolSize <= availableSize) {
            this.memoryCacheSize = targetMemoryCacheSize;
            this.bitmapPoolSize = targetPoolSize;
        } else {
            float part = ((float) availableSize) / (bitmapPoolScreens + memoryCacheScreens);
            this.memoryCacheSize = Math.round(part * memoryCacheScreens);
            this.bitmapPoolSize = Math.round(part * bitmapPoolScreens);
        }
        if (Log.isLoggable("MemorySizeCalculator", 3)) {
            Log.d("MemorySizeCalculator", "Calculation complete, Calculated memory cache size: " + toMb(this.memoryCacheSize) + ", pool size: " + toMb(this.bitmapPoolSize) + ", byte array size: " + toMb(this.arrayPoolSize) + ", memory class limited? " + (targetMemoryCacheSize + targetPoolSize > maxSize) + ", max size: " + toMb(maxSize) + ", memoryClass: " + activityManager.getMemoryClass() + ", isLowMemoryDevice: " + isLowMemoryDevice(activityManager));
        }
    }

    public int getMemoryCacheSize() {
        return this.memoryCacheSize;
    }

    public int getBitmapPoolSize() {
        return this.bitmapPoolSize;
    }

    public int getArrayPoolSizeInBytes() {
        return this.arrayPoolSize;
    }

    private static int getMaxSize(ActivityManager activityManager, float maxSizeMultiplier, float lowMemoryMaxSizeMultiplier) {
        float memoryClass = (float) ((activityManager.getMemoryClass() * 1024) * 1024);
        if (!isLowMemoryDevice(activityManager)) {
            lowMemoryMaxSizeMultiplier = maxSizeMultiplier;
        }
        return Math.round(memoryClass * lowMemoryMaxSizeMultiplier);
    }

    private String toMb(int bytes) {
        return Formatter.formatFileSize(this.context, (long) bytes);
    }

    private static boolean isLowMemoryDevice(ActivityManager activityManager) {
        if (VERSION.SDK_INT >= 19) {
            return activityManager.isLowRamDevice();
        }
        return false;
    }
}
