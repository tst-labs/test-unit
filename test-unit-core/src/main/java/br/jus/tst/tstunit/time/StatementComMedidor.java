package br.jus.tst.tstunit.time;

import java.util.Objects;
import java.util.concurrent.Callable;

import org.junit.runners.model.Statement;

/**
 * Implementação de {@link Statement} que utiliza um {@link MedidorTempoExecucao}.
 * 
 * @author Thiago Miranda
 * @since 30 de jun de 2017
 */
public class StatementComMedidor extends Statement {

    private final Statement defaultStatement;

    /**
     * Cria uma nova instância encapsulando um outro {@link Statement}.
     * 
     * @param defaultStatement
     *            a outra instância a ser encapsulada
     * @throws NullPointerException
     *             caso seja informado {@code null}
     */
    public StatementComMedidor(Statement defaultStatement) {
        this.defaultStatement = Objects.requireNonNull(defaultStatement, "defaultStatement");
    }

    @Override
    public void evaluate() throws Throwable { // NOSONAR
        MedidorTempoExecucao.getInstancia().medir(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                try {
                    defaultStatement.evaluate();
                } catch (Throwable throwed) { // NOSONAR
                    if (throwed instanceof Error) { // NOSONAR
                        throw (Error) throwed; // NOSONAR
                    } else {
                        throw (Exception) throwed; // NOSONAR
                    }
                }
                return null;
            }
        }, "Execução do código do próprio teste");
    }
}
