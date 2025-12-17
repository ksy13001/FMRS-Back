package com.ksy.fmrs.domain.enums;

public enum TransferType {
    PERMANENT, LOAN, LOAN_RETURN, FREE, NONE;

    /**
     * Normalize raw API value to {@link TransferType}.
     * Currency-prefixed values are treated as PERMANENT.
     */
    public static TransferType fromValue(String value) {
        if (value == null || value.isBlank()) {
            return NONE;
        }

        String trimmed = value.trim();
        String lower = trimmed.toLowerCase();

        if (trimmed.startsWith("€") || trimmed.startsWith("$") || trimmed.startsWith("£")) {
            return PERMANENT;
        }
        if ("loan".equals(lower)) {
            return LOAN;
        }
        if ("back from loan".equals(lower) || "return from loan".equals(lower) || "loan return".equals(lower)) {
            return LOAN_RETURN;
        }
        if (lower.startsWith("free")) {
            return FREE;
        }
        if ("n/a".equals(lower)) {
            return NONE;
        }
        return PERMANENT;
    }
}
