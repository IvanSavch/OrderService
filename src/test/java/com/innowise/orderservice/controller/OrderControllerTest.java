package com.innowise.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.model.dto.UserDto;
import com.innowise.orderservice.model.dto.order.OrderCreateDto;
import com.innowise.orderservice.model.dto.order.OrderResponseDto;
import com.innowise.orderservice.model.dto.order.OrderUpdateDto;
import com.innowise.orderservice.model.dto.orderitem.OrderItemDto;
import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class OrderControllerTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(options().dynamicPort().usingFilesUnderClasspath("src/test/resources"))
            .build();
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgres.getJdbcUrl());
        registry.add("spring.datasource.username", () -> postgres.getUsername());
        registry.add("spring.datasource.password", () -> postgres.getPassword());
        registry.add("user.service.url", wireMock::baseUrl);
        registry.add("resilience4j.circuitbreaker.instances.userService.minimum-number-of-calls", () -> "1");
        registry.add("resilience4j.circuitbreaker.instances.userService.sliding-window-size", () -> "1");
        registry.add("resilience4j.circuitbreaker.instances.userService.wait-duration-in-open-state", () -> "1s");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrderService orderService;

    private Item testItem;
    private Item testItem2;
    private Order testOrder;
    private UserDto testUser;

    protected void stubUserByEmail(String email, UserDto userDto) throws Exception {
        wireMock.stubFor(WireMock.get(("/users/email/" + email)).willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(objectMapper.writeValueAsString(userDto))
                .withStatus(200)));
    }
    protected void stubUserFindById(Long userId, UserDto userDto) throws Exception{
        wireMock.stubFor(
                WireMock.get("/users/" + userId).willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(objectMapper.writeValueAsString(userDto))
                                .withStatus(200)));
    }
    @BeforeEach
    void setUp() {
        wireMock.resetAll();
        orderRepository.deleteAll();
        itemRepository.deleteAll();

        testItem = new Item();
        testItem.setName("test1");
        testItem.setPrice(new BigDecimal(100));
        testItem = itemRepository.save(testItem);

        testItem2 = new Item();
        testItem2.setName("test2");
        testItem2.setPrice(new BigDecimal(50));
        testItem2 = itemRepository.save(testItem2);

        testUser = new UserDto();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testUser.setName("test");
        testUser.setSurname("test");
        testUser.setActive(true);
        testUser.setBirthDate(LocalDate.now());

        testOrder = new Order();
        testOrder.setUserId(testUser.getId());
        testOrder.setStatus(Order.OrderStatus.CREATED);
        testOrder.setDeleted(false);
        testOrder.setTotalPrice(new BigDecimal(200));
        testOrder.setCreatedAt(LocalDateTime.of(2025,12,12,1,0));


        List<OrderItem> orderItems = new ArrayList<>();

        OrderItem orderItem = new OrderItem();
        orderItem.setItem(testItem);
        orderItem.setQuantity(1);
        orderItem.setOrder(testOrder);
        orderItems.add(orderItem);

        testOrder.setList(orderItems);

        testOrder = orderRepository.save(testOrder);
    }
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;
    @BeforeEach
    void resetCircuitBreaker() {
        circuitBreakerRegistry.circuitBreaker("userService").reset();
    }
    @BeforeEach
    void setUpAuthentication() {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(1L, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create() throws Exception {

        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setItemId(testItem.getId());
        orderItemDto.setQuantity(2);

        OrderCreateDto orderCreateDto = new OrderCreateDto();
        orderCreateDto.setEmail("test@test.com");
        orderCreateDto.setOrderItemList(List.of(orderItemDto));

        stubUserByEmail("test@test.com", testUser);

        mockMvc.perform(post("/orders/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalPrice").value(200))
                .andExpect(jsonPath("$.items[0].itemId").value(testItem.getId()))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].name").value("test1"))
                .andExpect(jsonPath("$.items[0].price").value(100));

        wireMock.verify(getRequestedFor(urlPathEqualTo("/users/email/test@test.com")));
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    void findById() throws Exception {
        stubUserFindById(testUser.getId(), testUser);

        OrderResponseDto response = orderService.findById(testOrder.getId());

        assertNotNull(response);
        assertEquals(testOrder.getId(), response.getId());
        assertEquals(testUser.getId(), response.getUser().getId());
        assertEquals(testOrder.getStatus().toString(), response.getStatus());
        assertEquals(testOrder.getTotalPrice(), response.getTotalPrice());

        wireMock.verify(getRequestedFor(urlPathEqualTo("/users/" + testUser.getId())));
    }
    @Test
    void findByIdOrderNotFound() {
        Long nonExistentId = 9L;
        assertThrows(OrderNotFoundException.class, () -> orderService.findById(nonExistentId));
        wireMock.verify(0, getRequestedFor(urlPathMatching("/users/.*")));
    }
    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    void findByUserIdUser() throws Exception {
        stubUserFindById(testUser.getId(), testUser);

        Order order2 = new Order();
        order2.setUserId(testUser.getId());
        order2.setStatus(Order.OrderStatus.CREATED);
        order2.setTotalPrice(new java.math.BigDecimal(300));
        order2.setDeleted(false);
        orderRepository.save(order2);

        List<OrderResponseDto> responses = orderService.findByUserId(testUser.getId());

        assertNotNull(responses);
        assertEquals(2, responses.size());

        responses.forEach(response -> assertEquals(testUser.getId(), response.getUser().getId()));

        wireMock.verify(1, getRequestedFor(urlPathEqualTo("/users/" + testUser.getId())));
    }
    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    void findAllPagination() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);

        stubUserFindById(1L, testUser);
        List<OrderResponseDto> responses = orderService.findAll(pageable, null, null, null);

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    void findAllByStatus() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        stubUserFindById(1L, testUser);
        List<OrderResponseDto> results = orderService.findAll(pageable, Order.OrderStatus.CREATED, null, null);

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getStatus()).isEqualTo("CREATED");
    }

    @Test
    @Transactional
    void findAllByStatusAndDateRange() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        stubUserFindById(1L,testUser);
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();

        List<OrderResponseDto> responses = orderService.findAll(pageable, Order.OrderStatus.CREATED, from, to);

        assertEquals(1, responses.size());
        assertEquals("CREATED", responses.get(0).getStatus());
        assertEquals(1L, responses.get(0).getUser().getId());
    }
    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    void updateById() throws Exception {
        stubUserFindById(testUser.getId(), testUser);

        OrderItemDto itemDto1 = new OrderItemDto();
        itemDto1.setItemId(testItem.getId());
        itemDto1.setQuantity(3);

        OrderItemDto itemDto2 = new OrderItemDto();
        itemDto2.setItemId(testItem2.getId());
        itemDto2.setQuantity(2);

        OrderUpdateDto updateDto = new OrderUpdateDto();
        updateDto.setStatus(Order.OrderStatus.CREATED);
        updateDto.setOrderItem(List.of(itemDto1, itemDto2));

        OrderResponseDto response = orderService.updateById(testOrder.getId(), updateDto);

        assertNotNull(response);
        assertEquals(testOrder.getId(), response.getId());
        assertEquals(Order.OrderStatus.CREATED.toString(), response.getStatus());
        assertEquals(new BigDecimal("400"), response.getTotalPrice());
        assertEquals(2, response.getItems().size());

        Order updatedOrder = orderRepository.findById(testOrder.getId()).orElseThrow();
        assertEquals(Order.OrderStatus.CREATED, updatedOrder.getStatus());
        assertEquals(new BigDecimal("400"), updatedOrder.getTotalPrice());
        assertEquals(2, updatedOrder.getList().size());

        wireMock.verify(getRequestedFor(urlPathEqualTo("/users/" + testUser.getId())));
    }
    @Test
    void updateByIdOrderNotFound() {
        Long nonExistentId = 9L;

        OrderUpdateDto updateDto = new OrderUpdateDto();
        updateDto.setStatus(Order.OrderStatus.CREATED);
        updateDto.setOrderItem(List.of());

        assertThrows(OrderNotFoundException.class, () -> orderService.updateById(nonExistentId, updateDto));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteByIdOrder() {
        Long orderId = testOrder.getId();
        assertTrue(orderRepository.findById(orderId).isPresent());
        orderService.deleteById(orderId);

        wireMock.verify(0, getRequestedFor(urlPathMatching("/users/.*")));
    }

    @Test
    void deleteByIdOrderNotFound() {
        Long nonExistentId = 9L;
        assertThrows(OrderNotFoundException.class, () -> orderService.deleteById(nonExistentId));
    }
}
