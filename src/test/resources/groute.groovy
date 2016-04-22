/**
 * Created by kevinb on 22/04/16.
 */

import org.apache.camel.builder.RouteBuilder

class GRoute extends RouteBuilder {

    @Override
    void configure() throws Exception {
        from("direct:g_start")
            .routeId("groovy.route")
            .process({ exchange -> println("Groovy Test ------> ${exchange.in.body}") })
            .to("mock:g_end")
    }
}