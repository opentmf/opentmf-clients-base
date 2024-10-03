package com.pia.tmf.common.config;

import com.pia.client.common.model.BaseClientProperties;
import com.pia.client.common.service.api.TokenService;
import com.pia.tmf.common.client.impl.TmfClientBaseImpl;
import com.pia.tmf.common.exception.TmfClientException;
import com.pia.tmf.common.helper.TestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Gokhan Demir
 */
@RequiredArgsConstructor
@Getter
@Slf4j
public class TestTmfClientImpl<C, U, R>
    extends TmfClientBaseImpl<C, U, R>
    implements TestTmfClient<C, U, R> {

    private final Class<R> typeClass;
    private final TmfClientConfigurations.TmfClientConfig config;
    private final WebClient webClient;
    private final TokenService tokenService;
    private final BaseClientProperties clientProperties;

    @Override
    protected TmfClientConfigurations.TmfClientConfig getClientConfig() {
        return this.config;
    }

    @Override
    protected Class<R> getType() {
        return typeClass;
    }

    @Override
    protected Class<? extends TmfClientException> getExceptionType() {
        return TestException.class;
    }
}
