package br.jus.tst.tstunit.jaxrs;

/**
 * Exceção lançada quando ocorre algum erro ao converter JSON em objeto Java.
 * 
 * @author Thiago Miranda
 * @since 4 de abr de 2017
 */
public class JsonConverterException extends RuntimeException {

    private static final long serialVersionUID = 8750109369398041343L;

    public JsonConverterException() {
        super();
    }

    public JsonConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonConverterException(String message) {
        super(message);
    }

    public JsonConverterException(Throwable cause) {
        super(cause);
    }

}
