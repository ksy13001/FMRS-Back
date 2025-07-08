package com.ksy.fmrs.util.Jti;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Primary
@Component
public class UuidJtiProvider implements JtiProvider{

    @Override
    public String generateJti() {
        return UUID.randomUUID().toString();
    }
}
