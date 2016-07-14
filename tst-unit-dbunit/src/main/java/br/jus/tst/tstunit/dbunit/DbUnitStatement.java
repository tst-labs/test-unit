package br.jus.tst.tstunit.dbunit;

import java.util.Objects;

import org.junit.runners.model.Statement;

import br.jus.tst.tstunit.dbunit.jdbc.ScriptRunner;

/**
 * {@link Statement} que define o comportamento de um caso de teste que utilize o DBUnit.
 * 
 * @author Thiago Miranda
 * @since 14 de jul de 2016
 */
public class DbUnitStatement extends Statement {

    private final DbUnitDatabaseLoader databaseLoader;
    private final Statement defaultStatement;
    private final ScriptRunner scriptRunner;
    private final GeradorDtd geradorDtd;

    /**
     * Cria um novo statement.
     * 
     * @param databaseLoader
     *            utilizado para efetuar operações sobre os dados do banco - opcional
     * @param scriptRunner
     *            utilizado para rodar scripts antes e após a execução do statement
     * @param geradorDtd
     *            utilizado para gerar o arquivo DTD do <em>schema</em> do banco
     * @param defaultStatement
     *            o statement padrão (pai)
     */
    public DbUnitStatement(DbUnitDatabaseLoader databaseLoader, ScriptRunner scriptRunner, GeradorDtd geradorDtd, Statement defaultStatement) {
        this.databaseLoader = databaseLoader;
        this.geradorDtd = geradorDtd;
        this.scriptRunner = Objects.requireNonNull(scriptRunner, "scriptRunner");
        this.defaultStatement = Objects.requireNonNull(defaultStatement, "defaultStatement");
    }

    @Override
    public void evaluate() throws Throwable {
        scriptRunner.executarScriptAntes();

        if (geradorDtd != null) {
            geradorDtd.gerar();
        }

        if (databaseLoader != null) {
            databaseLoader.carregarBancoDados();
        }

        try {
            defaultStatement.evaluate();
        } finally {
            if (databaseLoader != null) {
                databaseLoader.limparBancoDados();
            }

            scriptRunner.executarScriptDepois();
        }
    }
}