package com.casestudy.stockexchange.controller;

import com.casestudy.stockexchange.CleanupH2DatabaseTestListener;
import com.casestudy.stockexchange.controller.dto.CreateStockRequest;
import com.casestudy.stockexchange.controller.dto.CreateStockResponse;
import com.casestudy.stockexchange.controller.dto.DeleteStockRequest;
import com.casestudy.stockexchange.controller.dto.UpdateStockPriceRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class, CleanupH2DatabaseTestListener.class})
class StockControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void test_should_return_no_content_when_update_stock_current_price_and_delete_stock() throws Exception {
        CreateStockRequest createStockRequest = CreateStockRequest.builder()
                .name("test stock")
                .description("test stock description")
                .currentPrice(1.34)
                .build();

        MvcResult createStockResult = mockMvc.perform(post("/api/v1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createStockRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        CreateStockResponse createStockResponse = objectMapper.readValue(createStockResult
                        .getResponse()
                        .getContentAsString(),
                CreateStockResponse.class);
        UpdateStockPriceRequest updateStockPriceRequest = UpdateStockPriceRequest.builder()
                .id(createStockResponse.getId())
                .currentPrice(10.34)
                .build();

        // when
        mockMvc.perform(put("/api/v1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStockPriceRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // then
        mockMvc.perform(get(String.format("/api/v1/stock/%s", createStockResponse.getId())))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.currentPrice", is(updateStockPriceRequest.getCurrentPrice())))
                .andExpect(jsonPath("$.lastUpdate", greaterThan(createStockResponse.getLastUpdate())));

        // when
        // then
        mockMvc.perform(delete("/api/v1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DeleteStockRequest
                                .builder()
                                .id(createStockResponse.getId())
                                .build()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }

}
