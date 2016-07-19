package br.jus.tst.tstunit.jpa;

/**
 * Exceção lançada quando ocorre algum erro durante a execução da extensão JPA.
 * 
 * @author Thiago Miranda
 * @since 18 de jul de 2016
 */
public class JpaException extends RuntimeException {

    private static final long serialVersionUID = -2260819194642576621L;

    /**
     * Cria uma nova exceção com uma mensagem de erro e a causa-raiz.
     * 
     * @param message
     *            a mensagem de erro
     * @param cause
     *            a causa-raiz
     */
    public JpaException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Cria uma nova exceção com uma mensagem de erro.
     * 
     * @param message
     *            a mensagem de erro
     */
    public JpaException(String message) {
        super(message);
    }
}
