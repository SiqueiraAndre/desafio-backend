package com.contapagar.application;

import com.contapagar.domain.exception.RegistroNaoEncontradoException;
import com.contapagar.domain.exception.DataPagamentoInvalidaException;
import com.contapagar.domain.exception.SituacaoInvalidaException;
import com.contapagar.domain.model.Conta;
import com.contapagar.domain.model.SituacaoConta;
import com.contapagar.domain.repository.ContaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Transactional
public class ContaService {

    private final ContaRepository contaRepository;

    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }


    public Conta criar(Conta conta) {
        validarDatas(conta);
        definirSituacaoInicial(conta);
        return contaRepository.save(conta);
    }

    public Conta obterPorId(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException(id));
    }

    public Page<Conta> listarComFiltros(
            LocalDate dataVencimento,
            BigDecimal valor,
            String descricao,
            Pageable pageable) {

        if (dataVencimento != null || descricao != null || valor != null) {
            return contaRepository.filtrar(dataVencimento, valor, descricao, pageable);
        }else {
            return contaRepository.findAll(pageable);
        }

    }

    public Conta atualizar(Long id, Conta contaAtualizada) {
        Conta contaExistente = obterPorId(id);
        atualizarCampos(contaExistente, contaAtualizada);
        validarDatas(contaExistente);
        return contaRepository.save(contaExistente);
    }

    public Conta atualizarSituacao(Long id, SituacaoConta novaSituacao) {
        if (novaSituacao == null) {
            throw new SituacaoInvalidaException("A situação da conta não pode ser nula.");
        }

        Conta conta = obterPorId(id);

        if (novaSituacao == SituacaoConta.PAGA) {
            conta.setDataPagamento(LocalDate.now());
        } else if (novaSituacao == SituacaoConta.PENDENTE) {
            conta.setDataPagamento(null);
        }

        conta.setSituacao(novaSituacao);

        try {
            validarDataPagamento(conta.getDataPagamento(), conta.getDataVencimento());
        } catch (IllegalArgumentException e) {
            throw new DataPagamentoInvalidaException(e.getMessage());
        }

        return contaRepository.save(conta);
    }

    public void excluir(Long id) {
        Conta conta = obterPorId(id);
        contaRepository.delete(conta);
    }

    public BigDecimal calcularTotalPagoPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        BigDecimal total = contaRepository.calcularTotalPago(dataInicial, dataFinal);
        return total != null ? total : BigDecimal.ZERO;
    }

    // Métodos auxiliares
    private void atualizarCampos(Conta destino, Conta origem) {
        destino.setDataVencimento(origem.getDataVencimento());
        destino.setDataPagamento(origem.getDataPagamento());
        destino.setValor(origem.getValor());
        destino.setDescricao(origem.getDescricao());

        if(origem.getSituacao() != null) {
            destino.setSituacao(origem.getSituacao());
        }
    }

    private void definirSituacaoInicial(Conta conta) {
        if(conta.getSituacao() == null) {
            conta.setSituacao(
                    conta.getDataPagamento() != null ?
                            SituacaoConta.PAGA : SituacaoConta.PENDENTE
            );
        }
    }

    private void validarDatas(Conta conta) {
        if(conta.getDataPagamento() != null) {
            validarDataPagamento(conta.getDataPagamento(), conta.getDataVencimento());
        }
    }

    private void validarDataPagamento(LocalDate dataPagamento, LocalDate dataVencimento) {
        if(dataPagamento != null && dataVencimento != null
                && dataPagamento.isAfter(dataVencimento)) {
            throw new IllegalArgumentException(
                    "Data de pagamento não pode ser posterior ao vencimento");
        }
    }
}