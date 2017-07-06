package br.jus.tst.tstunit.dbunit.script;

import java.io.*;
import java.sql.SQLException;

/**
 * Permite executar scripts SQL em uma conexão JDBC.
 * 
 * @author Thiago Miranda
 * @since 6 de jul de 2017
 *
 */
@FunctionalInterface
public interface ScriptRunner {

    /**
     * Executa um Script SQL.
     *
     * @param reader
     *            fonte do script a ser executado
     * @throws IOException
     *             caso ocorra algum erro de I/O ao processar o script
     * @throws SQLException
     *             caso ocorra algum erro ao executar o script
     */
    void runScript(Reader reader) throws IOException, SQLException;
}