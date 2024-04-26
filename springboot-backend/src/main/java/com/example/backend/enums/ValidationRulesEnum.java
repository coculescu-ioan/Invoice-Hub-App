package com.example.backend.enums;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public enum ValidationRulesEnum {

    DEFAULT {
        @Override
        public boolean isValid(String line) {
            return (line != null);
        }
    },

    TYPE(1) {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("Type=")) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    return parts[1].equals("Invoice") || parts[1].equals("CreditNote");
                }
            }
            return false;
        }
    },

    DATE(2) {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("Date=")) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    return isValidDate(parts[1]);
                }
            }
            return false;
        }

        private boolean isValidDate(String dateString) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            // Enforce strict date parsing, ensuring that invalid dates (e.g., February 30th) are not accepted.
            dateFormat.setLenient(false);
            try {
                dateFormat.parse(dateString);
                return true;
            } catch (ParseException e) {
                return false;
            }
        }
    },

    CURRENCY(3) {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("Currency=")) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    return isValidCurrency(parts[1]);
                }
            }
            return false;
        }

        private boolean isValidCurrency(String currency) {
            for (Currency curr : Currency.values()) {
                if (curr.name().equals(currency)) {
                    return true;
                }
            }
            return false;
        }
    },

    TAX_PERCENT(4) {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("TaxPercent=")) {
                String[] parts = line.split("=");
                if (parts.length == 2 && isNumeric(parts[1])) {
                    double value = Double.parseDouble(parts[1]);
                    return value > 0 && value < 100;
                }
            }
            return false;
        }
    },

    CLIENT_NAME(5) {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("ClientName=")) {
                String[] parts = line.split("=");
                return parts.length == 2 && !parts[1].isEmpty();
            }
            return false;
        }
    },

    CLIENT_ID(6) {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("ClientID=")) {
                String[] parts = line.split("=");
                return parts.length == 2 && parts[1].length() == 8;
            }
            return false;
        }
    },

    CLIENT_ADDRESS(7) {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("ClientAddress=")) {
                String[] parts = line.split("=");
                return parts.length == 2 && !parts[1].isEmpty();
            }
            return false;
        }
    },

    SUPPLIER_NAME(8) {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("SupplierName=")) {
                String[] parts = line.split("=");
                return parts.length == 2 && !parts[1].isEmpty();
            }
            return false;
        }
    },

    SUPPLIER_ID(9) {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("SupplierID=")) {
                String[] parts = line.split("=");
                return parts.length == 2 && parts[1].length() == 8;
            }
            return false;
        }
    },

    SUPPLIER_ADDRESS(10) {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("SupplierAddress=")) {
                String[] parts = line.split("=");
                return parts.length == 2 && !parts[1].isEmpty();
            }
            return false;
        }
    },

    TAX_EXCLUSIVE_AMOUNT(11) {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("TaxExclusiveAmount=")) {
                String[] parts = line.split("=");
                return parts.length == 2 && isNumeric(parts[1]);
            }
            return false;
        }
    },

    TAX_AMOUNT(12) {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("TaxAmount=")) {
                String[] parts = line.split("=");
                return parts.length == 2 && isNumeric(parts[1]);
            }
            return false;
        }
    },

    TAX_INCLUSIVE_AMOUNT(13) {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("TaxInclusiveAmount=")) {
                String[] parts = line.split("=");
                return parts.length == 2 && isNumeric(parts[1]);
            }
            return false;
        }
    },

    ITEM {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("Item=")) {
                String[] parts = line.substring(5).split("/");
                // name / quantity / unit_price
                return parts.length == 3 && !parts[0].isEmpty()
                        && isNumeric(parts[1]) && isNumeric(parts[2]) ;
            }
            return false;
        }
    };

    private static boolean isNumeric(String str) {
        return str.matches("\\d+(\\.\\d+)?");
    }

    private Integer lineIndex;

    ValidationRulesEnum() { }

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
            return ITEM;
        }
        return DEFAULT;
    }
}
