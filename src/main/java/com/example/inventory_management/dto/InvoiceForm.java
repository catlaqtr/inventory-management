package com.example.inventory_management.dto;

import java.util.ArrayList;
import java.util.List;

public class InvoiceForm {

    private String customerName;
    private String customerMobile;
    private String note;
    private String taxCode = "ON_HST";
    private List<InvoiceLineForm> lines = new ArrayList<>();

    public InvoiceForm() {
        lines.add(new InvoiceLineForm());
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public List<InvoiceLineForm> getLines() {
        return lines;
    }

    public void setLines(List<InvoiceLineForm> lines) {
        this.lines = lines;
    }
}
