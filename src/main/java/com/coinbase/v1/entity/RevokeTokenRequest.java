package com.coinbase.v1.entity;

import java.io.Serializable;

public class RevokeTokenRequest implements Serializable {
    private static final long serialVersionUID = -1445803678709058848L;
    private String token;

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
