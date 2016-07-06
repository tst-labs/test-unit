package br.jus.tst.tstunit.mockito;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

/**
 * Habilita o Mockito em uma classe de teste.
 * 
 * @author Thiago Miranda
 * @since 1 de jul de 2016
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Inherited
@Documented
public @interface HabilitarMockito {

}
