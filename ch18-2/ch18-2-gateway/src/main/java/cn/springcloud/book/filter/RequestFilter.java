package cn.springcloud.book.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;



public class RequestFilter implements GatewayFilter, Ordered {


    private static final String COUNT_Start_TIME = "countStartTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        URI requestUri = request.getURI();
        //只记录 http 请求(包含 https)
        String schema = requestUri.getScheme();
        if ((!"http".equals(schema) && !"https".equals(schema))) {
            return chain.filter(exchange);
        }
        String method = request.getMethodValue();
        String contentType = request.getHeaders().getFirst("Content-Type");
        //此处要排除流文件类型,比如上传的文件
        if ("POST".equals(method) && !contentType.startsWith("multipart/form-data")) {
            String bodyStr = resolveBodyFromRequest(request);
        }

        exchange.getAttributes().put(COUNT_Start_TIME, System.currentTimeMillis());
        return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {
                    Long startTime = exchange.getAttribute(COUNT_Start_TIME);
                    Long endTime=(System.currentTimeMillis() - startTime);
                    if (startTime != null) {
                    }
                })
        );
    }

    /**
     * 获取请求体中的字符串内容
     * @param serverHttpRequest
     * @return
     */
    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest){
        //获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        StringBuilder sb = new StringBuilder();

        body.subscribe(buffer -> {
            byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
            DataBufferUtils.release(buffer);
            String bodyString = new String(bytes, StandardCharsets.UTF_8);
            sb.append(bodyString);
        });
        return sb.toString();

    }

    @Override
    public int getOrder() {
        return 1;
    }
}