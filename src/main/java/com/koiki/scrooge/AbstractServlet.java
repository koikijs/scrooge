package com.koiki.scrooge;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

public class AbstractServlet extends HttpServlet {
	protected AutowireCapableBeanFactory ctx;

	@Override
	public void init() throws ServletException {
		super.init();
		ctx = ((ApplicationContext) getServletContext().getAttribute(
				"applicationContext")).getAutowireCapableBeanFactory();
		//The following line does the magic
		ctx.autowireBean(this);

	}
}
