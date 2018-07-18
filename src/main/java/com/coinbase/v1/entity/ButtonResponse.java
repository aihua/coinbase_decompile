package com.coinbase.v1.entity;

public class ButtonResponse extends Response {
    private static final long serialVersionUID = 1612851447116897220L;
    private Button _button;

    public Button getButton() {
        return this._button;
    }

    public void setButton(Button button) {
        this._button = button;
    }
}
