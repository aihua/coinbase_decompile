package com.coinbase.v1.entity;

import java.io.Serializable;

public class OrderNode implements Serializable {
    private static final long serialVersionUID = 9143405822047138714L;
    private Order _order;

    public Order getOrder() {
        return this._order;
    }

    public void setOrder(Order order) {
        this._order = order;
    }
}
