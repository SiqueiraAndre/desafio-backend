package com.contapagar.application;

import com.contapagar.domain.model.Conta;
import com.contapagar.domain.model.SituacaoConta;
import com.contapagar.domain.repository.ContaRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CsvImportService {

    private static final Logger logger = LoggerFactory.getLogger(CsvImportService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ContaRepository contaRepository;

    public CsvImportService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    public void processarCsv(MultipartFile arquivo) {
        List<Conta> contasValidas = new ArrayList<>();
        int linhaAtual = 1;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(arquivo.getInputStream()))) {

            HeaderColumnNameTranslateMappingStrategy<CsvContaDto> strategy =
                    new HeaderColumnNameTranslateMappingStrategy<>();
            strategy.setType(CsvContaDto.class);
            strategy.setColumnMapping(createColumnMapping());

            CsvToBean<CsvContaDto> csvToBean = new CsvToBeanBuilder<CsvContaDto>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            for (CsvContaDto csvConta : csvToBean) {
                try {
                    Conta conta = converterParaEntidade(csvConta, linhaAtual);
                    contasValidas.add(conta);
                } catch (CsvImportException e) {
                    logger.error("Erro na linha {}: {}", linhaAtual, e.getMessage());
                }
                linhaAtual++;
            }

            if (!contasValidas.isEmpty()) {
                contaRepository.saveAll(contasValidas);
                logger.info("Importação concluída. {} registros processados com sucesso.", contasValidas.size());
            }

        } catch (IOException e) {
            throw new CsvImportException("Erro ao ler arquivo CSV: " + e.getMessage());
        }
    }

    private Conta converterParaEntidade(CsvContaDto dto, int linha) {
        Conta conta = new Conta();

        try {
            if (dto.getDataVencimento() != null && !dto.getDataVencimento().isEmpty()) {
                conta.setDataVencimento(LocalDate.parse(dto.getDataVencimento(), DATE_FORMATTER));
            } else {
                throw new CsvImportException("Linha " + linha + ": Data de vencimento não pode ser vazia.");
            }

            if (dto.getValor() != null && !dto.getValor().isEmpty()) {
                conta.setValor(new BigDecimal(dto.getValor().replace(",", ".")));
            } else {
                throw new CsvImportException("Linha " + linha + ": Valor não pode ser vazio.");
            }

            conta.setDescricao(dto.getDescricao());

            if (dto.getDataPagamento() != null && !dto.getDataPagamento().isEmpty()) {
                conta.setDataPagamento(LocalDate.parse(dto.getDataPagamento(), DATE_FORMATTER));
                conta.setSituacao(SituacaoConta.PAGA);
            } else {
                conta.setSituacao(SituacaoConta.PENDENTE);
            }

        } catch (Exception e) {
            throw new CsvImportException(
                    String.format("Linha %d: %s - %s", linha, e.getClass().getSimpleName(), e.getMessage())
            );
        }

        return conta;
    }

    private Map<String, String> createColumnMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("data_vencimento", "dataVencimento");
        mapping.put("data_pagamento", "dataPagamento");
        mapping.put("valor", "valor");
        mapping.put("descricao", "descricao");
        return mapping;
    }

    // DTO corrigido com getters e setters
    public static class CsvContaDto {
        private String dataVencimento;
        private String dataPagamento;
        private String valor;
        private String descricao;

        public String getDataVencimento() {
            return dataVencimento;
        }

        public void setDataVencimento(String dataVencimento) {
            this.dataVencimento = dataVencimento;
        }

        public String getDataPagamento() {
            return dataPagamento;
        }

        public void setDataPagamento(String dataPagamento) {
            this.dataPagamento = dataPagamento;
        }

        public String getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor = valor;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public static class CsvImportException extends RuntimeException {
        public CsvImportException(String message) {
            super(message);
        }
    }
}