package br.jus.tst.tstunit.jaxrs.resteasy;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.weld.log.LoggerProducer;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.jus.tst.tstunit.TstUnitRunner;
import br.jus.tst.tstunit.cdi.HabilitarCdiAndMockito;
import br.jus.tst.tstunit.jaxrs.JaxRsEngine;

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

    @Test
    public void deveriaProcessarRecursoRest() {
        int valor = 1;

        String resposta = jaxRsEngine.registrarRecurso(stringResource).get("strings/%d").pathParams(valor).executar().deveRetornarObjetoDoTipo(Integer.class)
                .deveRetornarRespostaDoTipo(MediaType.TEXT_PLAIN_TYPE).deveRetornarStatusOk().getConteudoRespostaComoString();

        assertThat(resposta).isEqualTo(String.valueOf(valor));
    }
}
