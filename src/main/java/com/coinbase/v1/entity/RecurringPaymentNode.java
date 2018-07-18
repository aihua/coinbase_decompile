package com.coinbase.v1.entity;

import java.io.Serializable;

public class RecurringPaymentNode implements Serializable {
    private static final long serialVersionUID = 6607883611045097912L;
    private RecurringPayment _recurringPayment;

    public RecurringPayment getRecurringPayment() {
        return this._recurringPayment;
    }

    public void setRecurringPayment(RecurringPayment recurringPayment) {
        this._recurringPayment = recurringPayment;
    }
}
