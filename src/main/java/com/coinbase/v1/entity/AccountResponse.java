package com.coinbase.v1.entity;

public class AccountResponse extends Response {
    private static final long serialVersionUID = -874896173060788591L;
    private Account _account;

    public Account getAccount() {
        return this._account;
    }

    public void setAccount(Account account) {
        this._account = account;
    }
}
