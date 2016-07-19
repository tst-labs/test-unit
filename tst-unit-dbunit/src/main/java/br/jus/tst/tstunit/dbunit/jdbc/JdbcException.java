package br.jus.tst.tstunit.dbunit.jdbc;

/**
 * Exceção lançada quando ocorre algum erro durante operações JDBC.
 * 
 * @author Thiago Miranda
 * @since 18 de jul de 2016
 */
public class JdbcException extends RuntimeException {

    private static final long serialVersionUID = -2260819194642576621L;

    /**
     * Cria uma nova exceção com uma mensagem de erro e a causa-raiz.
     * 
     * @param message
     *            a mensagem de erro
     * @param cause
     *            a causa-raiz
     */
    public JdbcException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Cria uma nova exceção com uma mensagem de erro.
     * 
     * @param message
     *            a mensagem de erro
     */
    public JdbcException(String message) {
        super(message);
    }
}
