package com.excilys.projet.computerdb.servlet;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.excilys.projet.computerdb.exception.DataAccessException;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.service.CompanyService;
import com.excilys.projet.computerdb.utils.ApplicationContextHolder;
import com.mysql.jdbc.StringUtils;

public class UpdateCompanyServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		List<Company> cies = null;
		
		try {
			cies = ApplicationContextHolder.getContext().getBean("companyService", CompanyService.class).getCompanies();

			req.setAttribute("cies", cies);
			
			// gestion des messages d'information (insert/update/delete)
			
			String info = (String) req.getSession().getAttribute("info");
			
			if(!StringUtils.isEmptyOrWhitespaceOnly(info)) {
				req.setAttribute("info", info);
				req.getSession().setAttribute("info", null);
			}
			
			req.getServletContext().getRequestDispatcher("/WEB-INF/companies.jsp").forward(req, resp);
			
		} catch (DataAccessException e) {
			req.setAttribute("exception", e);
			req.getServletContext().getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
		}
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		
		int error = 0;
		
		String sid = req.getParameter("id");
		
		int id = 0;
		
		try {
			id = Integer.parseInt(sid);
		}
		catch (NumberFormatException e) {
			error++;
		}
		
		String name = req.getParameter("name");
		
		if(StringUtils.isEmptyOrWhitespaceOnly(name)) {
			error+=10;
		}
		else {
			Pattern p = Pattern.compile("^[\\w\\s+-/\"\'()]*$");
			Matcher m = p.matcher(name);
			
			if(!m.find()) {
				error+=10;
			}
		}
		
		if(error == 0) {
			
			Company cie = ApplicationContextHolder.getContext().getBean("companyService", CompanyService.class).updateCompany(new Company(id, name));
			
			StringBuilder info = new StringBuilder();
			
			if(cie == null) {
				info.append("Error : Update operation failed");
			}
			else {
				info.append("Done ! Company ").append(cie.getName()).append(" has been ");

				if(id > 0) {
					info.append("updated");
				}
				else {
					info.append("created");
				}
			}

			req.getSession().setAttribute("info", info.toString());
			
			resp.sendRedirect("companies");
			
		}
		else {
			List<Company> cies = null;
			
			try {
				cies = ApplicationContextHolder.getContext().getBean("companyService", CompanyService.class).getCompanies();

				req.setAttribute("cies", cies);

				req.setAttribute("err", error);
				
				req.getServletContext().getRequestDispatcher("/WEB-INF/companies.jsp").forward(req, resp);
				
			} catch (DataAccessException e) {
				req.setAttribute("exception", e);
				req.getServletContext().getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
			}
		}
	}
	
}
