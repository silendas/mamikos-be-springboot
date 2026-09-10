package com.mamikos.backend.exception;

import com.mamikos.backend.controller.KostController;
import com.mamikos.backend.service.KostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KostController.class)
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KostService kostService;

    @MockBean
    private com.mamikos.backend.security.JwtTokenProvider jwtTokenProvider;

    @MockBean
    private com.mamikos.backend.security.CustomUserDetailsService customUserDetailsService;

    @Test
    void handleResourceNotFoundException_ReturnsNotFound() throws Exception {
        when(kostService.getKostById(999L)).thenThrow(new ResourceNotFoundException("Kost not found with id 999"));

        mockMvc.perform(get("/api/kosts/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Kost not found with id 999"));
    }

    @Test
    void handleBadRequestException_ReturnsBadRequest() throws Exception {
        when(kostService.getKostById(1L)).thenThrow(new BadRequestException("Invalid request"));

        mockMvc.perform(get("/api/kosts/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid request"));
    }

    @Test
    void handleUnauthorizedException_ReturnsUnauthorized() throws Exception {
        when(kostService.getKostById(1L)).thenThrow(new UnauthorizedException("Unauthorized access"));

        mockMvc.perform(get("/api/kosts/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized access"));
    }
}
