package com.casestudy.stockexchange.controller;

import com.casestudy.stockexchange.CleanupH2DatabaseTestListener;
import com.casestudy.stockexchange.controller.dto.AddStockRequest;
import com.casestudy.stockexchange.controller.dto.CreateStockExchangeRequest;
import com.casestudy.stockexchange.controller.dto.CreateStockRequest;
import com.casestudy.stockexchange.controller.dto.CreateStockResponse;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class, CleanupH2DatabaseTestListener.class})
class StockExchangeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void test_should_return_created_when_create_stock_exchange() throws Exception {
        // given
        CreateStockExchangeRequest createStockExchangeRequest = CreateStockExchangeRequest.builder()
                .name("test stock exchange")
                .description("test stock exchange description")
                .build();
        // when
        // then
        mockMvc.perform(post("/api/v1/stock-exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createStockExchangeRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").isNotEmpty());
    }

    @Test
    void test_should_return_error_when_create_stock_exchange_if_stock_exchange_with_name_already_exists() throws Exception {
        // given
        CreateStockExchangeRequest createStockExchangeRequest = CreateStockExchangeRequest.builder()
                .name("test stock exchange")
                .description("test stock exchange description")
                .build();
        mockMvc.perform(post("/api/v1/stock-exchange")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createStockExchangeRequest))
                .accept(MediaType.APPLICATION_JSON));
        mockMvc.perform(post("/api/v1/stock-exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createStockExchangeRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("error").isNotEmpty());

    }

    @Test
    void test_should_return_no_content_when_add_stock_to_stock_exchange() throws Exception {
        CreateStockExchangeRequest createStockExchangeRequest = CreateStockExchangeRequest.builder()
                .name("test stock exchange")
                .description("test stock exchange description")
                .build();
        List<CreateStockRequest> createStockRequestList = List.of(CreateStockRequest.builder()
                        .name("test stock")
                        .description("test stock description")
                        .currentPrice(1.34)
                        .build(),
                CreateStockRequest.builder()
                        .name("stock 2")
                        .description("desc 2")
                        .currentPrice(3.32)
                        .build());
        List<CreateStockResponse> createStockResponseList = new ArrayList<>();
        List<AddStockRequest> addStockRequestList = new ArrayList<>();

        mockMvc.perform(post("/api/v1/stock-exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createStockExchangeRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        for (CreateStockRequest createStockRequest : createStockRequestList) {
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
            createStockResponseList.add(createStockResponse);
            addStockRequestList.add(AddStockRequest.builder().stockId(createStockResponse.getId()).build());
        }

        // when
        for (AddStockRequest addStockRequest : addStockRequestList) {
            mockMvc.perform(put(String.format("/api/v1/stock-exchange/%s", createStockExchangeRequest.getName()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(addStockRequest))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }

        // then
        mockMvc.perform(get(String.format("/api/v1/stock-exchange/%s", createStockExchangeRequest.getName())))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].id")
                        .value(containsInAnyOrder(createStockResponseList
                                .stream()
                                .map(createStockResponse -> createStockResponse.getId().intValue())
                                .toArray())));

    }


}
