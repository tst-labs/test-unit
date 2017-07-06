package br.jus.tst.tstunit.dbunit;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

import org.apache.commons.lang3.builder.Builder;
import org.junit.runners.model.Statement;

import br.jus.tst.tstunit.dbunit.dataset.DatabaseLoader;
import br.jus.tst.tstunit.dbunit.dtd.GeradorDtd;
import br.jus.tst.tstunit.dbunit.script.ExecutorScripts;
import br.jus.tst.tstunit.time.MedidorTempoExecucao;

/**
 * {@link Statement} que define o comportamento de um caso de teste que utilize o DBUnit.
 * 
 * @author Thiago Miranda
 * @since 14 de jul de 2016
 */
public class DbUnitStatement extends Statement {

    /**
     * <em>Builder</em> que facilita a criação de instâncias de {@link DbUnitStatement}.
     * 
     * @author Thiago Miranda
     * @since 18 de jul de 2016
     */
    public static class DbUnitStatementBuilder implements Builder<DbUnitStatement> {

        private Optional<DatabaseLoader> databaseLoader;
        private Optional<GeradorDtd> geradorDtd;
        private Optional<ExecutorScripts> executorScripts;
        private Statement defaultStatement;

        /**
         * Cria um novo <em>builder</em>.
         * 
         * @param defaultStatement
         *            o <em>statement</em>-pai do que será construído
         */
        DbUnitStatementBuilder(Statement defaultStatement) {
            this.defaultStatement = defaultStatement;
        }

        /**
         * Define a instância de {@link DatabaseLoader} utilizada para efetuar operações sobre os dados do banco (opcional).
         * 
         * @param databaseLoader
         *            a ser utilizado
         * @return {@code this}, para chamadas encadeadas de método
         */
        public DbUnitStatementBuilder usandoDatabaseLoader(Optional<DatabaseLoader> databaseLoader) {
            this.databaseLoader = databaseLoader;
            return this;
        }

        /**
         * Define a instância de {@link GeradorDtd} utilizada para gerar o arquivo DTD do <em>schema</em> do banco (opcional).
         * 
         * @param geradorDtd
         *            a ser utilizado
         * @return {@code this}, para chamadas encadeadas de método
         */
        public DbUnitStatementBuilder usandoGeradorDtd(Optional<GeradorDtd> geradorDtd) {
            this.geradorDtd = geradorDtd;
            return this;
        }

        /**
         * Define a instância de {@link ExecutorScripts} a ser utilizada para rodar scripts antes e após a execução do <em>statement</em> (obrigatório).
         * 
         * @param executorScripts
         *            a ser utilizado
         * @return {@code this}, para chamadas encadeadas de método
         */
        public DbUnitStatementBuilder usandoScriptRunner(Optional<ExecutorScripts> executorScripts) {
            this.executorScripts = executorScripts;
            return this;
        }

        @Override
        public DbUnitStatement build() {
            return new DbUnitStatement(defaultStatement, databaseLoader, executorScripts, geradorDtd);
        }
    }

    private final Statement defaultStatement;
    private final Optional<DatabaseLoader> databaseLoader;
    private final Optional<ExecutorScripts> executorScripts;
    private final Optional<GeradorDtd> geradorDtd;

    DbUnitStatement(Statement defaultStatement, Optional<DatabaseLoader> databaseLoader, Optional<ExecutorScripts> executorScripts, Optional<GeradorDtd> geradorDtd) {
        this.defaultStatement = Objects.requireNonNull(defaultStatement, "defaultStatement");
        this.databaseLoader = databaseLoader;
        this.geradorDtd = geradorDtd;
        this.executorScripts = executorScripts;
    }

    /**
     * Inicia a criação de um novo {@link DbUnitStatement}.
     * 
     * @param defaultStatement
     *            o <em>statement</em>-pai
     * @return <em>builder</em> para criar a instância
     * @throws NullPointerException
     *             caso seja informado {@code null}
     */
    public static DbUnitStatementBuilder aPartirDo(Statement defaultStatement) {
        return new DbUnitStatementBuilder(Objects.requireNonNull(defaultStatement, "defaultStatement"));
    }

    @Override
    public void evaluate() throws Throwable {
        MedidorTempoExecucao medidorTempoExecucao = MedidorTempoExecucao.getInstancia();

        medidorTempoExecucao.medir(() -> executorScripts.ifPresent(executarScriptAntes()), "Execução de Scripts ANTES do teste");
        medidorTempoExecucao.medir(() -> geradorDtd.ifPresent(GeradorDtd::gerar), "Geração de DTD");
        medidorTempoExecucao.medir(() -> databaseLoader.ifPresent(DatabaseLoader::carregarBancoDados), "Carga do Banco de Dados");

        try {
            defaultStatement.evaluate();
        } finally {
            medidorTempoExecucao.medir(() -> databaseLoader.ifPresent(DatabaseLoader::limparBancoDados), "Limpeza do Banco de Dados");
            medidorTempoExecucao.medir(() -> executorScripts.ifPresent(executarScriptDepois()), "Execução de Scripts APÓS o teste");
        }
    }

    private Consumer<? super ExecutorScripts> executarScriptAntes() {
        return runner -> {
            try {
                runner.executarScriptsAntes();
            } catch (SQLException | IOException exception) {
                throw new DBUnitException("Erro ao executar script antes do teste", exception);
            }
        };
    }

    private Consumer<? super ExecutorScripts> executarScriptDepois() {
        return runner -> {
            try {
                runner.executarScriptsDepois();
            } catch (SQLException | IOException exception) {
                throw new DBUnitException("Erro ao executar script após o teste", exception);
            }
        };
    }
}