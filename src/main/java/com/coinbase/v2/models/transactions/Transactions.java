package com.coinbase.v2.models.transactions;

import com.coinbase.v2.models.Pagination;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Transactions {
    @SerializedName("data")
    @Expose
    private List<Data> data = new ArrayList();
    @SerializedName("pagination")
    @Expose
    private Pagination pagination;

    public Pagination getPagination() {
        return this.pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public List<Data> getData() {
        return this.data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
}
