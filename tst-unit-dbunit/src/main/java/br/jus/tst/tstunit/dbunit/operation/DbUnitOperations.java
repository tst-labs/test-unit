package br.jus.tst.tstunit.dbunit.operation;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.dbunit.operation.DatabaseOperation;

import br.jus.tst.tstunit.dbunit.DBUnitException;

/**
 * Fornece funcionalidades utilitárias referentes a operações de banco de dados do DBUnit.
 * 
 * @author Thiago Miranda
 * @since 5 de mai de 2017
 */
public final class DbUnitOperations {

    private DbUnitOperations() {

    }

    /**
     * Carrega uma instância de {@link DatabaseOperation} a partir de seu nome.
     * 
     * @param nomeOperacao
     *            o nome da operação desejada
     * @param operacaoDefault
     *            a operação padrão, caso a operação informada seja {@code null}. Caso ela também seja {@code null}, será retornado {@code null}
     * @return a instância encontrada
     */
    public static DatabaseOperation carregarOperacao(String nomeOperacao, String operacaoDefault) {
        String operacaoString = Optional.ofNullable(StringUtils.stripToNull(nomeOperacao)).orElse(operacaoDefault);

        DatabaseOperation operacao;
        if (StringUtils.isNotBlank(operacaoString)) {
            try {
                operacao = (DatabaseOperation) DatabaseOperation.class.getField(operacaoString).get(null);
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException exception) {
                throw new DBUnitException("Operação inválida: " + operacaoString, exception);
            }
        } else {
            operacao = null;
        }

        return operacao;
    }
}
