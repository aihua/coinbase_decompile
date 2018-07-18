package com.coinbase.v1.entity;

import java.io.Serializable;

public class PaymentMethodNode implements Serializable {
    private static final long serialVersionUID = -3467480965918383878L;
    private PaymentMethod _paymentMethod;

    public PaymentMethod getPaymentMethod() {
        return this._paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this._paymentMethod = paymentMethod;
    }
}
