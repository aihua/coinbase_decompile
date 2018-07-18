package com.coinbase.v1.entity;

public class OrderResponse extends Response {
    private static final long serialVersionUID = 1217894460265345504L;
    private Order _order;

    public Order getOrder() {
        return this._order;
    }

    public void setOrder(Order order) {
        this._order = order;
    }
}
