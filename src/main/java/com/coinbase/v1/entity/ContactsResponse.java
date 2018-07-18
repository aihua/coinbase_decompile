package com.coinbase.v1.entity;

import com.coinbase.v1.deserializer.ContactsLifter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

public class ContactsResponse extends Response {
    private static final long serialVersionUID = 1579609741624298006L;
    private List<Contact> _contacts;

    public List<Contact> getContacts() {
        return this._contacts;
    }

    @JsonDeserialize(converter = ContactsLifter.class)
    public void setContacts(List<Contact> contacts) {
        this._contacts = contacts;
    }
}
