package com.coinbase.v1.entity;

import java.io.Serializable;

public class AddressNode implements Serializable {
    private static final long serialVersionUID = 3331153178753180883L;
    private Address _address;

    public Address getAddress() {
        return this._address;
    }

    public void setAddress(Address address) {
        this._address = address;
    }
}
