package android.support.transition;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

class ViewGroupOverlayApi14 extends ViewOverlayApi14 implements ViewGroupOverlayImpl {
    ViewGroupOverlayApi14(Context context, ViewGroup hostView, View requestingView) {
        super(context, hostView, requestingView);
    }

    static ViewGroupOverlayApi14 createFrom(ViewGroup viewGroup) {
        return (ViewGroupOverlayApi14) ViewOverlayApi14.createFrom(viewGroup);
    }

    public void add(View view) {
        this.mOverlayViewGroup.add(view);
    }

    public void remove(View view) {
        this.mOverlayViewGroup.remove(view);
    }
}
