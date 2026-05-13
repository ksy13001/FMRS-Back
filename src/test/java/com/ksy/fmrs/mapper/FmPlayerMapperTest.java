package com.ksy.fmrs.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.ksy.fmrs.domain.enums.FmVersion;
import com.ksy.fmrs.domain.player.FmPlayer;
import com.ksy.fmrs.dto.player.FmPlayerDto;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FmPlayerMapperTest {

    private ObjectMapper objectMapper;
    private FmPlayerMapper mapper;

    @BeforeEach
    void setUp() {
        objectMapper = buildObjectMapper();
        mapper = new FmPlayerMapper();
    }

    @Test
    @DisplayName("maps fmplayer json to entity")
    void toEntity_mapsJsonFields() throws Exception {
        FmPlayerDto dto = readDto("fmplayer/messi.json");
        dto.setName("Lionel Messi");

        FmPlayer result = mapper.toEntity(dto, FmVersion.FM26);

        assertThat(result.getFmUid()).isEqualTo(7458500);
        assertThat(result.getFmVersion()).isEqualTo(FmVersion.FM26);
        assertThat(result.getName()).isEqualTo("LIONEL MESSI");
        assertThat(result.getFirstName()).isEqualTo("LIONEL");
        assertThat(result.getLastName()).isEqualTo("MESSI");
        assertThat(result.getBirth()).isEqualTo(LocalDate.of(1987, 6, 24));
        assertThat(result.getNationName()).isEqualTo("ARGENTINA");
        assertThat(result.getCurrentAbility()).isEqualTo(172);
        assertThat(result.getPotentialAbility()).isEqualTo(200);
        assertThat(result.getPosition().getStriker()).isEqualTo(19);
        assertThat(result.getPosition().getAttackingMidRight()).isEqualTo(20);
        assertThat(result.getTechnicalAttributes().getDribbling()).isEqualTo(20);
        assertThat(result.getMentalAttributes().getVision()).isEqualTo(20);
        assertThat(result.getPhysicalAttributes().getPace()).isEqualTo(10);
        assertThat(result.getHiddenAttributes().getInjuryProneness()).isEqualTo(10);
        assertThat(result.getPersonalityAttributes().getAdaptability()).isEqualTo(17);
        assertThat(result.getGoalKeeperAttributes().getCommunication()).isEqualTo(3);
    }

    private ObjectMapper buildObjectMapper() {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ISO_LOCAL_DATE)
                .optionalStart()
                .appendLiteral('T')
                .appendPattern("HH:mm:ss")
                .optionalEnd()
                .toFormatter();
        JavaTimeModule timeModule = new JavaTimeModule();
        timeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(formatter));

        return new ObjectMapper().registerModule(timeModule);
    }

    private FmPlayerDto readDto(String resourcePath) throws IOException {
        try (InputStream input = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(resourcePath)) {
            assertThat(input).as("test resource missing: %s", resourcePath).isNotNull();
            return objectMapper.readValue(input, FmPlayerDto.class);
        }
    }
}
