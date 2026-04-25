package com.fof.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fof.demo.dto.AIResponse;
import com.fof.demo.entity.SaleEntity;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AIAnalysisServiceTest {

    private MockWebServer mockWebServer;

    @Mock
    private SaleService saleService;

    private AIAnalysisService aiAnalysisService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        aiAnalysisService = new AIAnalysisService(saleService, webClient);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    /**
     * Vérifie que le service récupère les ventes, les transforme en payload IA
     * et appelle correctement le microservice FastAPI.
     */
    @Test
    void analyzeSales_shouldSendSalesToAIService() throws Exception {

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("{}"));

        SaleEntity sale = new SaleEntity(
                1L,
                "Laptop",
                2,
                1000.0,
                LocalDateTime.of(2026, 4, 25, 10, 0),
                50,
                null
        );

        when(saleService.findAllEntitiesInBatches(500))
                .thenReturn(List.of(sale));

        AIResponse response = aiAnalysisService.analyzeSales();

        assertNotNull(response);

        RecordedRequest request = mockWebServer.takeRequest();

        assertEquals("POST", request.getMethod());
        assertEquals("/api/analyze", request.getPath());

        JsonNode body = objectMapper.readTree(request.getBody().readUtf8());

        assertTrue(body.has("data"));
        assertEquals(1, body.get("data").size());

        JsonNode firstSale = body.get("data").get(0);

        assertEquals("Laptop", firstSale.get("product").asText());
        assertEquals(1000.0, firstSale.get("price").asDouble());
        assertEquals(2, firstSale.get("quantity").asInt());
        assertEquals(50, firstSale.get("stock").asInt());

        verify(saleService, times(1)).findAllEntitiesInBatches(500);
    }

    /**
     * Vérifie que le service retourne une réponse vide si le microservice IA échoue.
     */
    @Test
    void analyzeSales_shouldReturnEmptyResponseWhenAIServiceFails() {

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("{\"error\":\"AI service unavailable\"}"));

        when(saleService.findAllEntitiesInBatches(500))
                .thenReturn(List.of());

        AIResponse response = aiAnalysisService.analyzeSales();

        assertNotNull(response);
        verify(saleService, times(1)).findAllEntitiesInBatches(500);
    }
}