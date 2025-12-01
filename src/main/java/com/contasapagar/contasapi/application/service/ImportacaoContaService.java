package com.contasapagar.contasapi.application.service;

import com.contasapagar.contasapi.application.dto.ImportacaoResponseDto;
import com.contasapagar.contasapi.application.mapper.ContaMapper;
import com.contasapagar.contasapi.domain.entity.Conta;
import com.contasapagar.contasapi.domain.repository.ContaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsável pela importação de contas via arquivo CSV.
 */
@Service
@Transactional
public class ImportacaoContaService {

    private static final Logger logger = LoggerFactory.getLogger(ImportacaoContaService.class);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String CSV_SEPARATOR = ";";
    private static final int EXPECTED_COLUMNS = 3; // dataVencimento;valor;descricao

    private final ContaRepository contaRepository;
    private final ContaMapper contaMapper;

    @Autowired
    public ImportacaoContaService(ContaRepository contaRepository, ContaMapper contaMapper) {
        this.contaRepository = contaRepository;
        this.contaMapper = contaMapper;
    }

    /**
     * Importa contas de um arquivo CSV
     * Formato esperado: dataVencimento;valor;descricao
     * Exemplo: 15/01/2024;150.50;Conta de luz
     */
    public ImportacaoResponseDto importarContas(MultipartFile arquivo) {
        logger.info("Iniciando importação de contas do arquivo: {}", arquivo.getOriginalFilename());

        validarArquivo(arquivo);

        List<String> mensagensErro = new ArrayList<>();
        int totalLinhas = 0;
        int sucessos = 0;
        int erros = 0;
        int numeroLinha = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(arquivo.getInputStream(), StandardCharsets.UTF_8))) {

            String linha;

            // Pula cabeçalho se existir
            boolean primeiraLinha = true;

            while ((linha = reader.readLine()) != null) {
                numeroLinha++;
                totalLinhas++;

                // Pula linha vazia
                if (linha.trim().isEmpty()) {
                    continue;
                }

                // Verifica se é cabeçalho
                if (primeiraLinha && isLinhaCabecalho(linha)) {
                    primeiraLinha = false;
                    totalLinhas--; // Não conta cabeçalho no total
                    continue;
                }

                primeiraLinha = false;

                try {
                    processarLinha(linha, numeroLinha);
                    sucessos++;

                } catch (Exception e) {
                    erros++;
                    String mensagemErro = String.format("Linha %d: %s", numeroLinha, e.getMessage());
                    mensagensErro.add(mensagemErro);
                    logger.warn("Erro ao processar linha {}: {}", numeroLinha, e.getMessage());
                }
            }

            ImportacaoResponseDto response = new ImportacaoResponseDto(
                    totalLinhas, sucessos, erros, mensagensErro
            );

            logger.info("Importação concluída. Total: {}, Sucessos: {}, Erros: {}",
                    totalLinhas, sucessos, erros);

            return response;

        } catch (Exception e) {
            logger.error("Erro durante importação do arquivo: {}", arquivo.getOriginalFilename(), e);
            throw new ImportacaoException("Erro ao processar arquivo: " + e.getMessage(), e);
        }
    }

    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ImportacaoException("Arquivo não pode estar vazio");
        }

        String nomeArquivo = arquivo.getOriginalFilename();
        if (nomeArquivo == null || !nomeArquivo.toLowerCase().endsWith(".csv")) {
            throw new ImportacaoException("Arquivo deve ser do tipo CSV");
        }

        // Verifica tamanho máximo (10MB)
        long tamanhoMaximo = 10 * 1024 * 1024; // 10MB
        if (arquivo.getSize() > tamanhoMaximo) {
            throw new ImportacaoException("Arquivo muito grande. Tamanho máximo permitido: 10MB");
        }
    }

    private boolean isLinhaCabecalho(String linha) {
        // Verifica se a linha parece ser um cabeçalho
        String linhaLower = linha.toLowerCase();
        return linhaLower.contains("data") &&
               (linhaLower.contains("valor") || linhaLower.contains("descri"));
    }

    private void processarLinha(String linha, int numeroLinha) {
        String[] campos = linha.split(CSV_SEPARATOR);

        if (campos.length != EXPECTED_COLUMNS) {
            throw new IllegalArgumentException(
                String.format("Número de colunas inválido. Esperado: %d, Encontrado: %d",
                        EXPECTED_COLUMNS, campos.length));
        }

        try {
            // Parse dos dados
            String dataVencimentoStr = campos[0].trim();
            String valorStr = campos[1].trim();
            String descricao = campos[2].trim();

            // Validações
            if (dataVencimentoStr.isEmpty()) {
                throw new IllegalArgumentException("Data de vencimento é obrigatória");
            }
            if (valorStr.isEmpty()) {
                throw new IllegalArgumentException("Valor é obrigatório");
            }
            if (descricao.isEmpty()) {
                throw new IllegalArgumentException("Descrição é obrigatória");
            }

            // Conversões
            LocalDate dataVencimento = parseDate(dataVencimentoStr);
            BigDecimal valor = parseDecimal(valorStr);

            // Validações de negócio
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Valor deve ser maior que zero");
            }

            if (descricao.length() > 500) {
                throw new IllegalArgumentException("Descrição não pode ter mais de 500 caracteres");
            }

            // Criar e salvar conta
            Conta conta = new Conta(dataVencimento, valor, descricao);
            contaRepository.save(conta);

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data de vencimento inválida. Formato esperado: dd/MM/yyyy");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor inválido. Use formato decimal com vírgula ou ponto");
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new IllegalArgumentException("Erro ao processar dados: " + e.getMessage());
        }
    }

    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            // Tenta outros formatos comuns
            try {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e2) {
                try {
                    return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                } catch (DateTimeParseException e3) {
                    throw new DateTimeParseException("Formato de data inválido: " + dateStr, dateStr, 0);
                }
            }
        }
    }

    private BigDecimal parseDecimal(String valorStr) {
        try {
            // Remove espaços e substitui vírgula por ponto
            String valorLimpo = valorStr.replace(" ", "").replace(",", ".");
            return new BigDecimal(valorLimpo);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Valor inválido: " + valorStr);
        }
    }

    /**
     * Gera template CSV para importação
     */
    public String gerarTemplateCsv() {
        StringBuilder template = new StringBuilder();
        template.append("data_vencimento;valor;descricao\n");
        template.append("15/01/2024;150.50;Conta de luz\n");
        template.append("20/01/2024;89.90;Conta de água\n");
        template.append("25/01/2024;45.00;Internet\n");

        return template.toString();
    }
}
