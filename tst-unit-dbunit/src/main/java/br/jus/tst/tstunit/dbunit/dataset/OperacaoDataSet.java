package br.jus.tst.tstunit.dbunit.dataset;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Objects;

import org.apache.commons.lang3.builder.*;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;

/**
 * Associa um arquivo de dados do DBUnit com as respectivas operações a serem executadas antes e após os testes.
 * 
 * @author Thiago Miranda
 * @since 3 de mai de 2017
 */
public class OperacaoDataSet implements Serializable {

    private static final long serialVersionUID = 3617026518809867576L;

    public static class Builder implements org.apache.commons.lang3.builder.Builder<OperacaoDataSet> {

        private OperacaoDataSet instancia;

        public Builder() {
            this.instancia = new OperacaoDataSet();
        }

        public Builder comDataSet(FlatXmlDataSet dataSet) {
            this.instancia.dataSet = Objects.requireNonNull(dataSet, "dataSet");
            return this;
        }

        public Builder operacaoPreTestes(DatabaseOperation operacao) {
            this.instancia.operacaoAntesTestes = Objects.requireNonNull(operacao, "operacao");
            return this;
        }

        public Builder operacaoPosTestes(DatabaseOperation operacao) {
            this.instancia.operacaoAposTestes = Objects.requireNonNull(operacao, "operacao");
            return this;
        }

        @Override
        public OperacaoDataSet build() {
            return instancia;
        }
    }

    private transient FlatXmlDataSet dataSet;
    private transient DatabaseOperation operacaoAntesTestes;
    private transient DatabaseOperation operacaoAposTestes;

    private OperacaoDataSet() {
    }

    /**
     * Inicia a criação de uma nova instância.
     * 
     * @return a instância sendo criada
     */
    public static Builder nova() {
        return new Builder();
    }

    public void executarOperacaoPreTestes(IDatabaseConnection connection) throws DatabaseUnitException, SQLException {
        operacaoAntesTestes.execute(connection, dataSet);
    }

    public void executarOperacaoPosTestes(IDatabaseConnection connection) throws DatabaseUnitException, SQLException {
        operacaoAposTestes.execute(connection, dataSet);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("dataSet", dataSet).append("operacaoAntesTestes", operacaoAntesTestes)
                .append("operacaoAposTestes", operacaoAposTestes).toString();
    }
}
