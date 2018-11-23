package br.jus.tst.tstunit.jpa;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

import javax.inject.Qualifier;

/**
 * Qualificador utilizado para indicar que existe uma única instância do tipo anotado.
 * 
 * @author Thiago Miranda
 * @since 6 de out de 2016
 */
@Qualifier
@Retention(RUNTIME)
@Target(FIELD)
@Inherited
@Documented
public @interface Unico {

}
