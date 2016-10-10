package br.jus.tst.tstunit.jpa;

import javax.persistence.*;

/**
 * Entidade de testes.
 * 
 * @author Thiago Miranda
 * @since 14 de jul de 2016
 */
@Entity
@Table
class Entidade {

    @Id
    private int id;
}