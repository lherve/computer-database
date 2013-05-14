package com.excilys.projet.computerdb.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.excilys.projet.computerdb.model.Page;
import com.excilys.projet.computerdb.service.ComputerService;
import com.mysql.jdbc.StringUtils;

public class ListComputersServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
//		ServletContext application = getServletContext();
//		String filepath = application.getRealPath("WEB-INF/conf/app.properties");
//		File file = new File(filepath);
//		FileInputStream in = new FileInputStream(file);
//		
//		System.out.println(filepath);
		
		String info = (String) req.getSession().getAttribute("info");
		
		if(!StringUtils.isNullOrEmpty(info)) {
			req.setAttribute("info", info);
			req.getSession().setAttribute("info", null);
		}
		
		String search = req.getParameter("search");
		
		int count = ComputerService.I.count(search);
		
		req.setAttribute("count", count);
		
		String spage = req.getParameter("page");
		
		int n = 0;
		
		if(!StringUtils.isNullOrEmpty(spage)) {
			try {
				n = Integer.parseInt(spage);
			}
			catch(NumberFormatException e) {
				e.printStackTrace();
			}
		}
		
		if(n < 0) {
			n = 0;
		}
		else if(n >= (count / Page.SIZE)) {
			n = -count / Page.SIZE;
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

		Page page = ComputerService.I.loadPage(n, s, search);

		if(page.getEnd() > count)
			page.setEnd(count);
		
		if(count == 0)
			page.setStart(0);
		
		req.setAttribute("page", page);

		req.setAttribute("s", s);

		req.getServletContext().getRequestDispatcher("/WEB-INF/list.jsp").forward(req, resp);
	}
	
}
