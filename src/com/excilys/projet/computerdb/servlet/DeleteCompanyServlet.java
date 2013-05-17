package com.excilys.projet.computerdb.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.excilys.projet.computerdb.service.CompanyService;

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
			success = CompanyService.I.deleteCompany(id);
			}
		catch (NumberFormatException e){
			e.printStackTrace();
		}
		
		if(success) {
			req.getSession().setAttribute("info", "Company has been deleted");
		}
		else {
			req.getSession().setAttribute("info", "Delete operation failed");
		}
		
		resp.sendRedirect("companies");
	}
	
}
