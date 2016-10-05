package br.jus.tst.tstunit.jpa.annotation;

/**
 * 
 * @author Thiago Miranda
 * @since 4 de out de 2016
 */
public class AnnotationException extends RuntimeException {

    private static final long serialVersionUID = -600525768586803852L;

    /**
     * 
     * @param message
     */
    public AnnotationException(String message) {
        super(message);
    }

    /**
     * 
     * @param message
     * @param cause
     */
    public AnnotationException(String message, Throwable cause) {
        super(message, cause);
    }
}
