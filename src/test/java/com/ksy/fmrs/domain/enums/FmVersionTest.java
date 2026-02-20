package com.ksy.fmrs.domain.enums;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FmVersionTest {

    private FmVersion fmVersion;

    @Test
    void String_to_FmVersion(){
        // given
        String fmVersionStr = "FM26";
        // when && then
        Assertions.assertThat(FmVersion.FM26)
                .isEqualTo(FmVersion.fromString(fmVersionStr));
    }

    @Test
    void Lower_String_to_FmVersion(){
        // given
        String fmVersionStr = "fm26";
        // when && then
        Assertions.assertThat(FmVersion.FM26)
                .isEqualTo(FmVersion.fromString(fmVersionStr));
    }

    @Test
    void null_String_is_exception(){
        // given
        String fmVersionStr = "";
        // when && then
        Assertions.assertThatThrownBy(() -> FmVersion.fromString(fmVersionStr))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void invalid_version_is_exception(){
        // given
        String fmVersionStr = "fm05";
        // when && then
        Assertions.assertThatThrownBy(() -> FmVersion.fromString(fmVersionStr))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void return_latest_fm_version(){
        Assertions.assertThat(FmVersion.FM26)
                .isEqualTo(FmVersion.latest());
    }
}