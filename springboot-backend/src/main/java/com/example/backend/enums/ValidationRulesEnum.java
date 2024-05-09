package com.example.backend.enums;

import lombok.Getter;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public enum ValidationRulesEnum {

    DEFAULT("Error: The input line cannot be null.") {
        @Override
        public boolean isValid(String line) {
            return (line != null);
        }
    },

    TYPE(1, "Error: The input line must start with 'Type=' and " +
            "the value after '=' must match one of the predefined type enumerations.") {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("Type=")) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    return Arrays.stream(TypeEnum.values())
                            .map(Enum::name)
                            .toList()
                            .contains(parts[1]);
                }
            }
            return false;
        }
    },

    DATE(2, "Error: The input line must start with 'Date=' and " +
            "the date format must be 'yyyy-MM-dd' with valid month and day values.") {
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

    CURRENCY(3, "Error: The input line must start with 'Currency=' and " +
            "the value after '=' must match one of the predefined currency enumerations.") {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("Currency=")) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    return Arrays.stream(CurrencyEnum.values())
                            .map(Enum::name)
                            .toList()
                            .contains(parts[1]);
                }
            }
            return false;
        }
    },

    TAX_PERCENT(4, "Error: The input line must start with 'TaxPercent=' and " +
            "the value must be a numeric percentage between 0 and 100, exclusive.") {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("TaxPercent=")) {
                String[] parts = line.split("=");
                if (parts.length == 2 && isNumeric(parts[1])) {
                    BigDecimal value = new BigDecimal(parts[1]);
                    return value.compareTo(BigDecimal.ZERO) > 0 && value.compareTo(BigDecimal.valueOf(100)) < 0;
                }
            }
            return false;
        }
    },

    CLIENT_NAME(5, "Error: The input line must start with 'ClientName=' and " +
            "the name following '=' cannot be empty.") {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("ClientName=")) {
                String[] parts = line.split("=");
                return parts.length == 2 && !parts[1].isEmpty();
            }
            return false;
        }
    },

    CLIENT_ID(6, "Error: The input line must start with 'ClientID=' and " +
            "the ID following '=' must be exactly 8 characters long.") {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("ClientID=")) {
                String[] parts = line.split("=");
                return parts.length == 2 && parts[1].length() == 8;
            }
            return false;
        }
    },

    CLIENT_ADDRESS(7, "Error: The input line must start with 'ClientAddress=' and " +
            "the address following '=' cannot be empty.") {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("ClientAddress=")) {
                String[] parts = line.split("=");
                return parts.length == 2 && !parts[1].isEmpty();
            }
            return false;
        }
    },

    SUPPLIER_NAME(8, "Error: The input line must start with 'SupplierName=' and " +
            "the name following '=' cannot be empty.") {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("SupplierName=")) {
                String[] parts = line.split("=");
                return parts.length == 2 && !parts[1].isEmpty();
            }
            return false;
        }
    },

    SUPPLIER_ID(9, "Error: The input line must start with 'SupplierID=' and " +
            "the ID following '=' must be exactly 8 characters long.") {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("SupplierID=")) {
                String[] parts = line.split("=");
                return parts.length == 2 && parts[1].length() == 8;
            }
            return false;
        }
    },

    SUPPLIER_ADDRESS(10, "Error: The input line must start with 'SupplierAddress=' and " +
            "the address following '=' cannot be empty.") {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("SupplierAddress=")) {
                String[] parts = line.split("=");
                return parts.length == 2 && !parts[1].isEmpty();
            }
            return false;
        }
    },

    TAX_EXCLUSIVE_AMOUNT(11, "Error: The input line must start with 'TaxExclusiveAmount=' and " +
            "the value after '=' must be a valid numeric amount.") {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("TaxExclusiveAmount=")) {
                String[] parts = line.split("=");
                return parts.length == 2 && isNumeric(parts[1]);
            }
            return false;
        }
    },

    TAX_AMOUNT(12, "Error: The input line must start with 'TaxAmount=' and " +
            "the value after '=' must be a valid numeric amount.") {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("TaxAmount=")) {
                String[] parts = line.split("=");
                return parts.length == 2 && isNumeric(parts[1]);
            }
            return false;
        }
    },

    TAX_INCLUSIVE_AMOUNT(13, "Error: The input line must start with 'TaxInclusiveAmount=' and " +
            "the value after '=' must be a valid numeric amount.") {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("TaxInclusiveAmount=")) {
                String[] parts = line.split("=");
                return parts.length == 2 && isNumeric(parts[1]);
            }
            return false;
        }
    },

    ITEM("Error: The input line must start with 'Item=' and follow the format 'Item=name,quantity,unit_price' " +
            "with non-empty name and numeric quantity and unit price.") {
        @Override
        public boolean isValid(String line) {
            if (line != null && line.startsWith("Item=")) {
                String[] parts = line.substring(5).split(",");
                return parts.length == 3 && !parts[0].isEmpty()
                        && isNumeric(parts[1]) && isNumeric(parts[2]);
            }
            return false;
        }
    };

    private static boolean isNumeric(String str) {
        return str.matches("\\d+(\\.\\d+)?");
    }

    private Integer lineIndex;
    @Getter
    private final String errorMessage;

    ValidationRulesEnum(Integer lineIndex, String errorMessage) {
        this.lineIndex = lineIndex;
        this.errorMessage = errorMessage;
    }

    ValidationRulesEnum(String errorMessage) {
        this.errorMessage = errorMessage;
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
