package com.laft.client.application.service;

import com.laft.client.application.dto.ClientResponseDTO;
import com.laft.client.application.mapper.ClientMapper;
import com.laft.shared.domain.model.Client;
import com.laft.shared.domain.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientService clientService;

    @Test
    void getAllClients_ShouldReturnListOfClients() {
        // Arrange
        Client client1 = new Client();
        client1.setClientId("1");
        Client client2 = new Client();
        client2.setClientId("2");

        ClientResponseDTO dto1 = new ClientResponseDTO();
        dto1.setClientId("1");
        ClientResponseDTO dto2 = new ClientResponseDTO();
        dto2.setClientId("2");

        when(clientRepository.findAll()).thenReturn(Arrays.asList(client1, client2));
        when(clientMapper.toResponseDTO(client1)).thenReturn(dto1);
        when(clientMapper.toResponseDTO(client2)).thenReturn(dto2);

        // Act
        List<ClientResponseDTO> result = clientService.getAllClients();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getClientId());
        assertEquals("2", result.get(1).getClientId());
    }
}
