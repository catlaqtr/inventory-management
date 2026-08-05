package com.example.inventory_management.model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public enum ProvincialTax {

    ON_HST("ON HST", "Ontario HST", new BigDecimal("13")),
    BC_GST_PST("BC GST+PST", "British Columbia GST + PST", new BigDecimal("12")),
    AB_GST("AB GST", "Alberta GST", new BigDecimal("5")),
    QC_GST_QST("QC GST+QST", "Quebec GST + QST", new BigDecimal("14.975")),
    MB_GST_RST("MB GST+RST", "Manitoba GST + RST", new BigDecimal("12")),
    SK_GST_PST("SK GST+PST", "Saskatchewan GST + PST", new BigDecimal("11")),
    NS_HST("NS HST", "Nova Scotia HST", new BigDecimal("15")),
    NB_HST("NB HST", "New Brunswick HST", new BigDecimal("15")),
    NL_HST("NL HST", "Newfoundland and Labrador HST", new BigDecimal("15")),
    PE_HST("PE HST", "Prince Edward Island HST", new BigDecimal("15")),
    NT_GST("NT GST", "Northwest Territories GST", new BigDecimal("5")),
    NU_GST("NU GST", "Nunavut GST", new BigDecimal("5")),
    YT_GST("YT GST", "Yukon GST", new BigDecimal("5")),
    NO_TAX("No Tax", "No tax applied", BigDecimal.ZERO);

    private final String code;
    private final String label;
    private final BigDecimal ratePercent;

    ProvincialTax(String code, String label, BigDecimal ratePercent) {
        this.code = code;
        this.label = label;
        this.ratePercent = ratePercent;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public BigDecimal getRatePercent() {
        return ratePercent;
    }

    public String getDropdownLabel() {
        return code + " (" + ratePercent.stripTrailingZeros().toPlainString() + "%)";
    }

    public static ProvincialTax fromName(String name) {
        if (name == null || name.isBlank()) {
            return ON_HST;
        }
        return Arrays.stream(values())
                .filter(tax -> tax.name().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid tax selection."));
    }

    public static List<ProvincialTax> all() {
        return Arrays.asList(values());
    }
}
