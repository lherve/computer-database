package com.excilys.projet.computerdb.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.excilys.projet.computerdb.service.CompanyService;
import com.excilys.projet.computerdb.utils.ApplicationContextHolder;

public class DeleteCompanyServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendRedirect("companies");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		boolean success = false;

		try {
			int id = Integer.parseInt(req.getParameter("id"));
			success = ApplicationContextHolder.getContext().getBean("companyService", CompanyService.class).deleteCompany(id);
			}
		catch (NumberFormatException e){
		}
		
		if(success) {
			req.getSession().setAttribute("info", "Done ! Company has been deleted");
		}
		else {
			req.getSession().setAttribute("info", "Error : Delete operation failed");
		}
		
		resp.sendRedirect("companies");
	}
	
}
