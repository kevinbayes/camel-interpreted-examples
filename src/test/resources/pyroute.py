from org.apache.camel.builder import RouteBuilder
from org.apache.camel import Processor

class PythonProcessor(Processor):
    def process(self, exchange):
        print "Python route ----> " + exchange.getIn().getBody()


class PythonRoute(RouteBuilder):
    def configure(self):
        self.fromF("direct:py_start").routeId("python.route").process(PythonProcessor()).to("mock:py_end")