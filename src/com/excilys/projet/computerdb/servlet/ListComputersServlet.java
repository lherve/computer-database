package com.excilys.projet.computerdb.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.excilys.projet.computerdb.daoImpl.ComputerDao;
import com.excilys.projet.computerdb.model.Page;
import com.mysql.jdbc.StringUtils;

public class ListComputersServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String search = req.getParameter("search");
		
		int count = ComputerDao.I.count(search);
		
		req.setAttribute("count", count);
		
		String spage = req.getParameter("page");
		
		int p = 0;
		
		if(!StringUtils.isNullOrEmpty(spage)) {
			try {
				p = Integer.parseInt(spage);
			}
			catch(NumberFormatException e) {
				e.printStackTrace();
			}
		}
		
		if(p < 0) {
			p = 0;
		}
		else if(p >= (count / Page.SIZE)) {
			p = -count / Page.SIZE;
		}
		
		String sort = req.getParameter("s");
		
		int s = 1;
		
		if(!StringUtils.isNullOrEmpty(sort)) {
			try {
				s = Integer.parseInt(sort);
			}
			catch(NumberFormatException e) {
				e.printStackTrace();
			}
		}

		Page page = new Page(p, true, s, search);

		if(page.getEnd() > count)
			page.setEnd(count);
		
		if(count == 0)
			page.setStart(0);
		
		req.setAttribute("page", page);

		req.setAttribute("s", s);
		
		req.getServletContext().getRequestDispatcher("/list.jsp").forward(req, resp);
	}
	
}
