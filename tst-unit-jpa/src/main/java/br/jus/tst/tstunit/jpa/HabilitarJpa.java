package br.jus.tst.tstunit.jpa;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

/**
 * Habilita o JPA numa classe de teste.
 * 
 * @author Thiago Miranda
 * @since 1 de jul de 2016
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Inherited
@Documented
public @interface HabilitarJpa {

    /**
     * O nome da unidade de persistência utilizada. Normalmente está definida em um arquivo {@code persistence.xml}.
     * 
     * @return o nome da unidade de persistência
     */
    String persistenceUnitName();

    /**
     * Classe utilizada para gerar o <em>schema</em> de banco de dados.
     * 
     * @return a classe
     */
    Class<? extends GeradorSchema> geradorSchema() default GeradorSchemaCdi.class;
}
