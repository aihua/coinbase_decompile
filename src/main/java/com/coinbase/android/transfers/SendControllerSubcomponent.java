package com.coinbase.android.transfers;

import com.coinbase.android.ControllerScope;

@ControllerScope
public interface SendControllerSubcomponent {
    void inject(SendController sendController);
}
