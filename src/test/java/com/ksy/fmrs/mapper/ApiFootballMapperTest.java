package com.ksy.fmrs.mapper;

import com.ksy.fmrs.domain.enums.TransferType;
import com.ksy.fmrs.dto.apiFootball.ApiFootballTransfers;
import com.ksy.fmrs.dto.transfer.TransferRequestDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ApiFootballMapperTest {

    private final ApiFootballMapper mapper = new ApiFootballMapper();

    @Test
    void toDto_mapsAllTransfersAndParsesTypeAndFee() {
        OffsetDateTime updatedAt = OffsetDateTime.parse("2024-07-01T12:30:00Z");
        ApiFootballTransfers data = new ApiFootballTransfers(
                "transfers",
                null,
                null,
                2,
                new ApiFootballTransfers.PagingDto(1, 1),
                List.of(
                        new ApiFootballTransfers.PlayerTransfersDto(
                                new ApiFootballTransfers.PlayerSummaryDto(100, "John Doe"),
                                updatedAt,
                                List.of(
                                        new ApiFootballTransfers.TransferDto(
                                                LocalDate.of(2024, 1, 10),
                                                "€10M",
                                                new ApiFootballTransfers.TransferTeamsDto(
                                                        new ApiFootballTransfers.TransferTeamDto(10, "To FC", "to-logo"),
                                                        new ApiFootballTransfers.TransferTeamDto(5, "From FC", "from-logo")
                                                )
                                        ),
                                        new ApiFootballTransfers.TransferDto(
                                                LocalDate.of(2023, 8, 5),
                                                "Free",
                                                new ApiFootballTransfers.TransferTeamsDto(
                                                        new ApiFootballTransfers.TransferTeamDto(12, "Next FC", "next-logo"),
                                                        new ApiFootballTransfers.TransferTeamDto(10, "To FC", "to-logo")
                                                )
                                        )
                                )
                        )
                )
        );

        List<TransferRequestDto> result = mapper.toDto(data);

        assertThat(result).hasSize(2);

        TransferRequestDto paid = result.getFirst();
        assertThat(paid.playerApiId()).isEqualTo(100);
        assertThat(paid.fromTeamApiId()).isEqualTo(5);
        assertThat(paid.toTeamApiId()).isEqualTo(10);
        assertThat(paid.transferType()).isEqualTo(TransferType.PERMANENT);
        assertThat(paid.fee()).isEqualTo(10_000_000d);
        assertThat(paid.currency()).isEqualTo("EUR");
        assertThat(paid.date()).isEqualTo(LocalDate.of(2024, 1, 10));
        assertThat(paid.update()).isEqualTo(updatedAt.toLocalDateTime());

        TransferRequestDto free = result.get(1);
        assertThat(free.transferType()).isEqualTo(TransferType.FREE);
        assertThat(free.fee()).isNull();
        assertThat(free.currency()).isNull();
        assertThat(free.fromTeamApiId()).isEqualTo(10);
        assertThat(free.toTeamApiId()).isEqualTo(12);
        assertThat(free.date()).isEqualTo(LocalDate.of(2023, 8, 5));
        assertThat(free.update()).isEqualTo(updatedAt.toLocalDateTime());
    }

    @Test
    void toDto_handles_various_transfer_types_like_real_response() {
        OffsetDateTime update1 = OffsetDateTime.parse("2025-12-08T00:22:21+00:00");
        OffsetDateTime update2 = OffsetDateTime.parse("2025-07-29T11:02:50+00:00");

        ApiFootballTransfers data = new ApiFootballTransfers(
                "transfers",
                null,
                null,
                10,
                new ApiFootballTransfers.PagingDto(1, 1),
                List.of(
                        new ApiFootballTransfers.PlayerTransfersDto(
                                new ApiFootballTransfers.PlayerSummaryDto(101, "Player A"),
                                update1,
                                List.of(
                                        new ApiFootballTransfers.TransferDto(
                                                LocalDate.of(2017, 7, 1),
                                                "Free",
                                                new ApiFootballTransfers.TransferTeamsDto(
                                                        new ApiFootballTransfers.TransferTeamDto(102, "Nancy", null),
                                                        new ApiFootballTransfers.TransferTeamDto(98, "Ajaccio", null)
                                                )
                                        ),
                                        new ApiFootballTransfers.TransferDto(
                                                LocalDate.of(2024, 8, 21),
                                                "Loan",
                                                new ApiFootballTransfers.TransferTeamsDto(
                                                        new ApiFootballTransfers.TransferTeamDto(102, "Nancy", null),
                                                        new ApiFootballTransfers.TransferTeamDto(111, "Le Havre", null)
                                                )
                                        ),
                                        new ApiFootballTransfers.TransferDto(
                                                LocalDate.of(2025, 7, 1),
                                                "Back from Loan",
                                                new ApiFootballTransfers.TransferTeamsDto(
                                                        new ApiFootballTransfers.TransferTeamDto(111, "Le Havre", null),
                                                        new ApiFootballTransfers.TransferTeamDto(102, "Nancy", null)
                                                )
                                        ),
                                        new ApiFootballTransfers.TransferDto(
                                                LocalDate.of(2025, 6, 29),
                                                "Return from loan",
                                                new ApiFootballTransfers.TransferTeamsDto(
                                                        new ApiFootballTransfers.TransferTeamDto(111, "Le Havre", null),
                                                        new ApiFootballTransfers.TransferTeamDto(102, "Nancy", null)
                                                )
                                        )
                                )
                        ),
                        new ApiFootballTransfers.PlayerTransfersDto(
                                new ApiFootballTransfers.PlayerSummaryDto(202, "Player B"),
                                update2,
                                List.of(
                                        new ApiFootballTransfers.TransferDto(
                                                LocalDate.of(2024, 8, 29),
                                                "Free agent",
                                                new ApiFootballTransfers.TransferTeamsDto(
                                                        new ApiFootballTransfers.TransferTeamDto(102, "Nancy", null),
                                                        new ApiFootballTransfers.TransferTeamDto(null, "Rocha", null)
                                                )
                                        ),
                                        new ApiFootballTransfers.TransferDto(
                                                LocalDate.of(2023, 7, 1),
                                                "€ 600K",
                                                new ApiFootballTransfers.TransferTeamsDto(
                                                        new ApiFootballTransfers.TransferTeamDto(116, "Lens", null),
                                                        new ApiFootballTransfers.TransferTeamDto(102, "Nancy", null)
                                                )
                                        ),
                                        new ApiFootballTransfers.TransferDto(
                                                LocalDate.of(2025, 8, 12),
                                                "Transfer",
                                                new ApiFootballTransfers.TransferTeamsDto(
                                                        new ApiFootballTransfers.TransferTeamDto(102, "Nancy", null),
                                                        new ApiFootballTransfers.TransferTeamDto(880, "Reggiana", null)
                                                )
                                        ),
                                        new ApiFootballTransfers.TransferDto(
                                                LocalDate.of(2018, 7, 1),
                                                "N/A",
                                                new ApiFootballTransfers.TransferTeamsDto(
                                                        new ApiFootballTransfers.TransferTeamDto(1063, "Saint Etienne", null),
                                                        new ApiFootballTransfers.TransferTeamDto(102, "Nancy", null)
                                                )
                                        ),
                                        new ApiFootballTransfers.TransferDto(
                                                LocalDate.of(2025, 8, 25),
                                                "-",
                                                new ApiFootballTransfers.TransferTeamsDto(
                                                        new ApiFootballTransfers.TransferTeamDto(102, "Nancy", null),
                                                        new ApiFootballTransfers.TransferTeamDto(89, "Dijon", null)
                                                )
                                        )
                                )
                        )
                )
        );

        List<TransferRequestDto> result = mapper.toDto(data);

        assertThat(result).hasSize(9);

        // Free
        TransferRequestDto free = result.get(0);
        assertThat(free.transferType()).isEqualTo(TransferType.FREE);
        assertThat(free.fee()).isNull();

        // Loan
        TransferRequestDto loan = result.get(1);
        assertThat(loan.transferType()).isEqualTo(TransferType.LOAN);

        // Back from Loan
        TransferRequestDto backFromLoan = result.get(2);
        assertThat(backFromLoan.transferType()).isEqualTo(TransferType.LOAN_RETURN);

        // Return from loan
        TransferRequestDto returnFromLoan = result.get(3);
        assertThat(returnFromLoan.transferType()).isEqualTo(TransferType.LOAN_RETURN);

        // Free agent -> FREE (also permits null outgoing team)
        TransferRequestDto freeAgent = result.get(4);
        assertThat(freeAgent.transferType()).isEqualTo(TransferType.FREE);
        assertThat(freeAgent.fee()).isNull();
        assertThat(freeAgent.fromTeamApiId()).isNull();
        assertThat(freeAgent.currency()).isNull();

        // Currency -> PERMANENT with parsed fee
        TransferRequestDto priced = result.get(5);
        assertThat(priced.transferType()).isEqualTo(TransferType.PERMANENT);
        assertThat(priced.fee()).isEqualTo(600_000d);
        assertThat(priced.currency()).isEqualTo("EUR");

        // Transfer (plain string) -> default PERMANENT
        TransferRequestDto plainTransfer = result.get(6);
        assertThat(plainTransfer.transferType()).isEqualTo(TransferType.PERMANENT);

        // N/A -> NONE
        TransferRequestDto na = result.get(7);
        assertThat(na.transferType()).isEqualTo(TransferType.NONE);

        // "-" -> defaults to PERMANENT
        TransferRequestDto dash = result.get(8);
        assertThat(dash.transferType()).isEqualTo(TransferType.PERMANENT);
    }
}
