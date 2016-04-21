/**
 * Created by kevinb on 20/04/16.
 */

var RouteBuilder = Java.type("org.apache.camel.builder.RouteBuilder");
var TheOriginalRouteBuilder = Java.extend(RouteBuilder);

var OriginalProcess = Java.type("org.apache.camel.Processor");
var TheOriginalProcess = Java.extend(OriginalProcess);

var process = new TheOriginalProcess() {
    process: function(exchange) {
        print(exchange.getIn().getBody());
    }
}

var route = new TheOriginalRouteBuilder() {
    configure: function() {
        var parent = Java.super(route);
        parent
            .from("direct:js_start")
            .routeId("kevin.route")
            .process(process)
            .to("mock:js_end");
    }
}

route