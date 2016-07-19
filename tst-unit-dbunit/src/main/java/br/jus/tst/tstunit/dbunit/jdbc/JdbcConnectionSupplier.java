package br.jus.tst.tstunit.dbunit.jdbc;

import java.sql.*;
import java.util.*;
import java.util.function.Supplier;

import org.apache.commons.lang3.*;

/**
 * Classe que fornece acesso a conexões JDBC.
 * 
 * @author Thiago Miranda
 * @since 7 de jul de 2016
 */
public class JdbcConnectionSupplier implements Supplier<Connection> {

    private static final String NOME_PROPRIEDADE_DRIVER_CLASS = "driverClass";
    private static final String NOME_PROPRIEDADE_DB_URL = "url";

    private final Properties propriedadesJdbc;

    /**
     * Cria uma nova instância que irá utilizar as propriedades JDBC informadas.
     * 
     * @param propriedadesJdbc
     *            as propriedades JDBC
     * @throws NullPointerException
     *             caso seja informado {@code null}
     */
    public JdbcConnectionSupplier(Properties propriedadesJdbc) {
        this.propriedadesJdbc = Objects.requireNonNull(propriedadesJdbc, "propriedadesJdbc");
    }

    /**
     * @throws JdbcException
     *             caso ocorra algum erro ao abrir a conexão JDBC
     */
    @Override
    public Connection get() {
        try {
            Class.forName(validarPropriedadeExistente(NOME_PROPRIEDADE_DRIVER_CLASS));
            return DriverManager.getConnection(validarPropriedadeExistente(NOME_PROPRIEDADE_DB_URL), propriedadesJdbc);
        } catch (SQLException | ClassNotFoundException exception) {
            throw new JdbcException("Erro ao abrir conexão JDBC", exception);
        }
    }

    private String validarPropriedadeExistente(String nomePropriedade) {
        String valorPropriedade = (String) propriedadesJdbc.getProperty(nomePropriedade);
        Validate.validState(StringUtils.isNotBlank(valorPropriedade), "Propriedade '%s' não definida", nomePropriedade);
        return valorPropriedade;
    }
}