package com.springfield.springfield_iptu_rest;

import feign.FeignException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableFeignClients(clients = IptuApiCobrancaClientTest.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class IptuCobrancaEndpointTest {

    @LocalServerPort
    private int port;

    @Autowired
    private IptuApiCobrancaClientTest apiClient;

    private ClienteValidacaoCidadao cidadaoValidadorMock;

    private static Integer cidadaoOkId = 10001;
    private static Integer cidadaoNaoExisteId = 99999;
    private static Integer anoTeste = 2024;
    private static Long idPrimeiraCobranca;

    @BeforeEach
    void setupMocks() {
        doNothing().when(cidadaoValidadorMock).verificarCidadaoExiste(cidadaoOkId);
        doThrow(FeignException.NotFound.class).when(cidadaoValidadorMock).verificarCidadaoExiste(cidadaoNaoExisteId);
    }

    @Test
    @Order(1)
    void testGerarCobranca_Sucesso() {
        ResponseEntity<IptuDebitoAno> response = apiClient.gerarCobranca(cidadaoOkId, anoTeste, "PARCELADA");
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(12, response.getBody().getCobrancas().size());
    }

    @Test
    @Order(2)
    void testGerarCobranca_CidadaoInexistente() {
        try {
            apiClient.gerarCobranca(cidadaoNaoExisteId, anoTeste, "PARCELADA");
            fail("Deveria falhar por cidadão inexistente");
        } catch (FeignException e) {
            assertEquals(HttpStatus.BAD_REQUEST.value(), e.status());
            assertTrue(e.contentUTF8().contains("Cidadao nao existe"));
        }
    }

    @Test
    @Order(3)
    void testVerStatus_AposGerar() {
        ResponseEntity<StatusIptuDTO> response = apiClient.verStatus(cidadaoOkId, anoTeste);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0.0, response.getBody().getTotalPago());
        assertEquals(12000.0, response.getBody().getTotalRestante());
        idPrimeiraCobranca = response.getBody().getListaCobrancas().get(0).getId();
    }

    @Test
    @Order(4)
    void testPagarCobranca_Sucesso() {
        assertNotNull(idPrimeiraCobranca);
        ResponseEntity<IptuCobrancaMes> response = apiClient.pagarCobranca(idPrimeiraCobranca);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getStatusPago());
    }

    @Test
    @Order(5)
    void testPagarCobranca_JaPaga() {
        assertNotNull(idPrimeiraCobranca);
        try {
            apiClient.pagarCobranca(idPrimeiraCobranca);
            fail("Deveria falhar por cobrança já paga");
        } catch (FeignException e) {
            assertEquals(HttpStatus.BAD_REQUEST.value(), e.status());
            assertTrue(e.contentUTF8().contains("ja esta paga"));
        }
    }

    @Test
    @Order(6)
    void testVerStatus_AposPagar() {
        ResponseEntity<StatusIptuDTO> response = apiClient.verStatus(cidadaoOkId, anoTeste);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1000.0, response.getBody().getTotalPago());
        assertEquals(11000.0, response.getBody().getTotalRestante());
    }
}
