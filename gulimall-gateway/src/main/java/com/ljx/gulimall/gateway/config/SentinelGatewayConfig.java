package com.ljx.gulimall.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.fastjson.JSON;
import com.ljx.common.utils.R;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class SentinelGatewayConfig {

    /**
     *
     * 配置网关限流
     */
    public SentinelGatewayConfig() {
        GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {

                R error = R.error(t.getMessage());
                String errorJson = JSON.toJSONString(error);

                Mono<ServerResponse> body = ServerResponse.ok().body(Mono.just(errorJson), String.class);
                return body;
            }
        });
    }

}
