package br.jus.tst.tstunit.dbunit.dataset;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

/**
 * Permite que sejam definidos múltiplos arquivos de dados do DBUnit em um mesmo teste.
 * 
 * @author Thiago Miranda
 * @since 5 de mai de 2017
 */
@Documented
@Inherited
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface UsarDataSets {

    /**
     * As configurações dos arquivos de dados.
     * 
     * @return as configurações
     */
    UsarDataSet[] value();
}
