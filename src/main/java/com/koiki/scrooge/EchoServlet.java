package com.koiki.scrooge;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/*
https://stackoverflow.com/questions/18745770/spring-injection-into-servlet
 */
@Slf4j
public class EchoServlet extends AbstractServlet {
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		log.info("yeah");
		response.getWriter().println("Hello!");
	}
}
