package br.jus.tst.tstunit;

/**
 * TODO Javadoc
 * 
 * @author Thiago Miranda
 * @since 4 de jul de 2016
 */
public class TstUnitException extends Exception {

    private static final long serialVersionUID = -4537790044890316767L;

    public TstUnitException(String message, Throwable cause) {
        super(message, cause);
    }

    public TstUnitException(String message) {
        super(message);
    }
}
