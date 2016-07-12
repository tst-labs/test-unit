TST Unit
==============================

Biblioteca que auxilia no desenvolvimento de testes unitários e de integração.

---

Uso
----------

Para utilizar o TST Unit em seu projeto, adicione o módulo abaixo como dependência:

```xml
<dependency>
    <groupId>br.jus.tst</groupId>
    <artifactId>tst-unit-core</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

Em seguida, é necessário ter um arquivo `tstunit.properties` em seu _classpath_ (normalmente em `src/test/resources`). No momento, esse arquivo pode ser criado vazio.

E então, utilize a classe `TSTUnitRunner` para rodar seus testes:

```java
package br.jus.tst.teste;

@RunWith(TstUnitRunner.class)
public class MinhaClasseTeste {

    @Test
    public void teste() {
        // ...
    }
}
```

Fazendo apenas isso, seu teste passa a contar com alguns recursos básicos, como a impressão dos nomes dos testes que estão rodando:

```
>>>>>>>>>> Executando: br.jus.tst.teste.MinhaClasseTeste.teste <<<<<<<<<<
```

Esse recurso pode ser desativado ou customizado através da anotação `@ImprimirNomeTeste`.

Para utilizar outros recursos, você pode adicionar extensões, que adicionam novas funcionalidades ao TST Unit.

### Extensões

As extensões atualmente existentes são:

* _tst-unit-cdi_: habilita o uso de [CDI](http://weld.cdi-spec.org/) nos testes (obs.: como muitas vezes o uso de CDI é feito em conjunto com mocks, o Mockito também já é habilitado por padrão através dessa extensão, não sendo necessário o uso da _tst-unit-mockito_);
* _tst-unit-dbunit_: habilita o uso do [DBUnit](http://dbunit.sourceforge.net/) nos testes;
* _tst-unit-mockito_: habilita o uso do [Mockito](http://mockito.org/) nos testes;
* _tst-unit-jpa_: habilita o uso de JPA nos testes.

Para usar uma extensão, basta adicionar a respectiva dependência ao seu projeto:

```xml
<dependency>
    <groupId>br.jus.tst</groupId>
    <artifactId>tst-unit-dbunit</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

Cada extensão define uma anotação que pode ser incluída em suas classes de testes para ativá-la:

```java
package br.jus.tst.teste;

@RunWith(TstUnitRunner.class)
@HabilitarXxx
public class MinhaClasseTeste {

    @Test
    public void teste() {
        // ...
    }
}
```

#### TST Unit CDI

