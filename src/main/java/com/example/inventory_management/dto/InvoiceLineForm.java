package com.example.inventory_management.dto;

public class InvoiceLineForm {

    private Integer productId;
    private Integer quantity;

    public InvoiceLineForm() {
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
