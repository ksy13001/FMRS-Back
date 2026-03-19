package com.ksy.fmrs.domain.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TransferTypeTest {

    @Test
    void fromValue_currencyPrefixedIsPermanent() {
        assertThat(TransferType.fromValue("€10m")).isEqualTo(TransferType.PERMANENT);
        assertThat(TransferType.fromValue("$5m")).isEqualTo(TransferType.PERMANENT);
        assertThat(TransferType.fromValue("£1.5m")).isEqualTo(TransferType.PERMANENT);
    }

    @Test
    void fromValue_loanAndLoanReturn() {
        assertThat(TransferType.fromValue("Loan")).isEqualTo(TransferType.LOAN);
        assertThat(TransferType.fromValue("loan")).isEqualTo(TransferType.LOAN);
        assertThat(TransferType.fromValue("Back from Loan")).isEqualTo(TransferType.LOAN_RETURN);
        assertThat(TransferType.fromValue("return from loan")).isEqualTo(TransferType.LOAN_RETURN);
        assertThat(TransferType.fromValue("loan return")).isEqualTo(TransferType.LOAN_RETURN);
    }

    @Test
    void fromValue_freeAndNone() {
        assertThat(TransferType.fromValue("Free agent")).isEqualTo(TransferType.FREE);
        assertThat(TransferType.fromValue("free")).isEqualTo(TransferType.FREE);
        assertThat(TransferType.fromValue("N/A")).isEqualTo(TransferType.NONE);
        assertThat(TransferType.fromValue("")).isEqualTo(TransferType.NONE);
        assertThat(TransferType.fromValue(null)).isEqualTo(TransferType.NONE);
    }

    @Test
    void fromValue_unknownDefaultsToPermanent() {
        assertThat(TransferType.fromValue("Trade")).isEqualTo(TransferType.PERMANENT);
    }
}
