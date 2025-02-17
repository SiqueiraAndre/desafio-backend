package com.contapagar.application;

import com.contapagar.domain.model.Conta;
import com.contapagar.domain.repository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        String csvContent = "dataVencimento,dataPagamento,valor,descricao,situacao\n" +
                "2025-02-20,2025-02-15,150.75,Conta de luz,PAGA\n";

        csvFile = new MockMultipartFile(
                "file",
                "contas.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Test
    void deveImportarContasDoCsv() throws IOException {
//        csvImportService.importarContas(csvFile);

        verify(contaRepository, times(1)).saveAll(anyList());
    }
}
