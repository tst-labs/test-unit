package br.jus.tst.tstunit.time;

import java.util.Objects;

import org.junit.runners.model.Statement;

import br.jus.tst.tstunit.TstUnitRuntimeException;

/**
 * 
 * @author Thiago Miranda
 * @since 30 de jun de 2017
 */
public class StatementComMedidor extends Statement {

    private final Statement defaultStatement;

    /**
     * 
     * @param defaultStatement
     */
    public StatementComMedidor(Statement defaultStatement) {
        this.defaultStatement = Objects.requireNonNull(defaultStatement, "defaultStatement");
    }

    @Override
    public void evaluate() throws Throwable {
        MedidorTempoExecucao.getInstancia().medir(() -> {
            try {
                defaultStatement.evaluate();
            } catch (Throwable exception) {
                throw new TstUnitRuntimeException("Erro ao executar Statement: " + defaultStatement, exception);
            }
        }, "Execução do código do próprio teste");
    }
}
