package me.bayes;

import static org.junit.Assert.*;

import groovy.util.GroovyScriptEngine;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.ResourceUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CamelDynamicApplication.class)
@WebAppConfiguration
public class CamelDynamicApplicationTests {

	@Autowired
	private CamelContext camelContext;

	@Produce()
	protected ProducerTemplate template;

	@EndpointInject(uri = "mock:js_end")
	protected MockEndpoint end;

	@EndpointInject(uri = "mock:py_end")
	protected MockEndpoint pyEnd;

	@EndpointInject(uri = "mock:g_end")
	protected MockEndpoint gEnd;

	@EndpointInject(uri = "mock:end")
	protected MockEndpoint javaEnd;

	@EndpointInject(uri = "mock:dynamic_end")
	protected MockEndpoint dynamicEnd;

	@Test
	@DirtiesContext
	public void contextLoadsJavascript() throws Exception {

		assertNotNull(camelContext);

		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

		RouteBuilder result = (RouteBuilder)engine.eval(new FileReader(ResourceUtils.getFile("classpath:jsroute.js")));

		camelContext.addRoutes(result);

		camelContext.startRoute("kevin.route");

		template.sendBody("direct:js_start", "Testing JavaScript");
		template.sendBody("direct:start", "Testing Java");

		end.assertIsSatisfied();
		javaEnd.assertIsSatisfied();
	}

	@Test
	@DirtiesContext
	public void testDynamicJava() throws Exception {

		final File source = ResourceUtils.getFile("classpath:DynamicJavaRoute.java");

		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		compiler.run(null, null, null, source.getPath());

		URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { source.getParentFile().toURI().toURL() }, this.getClass().getClassLoader());

		Class<?> cls = Class.forName("DynamicJavaRoute", true, classLoader); // Should print "hello".

		RouteBuilder result = (RouteBuilder) cls.newInstance();

		System.out.println(result);

		camelContext.addRoutes(result);

		camelContext.startRoute("java.dynamic.route");

		template.sendBody("direct:dynamic_start", "Testing Java");

		dynamicEnd.assertIsSatisfied();
	}

	@Test
	@DirtiesContext
	public void contextLoadsPython() throws Exception {

		assertNotNull(camelContext);

		final File source = ResourceUtils.getFile("classpath:pyroute.py");

		PythonInterpreter python = new PythonInterpreter();

		python.execfile(new FileInputStream(source));
		PyObject buildingClass = python.get("PythonRoute");

		RouteBuilder result = (RouteBuilder) buildingClass.__call__().__tojava__(RouteBuilder.class);

		camelContext.addRoutes(result);

		camelContext.startRoute("python.route");

		template.sendBody("direct:py_start", "Testing Python");

		pyEnd.assertIsSatisfied();
	}

	@Test
	@DirtiesContext
	public void contextLoadsGroovy() throws Exception {

		assertNotNull(camelContext);

		final File source = ResourceUtils.getFile("classpath:groute.groovy");

		GroovyScriptEngine engine = new GroovyScriptEngine(new URL[]{source.getParentFile().toURI().toURL()}, this.getClass().getClassLoader());

		Class clazz = engine.loadScriptByName("groute.groovy");

		RouteBuilder result = (RouteBuilder) clazz.newInstance();

		camelContext.addRoutes(result);

		camelContext.startRoute("groovy.route");

		template.sendBody("direct:g_start", "Testing Groovy");

		gEnd.assertIsSatisfied();
	}
}
