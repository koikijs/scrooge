package com.koiki.scrooge;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

@Slf4j
@Component
public class MyDispatcherServlet extends DispatcherServlet {
	protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.info("i am here");
		super.doService(request, response);
	}
}
