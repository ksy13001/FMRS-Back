package com.ksy.fmrs.service;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;


class MappingServiceTest {


    @Test
    void 메서드명() throws Exception{
        // given
        JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();
        System.out.println(similarity.apply("Luis Alberto"+  "Suarez Díaz", "Luis"+"Suarez"));
        // when

        // then
    }

}