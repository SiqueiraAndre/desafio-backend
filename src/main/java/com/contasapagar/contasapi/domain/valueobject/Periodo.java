package com.contasapagar.contasapi.domain.valueobject;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Value Object que representa um período entre duas datas.
 * É imutável e contém validações de negócio.
 */
public class Periodo {

    private final LocalDate dataInicio;
    private final LocalDate dataFim;

    public Periodo(LocalDate dataInicio, LocalDate dataFim) {
        Objects.requireNonNull(dataInicio, "Data de início não pode ser nula");
        Objects.requireNonNull(dataFim, "Data de fim não pode ser nula");

        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Data de início deve ser anterior ou igual à data de fim");
        }

        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    /**
     * Cria um período do mês atual
     */
    public static Periodo mesAtual() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        LocalDate fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth());
        return new Periodo(inicioMes, fimMes);
    }

    /**
     * Cria um período do ano atual
     */
    public static Periodo anoAtual() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioAno = hoje.withDayOfYear(1);
        LocalDate fimAno = hoje.withDayOfYear(hoje.lengthOfYear());
        return new Periodo(inicioAno, fimAno);
    }

    /**
     * Verifica se uma data está contida no período
     */
    public boolean contem(LocalDate data) {
        if (data == null) {
            return false;
        }
        return !data.isBefore(dataInicio) && !data.isAfter(dataFim);
    }

    /**
     * Verifica se este período sobrepõe com outro
     */
    public boolean sobrepoeCom(Periodo outroPeriodo) {
        Objects.requireNonNull(outroPeriodo, "Período não pode ser nulo");

        return !this.dataFim.isBefore(outroPeriodo.dataInicio) &&
               !outroPeriodo.dataFim.isBefore(this.dataInicio);
    }

    /**
     * Retorna a quantidade de dias do período
     */
    public long quantidadeDias() {
        return dataInicio.until(dataFim).getDays() + 1; // +1 para incluir o último dia
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Periodo periodo = (Periodo) o;
        return Objects.equals(dataInicio, periodo.dataInicio) &&
               Objects.equals(dataFim, periodo.dataFim);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataInicio, dataFim);
    }

    @Override
    public String toString() {
        return "Periodo{" +
                "dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                '}';
    }
}
