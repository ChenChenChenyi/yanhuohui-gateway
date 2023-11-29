package com.chenyi.yanhuohui.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * GlobalFilter 是一个全局过滤器，应用于所有的路由请求；
 * 而 AbstractGatewayFilterFactory 是用于创建自定义的 GatewayFilter 的抽象类
 */
@Component
@Slf4j
public class IgnoreAuthFilter extends AbstractGatewayFilterFactory<IgnoreAuthFilter.Config> {
    public IgnoreAuthFilter() {
        super(Config.class);
        log.info("IgnoreFilter 进入 IgnoreAuthGatewayFilterFactory ");
    }

    @Override
    public GatewayFilter apply(Config config) {
        log.info("IgnoreFilter 进入  apply");
        /*return (exchange, chain) -> {
            if (!config.isIgnoreGlobalFilter()) {
                return chain.filter(exchange);
            }
            return chain.filter(exchange);
        };*/
        //===============================注意=================================
        //下面的内部类写法，是为了指定过滤器的优先级，要优先于全局过滤器，否则
        //容易造成全局过滤器 拦截到指定 局部过滤器的配置内容。
        return new InnerFilter(config);
    }

    /**
     * 创建一个内部类，来实现2个接口，指定顺序
     * 这里通过Ordered指定优先级
     */
    private class InnerFilter implements GatewayFilter, Ordered {

        private Config config;

        InnerFilter(Config config) {
            this.config = config;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            /*System.out.println("  pre 自定义过滤器工厂 AAAA  " + this.getClass().getSimpleName());
            boolean root = true == config.isIgnoreGlobalFilter();
            if (root) {
                System.out.println("  is root ");
            } else {
                System.out.println("  is no root ");
            }
            // 在then方法里的，相当于aop中的后置通知
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                System.out.println("  post 自定义过滤器工厂 AAAA " + this.getClass().getSimpleName());
            }));*/
            log.info("进入innerFilter=====" + config.isIgnoreGlobalFilter());
            if (config.isIgnoreGlobalFilter() == true) {
                exchange.getAttributes().put(AttrbuteConstant.ATTRIBUTE_IGNORE_TEST_GLOBAL_FILTER, true);
            }
            return chain.filter(exchange);
        }

        @Override
        public int getOrder() {
            return -1000;
        }
    }


    public static class Config{
        boolean ignoreGlobalFilter;

        public boolean isIgnoreGlobalFilter() {
            return ignoreGlobalFilter;
        }

        public void setIgnoreGlobalFilter(boolean ignoreGlobalFilter) {
            this.ignoreGlobalFilter = ignoreGlobalFilter;
        }
    }

    //这个name方法 用来在yml配置中指定对应的过滤器名称
    @Override
    public String name() {
        return "IgnoreAuthFilter";
    }
}
