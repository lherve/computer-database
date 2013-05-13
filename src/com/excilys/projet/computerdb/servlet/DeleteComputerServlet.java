package com.excilys.projet.computerdb.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.excilys.projet.computerdb.service.ComputerService;


public class DeleteComputerServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		boolean success = false;
		
		try {
			int id = Integer.parseInt(req.getParameter("id"));
			success = ComputerService.I.deleteComputer(id);
			}
		catch (NumberFormatException e){
			e.printStackTrace();
		}
		
		if(success) {
			resp.sendRedirect("");
		}
		else {
			req.getServletContext().getRequestDispatcher("/update.jsp").forward(req, resp);
		}
	}

}
