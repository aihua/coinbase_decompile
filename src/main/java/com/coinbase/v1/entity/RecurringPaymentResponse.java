package com.coinbase.v1.entity;

public class RecurringPaymentResponse extends Response {
    private static final long serialVersionUID = -1740844042364630330L;
    private RecurringPayment _recurringPayment;

    public RecurringPayment getRecurringPayment() {
        return this._recurringPayment;
    }

    public void setRecurringPayment(RecurringPayment recurringPayment) {
        this._recurringPayment = recurringPayment;
    }
}
