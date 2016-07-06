package br.jus.tst.tstunit.dbunit;

/**
 * 
 * @author Thiago Miranda
 * @since 6 de jul de 2016
 */
public interface GeradorSchema {

    void create();
    
    void drop();
}
