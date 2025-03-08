package com.contapagar.controller;

import com.contapagar.application.CsvImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/contas")
public class ImportacaoController {

    private static final Logger logger = LoggerFactory.getLogger(ImportacaoController.class);
    private final CsvImportService csvImportService;

    public ImportacaoController(CsvImportService csvImportService) {
        this.csvImportService = csvImportService;
    }

    @PostMapping(value = "/importar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> importarCsv(@RequestParam(value = "arquivo", required = false) MultipartFile arquivo) {
        Map<String, Object> response = new HashMap<>();

        // Verifica se o arquivo está ausente ou vazio
        if (arquivo == null || arquivo.isEmpty()) {
            logger.warn("Tentativa de importação sem arquivo anexado.");

            response.put("status", "error");
            response.put("errorType", "NO_FILE_PROVIDED");
            response.put("message", "Nenhum arquivo foi anexado para importação.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            logger.info("Iniciando importação de arquivo: {}", arquivo.getOriginalFilename());

            csvImportService.processarCsv(arquivo);

            response.put("status", "success");
            response.put("message", "Arquivo processado com sucesso");
            response.put("arquivo", arquivo.getOriginalFilename());

            logger.info("Importação concluída com sucesso: {}", arquivo.getOriginalFilename());
            return ResponseEntity.ok(response);

        } catch (CsvImportService.CsvImportException e) {
            logger.error("Erro na importação: {}", e.getMessage());

            response.put("status", "error");
            response.put("errorType", "CSV_PROCESSING_ERROR");
            response.put("message", e.getMessage());
            response.put("arquivo", arquivo.getOriginalFilename());

            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(response);

        } catch (Exception e) {
            logger.error("Erro inesperado durante a importação: ", e);

            response.put("status", "error");
            response.put("errorType", "INTERNAL_SERVER_ERROR");
            response.put("message", "Ocorreu um erro interno durante o processamento");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}