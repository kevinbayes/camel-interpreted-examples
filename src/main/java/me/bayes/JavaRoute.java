package me.bayes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Created by kevinb on 21/04/16.
 */
@Component
public class JavaRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:start")
                .routeId("java.route")
                .process(new Processor() {

                    @Override
                    public void process(Exchange exchange) throws Exception {
                        System.out.println(exchange.getIn().getBody());
                    }
                }).to("mock:end");

    }
}
