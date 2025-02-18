package com.contapagar.application;

import com.contapagar.domain.repository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsvImportServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private CsvImportService csvImportService;

    private MockMultipartFile csvFile;

    @BeforeEach
    void setUp() {
        String csvContent = "data_vencimento,data_pagamento,valor,descricao,situacao\n" +
                "10/08/2024,12/08/2024,150.75,Conta de luz,PAGA\n";

        csvFile = new MockMultipartFile(
                "arquivo",
                "arquivo.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Test
    void deveImportarContasDoCsv() throws IOException {
        csvImportService.processarCsv(csvFile);

        verify(contaRepository, times(1)).saveAll(anyList());
    }
}
