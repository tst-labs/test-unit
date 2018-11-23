package br.jus.tst.tstunit.jpa.annotation;

/**
 * Exceção lançada quando ocorre algum erro no processamento de anotações.
 * 
 * @author Thiago Miranda
 * @since 4 de out de 2016
 */
public class AnnotationException extends RuntimeException {

    private static final long serialVersionUID = -600525768586803852L;

    /**
     * Cria uma nova exceção com uma mensagem descritiva.
     * 
     * @param message
     *            a mensagem
     * @see RuntimeException#RuntimeException(String)
     */
    public AnnotationException(String message) {
        super(message);
    }

    /**
     * Cria uma nova exceção com uma mensagem descritiva e a causa-raíz.
     * 
     * @param message
     *            a mensagem
     * @param cause
     *            a causa-raíz
     * @see RuntimeException#RuntimeException(String, Throwable)
     */
    public AnnotationException(String message, Throwable cause) {
        super(message, cause);
    }
}
