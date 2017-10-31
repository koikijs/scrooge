package com.koiki.scrooge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Slf4j
@Component
public class MyFilter implements Filter {
	/**
	 * https://stackoverflow.com/questions/10898056/how-to-find-all-controllers-in-spring-mvc
	 */
	@Autowired
	private RequestMappingHandlerMapping handlerMapping;
	@Autowired
	private ObjectMapper mapper;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("init!!");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("Before!!");
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String jsonSchema = httpRequest.getHeader("json-schema");
		Map<RequestMappingInfo, HandlerMethod> aaa = handlerMapping.getHandlerMethods();
		log.info(aaa.toString());
		RequestMappingInfo requestInfo = new RequestMappingInfo(
				new PatternsRequestCondition("/events"),
				new RequestMethodsRequestCondition(RequestMethod.GET),
				null,
				null,
				null,
				null,
				null);
		Map.Entry<RequestMappingInfo, HandlerMethod> bbb = aaa.entrySet()
				.stream()
				.filter(e -> e.getKey().getPatternsCondition().getPatterns().contains("/events")
							 && e.getKey().getMethodsCondition().getMethods().contains(RequestMethod.POST))
				.findFirst()
				.orElse(null);
		log.info("test:" + bbb.toString());

		HandlerMethod ccc = bbb.getValue();
Class<?> requestClazz = ccc.getMethodParameters()[0].getParameterType();

		mapper.registerModule(new Hibernate5Module());

		JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
		JsonSchema schema = schemaGen.generateSchema(requestClazz);
String schemaString = mapper.writeValueAsString(schema);
		log.info(schemaString);

		if (! "true".equals(jsonSchema)) {
			chain.doFilter(request, response);
		} else {
			response.getWriter().print(schemaString);
		}
		System.out.println("After!!");
	}

	@Override
	public void destroy() {
		System.out.println("destroy!!");
	}
}