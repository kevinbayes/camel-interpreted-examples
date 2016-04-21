import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Created by kevinb on 21/04/16.
 */
@Component
public class DynamicJavaRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:dynamic_start")
                .routeId("java.dynamic.route")
                .process(exchange -> {
                        System.out.println("Dynamic ---> " + exchange.getIn().getBody());
                    }).to("mock:end");

    }
}
