package com.excilys.projet.computerdb.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.excilys.projet.computerdb.exception.DBException;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.model.Computer;
import com.excilys.projet.computerdb.service.CompanyService;

public class InsertComputerServlet extends HttpServlet {

	private ApplicationContext applicationContext;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Computer cpu = new Computer(-1, "");
		
		req.setAttribute("cpu", cpu);
		
		List<Company> cies = null;
		
		try {
			cies = applicationContext.getBean("companyService", CompanyService.class).getCompanies();

			req.setAttribute("cies", cies);

			req.getServletContext().getRequestDispatcher("/WEB-INF/update.jsp").forward(req, resp);
			
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
