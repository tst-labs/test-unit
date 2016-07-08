package br.jus.tst.tstunit.dbunit.jdbc;

import java.sql.*;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * Classe que fornece acesso a conexões JDBC.
 * 
 * @author Thiago Miranda
 * @since 7 de jul de 2016
 */
public class JdbcConnectionSupplier implements Supplier<Connection> {

    private final Properties propriedadesJdbc;

    /**
     * Cria uma nova instância que irá utilizar as propriedades JDBC informadas.
     * 
     * @param propriedadesJdbc
     *            as propriedades JDBC
     */
    public JdbcConnectionSupplier(Properties propriedadesJdbc) {
        this.propriedadesJdbc = propriedadesJdbc;
    }

    @Override
    public Connection get() {
        try {
            Class.forName(propriedadesJdbc.getProperty("driverClass").toString());
            return DriverManager.getConnection(propriedadesJdbc.get("url").toString(), propriedadesJdbc);
        } catch (SQLException | ClassNotFoundException exception) {
            throw new RuntimeException("Erro ao abrir conexão JDBC", exception);
        }
    }
}