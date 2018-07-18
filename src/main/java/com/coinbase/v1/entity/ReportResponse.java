package com.coinbase.v1.entity;

public class ReportResponse extends Response {
    private static final long serialVersionUID = -4493964618141896877L;
    private Report _report;

    public Report getReport() {
        return this._report;
    }

    public void setReport(Report report) {
        this._report = report;
    }
}
