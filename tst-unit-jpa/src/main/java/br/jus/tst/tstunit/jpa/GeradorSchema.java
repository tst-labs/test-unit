package br.jus.tst.tstunit.jpa;

/**
 * Classe responsável por gerar o <em>schema</em> de banco de dados.
 * 
 * @author Thiago Miranda
 * @since 6 de jul de 2016
 */
public interface GeradorSchema {

    /**
     * Cria o <em>schema</em> de banco de dados.
     * 
     * @throws JpaException
     *             caso ocorra algum erro ao criar o <em>schema</em>
     */
    void criar() throws JpaException;

    /**
     * Derruba o <em>schema</em> de banco de dados.
     * 
     * @throws JpaException
     *             caso ocorra algum erro ao derrubar o <em>schema</em>
     */
    void destruir() throws JpaException;
}
