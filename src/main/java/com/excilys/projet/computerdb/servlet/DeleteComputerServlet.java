package com.excilys.projet.computerdb.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.excilys.projet.computerdb.service.ComputerService;

public class DeleteComputerServlet extends HttpServlet {

	private ApplicationContext applicationContext;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		boolean success = false;
		
		try {
			int id = Integer.parseInt(req.getParameter("id"));
			success = applicationContext.getBean("computerService", ComputerService.class).deleteComputer(id);
			}
		catch (NumberFormatException e){
		}
		
		if(success) {
			req.getSession().setAttribute("info", "Done ! Computer has been deleted");
		}
		else {
			req.getSession().setAttribute("info", "Error : Delete operation failed");
		}
		
		resp.sendRedirect("computers");
	}

	@Override
	public void init() throws ServletException {
		if(applicationContext == null) {
			applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		}
	}
	
}
