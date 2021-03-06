package com.coinbase.android.buysell;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import com.coinbase.android.ControllerScope;
import com.coinbase.android.R;
import com.coinbase.android.paymentmethods.linkedaccounts.LinkedAccountsPickerController;
import com.coinbase.android.paymentmethods.linkedaccounts.LinkedAccountsPickerPresenter;
import com.coinbase.android.settings.GoToSettingsDialogController;
import com.coinbase.android.splittesting.SplitTestConstants;
import com.coinbase.android.splittesting.SplitTesting;
import com.coinbase.android.ui.ActionBarController;
import com.coinbase.android.ui.ApiErrorDialogController;
import com.coinbase.android.ui.ControllerLifeCycle;
import com.coinbase.android.ui.ModalControllerLifeCycle;
import com.coinbase.api.internal.ApiConstants;
import com.coinbase.v2.models.account.Data;
import com.coinbase.v2.models.transfers.Transfer;
import com.google.gson.Gson;
import java.util.List;
import javax.inject.Inject;

@ControllerScope
public class SellRouter {
    public static final String ACCOUNT = "BUY_SELL_CONFIRMATION_ACCOUNT";
    private static final String BLOG_URL = "https://blog.coinbase.com";
    static final String HIDE_PAYMENT_METHOD = "HIDE_PAYMENT_METHOD";
    public static final String PAGE_SOURCE = "PAGE_SOURCE";
    public static final String PAYMENT_METHOD = "BUY_SELL_CONFIRMATION_PAYMENT_METHOD";
    public static final String SELECTED_ACCOUNT = "selected_account";
    public static final String SELECTED_CURRENCY_CODE = "selected_currency_code";
    public static final String TRANSFER = "BUY_SELL_CONFIRMATION_TRANSFER";
    private final ActionBarController mController;
    private final SplitTesting mSplitTesting;

    public enum SellSource {
        ACCOUNT(ApiConstants.ACCOUNT),
        CHARTS("charts");
        
        private final String mSource;

        private SellSource(String source) {
            this.mSource = source;
        }

        public String toString() {
            return this.mSource;
        }
    }

    @Inject
    public SellRouter(ActionBarController controller, SplitTesting splitTesting) {
        this.mController = controller;
        this.mSplitTesting = splitTesting;
    }

    public void routeToSellModal(Data selectedAccount, SellSource source) {
        if (selectedAccount != null && selectedAccount.getCurrency() != null) {
            Bundle args = new Bundle();
            args.putString("selected_account", new Gson().toJson((Object) selectedAccount));
            args.putString("selected_currency_code", selectedAccount.getCurrency().getCode());
            if (source != null) {
                args.putString("PAGE_SOURCE", source.toString());
            }
            routeToSellModalWithArgs(this.mController.appendModalArgs(args));
        }
    }

    public void routeToSellModal(String selectedCurrencyCode, SellSource source) {
        if (!TextUtils.isEmpty(selectedCurrencyCode)) {
            Bundle args = new Bundle();
            args.putString("selected_currency_code", selectedCurrencyCode);
            if (source != null) {
                args.putString("PAGE_SOURCE", source.toString());
            }
            routeToSellModalWithArgs(this.mController.appendModalArgs(args));
        }
    }

    void routeToSellConfirmation(Transfer transfer, Data selectedAccount, com.coinbase.v2.models.paymentMethods.Data selectedPaymentMethod, List<com.coinbase.v2.models.paymentMethods.Data> paymentMethodList) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("BUY_SELL_CONFIRMATION_TRANSFER", gson.toJson((Object) transfer));
        args.putString("BUY_SELL_CONFIRMATION_PAYMENT_METHOD", gson.toJson((Object) selectedPaymentMethod));
        args.putString("selected_account", gson.toJson((Object) selectedAccount));
        args.putString(AbstractBuySellConfirmationPresenter.PAYMENT_METHOD_LIST, gson.toJson((Object) paymentMethodList));
        this.mController.pushModalController(new SellConfirmationController(this.mController.appendModalArgs(args)));
    }

    void routeToSellSuccess(com.coinbase.v2.models.transfers.Data transferData, Data selectedAccount) {
        if (transferData == null || selectedAccount == null) {
            closeModal();
            return;
        }
        Gson gson = new Gson();
        Bundle args = new Bundle();
        args.putString(BuySellSuccessPresenter.BUY_SELL_SUCCESS_TRANSFER_DATA, gson.toJson((Object) transferData));
        args.putString(BuySellSuccessPresenter.BUY_SELL_SUCCESS_ACCOUNT, gson.toJson((Object) selectedAccount));
        this.mController.pushModalController(new BuySellSuccessController(this.mController.appendModalArgs(args)), true);
    }

    void routeToLinkedAccountsPicker(List<com.coinbase.v2.models.paymentMethods.Data> paymentMethodList, com.coinbase.v2.models.paymentMethods.Data selectedPaymentMethod) {
        if (paymentMethodList != null) {
            Gson gson = new Gson();
            String paymentMethodString = gson.toJson((Object) paymentMethodList);
            if (paymentMethodString != null) {
                Bundle bundle = new Bundle();
                bundle.putString(LinkedAccountsPickerPresenter.PAYMENT_METHOD_LIST, paymentMethodString);
                bundle.putString(LinkedAccountsPickerPresenter.SELECTED_PAYMENT_METHOD, gson.toJson((Object) selectedPaymentMethod));
                this.mController.pushModalController(new LinkedAccountsPickerController(this.mController.appendModalArgs(bundle)));
            }
        }
    }

    void routeToError(String errorTitle, String errorMessage) {
        Bundle bundle = new Bundle();
        bundle.putString(ApiErrorDialogController.ERROR_TITLE, errorTitle);
        bundle.putString("error_message", errorMessage);
        this.mController.pushDialogModalController(new ApiErrorDialogController(this.mController.appendModalArgs(bundle)));
    }

    void routeToGoToSettingsDialog() {
        this.mController.pushDialogModalController(new GoToSettingsDialogController(this.mController.appendModalArgs(new Bundle())));
    }

    void routeToSupportBlog() {
        this.mController.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(BLOG_URL)));
    }

    void closeModal() {
        ControllerLifeCycle controllerLifeCycle = this.mController.getControllerLifeCycle();
        if (controllerLifeCycle instanceof ModalControllerLifeCycle) {
            ((ModalControllerLifeCycle) controllerLifeCycle).closeModal();
        }
    }

    private void routeToSellModalWithArgs(Bundle args) {
        if (args == null) {
            args = new Bundle();
        }
        args.putBoolean(HIDE_PAYMENT_METHOD, this.mSplitTesting.isInGroup(SplitTestConstants.REMOVE_BUY_SELL_PAYMENT_METHOD_SPLIT_TEST, "tEsTdIsAbLeD"));
        args.putInt(ActionBarController.OVERIDDEN_UP_INDICATOR, R.drawable.modal_close_gray);
        this.mController.pushModalController(new SellController(args));
    }
}
