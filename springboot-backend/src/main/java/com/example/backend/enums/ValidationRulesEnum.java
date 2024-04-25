package com.example.backend.enums;

public enum ValidationRulesEnum {

    DEFAULT {
        @Override
        public boolean isValid(String line) {
            return (line != null);
        }
    },
    TYPE(1){
        @Override
        public boolean isValid(String line) {
            return (line != null && line.startsWith("Type="));
            // Invoice / CreditNote (split)
        }
    },
    DATE(2){
        @Override
        public boolean isValid(String line) {
            return (line != null && line.startsWith("Date="));
            // Parsing date method
        }
    },
    CURRENCY(3){
        @Override
        public boolean isValid(String line) {
            return (line != null && line.startsWith("Currency="));
            // Found in Currency enum
        }
    },
    TAX_PERCENT(4){
        @Override
        public boolean isValid(String line) {
            return (line != null && line.startsWith("TaxPercent="));
            // isNumeric & between 0-100
        }
    },
    CLIENT_NAME(5){
        @Override
        public boolean isValid(String line) {
            return (line != null && line.startsWith("ClientName="));
            // nonEmpty
        }
    },
    CLIENT_ID(6){
        @Override
        public boolean isValid(String line) {
            return (line != null && line.startsWith("ClientID="));
            // nonEmpty & length=8
        }
    },
    CLIENT_ADDRESS(7){
        @Override
        public boolean isValid(String line) {
            return (line != null && line.startsWith("ClientAddress="));
            // nonEmpty
        }
    },
    SUPPLIER_NAME(8){
        @Override
        public boolean isValid(String line) {
            return (line != null && line.startsWith("SupplierName="));
            // nonEmpty
        }
    },
    SUPPLIER_ID(9){
        @Override
        public boolean isValid(String line) {
            return (line != null && line.startsWith("SupplierID="));
            // nonEmpty & length=8
        }
    },
    SUPPLIER_ADDRESS(10){
        @Override
        public boolean isValid(String line) {
            return (line != null && line.startsWith("SupplierAddress="));
            // nonEmpty
        }
    },
    TAX_EXCLUSIVE_AMOUNT(11){
        @Override
        public boolean isValid(String line) {
            return (line != null && line.startsWith("TaxExclusiveAmount="));
            // isNumeric
        }
    },
    TAX_AMOUNT(12){
        @Override
        public boolean isValid(String line) {
            return (line != null && line.startsWith("TaxAmount="));
            // isNumeric
        }
    },
    TAX_INCLUSIVE_AMOUNT(13){
        @Override
        public boolean isValid(String line) {
            return (line != null && line.startsWith("TaxInclusiveAmount="));
            // isNumeric
        }
    },
    LINE {
        @Override
        public boolean isValid(String line) {
            return (line != null && line.startsWith("Line="));
            // Using = as delimiter: nonEmpty
            // Using / as delimiter: first elem string, second elem numeric
        }
    };


    private Integer lineIndex;

    ValidationRulesEnum() {

    }

    ValidationRulesEnum(Integer lineIndex) {
        this.lineIndex = lineIndex;
    }

    public abstract boolean isValid(String line);

    public static ValidationRulesEnum findByLineIndex(int index) {
        for(ValidationRulesEnum rule : ValidationRulesEnum.values()) {
            if(rule.lineIndex != null && index == rule.lineIndex) {
                return rule;
            }
        }
        if(index > 13) {
            return LINE;
        }
        return DEFAULT;
    }
}
