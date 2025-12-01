package com.contasapagar.contasapi.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o Value Object Periodo
 */
@DisplayName("Periodo Value Object Tests")
class PeriodoTest {

    @Nested
    @DisplayName("Criação de Período")
    class CriacaoPeriodoTest {

        @Test
        @DisplayName("Deve criar período válido")
        void deveCriarPeriodoValido() {
            // Arrange
            LocalDate dataInicio = LocalDate.of(2024, 1, 1);
            LocalDate dataFim = LocalDate.of(2024, 12, 31);

            // Act
            Periodo periodo = new Periodo(dataInicio, dataFim);

            // Assert
            assertEquals(dataInicio, periodo.getDataInicio());
            assertEquals(dataFim, periodo.getDataFim());
        }

        @Test
        @DisplayName("Deve criar período com mesma data")
        void deveCriarPeriodoComMesmaData() {
            // Arrange
            LocalDate data = LocalDate.of(2024, 6, 15);

            // Act
            Periodo periodo = new Periodo(data, data);

            // Assert
            assertEquals(data, periodo.getDataInicio());
            assertEquals(data, periodo.getDataFim());
        }

        @Test
        @DisplayName("Deve falhar com data início nula")
        void deveFalharComDataInicioNula() {
            // Arrange
            LocalDate dataFim = LocalDate.of(2024, 12, 31);

            // Act & Assert
            assertThrows(NullPointerException.class, () -> {
                new Periodo(null, dataFim);
            });
        }

        @Test
        @DisplayName("Deve falhar com data fim nula")
        void deveFalharComDataFimNula() {
            // Arrange
            LocalDate dataInicio = LocalDate.of(2024, 1, 1);

            // Act & Assert
            assertThrows(NullPointerException.class, () -> {
                new Periodo(dataInicio, null);
            });
        }

        @Test
        @DisplayName("Deve falhar se data início posterior à data fim")
        void deveFalharSeDataInicioPosteriorDataFim() {
            // Arrange
            LocalDate dataInicio = LocalDate.of(2024, 12, 31);
            LocalDate dataFim = LocalDate.of(2024, 1, 1);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                new Periodo(dataInicio, dataFim);
            });
        }
    }

    @Nested
    @DisplayName("Verificações de Período")
    class VerificacoesPeriodoTest {

        @Test
        @DisplayName("Deve verificar se data está no período")
        void deveVerificarSeDataEstaNoPeriodo() {
            // Arrange
            LocalDate dataInicio = LocalDate.of(2024, 6, 1);
            LocalDate dataFim = LocalDate.of(2024, 6, 30);
            Periodo periodo = new Periodo(dataInicio, dataFim);

            LocalDate dataDentro = LocalDate.of(2024, 6, 15);
            LocalDate dataAntes = LocalDate.of(2024, 5, 31);
            LocalDate dataDepois = LocalDate.of(2024, 7, 1);

            // Assert
            assertTrue(periodo.contem(dataDentro));
            assertTrue(periodo.contem(dataInicio)); // Limite inferior inclusivo
            assertTrue(periodo.contem(dataFim)); // Limite superior inclusivo
            assertFalse(periodo.contem(dataAntes));
            assertFalse(periodo.contem(dataDepois));
        }

        @Test
        @DisplayName("Deve calcular quantidade de dias")
        void deveCalcularQuantidadeDias() {
            // Arrange
            LocalDate dataInicio = LocalDate.of(2024, 6, 1);
            LocalDate dataFim = LocalDate.of(2024, 6, 30);
            Periodo periodo = new Periodo(dataInicio, dataFim);

            // Act
            long dias = periodo.quantidadeDias();

            // Assert
            assertEquals(30, dias); // 30 dias entre 1º e 30 de junho (inclusive)
        }

        @Test
        @DisplayName("Deve calcular zero dias para mesmo dia")
        void deveCalcularZeroDiasParaMesmoDia() {
            // Arrange
            LocalDate data = LocalDate.of(2024, 6, 15);
            Periodo periodo = new Periodo(data, data);

            // Act
            long dias = periodo.quantidadeDias();

            // Assert
            assertEquals(1, dias); // Mesmo dia conta como 1 dia
        }
    }

    @Nested
    @DisplayName("Equals e HashCode")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("Deve ser igual com mesmas datas")
        void deveSerIgualComMesmasDatas() {
            // Arrange
            LocalDate dataInicio = LocalDate.of(2024, 1, 1);
            LocalDate dataFim = LocalDate.of(2024, 12, 31);

            Periodo periodo1 = new Periodo(dataInicio, dataFim);
            Periodo periodo2 = new Periodo(dataInicio, dataFim);

            // Assert
            assertEquals(periodo1, periodo2);
            assertEquals(periodo1.hashCode(), periodo2.hashCode());
        }

        @Test
        @DisplayName("Deve ser diferente com datas diferentes")
        void deveSerDiferenteComDatasDiferentes() {
            // Arrange
            Periodo periodo1 = new Periodo(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 30));
            Periodo periodo2 = new Periodo(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 12, 31));

            // Assert
            assertNotEquals(periodo1, periodo2);
        }

        @Test
        @DisplayName("Deve ser diferente de null e outros objetos")
        void deveSerDiferenteDeNullEOutrosObjetos() {
            // Arrange
            Periodo periodo = new Periodo(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));

            // Assert
            assertNotEquals(periodo, null);
            assertNotEquals(periodo, "string");
            assertNotEquals(periodo, LocalDate.now());
        }
    }

    @Nested
    @DisplayName("ToString")
    class ToStringTest {

        @Test
        @DisplayName("Deve gerar string representativa")
        void deveGerarStringRepresentativa() {
            // Arrange
            LocalDate dataInicio = LocalDate.of(2024, 6, 1);
            LocalDate dataFim = LocalDate.of(2024, 6, 30);
            Periodo periodo = new Periodo(dataInicio, dataFim);

            // Act
            String resultado = periodo.toString();

            // Assert
            assertTrue(resultado.contains("2024-06-01"));
            assertTrue(resultado.contains("2024-06-30"));
        }
    }
}
