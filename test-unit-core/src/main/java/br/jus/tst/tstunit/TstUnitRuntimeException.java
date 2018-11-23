package br.jus.tst.tstunit;

/**
 * Exceção lançada quando ocorre algum erro durante o funcionamento do TST Unit.
 * 
 * @author Thiago Miranda
 * @since 19 de jul de 2016
 */
public class TstUnitRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -4537790044890316767L;

    /**
     * Cria uma nova exceção com uma mensagem de erro e a causa-raiz.
     * 
     * @param message
     *            a mensagem de erro
     * @param cause
     *            a causa-raiz
     */
    public TstUnitRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Cria uma nova exceção com a causa-raiz.
     * 
     * @param cause
     *            a causa-raiz
     */
    public TstUnitRuntimeException(Throwable cause) {
        super(cause);
    }

    /**
     * Cria uma nova exceção com uma mensagem de erro.
     * 
     * @param message
     *            a mensagem de erro
     */
    public TstUnitRuntimeException(String message) {
        super(message);
    }
}
