package com.contasapagar.contasapi.presentation.controller;

import com.contasapagar.contasapi.application.dto.ImportacaoResponseDto;
import com.contasapagar.contasapi.application.service.ImportacaoContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller REST para importação de contas via CSV.
 */
@RestController
@RequestMapping("/contas/importacao")
@Tag(name = "Importação", description = "API para importação de contas via arquivo CSV")
public class ImportacaoController {

    private static final Logger logger = LoggerFactory.getLogger(ImportacaoController.class);

    private final ImportacaoContaService importacaoService;

    @Autowired
    public ImportacaoController(ImportacaoContaService importacaoService) {
        this.importacaoService = importacaoService;
    }

    /**
     * Importa contas de um arquivo CSV
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importar contas via CSV",
               description = "Importa múltiplas contas a partir de um arquivo CSV. " +
                           "Formato esperado: data_vencimento;valor;descricao (separado por ponto e vírgula)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Importação processada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Arquivo inválido ou erro no processamento"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "413", description = "Arquivo muito grande"),
            @ApiResponse(responseCode = "415", description = "Tipo de arquivo não suportado")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ImportacaoResponseDto> importarContas(
            @Parameter(description = "Arquivo CSV com as contas a serem importadas", required = true)
            @RequestParam("arquivo") MultipartFile arquivo) {

        logger.info("Recebida requisição para importar contas do arquivo: {} (tamanho: {} bytes)",
                arquivo.getOriginalFilename(), arquivo.getSize());

        ImportacaoResponseDto resultado = importacaoService.importarContas(arquivo);

        logger.info("Importação concluída. Total: {}, Sucessos: {}, Erros: {}",
                resultado.getTotalLinhas(), resultado.getSucessos(), resultado.getErros());

        // Retorna status 200 mesmo se houver erros parciais
        // O cliente deve verificar o campo 'erros' na resposta
        return ResponseEntity.ok(resultado);
    }

    /**
     * Baixa um template CSV de exemplo
     */
    @GetMapping("/template")
    @Operation(summary = "Baixar template CSV",
               description = "Baixa um arquivo de template CSV com formato e exemplos para importação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template gerado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<String> baixarTemplate() {

        logger.debug("Recebida requisição para baixar template CSV");

        String templateContent = importacaoService.gerarTemplateCsv();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", "template_importacao_contas.csv");

        logger.debug("Template CSV gerado com sucesso");

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateContent);
    }

    /**
     * Retorna informações sobre o formato de importação
     */
    @GetMapping("/info")
    @Operation(summary = "Informações sobre importação",
               description = "Retorna informações sobre o formato esperado para importação de contas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações retornadas com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ImportacaoInfoDto> obterInformacoesImportacao() {

        logger.debug("Recebida requisição para informações de importação");

        ImportacaoInfoDto info = new ImportacaoInfoDto();

        return ResponseEntity.ok(info);
    }

    /**
     * DTO com informações sobre a importação
     */
    public static class ImportacaoInfoDto {
        private String formato = "CSV (Comma Separated Values)";
        private String separador = "Ponto e vírgula (;)";
        private String encoding = "UTF-8";
        private String tamanhoMaximo = "10MB";
        private String[] campos = {"data_vencimento", "valor", "descricao"};
        private String[] formatosData = {"dd/MM/yyyy", "yyyy-MM-dd", "dd-MM-yyyy"};
        private String formatoValor = "Decimal com ponto ou vírgula (ex: 150.50 ou 150,50)";
        private String exemplo = "15/01/2024;150.50;Conta de luz";
        private String observacao = "A primeira linha pode conter cabeçalho e será ignorada se detectada";

        // Getters
        public String getFormato() { return formato; }
        public String getSeparador() { return separador; }
        public String getEncoding() { return encoding; }
        public String getTamanhoMaximo() { return tamanhoMaximo; }
        public String[] getCampos() { return campos; }
        public String[] getFormatosData() { return formatosData; }
        public String getFormatoValor() { return formatoValor; }
        public String getExemplo() { return exemplo; }
        public String getObservacao() { return observacao; }
    }
}
