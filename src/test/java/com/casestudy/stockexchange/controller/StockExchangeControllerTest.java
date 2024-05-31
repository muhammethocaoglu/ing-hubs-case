package com.casestudy.stockexchange.controller;

import com.casestudy.stockexchange.CleanupH2DatabaseTestListener;
import com.casestudy.stockexchange.controller.dto.*;
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
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

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
    void test_should_return_bad_request_when_create_stock_exchange() throws Exception {
        // given
        CreateStockExchangeRequest createStockExchangeRequest = CreateStockExchangeRequest.builder()
                .name("test stock exchange")
                .build();
        // when
        // then
        mockMvc.perform(post("/api/v1/stock-exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createStockExchangeRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("errors").isNotEmpty());
    }

    @Test
    void test_should_return_no_content_when_add_and_delete_stock_from_stock_exchange_or_delete_stock() throws Exception {
        CreateStockExchangeRequest createStockExchangeRequest = CreateStockExchangeRequest.builder()
                .name("test stock exchange")
                .description("test stock exchange description")
                .build();
        List<CreateStockRequest> createStockRequestList = new ArrayList<>();
        for(int i = 0 ; i < 5 ; i++){
            createStockRequestList.add(CreateStockRequest.builder()
                    .name("test stock " + i)
                    .description("test stock description" + 1)
                    .currentPrice(1.34 + i)
                    .build());
        }

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
                .andExpect(jsonPath("$.stocks[*].id")
                        .value(containsInAnyOrder(createStockResponseList
                                .stream()
                                .map(createStockResponse -> createStockResponse.getId().intValue())
                                .toArray())))
                .andExpect(jsonPath("$.liveInMarket", is(true)));

        // when
        mockMvc.perform(delete(String.format("/api/v1/stock-exchange/%s", createStockExchangeRequest.getName()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DeleteStockFromStockExchangeRequest
                                .builder()
                                .stockId(createStockResponseList.get(0).getId())
                                .build()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //then
        mockMvc.perform(get(String.format("/api/v1/stock-exchange/%s", createStockExchangeRequest.getName())))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.stocks", hasSize(4)))
                .andExpect(jsonPath("$.liveInMarket", is(false)));

        // when
        mockMvc.perform(delete("/api/v1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DeleteStockRequest
                                .builder()
                                .id(createStockResponseList.get(1).getId())
                                .build()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // then
        mockMvc.perform(get(String.format("/api/v1/stock-exchange/%s", createStockExchangeRequest.getName())))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.stocks", hasSize(3)));

    }

    @Test
    void test_should_return_not_found_when_add_stock_to_stock_exchange_if_stock_exchange_does_not_exist() throws Exception {
        // given
        AddStockRequest addStockRequest = AddStockRequest.builder().stockId(1L).build();

        // when
        // then
        mockMvc.perform(put(String.format("/api/v1/stock-exchange/%s", "test stock exchange"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addStockRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error").isNotEmpty());

    }


}
