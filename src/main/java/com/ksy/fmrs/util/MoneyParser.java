package com.ksy.fmrs.util;

public class MoneyParser {

    public record MoneyParseResult(Double amount, String currency) {}

    /**
     * Parse transfer fee strings like "€10M", "$ 1.2M", "£ 5.4K", or "&pound; 5.4"
     * into numeric amount (scaled by M/K) and ISO-ish currency code.
     * Returns null when the string cannot be parsed.
     */
    public static MoneyParseResult parse(String fee) {
        try {
            if (fee == null || fee.isBlank()) {
                return null;
            }

            String normalized = fee.replace("&pound;", "£").trim();
            String upper = normalized.toUpperCase();

            String currency = null;
            if (upper.contains("€")) {
                currency = "EUR";
            } else if (upper.contains("$")) {
                currency = "USD";
            } else if (upper.contains("£")) {
                currency = "GBP";
            }

            boolean isMillion = upper.contains("M");
            boolean isThousand = upper.contains("K");

            // Strip currency symbols, separators, and unit letters before parsing.
            normalized = normalized
                    .replaceAll("[€$£,\\s]", "")
                    .replaceAll("(?i)[MK]", "");

            if (normalized.isEmpty()) {
                return null;
            }

            double amount = Double.parseDouble(normalized);
            double scale = isMillion ? 1_000_000d : (isThousand ? 1_000d : 1d);
            return new MoneyParseResult(amount * scale, currency);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Backward-compatible helper that returns only the amount.
     */
    public static Double eurToDouble(String fee) {
        MoneyParseResult result = parse(fee);
        return result != null ? result.amount() : null;
    }
}
