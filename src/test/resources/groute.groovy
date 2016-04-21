/**
 * Created by kevinb on 22/04/16.
 */

import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.Processor

class GProcessor implements Processor {

    @Override
    void process(Exchange exchange) throws Exception {

        println("Groovy Test ------> ${exchange.in.body}")

    }
}

class GRoute extends RouteBuilder {

    @Override
    void configure() throws Exception {
        from("direct:g_start")
            .routeId("groovy.route")
            .process(GProcessor())
            .to("mock:g_end")
    }
}