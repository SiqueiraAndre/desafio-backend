package com.contasapagar.contasapi.infrastructure.config;

import com.contasapagar.contasapi.domain.entity.Conta;
import com.contasapagar.contasapi.domain.repository.ContaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço para tarefas agendadas relacionadas às contas.
 * Atualiza automaticamente o status de contas vencidas.
 */
@Component
public class ContaScheduledService {

    private static final Logger logger = LoggerFactory.getLogger(ContaScheduledService.class);

    private final ContaRepository contaRepository;

    @Autowired
    public ContaScheduledService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    /**
     * Executa diariamente à meia-noite para verificar contas vencidas
     */
    @Scheduled(cron = "0 0 0 * * ?") // Todos os dias à meia-noite
    @Transactional
    public void verificarContasVencidas() {
        logger.info("Iniciando verificação de contas vencidas...");

        try {
            List<Conta> contasVencidas = contaRepository.findContasVencidas();

            for (Conta conta : contasVencidas) {
                conta.verificarVencimento();
                contaRepository.save(conta);
            }

            logger.info("Verificação concluída. {} contas marcadas como vencidas.",
                       contasVencidas.size());

        } catch (Exception e) {
            logger.error("Erro ao verificar contas vencidas: {}", e.getMessage(), e);
        }
    }

}
