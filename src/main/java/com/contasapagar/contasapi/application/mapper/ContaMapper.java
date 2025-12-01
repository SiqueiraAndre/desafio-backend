package com.contasapagar.contasapi.application.mapper;

import com.contasapagar.contasapi.application.dto.ContaRequestDto;
import com.contasapagar.contasapi.application.dto.ContaResponseDto;
import com.contasapagar.contasapi.application.dto.ValorTotalPagoPeriodoDto;
import com.contasapagar.contasapi.domain.entity.Conta;
import com.contasapagar.contasapi.domain.valueobject.Periodo;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper responsável por converter entre entidades de domínio e DTOs.
 */
@Component
public class ContaMapper {

    /**
     * Converte ContaRequestDto para entidade Conta
     */
    public Conta toEntity(ContaRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return new Conta(
                dto.getDataVencimento(),
                dto.getValor(),
                dto.getDescricao()
        );
    }

    /**
     * Converte entidade Conta para ContaResponseDto
     */
    public ContaResponseDto toDto(Conta conta) {
        if (conta == null) {
            return null;
        }

        return new ContaResponseDto(
                conta.getId(),
                conta.getDataVencimento(),
                conta.getDataPagamento(),
                conta.getValor(),
                conta.getDescricao(),
                conta.getSituacao(),
                conta.getCreatedAt(),
                conta.getUpdatedAt()
        );
    }

    /**
     * Converte lista de entidades para lista de DTOs
     */
    public List<ContaResponseDto> toDto(List<Conta> contas) {
        if (contas == null) {
            return null;
        }

        return contas.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Converte Page de entidades para Page de DTOs
     */
    public Page<ContaResponseDto> toDto(Page<Conta> contasPage) {
        if (contasPage == null) {
            return null;
        }

        return contasPage.map(this::toDto);
    }

    /**
     * Atualiza entidade existente com dados do DTO
     */
    public void updateEntity(Conta conta, ContaRequestDto dto) {
        if (conta == null || dto == null) {
            return;
        }

        conta.atualizar(
                dto.getDataVencimento(),
                dto.getValor(),
                dto.getDescricao()
        );
    }

    /**
     * Cria ValorTotalPagoPeriodoDto
     */
    public ValorTotalPagoPeriodoDto toValorTotalPagoPeriodoDto(Periodo periodo,
                                                              BigDecimal valorTotal,
                                                              Integer quantidadeContas) {
        return new ValorTotalPagoPeriodoDto(
                periodo.getDataInicio(),
                periodo.getDataFim(),
                valorTotal != null ? valorTotal : BigDecimal.ZERO,
                quantidadeContas != null ? quantidadeContas : 0
        );
    }
}
