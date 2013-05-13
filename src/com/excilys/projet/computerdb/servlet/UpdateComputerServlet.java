package com.excilys.projet.computerdb.servlet;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.excilys.projet.computerdb.daoImpl.ComputerDao;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.model.Computer;
import com.excilys.projet.computerdb.service.ComputerService;
import com.mysql.jdbc.StringUtils;

public class UpdateComputerServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String sid = req.getParameter("id");
		
		int id = 0;
		
		boolean idOk = false;
		
		if(!StringUtils.isNullOrEmpty(sid)) {
			
			try {
				id = Integer.parseInt(sid);
				
				if(id > 0) {
					
					Computer cpu = ComputerDao.I.get(id);
					
					if(cpu != null) {
						
						idOk = true;
						
						req.setAttribute("cpu", cpu);
						
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						
						String introduced = cpu.getIntroduced() != null ? df.format(cpu.getIntroduced().getTime()).toString() : null;
						String discontinued = cpu.getDiscontinued() != null ? df.format(cpu.getDiscontinued().getTime()).toString() : null;
						
						req.setAttribute("introduced", introduced);
						req.setAttribute("discontinued", discontinued);
						
						req.setAttribute("cies", ComputerService.I.getCompanies());

						req.getServletContext().getRequestDispatcher("/update.jsp").forward(req, resp);
						
					}
					
				}
				
			}
			catch (NumberFormatException e){
				e.printStackTrace();
			}
			
		}
		
		if(!idOk) {
			resp.sendRedirect("");
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		int id = 0;
		
		try {
			id = Integer.parseInt(req.getParameter("id"));
		}
		catch(NumberFormatException e) {
			e.printStackTrace();
		}
		
		if(id == 0) {
			resp.sendRedirect("");
		}
		else {
		
			// Check each parameter
			
			int error = 0;
			
			String name = req.getParameter("name");
			
			if(!StringUtils.isNullOrEmpty(name)) {
				
				Pattern p = Pattern.compile("^[\\w\\s+-/\"()]*$");
				Matcher m = p.matcher(name);
				
				if(!m.find()) {
					error++;
				}
				
			}
			else {
				error++;
			}
			
			Computer cpu = new Computer(id, name);
			
			
			String sintroduced = req.getParameter("introduced");
			Calendar introduced = null;
			
			if(!StringUtils.isNullOrEmpty(sintroduced)) {
				
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				
				try {
					introduced = Calendar.getInstance();
					df.setLenient(false);
					introduced.setTime(df.parse(sintroduced));

					if(introduced.after(new Date())) {
						error += 10;
					}
					
				} catch (ParseException e) {
					error += 10;
				}
				
			}
			
			cpu.setIntroduced(introduced);
			
			
			String sdiscontinued = req.getParameter("discontinued");
			Calendar discontinued = null;

			if(!StringUtils.isNullOrEmpty(sdiscontinued)) {
				
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				
				try {
					discontinued = Calendar.getInstance();
					df.setLenient(false);
					discontinued.setTime(df.parse(sdiscontinued));
					
					if(discontinued.before(cpu.getIntroduced())) {
						error += 100;
					}
				} catch (ParseException e) {
					error += 100;
				}
				
			}
			
			cpu.setDiscontinued(discontinued);
			
			
			String scompany = req.getParameter("company");
			
			cpu.setCompany(null);
			
			if(!StringUtils.isNullOrEmpty(scompany)) {
				
				try {
					int company = Integer.parseInt(scompany);
					cpu.setCompany(new Company(company, ""));
				}
				catch(NumberFormatException e) {
					System.out.println("Erreur de format lors de la récupération de l'id company : '"+scompany+"'");
				}
				
			}

			
			if(error > 0) {
				req.setAttribute("err", error);
				
				req.setAttribute("cpu", cpu);
				req.setAttribute("introduced", sintroduced);
				req.setAttribute("discontinued", sdiscontinued);
				
				req.setAttribute("cies", ComputerService.I.getCompanies());
				
				req.getServletContext().getRequestDispatcher("/update.jsp").forward(req, resp);
			}
			else {
				ComputerDao.I.update(cpu);
				resp.sendRedirect("?search="+cpu.getName());
			}
		}
		
	}
	
}