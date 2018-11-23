package br.jus.tst.tstunit.jaxrs;

/**
 * Exceção lançada quando ocorre algum erro em tempo de execução no TEST Unit JAX-RS.
 * 
 * @author Thiago Miranda
 * @since 29 de mar de 2017
 */
public class JaxRsException extends RuntimeException {

    private static final long serialVersionUID = 5631954278957200201L;

    public JaxRsException(String message, Throwable cause) {
        super(message, cause);
    }
}
