package com.mamikos.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mamikos.backend.dto.KostRequest;
import com.mamikos.backend.dto.KostResponse;
import com.mamikos.backend.dto.PageResponse;
import com.mamikos.backend.security.CustomUserDetailsService;
import com.mamikos.backend.security.JwtTokenProvider;
import com.mamikos.backend.service.KostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KostController.class)
@AutoConfigureMockMvc(addFilters = false)
class KostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KostService kostService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void createKost_Success() throws Exception {
        KostRequest request = new KostRequest();
        request.setName("Kost Mawar");
        request.setLocation("Jakarta");
        request.setPrice(1500000.0);
        request.setDescription("Nice kost");
        request.setRoomCount(5);

        KostResponse response = KostResponse.builder()
                .id(1L)
                .name("Kost Mawar")
                .location("Jakarta")
                .price(1500000.0)
                .description("Nice kost")
                .roomCount(5)
                .ownerId(1L)
                .ownerName("owner1")
                .build();

        when(kostService.createKost(anyString(), any(KostRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/kosts")
                        .principal(() -> "owner1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Kost Mawar"));
    }

    @Test
    void getKostById_Success() throws Exception {
        KostResponse response = KostResponse.builder()
                .id(1L)
                .name("Kost Mawar")
                .location("Jakarta")
                .price(1500000.0)
                .description("Nice kost")
                .roomCount(5)
                .ownerId(1L)
                .ownerName("owner1")
                .build();

        when(kostService.getKostById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/kosts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void searchKosts_Success() throws Exception {
        PageResponse<KostResponse> pageResponse = PageResponse.<KostResponse>builder()
                .content(List.of(KostResponse.builder().id(1L).name("Kost Mawar").build()))
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1L)
                .totalPages(1)
                .last(true)
                .build();

        when(kostService.searchKosts(any(), any(), any(), any(), any(), anyInt(), anyInt())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/kosts/search")
                        .param("location", "Jakarta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].name").value("Kost Mawar"));
    }
    @Test
    void createKost_InvalidInput_ReturnsBadRequest() throws Exception {
        KostRequest request = new KostRequest();
        request.setName("");
        request.setLocation("");
        request.setPrice(-100.0);
        request.setRoomCount(0);

        mockMvc.perform(post("/api/kosts")
                        .principal(() -> "owner1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

}

