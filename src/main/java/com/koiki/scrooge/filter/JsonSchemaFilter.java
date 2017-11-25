package com.koiki.scrooge.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonSchemaFilter implements Filter {
	private final RequestMappingHandlerMapping handlerMapping;
	private final ObjectMapper mapper;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException { }

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		try {

			HttpServletRequest httpRequest = (HttpServletRequest) request;

			if (!"true".equalsIgnoreCase(httpRequest.getHeader("json-schema"))) {
				// proceed the normal process
				chain.doFilter(request, response);

			} else {
				// proceed to return JSON schema
				Optional<Map.Entry<RequestMappingInfo, HandlerMethod>> matchedRequestMappingEntry = handlerMapping.getHandlerMethods()
						.entrySet()
						.stream()
						.filter(e -> e.getKey().getMatchingCondition(httpRequest) != null)
						.findFirst();

				if (!matchedRequestMappingEntry.isPresent()) {
					// proceed the normal process
					log.debug("could not find matched request mapping, request path: {}, request method: {}",
							httpRequest.getPathInfo(), httpRequest.getMethod());
					chain.doFilter(request, response);

				} else {
					HandlerMethod handlerMethod = matchedRequestMappingEntry.get().getValue();
					Class<?> requestBodyClass = handlerMethod.getMethodParameters()[0].getParameterType();

					JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
					JsonSchema schema = schemaGen.generateSchema(requestBodyClass);
					String schemaString = mapper.writeValueAsString(schema);

					// return JSON Schema
					response.getWriter().print(schemaString);
				}
			}

		} catch (Exception e) {
			log.error("Exception happened when proceeding json-schema", e);
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() { }
}