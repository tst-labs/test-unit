package br.jus.tst.tstunit.jaxrs.resteasy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.nio.charset.Charset;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.weld.log.LoggerProducer;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import br.jus.tst.tstunit.TstUnitRunner;
import br.jus.tst.tstunit.cdi.HabilitarCdiAndMockito;
import br.jus.tst.tstunit.jaxrs.*;

/**
 * Testes de integração da {@link ResteasyEngine}.
 * 
 * @author Thiago Miranda
 * @since 30 de mar de 2017
 */
@RunWith(TstUnitRunner.class)
@HabilitarCdiAndMockito
@AdditionalClasses({ ResteasyCdiExtension.class, ResteasyEngine.class, LoggerProducer.class })
public class ResteasyEngineIT {

    @Inject
    @Resteasy
    private JaxRsEngine jaxRsEngine;

    @Inject
    private StringResource stringResource;

    @Before
    public void setUp() {
        jaxRsEngine.registrarRecurso(stringResource);
    }

    @Test
    public void deveriaProcessarRecursoRest() {
        int valor = 1;

        String resposta = jaxRsEngine.get("strings/%d").pathParams(valor).executar()
                .deveRetornarRespostaDoTipo(MediaType.TEXT_PLAIN_TYPE).deveRetornarStatusOk().getConteudoRespostaComoString();

        assertThat(resposta).isEqualTo(String.valueOf(valor));
    }

    @Test
    @Deprecated
    public void deveriaProcessarRecursoRestUtilizandoConverter() {
        int valor = 1;
        String valorString = String.valueOf(valor);

        JsonToObjectConverter converter = mock(JsonToObjectConverter.class);
        when(converter.jsonToObject(anyString(), Mockito.eq(String.class))).thenReturn(valorString);

        String resposta = jaxRsEngine.get("strings/%d").pathParams(valor).executar().deveRetornarRespostaDoTipo(MediaType.TEXT_PLAIN_TYPE).deveRetornarStatusOk()
                .deveRetornarObjetoDoTipo(String.class).getObjetoRespostaUsando(converter);

        assertThat(resposta).isEqualTo(valorString);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deveriaProcessarRecursoRestUtilizandoFunction() throws Exception {
        int valor = 1;
        String valorString = String.valueOf(valor);

        JsonToObjectFunction<String> converter = mock(JsonToObjectFunction.class);
        when(converter.apply(any(InputStream.class))).thenReturn(valorString);

        String resposta = jaxRsEngine.get("strings/%d").pathParams(valor).executar().deveRetornarRespostaDoTipo(MediaType.TEXT_PLAIN_TYPE).deveRetornarStatusOk()
                .getObjetoRespostaUsando(converter);

        assertThat(resposta).isEqualTo(valorString);
        verify(converter).apply(any(InputStream.class));
    }

    @Test
    public void deveriaProcessarCorpoRequisicao() {
        int numero = 1;
        String novoConteudo = "TESTE2";

        String string = jaxRsEngine.put("strings/%d").pathParams(numero).contentType(MediaType.APPLICATION_JSON_TYPE).content(novoConteudo.getBytes(Charset.defaultCharset()))
                .executar().deveRetornarStatusOk().deveRetornarRespostaDoTipo(MediaType.TEXT_PLAIN_TYPE)
                .getConteudoRespostaComoString();

        assertThat(string).contains(String.valueOf(numero));
        assertThat(string).contains(novoConteudo);
    }
}
