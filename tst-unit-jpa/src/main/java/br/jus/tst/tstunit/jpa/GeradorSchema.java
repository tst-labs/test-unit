package br.jus.tst.tstunit.jpa;

import java.io.Serializable;

import javax.enterprise.inject.spi.CDI;
import javax.persistence.EntityManager;

/**
 * Classe responsável por gerar o <em>schema</em> de banco de dados.
 * 
 * @author Thiago Miranda
 * @since 6 de jul de 2016
 */
public class GeradorSchema implements Serializable {

    private static final long serialVersionUID = -7371478997025466447L;

    /**
     * Cria o <em>schema</em> de banco de dados.
     */
    public void create() {
        CDI.current().select(EntityManager.class).get();
    }
}
