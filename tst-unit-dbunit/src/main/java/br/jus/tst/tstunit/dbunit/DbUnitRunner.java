package br.jus.tst.tstunit.dbunit;

import java.io.File;
import java.sql.*;
import java.util.*;

import org.apache.commons.lang3.*;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import br.jus.tst.tstunit.Configuracao;

/**
 * TODO Javadoc
 * 
 * @author Thiago Miranda
 * @since 4 de jul de 2016
 */
public class DbUnitRunner {

    private final class DbUnitStatement extends Statement {

        private final DbUnitDatabaseLoader databaseLoader;
        private final Statement statement;

        public DbUnitStatement(DbUnitDatabaseLoader databaseLoader, Statement statement) {
            this.databaseLoader = databaseLoader;
            this.statement = statement;
        }

        @Override
        public void evaluate() throws Throwable {
            databaseLoader.carregarBancoDados();

            try {
                statement.evaluate();
            } finally {
                databaseLoader.limparBancoDados();
            }
        }
    }

    private final Class<?> classeTeste;
    private final Configuracao configuracao;
    private final String nomeSchema;

    /**
     * 
     * @param classeTeste
     * @param nomeSchema
     * @param configuracao
     */
    public DbUnitRunner(Class<?> classeTeste, String nomeSchema, Configuracao configuracao) {
        super();
        this.classeTeste = classeTeste;
        this.nomeSchema = nomeSchema;
        this.configuracao = configuracao;
    }

    /**
     * 
     * @param statement
     * @param method
     * @return
     * @throws NullPointerException
     *             caso qualquer parâmetro seja {@code null}
     */
    public Statement criarStatement(Statement statement, FrameworkMethod method) {
        Objects.requireNonNull(statement, "statement");
        Objects.requireNonNull(method, "method");

        UsarDataSet usarDataSet = ObjectUtils.defaultIfNull(method.getAnnotation(UsarDataSet.class), classeTeste.getAnnotation(UsarDataSet.class));
        Properties propriedadesJdbc = configuracao.getSubPropriedades("jdbc");

        String datasetsDir = StringUtils.defaultIfBlank((String) configuracao.getSubPropriedades("dbunit").get("datasets.dir"), "datasets");

        DbUnitDatabaseLoader databaseLoader = new DbUnitDatabaseLoader(datasetsDir + File.separatorChar + usarDataSet.value(), () -> {
            try {
                Class.forName(propriedadesJdbc.getProperty("driverClass").toString());
                return DriverManager.getConnection(propriedadesJdbc.get("url").toString(), propriedadesJdbc);
            } catch (SQLException | ClassNotFoundException exception) {
                throw new RuntimeException("Erro ao abrir conexão JDBC", exception);
            }
        });
        databaseLoader.setSchema(nomeSchema);

        return new DbUnitStatement(databaseLoader, statement);
    }
}