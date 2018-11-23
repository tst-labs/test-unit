package br.jus.tst.tstunit.jaxrs.resteasy;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

import javax.inject.Qualifier;

/**
 * Qualificador que indica que deve ser utilizada uma implementação do RestEasy.
 * 
 * @author Thiago Miranda
 * @since 29 de mar de 2017
 */
@Qualifier
@Documented
@Retention(RUNTIME)
@Target({ METHOD, TYPE, FIELD })
public @interface Resteasy {

}
