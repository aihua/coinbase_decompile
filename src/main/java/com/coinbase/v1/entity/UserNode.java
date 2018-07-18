package com.coinbase.v1.entity;

import java.io.Serializable;

public class UserNode implements Serializable {
    private static final long serialVersionUID = -6543933340956648721L;
    private User _user;

    public User getUser() {
        return this._user;
    }

    public void setUser(User user) {
        this._user = user;
    }
}
