package com.travels.backend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travels.backend.model.Package;
import com.travels.backend.model.PackageStatus;
import com.travels.backend.repository.PackageRepository;

@SpringBootTest
@AutoConfigureMockMvc
class BookingPaymentFlowIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PackageRepository packageRepository;

    @MockitoBean
    private JavaMailSender javaMailSender;

    private Long approvedPackageId;

    @BeforeEach
    void seedApprovedPackage() {
        Package pkg = Package.builder()
                .name("Paquete integración")
                .description("Descripción de prueba")
                .price(250.0)
                .destination("Bogotá")
                .durationDays(2)
                .maxParticipants(20)
                .status(PackageStatus.APPROVED)
                .averageRating(0.0)
                .build();
        approvedPackageId = packageRepository.save(pkg).getId();
    }

    @Test
    void login_createBooking_confirmPayment_verifyConfirmed_leaveReview() throws Exception {
        String email = "flow-" + System.nanoTime() + "@test.com";
        String password = "secret123";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(java.util.Map.of(
                                "name", "Cliente Flow",
                                "email", email,
                                "password", password))))
                .andExpect(status().isCreated());

        String loginJson = mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(java.util.Map.of(
                                "email", email,
                                "password", password))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(loginJson).path("token").asText();
        assertThat(token).isNotBlank();

        String bookingJson = mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(java.util.Map.of(
                                "packageId", approvedPackageId,
                                "travelDate", LocalDate.now().plusWeeks(2).toString(),
                                "numberOfParticipants", 1))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long bookingId = objectMapper.readTree(bookingJson).path("id").asLong();

        mockMvc.perform(post("/api/payments/confirm")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content("{\"bookingId\":" + bookingId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        mockMvc.perform(get("/api/bookings/" + bookingId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(java.util.Map.of(
                                "bookingId", bookingId,
                                "rating", 5,
                                "comment", "Excelente experiencia"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    void badRequest_validationResponse_hasMessageAndStatus() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\":\"x\",\"email\":\"missing-pass@test.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").value(400));
    }
}