Habilita o CDI nos testes. Observar que as precondições para funcionamento do CDI devem ser seguidas (como ter um `beans.xml` no seu _classpath_). Por comodidade, essa extensão já habilita o uso do Mockito nos testes. É utilizado o [Weld](http://weld.cdi-spec.org/) como implementação do CDI.

A maior parte do funcionamento é delegado para o [CDI Unit](http://jglue.org/cdi-unit/).

##### Dependência

```xml
<dependency>
    <groupId>br.jus.tst</groupId>
    <artifactId>tst-unit-cdi</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

##### Uso

Além da anotação `@HabilitarCdiAndMockito`, as anotações do CDI Unit podem ser usadas normalmente. Ver [CDI Unit User Guide](http://jglue.org/cdi-unit-user-guide/).

```java
package br.jus.tst.teste;

@RunWith(TstUnitRunner.class)
@HabilitarCdiAndMockito
@AdditionalClasses({ MeuProdutor.class }) // anotação do CDI Unit
public class MinhaClasseTeste {

    @Inject
    private MinhaClasse instancia;
    
    @Produces
    @ProducesAlternative
    @Mock
    private MeuRecursoExterno recurso;

    @Test
    public void teste() {
        // ...
    }
}
```

#### TST Unit DBUnit

##### Dependência

```xml
<dependency>
    <groupId>br.jus.tst</groupId>
    <artifactId>tst-unit-dbunit</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

##### Uso

Além da anotação `@HabilitarDbUnit`, existe um outro conjunto de anotações que podem ser úteis para a execução do teste:

* `@RodarScriptAntes`: define um arquivo de script a ser executado antes de um método de teste ou antes de todos os métodos de teste de uma classe. Por padrão, o arquivo é procurado dentro de um diretório `scripts` no _classpath_.
* `@RodarScriptDepois`: define um arquivo de script a ser executado após um método de teste ou após todos os métodos de teste de uma classe. Por padrão, o arquivo é procurado dentro de um diretório `scripts` no _classpath_.
* `@UsarDataSet`: define um arquivo de _dataset_ do DBUnit a ser utilizado pelo teste. O banco é carregado com os dados definidos no arquivo antes da execução do teste (operação _CLEAN-INSERT_). Por padrão, o arquivo é procurado dentro de um diretório `datasets` no _classpath_.

```java
package br.jus.tst.teste;

@RunWith(TstUnitRunner.class)
@HabilitarDbUnit(nomeSchema = "TT")
@RodarScriptAntes("criar-schema.sql")
@RodarScriptDepois("drop-schema.sql")
public class MinhaClasseTeste {

    @Inject
    private MinhaClasse instancia;

    @Test
    @UsarDataSet("meus-dados.xml")
    public void teste() {
        // ...
    }
}
```

Essa extensão também necessita que algumas configurações sejam definidas no arquivo `tstunit.properties`:

```
# Parâmetros obrigatórios:

# Nome da classe de driver SQL a ser utilizada para abrir conexões JDBC
jdbc.driverClass=
# URL do banco de dados
jdbc.url=
# Usuário de banco de dados
jdbc.user=
# Senha do usuário de banco de dados
jdbc.password=

# Parâmetros opcionais:

# Diretório no classpath onde serão buscados os arquivos de dataset
dbunit.datasets.dir=
# Diretório no classpath onde serão buscados os arquivos de script
dbunit.scripts.dir=
# Nome da classe de DataType factory - ver http://dbunit.sourceforge.net/properties.html#DataType_factory
dbunit.dataTypeFactoryClass=
```

#### TST Unit JPA

##### Dependência

```xml
<dependency>
    <groupId>br.jus.tst</groupId>
    <artifactId>tst-unit-jpa</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

##### Uso

Utilize a anotação `@HabilitarJpa` em seus testes.

Atualmente, essa extensão só pode ser usada em conjunto com a _TST Unit CDI_. Sendo assim, normalmente seus testes que a utilizem ficarão com uma estrutura semelhante à essa:

```java
package br.jus.tst.teste;

@RunWith(TstUnitRunner.class)
@HabilitarCdiAndMockito
@AdditionalPackages({ TestEntityManagerProducer.class }) // adiciona o produtor de EntityManager ao classpath do CDI Unit
@HabilitarJpa(persistenceUnitName = "meuPU")
public class MinhaClasseTeste {

    // @Inject
    // private EntityManager em;
    @Inject
    private MinhaClasseQueUsaEntityManager instancia;

    @Test
    public void teste() {
        // ...
    }
}
```

Caso você queira usar essa extensão em conjunto com a _TST Unit DBUnit_, é possível gerar o _schema_ de banco antes de cada teste:

```java
package br.jus.tst.teste;

@RunWith(TstUnitRunner.class)
@HabilitarCdiAndMockito
@AdditionalPackages({ TestEntityManagerProducer.class })
@HabilitarDbUnit
@HabilitarJpa(persistenceUnitName = "meuPU")
public class MinhaClasseTeste {

    @Inject
    private MinhaClasseQueUsaEntityManager instancia;

    @Test
    @UsarDataSet("meu-dataset.xml")
    public void teste() {
        // ...
    }
}
```

Basta definir a propriedade `hibernate.hbm2ddl.auto` no seu arquivo `persistence.xml` utilizado pelos testes com o valor `create`:

```xml
<persistence-unit name="meuPU">

	...
	
    <properties>
    	...
    	<property name="hibernate.hbm2ddl.auto" value="create" />
       ...
    </properties>
</persistence-unit>
```

OBS.: O valor `create-drop` não é suportado dessa forma pois o JPA irá derrubar o _schema_ assim que o último `EntityManager` for fechado, ocasionando erros na execução do _TST Unit DBUnit_, que irá tentar limpar o banco de dados em seguida.

#### TST Unit Mockito

##### Dependência

```xml
<dependency>
    <groupId>br.jus.tst</groupId>
    <artifactId>tst-unit-mockito</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

##### Uso

```java
package br.jus.tst.teste;

@RunWith(TstUnitRunner.class)
@HabilitarMockito
public class MinhaClasseTeste {

    @Mock
    private MinhaClasse instancia;

    @Test
    public void teste() {
        // ...
    }
}
```
