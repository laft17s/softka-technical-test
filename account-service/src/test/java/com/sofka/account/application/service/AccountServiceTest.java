package com.laft.account.application.service;

import com.laft.account.application.dto.AccountResponseDTO;
import com.laft.account.application.mapper.AccountMapper;
import com.laft.shared.domain.model.Account;
import com.laft.shared.domain.repository.AccountRepository;
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
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    @Test
    void getAllAccounts_ShouldReturnListOfAccounts() {
        // Arrange
        Account account1 = new Account();
        account1.setAccountNumber("123");
        Account account2 = new Account();
        account2.setAccountNumber("456");

        AccountResponseDTO dto1 = new AccountResponseDTO();
        dto1.setAccountNumber("123");
        AccountResponseDTO dto2 = new AccountResponseDTO();
        dto2.setAccountNumber("456");

        when(accountRepository.findAll()).thenReturn(Arrays.asList(account1, account2));
        when(accountMapper.toResponseDTO(account1)).thenReturn(dto1);
        when(accountMapper.toResponseDTO(account2)).thenReturn(dto2);

        // Act
        List<AccountResponseDTO> result = accountService.getAllAccounts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("123", result.get(0).getAccountNumber());
        assertEquals("456", result.get(1).getAccountNumber());
    }
}
