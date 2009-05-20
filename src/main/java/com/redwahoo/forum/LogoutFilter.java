package com.redwahoo.forum;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import edu.yale.its.tp.cas.client.filter.CASFilter;

public class LogoutFilter implements Filter{

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		// continue jforum logout.page
		chain.doFilter(request, response);

		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession();
		session.removeAttribute(CASFilter.CAS_FILTER_USER);
	}

	public void init(FilterConfig config) throws ServletException {
	}

}
