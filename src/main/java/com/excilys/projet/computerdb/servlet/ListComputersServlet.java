package com.excilys.projet.computerdb.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.excilys.projet.computerdb.exception.DBException;
import com.excilys.projet.computerdb.model.Page;
import com.excilys.projet.computerdb.service.ComputerService;
import com.excilys.projet.computerdb.utils.ApplicationContextHolder;

import com.mysql.jdbc.StringUtils;

public class ListComputersServlet extends HttpServlet {

	private ApplicationContext applicationContext;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String search = req.getParameter("search");
		
		String spage = req.getParameter("page");
		
		int n = 0;
		
		if(!StringUtils.isEmptyOrWhitespaceOnly(spage)) {
			try {
				n = Integer.parseInt(spage);
			}
			catch(NumberFormatException e) {
			}
		}
		
		if(n < 0) {
			n = 0;
		}
		
		String sort = req.getParameter("s");
		
		int s = 1;
		
		if(!StringUtils.isEmptyOrWhitespaceOnly(sort)) {
			try {
				s = Integer.parseInt(sort);
				if(s > 4 || s < -4 || s == 0) {
					s = 1;
				}
			}
			catch(NumberFormatException e) {
			}
		}

		try {
			Page page = ApplicationContextHolder.getContext().getBean("computerService", ComputerService.class).loadPage(n, s, search);

			req.setAttribute("page", page);
	
			req.setAttribute("s", s);
			
			// gestion des messages d'information (insert/update/delete)
			
			String info = (String) req.getSession().getAttribute("info");
			
			if(!StringUtils.isEmptyOrWhitespaceOnly(info)) {
				req.setAttribute("info", info);
				req.getSession().setAttribute("info", null);
			}
	
			req.getServletContext().getRequestDispatcher("/WEB-INF/list.jsp").forward(req, resp);
			
		} catch (DBException e) {
			req.setAttribute("exception", e);
			req.getServletContext().getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
		}
		
	}
	
	@Override
	public void init() throws ServletException {
		if(applicationContext == null) {
			applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		}
	}
	
}
