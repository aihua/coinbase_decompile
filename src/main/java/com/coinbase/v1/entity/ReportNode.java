package com.coinbase.v1.entity;

import java.io.Serializable;

public class ReportNode implements Serializable {
    private static final long serialVersionUID = 3996102881697459153L;
    private Report _Report;

    public Report getReport() {
        return this._Report;
    }

    public void setReport(Report Report) {
        this._Report = Report;
    }
}
