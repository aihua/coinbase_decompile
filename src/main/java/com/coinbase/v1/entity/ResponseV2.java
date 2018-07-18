package com.coinbase.v1.entity;

import com.coinbase.v1.deserializer.ErrorsCollector;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;

public class ResponseV2 extends Response {
    private static final long serialVersionUID = -3158292531826963674L;
    private String _errors;

    public static class Pagination implements Serializable {
        private static final long serialVersionUID = -9185630581624264339L;
        String _after;
        String _before;
        String _nextUri;
        String _order;
        String _previousUri;

        public String getBefore() {
            return this._before;
        }

        public void setBefore(String before) {
            this._before = before;
        }

        public String getAfter() {
            return this._after;
        }

        public void setAfter(String after) {
            this._after = after;
        }

        public String getOrder() {
            return this._order;
        }

        public void setOrder(String order) {
            this._order = order;
        }

        public String getPreviousUri() {
            return this._previousUri;
        }

        public void setPreviousUri(String previousUri) {
            this._previousUri = previousUri;
        }

        public String getNextUri() {
            return this._nextUri;
        }

        public void setNextUri(String nextUri) {
            this._nextUri = nextUri;
        }
    }

    @JsonDeserialize(converter = ErrorsCollector.class)
    public void setErrors(String errors) {
        this._errors = errors;
    }

    public boolean hasErrors() {
        return this._errors != null;
    }

    public String getErrors() {
        return this._errors;
    }
}
