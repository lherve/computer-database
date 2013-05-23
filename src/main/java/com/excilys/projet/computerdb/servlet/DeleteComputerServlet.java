package com.excilys.projet.computerdb.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.excilys.projet.computerdb.service.ComputerService;
import com.excilys.projet.computerdb.utils.ApplicationContextHolder;

public class DeleteComputerServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		boolean success = false;
		
		try {
			int id = Integer.parseInt(req.getParameter("id"));
			success = ApplicationContextHolder.getContext().getBean("computerService", ComputerService.class).deleteComputer(id);
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

}
