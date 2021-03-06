package br.jus.tst.tstunit.jaxrs.resteasy;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.ClassUtils;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.*;
import org.jglue.cdiunit.ContextController;
import org.slf4j.*;

import br.jus.tst.tstunit.jaxrs.*;

/**
 * Implementação de requisição REST utilizando o RestEasy.
 * 
 * @author Thiago Miranda
 * @since 29 de mar de 2017
 */
public class ResteasyRequest implements MockRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResteasyRequest.class);

    protected Dispatcher dispatcher;

    private HttpMethod httpMethod;
    private String uriTemplate;
    private Object[] pathParams;
    private Map<String, Object[]> queryParams;
    private ContextController contextController;

    private Optional<MediaType> mediaTypeOptional;
    private Optional<InputStream> conteudoOptional;

    public ResteasyRequest(HttpMethod httpMethod, Dispatcher dispatcher, String uriTemplate, ContextController contextController) {
        this.httpMethod = Objects.requireNonNull(httpMethod, "httpMethod");
        this.dispatcher = Objects.requireNonNull(dispatcher, "dispatcher");
        this.uriTemplate = Objects.requireNonNull(uriTemplate, "uriTemplate");
        this.contextController = Objects.requireNonNull(contextController, "contextController");
        this.queryParams = new HashMap<>();

        mediaTypeOptional = Optional.empty();
        conteudoOptional = Optional.empty();
    }

    @Override
    public MockRequest pathParams(Object... params) {
        this.pathParams = Arrays.stream(params).map(this::encodeParam).collect(Collectors.toList()).toArray();
        return this;
    }

    private Object encodeParam(Object param) {
        Object encodedParam;

        try {
            if (ClassUtils.isPrimitiveWrapper(param.getClass())) {
                // parâmetro primitivo - não é necessãrio encoding
                encodedParam = param;
            } else {
                encodedParam = URLEncoder.encode(param.toString(), Charset.defaultCharset().name()).replaceAll("\\+", "%20");
            }
        } catch (UnsupportedEncodingException exception) {
            throw new JaxRsException("Erro ao processar parâmetro: " + param, exception);
        }

        return encodedParam;
    }

    /**
     * @throws NullPointerException
     *             caso qualquer parâmetro seja {@code null}
     */
    @Override
    public MockRequest queryParam(String key, Object... values) {
        queryParams.put(Objects.requireNonNull(key, "key"), Objects.requireNonNull(values, "values"));
        return this;
    }

    /**
     * @throws NullPointerException
     *             caso {@code params} seja {@code null}
     */
    @Override
    public MockRequest queryParams(Map<String, Object[]> params) {
        queryParams.putAll(Objects.requireNonNull(params, "params"));
        return this;
    }

    @Override
    public MockRequest contentType(MediaType contentType) {
        mediaTypeOptional = Optional.ofNullable(contentType);
        return this;
    }

    /**
     * @throws NullPointerException
     *             caso {@code contentType} seja {@code null}
     */
    @Override
    public MockRequest contentType(String contentType) {
        return contentType(MediaType.valueOf(Objects.requireNonNull(contentType, "contentType")));
    }

    /**
     * @throws NullPointerException
     *             caso {@code converter} seja informado {@code null}
     */
    @Override
    @Deprecated
    public MockRequest content(Object conteudo, JsonToObjectConverter converter) {
        this.conteudoOptional = conteudo != null ? Objects.requireNonNull(converter, "converter").objectToJson(conteudo) : Optional.empty();
        return this;
    }

    /**
     * @throws NullPointerException
     *             caso {@code converter} seja informado {@code null}
     */
    @Override
    public MockRequest content(Object conteudo, ObjectToJsonFunction converter) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if (conteudo != null) {
            try {
                Objects.requireNonNull(converter, "converter").apply(stream, conteudo);
            } catch (Exception exception) { // NOSONAR
                throw new JaxRsException("Erro ao converter objeto em JSON: " + conteudo, exception);
            }
        }

        this.conteudoOptional = stream.size() > 0 ? Optional.of(new ByteArrayInputStream(stream.toByteArray())) : Optional.empty();

        return this;
    }

    @Override
    public MockRequest content(byte[] conteudo) {
        this.conteudoOptional = conteudo != null ? Optional.of(new ByteArrayInputStream(conteudo)) : Optional.empty();
        return this;
    }

    @Override
    public MockResponse executar() {
        try {
            MockHttpRequest request = (MockHttpRequest) MethodUtils.invokeExactStaticMethod(MockHttpRequest.class, httpMethod.name().toLowerCase(), uriFormatada());
            MockHttpResponse response = new MockHttpResponse();
            invoke(request, response);
            return new ResteasyResponse(response);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            throw new JaxRsException("Erro ao processar requisição", exception);
        }
    }

    protected String uriFormatada() {
        StringBuilder uriBuilder = new StringBuilder(String.format(uriTemplate, pathParams));

        if (!queryParams.isEmpty()) {
            uriBuilder.append('?').append(1).append('=').append(1);
        }

        queryParams.forEach((key, values) -> Arrays.stream(values).forEach((value) -> uriBuilder.append('&').append(key).append('=').append(value)));

        return uriBuilder.toString();
    }

    protected void invoke(MockHttpRequest request, MockHttpResponse response) {
        mediaTypeOptional.ifPresent(request::contentType);
        conteudoOptional.ifPresent(request::content);

        contextController.openRequest();
        LOGGER.info("{} {}", httpMethod, uriFormatada());
        dispatcher.invoke(request, response);
        contextController.closeRequest();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
}
