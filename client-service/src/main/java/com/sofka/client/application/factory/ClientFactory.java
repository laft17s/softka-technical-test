package com.laft.client.application.factory;

import com.laft.client.application.dto.CreateClientDTO;
import com.laft.client.application.strategy.PasswordEncryptionStrategy;
import com.laft.common.constants.ErrorConstants;
import com.laft.common.exception.ResourceNotFoundException;
import com.laft.shared.domain.model.Client;
import com.laft.shared.domain.model.Status;
import com.laft.shared.domain.model.Gender;
import com.laft.shared.domain.model.Person;
import com.laft.shared.domain.repository.StatusRepository;
import com.laft.shared.domain.repository.GenderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Factory para crear instancias de Client
 * Patrón: Factory
 */
@Component
@RequiredArgsConstructor
public class ClientFactory {
    
    private final GenderRepository genderRepository;
    private final StatusRepository statusRepository;
    private final PasswordEncryptionStrategy passwordEncryptionStrategy;
    
    /**
     * Crea una nueva instancia de Client desde un DTO
     * @param dto datos del cliente
     * @return nueva instancia de Client
     */
    public Client createFromDTO(CreateClientDTO dto) {
        // Buscar género
        Gender gender = genderRepository.findByCodeAndActiveTrue(dto.getGender())
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorConstants.GENERO_NO_ENCONTRADO + ": " + dto.getGender()
            ));
        
        // Buscar estado
        Status status = statusRepository.findByCodeAndActiveTrue(dto.getStatus())
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorConstants.ESTADO_NO_ENCONTRADO + ": " + dto.getStatus()
            ));
        
        // Encriptar contraseña usando Strategy
        String encryptedPassword = passwordEncryptionStrategy.encrypt(dto.getPassword());
        
        // Crear Client usando Builder pattern
        Client client = new Client();
        
        // Setear propiedades de Persona
        client.setName(dto.getName());
        client.setGender(gender);
        client.setAge(dto.getAge());
        client.setIdentification(dto.getIdentification());
        client.setAddress(dto.getAddress());
        client.setPhone(dto.getPhone());
        
        // Setear propiedades de Client
        client.setPassword(encryptedPassword);
        client.setStatus(status);
        
        return client;
    }
    
    /**
     * Actualiza un Client existente con nuevos datos
     * @param cliente cliente existente
     * @param genero nuevo género (opcional)
     * @param estado nuevo estado (opcional)
     * @param passwordEncriptada nueva contraseña encriptada (opcional)
     */
    public void updateClient(Client client, String name, Gender gender, Integer age,
                              String address, String phone, Status status, 
                              String encryptedPassword) {
        if (name != null) {
            client.setName(name);
        }
        if (gender != null) {
            client.setGender(gender);
        }
        if (age != null) {
            client.setAge(age);
        }
        if (address != null) {
            client.setAddress(address);
        }
        if (phone != null) {
            client.setPhone(phone);
        }
        if (status != null) {
            client.setStatus(status);
        }
        if (encryptedPassword != null) {
            client.setPassword(encryptedPassword);
        }
    }
}
