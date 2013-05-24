package com.excilys.projet.computerdb.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.excilys.projet.computerdb.exception.DBException;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.model.Computer;
import com.excilys.projet.computerdb.service.CompanyService;
import com.excilys.projet.computerdb.utils.ApplicationContextHolder;

public class InsertComputerServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Computer cpu = new Computer(-1, "");
		
		req.setAttribute("cpu", cpu);
		
		List<Company> cies = null;
		
		try {
			cies = ApplicationContextHolder.getContext().getBean("companyService", CompanyService.class).getCompanies();

			req.setAttribute("cies", cies);

			req.getServletContext().getRequestDispatcher("/WEB-INF/update.jsp").forward(req, resp);
			
		} catch (DBException e) {
			
			req.setAttribute("exception", e);
			
			req.getServletContext().getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
			
		}
		
	}
	
}
