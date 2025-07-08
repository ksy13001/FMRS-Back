package com.ksy.fmrs.util.Jti;


public class TestJtiProvider implements JtiProvider {

    private final String jti;

    public  TestJtiProvider(String jti) {
        this.jti = jti;
    }

    @Override
    public String generateJti() {
        return this.jti;
    }
}
