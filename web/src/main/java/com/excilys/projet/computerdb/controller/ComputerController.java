package com.excilys.projet.computerdb.controller;

import com.excilys.projet.computerdb.exception.DBException;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.model.Computer;
import com.excilys.projet.computerdb.model.Page;
import com.excilys.projet.computerdb.service.CompanyService;
import com.excilys.projet.computerdb.service.ComputerService;

import com.mysql.jdbc.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/computer")
public class ComputerController {
    
    @Autowired
    private ComputerService computerService;
    
    @Autowired
    private CompanyService companyService;
    
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView listComputers(@RequestParam(required = false) String search, 
                                @RequestParam(value = "page", required = false) String spage,
                                @RequestParam(value = "s", required = false) String sort,
                                @RequestParam(required = false) String info) {
        
        int pageNumber = 0;
		
        if(!StringUtils.isEmptyOrWhitespaceOnly(spage)) {
            try {
                pageNumber = Integer.parseInt(spage);
            }
            catch(NumberFormatException e) {
            }
        }

        if(pageNumber < 0) {
            pageNumber = 0;
        }
        
        
        int s = 1;
		
        if(!StringUtils.isEmptyOrWhitespaceOnly(sort)) {
            try {
                s = Integer.parseInt(sort);
                if(s > 4 || s < -4 || s == 0) {
                    s = 1;
                }
            }
            catch(NumberFormatException e) {
            }
        }
        
        
        ModelAndView mv = new ModelAndView();
        
        try {
            Page page = computerService.loadPage(pageNumber, s, search);

            mv.addObject("page", page);
            mv.addObject("s", s);

            if(!StringUtils.isEmptyOrWhitespaceOnly(info)) {
            	mv.addObject("info", info);
            }

            //mv.setViewName("computer");

        } catch (DBException e) {
            mv.addObject("exception", e);
            mv.setViewName("error");
        }
        
        return mv;
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ModelAndView showComputer(@PathVariable int id) throws DBException {
        
	boolean idOk = false;
	
        ModelAndView mv = new ModelAndView();
        
            try {

                if(id > 0) {

                        Computer cpu = computerService.getComputer(id);

                        if(idOk = (cpu != null)) {

                            List<Company> cies = companyService.getCompanies();

                            mv.addObject("cpu", cpu);
                            mv.addObject("cies", cies);

                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                            mv.addObject("introduced", (cpu.getIntroduced() != null) ? df.format(cpu.getIntroduced().getTime()).toString() : null);
                            mv.addObject("discontinued", (cpu.getDiscontinued() != null) ? df.format(cpu.getDiscontinued().getTime()).toString() : null);

                            mv.setViewName("update");

                        }

                }

            }
            catch (NumberFormatException e){
            }

        if(!idOk) {
            mv.setViewName("redirect:/x/computer");
        }
        
        return mv;
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/{id}")
    public ModelAndView doUpdate(@PathVariable int id,
                                @RequestParam String name,
                                @RequestParam(value = "introduced", required = false) String sintroduced,
                                @RequestParam(value = "discontinued", required = false) String sdiscontinued,
                                @RequestParam(value = "company", required = false) String scompany) throws DBException {
        
        ModelAndView mv = new ModelAndView();

        if(id == 0) {
            mv.setViewName("redirect:/x/home");
        }
        else {
            
            int error = 0;

            if(!StringUtils.isEmptyOrWhitespaceOnly(name)) {

                Pattern p = Pattern.compile("^[\\w\\s+-/\"\'()]*$");
                Matcher m = p.matcher(name);

                if(!m.find()) {
                    error++;
                }

            }
            else {
                error++;
            }
            
            Computer cpu = new Computer(id, name);


            Date introduced = null;

            if(!StringUtils.isNullOrEmpty(sintroduced)) {

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                try {
                    introduced = new Date();
                    df.setLenient(false);
                    introduced.setTime(df.parse(sintroduced).getTime());

                    if(introduced.after(new Date())) {
                        error += 10;
                    }

                } catch (ParseException e) {
                    error += 10;
                }

            }
            
            cpu.setIntroduced(introduced);


            Date discontinued = null;

            if(!StringUtils.isNullOrEmpty(sdiscontinued)) {

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                try {
                    discontinued =new Date();
                    df.setLenient(false);
                    discontinued.setTime(df.parse(sdiscontinued).getTime());

                    if(discontinued.before(cpu.getIntroduced())) {
                        error += 100;
                    }
                    
                } catch (ParseException e) {
                    error += 100;
                }

            }

            cpu.setDiscontinued(discontinued);

            
            Company cie = null;

            if(!StringUtils.isEmptyOrWhitespaceOnly(scompany)) {

                try {
                    int company = Integer.parseInt(scompany);
                    cie = new Company(company, null);
                }
                catch(NumberFormatException e) {
                }

            }
            
            cpu.setCompany(cie);

            
            if(error > 0) {

                mv.addObject("cies", companyService.getCompanies());
                mv.addObject("err", error);
                
                mv.addObject("cpu", cpu);
                mv.addObject("introduced", sintroduced);
                mv.addObject("discontinued", sdiscontinued);
                mv.addObject("company", scompany);
                
                mv.setViewName("update");

            }
            else {
                StringBuilder sb = new StringBuilder("");

                if((computerService.updateComputer(cpu)).getId() == 0) {
                    sb.append("Update operation failed");
                }
                else {
                    sb.append("Computer ").append(cpu.getName()).append(" has been ");

                    if(cpu.getId() > 0) {
                        sb.append("updated");
                    }
                    else {
                        sb.append("created");
                    }
                }

                mv.addObject("info", sb.toString());
                
                mv.setViewName("redirect:/x/computer?search="+cpu.getName());
            }
            
        }
        
        return mv;
    }
    
    @RequestMapping(value = "/{id}/delete")
    public ModelAndView doDelete(@PathVariable int id) {
        
        ModelAndView mv = new ModelAndView();
        
        boolean success = false;
		
        success = computerService.deleteComputer(id);

        if(success) {
            mv.addObject("info", "Done ! Computer has been deleted");
        }
        else {
        	mv.addObject("info", "Error : Delete operation failed");
        }

        mv.setViewName("redirect:/x/computer");
        
        return mv;
    }
    
    @RequestMapping(value = "/new")
    public ModelAndView showNew() throws DBException {
        
        ModelAndView mv = new ModelAndView();
        
        mv.addObject("cies", companyService.getCompanies());
        mv.addObject("cpu", new Computer(-1, ""));
    
        mv.setViewName("update");
            
        return mv;
    }
    
    
    @ExceptionHandler({DBException.class, DataAccessException.class})
    public ModelAndView handleDBException(DBException e) {
    	ModelAndView mv = new ModelAndView();
    	mv.addObject("exception", e);
    	mv.setViewName("error");
    	return mv;
    }
    
    @ExceptionHandler({TypeMismatchException.class})
    public ModelAndView handleWrongParameterType(TypeMismatchException e) {
    	ModelAndView mv = new ModelAndView();
    	mv.addObject("exception", new Exception("Impossible de trouver l'ordinateur associé. L'identifiant renseigné n'est pas valide."));
    	mv.setViewName("error");
    	return mv;
    }
    
}
