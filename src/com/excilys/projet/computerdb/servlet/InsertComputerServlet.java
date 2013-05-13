package com.excilys.projet.computerdb.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.excilys.projet.computerdb.dao.Dao.Order;
import com.excilys.projet.computerdb.dao.Dao.Sort;
import com.excilys.projet.computerdb.daoImpl.CompanyDao;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.model.Computer;
import com.excilys.projet.computerdb.service.ComputerService;


public class InsertComputerServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Computer cpu = new Computer(-1, "");
		
		req.setAttribute("cpu", cpu);
		
		req.setAttribute("cies", ComputerService.I.getCompanies());
		
		req.getServletContext().getRequestDispatcher("/WEB-INF/update.jsp").forward(req, resp);
		
	}
	
}
