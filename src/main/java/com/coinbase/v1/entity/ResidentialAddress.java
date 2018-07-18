package com.coinbase.v1.entity;

import java.io.Serializable;

public class ResidentialAddress implements Serializable {
    private static final long serialVersionUID = -7002327789359155010L;
    String _address1;
    String _address2;
    String _address3;
    String _city;
    String _postalCode;
    String _state;

    public String getAddress1() {
        return this._address1;
    }

    public void setAddress1(String address1) {
        this._address1 = address1;
    }

    public String getAddress2() {
        return this._address2;
    }

    public void setAddress2(String address2) {
        this._address2 = address2;
    }

    public String getAddress3() {
        return this._address3;
    }

    public void setAddress3(String address3) {
        this._address3 = address3;
    }

    public String getCity() {
        return this._city;
    }

    public void setCity(String city) {
        this._city = city;
    }

    public String getState() {
        return this._state;
    }

    public void setState(String state) {
        this._state = state;
    }

    public String getPostalCode() {
        return this._postalCode;
    }

    public void setPostalCode(String postalCode) {
        this._postalCode = postalCode;
    }
}
