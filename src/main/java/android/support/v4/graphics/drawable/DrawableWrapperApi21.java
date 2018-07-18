package android.support.v4.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build.VERSION;
import android.util.Log;
import java.lang.reflect.Method;

class DrawableWrapperApi21 extends DrawableWrapperApi19 {
    private static Method sIsProjectedDrawableMethod;

    private static class DrawableWrapperStateLollipop extends DrawableWrapperState {
        DrawableWrapperStateLollipop(DrawableWrapperState orig, Resources res) {
            super(orig, res);
        }

        public Drawable newDrawable(Resources res) {
            return new DrawableWrapperApi21(this, res);
        }
    }

    DrawableWrapperApi21(Drawable drawable) {
        super(drawable);
        findAndCacheIsProjectedDrawableMethod();
    }

    DrawableWrapperApi21(DrawableWrapperState state, Resources resources) {
        super(state, resources);
        findAndCacheIsProjectedDrawableMethod();
    }

    public void setHotspot(float x, float y) {
        this.mDrawable.setHotspot(x, y);
    }

    public void setHotspotBounds(int left, int top, int right, int bottom) {
        this.mDrawable.setHotspotBounds(left, top, right, bottom);
    }

    public void getOutline(Outline outline) {
        this.mDrawable.getOutline(outline);
    }

    public Rect getDirtyBounds() {
        return this.mDrawable.getDirtyBounds();
    }

    public void setTintList(ColorStateList tint) {
        if (isCompatTintEnabled()) {
            super.setTintList(tint);
        } else {
            this.mDrawable.setTintList(tint);
        }
    }

    public void setTint(int tintColor) {
        if (isCompatTintEnabled()) {
            super.setTint(tintColor);
        } else {
            this.mDrawable.setTint(tintColor);
        }
    }

    public void setTintMode(Mode tintMode) {
        if (isCompatTintEnabled()) {
            super.setTintMode(tintMode);
        } else {
            this.mDrawable.setTintMode(tintMode);
        }
    }

    public boolean setState(int[] stateSet) {
        if (!super.setState(stateSet)) {
            return false;
        }
        invalidateSelf();
        return true;
    }

    protected boolean isCompatTintEnabled() {
        if (VERSION.SDK_INT != 21) {
            return false;
        }
        Drawable drawable = this.mDrawable;
        if ((drawable instanceof GradientDrawable) || (drawable instanceof DrawableContainer) || (drawable instanceof InsetDrawable) || (drawable instanceof RippleDrawable)) {
            return true;
        }
        return false;
    }

    DrawableWrapperState mutateConstantState() {
        return new DrawableWrapperStateLollipop(this.mState, null);
    }

    private void findAndCacheIsProjectedDrawableMethod() {
        if (sIsProjectedDrawableMethod == null) {
            try {
                sIsProjectedDrawableMethod = Drawable.class.getDeclaredMethod("isProjected", new Class[0]);
            } catch (Exception ex) {
                Log.w("DrawableWrapperApi21", "Failed to retrieve Drawable#isProjected() method", ex);
            }
        }
    }
}