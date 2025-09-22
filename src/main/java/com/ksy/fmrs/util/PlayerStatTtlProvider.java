package com.ksy.fmrs.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@Component
public class PlayerStatTtlProvider {

    @Value("${player.stat.ppl}")
    private Duration ttl;

}
