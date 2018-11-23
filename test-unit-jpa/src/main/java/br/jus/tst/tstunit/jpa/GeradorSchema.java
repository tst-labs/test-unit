package br.jus.tst.tstunit.jpa;

import java.util.Map;

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

    /**
     * Obtém propriedades adicionais a serem repassadas diretamente para o framework ORM.
     * 
     * @return as propriedades (pode estar vazio)
     */
    Map<String, String> getPropriedadesAdicionais();
}
