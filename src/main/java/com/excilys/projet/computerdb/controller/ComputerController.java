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

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;

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
                                @RequestParam(value = "s", required = false) String sort) {
        
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

            // gestion des messages d'information (insert/update/delete)
            
//            String info = (String) req.getSession().getAttribute("info");
//
//            if(!StringUtils.isEmptyOrWhitespaceOnly(info)) {
//                    req.setAttribute("info", info);
//                    req.getSession().setAttribute("info", null);
//            }

            mv.setViewName("list");

        } catch (DBException e) {
            mv.addObject("exception", e);
            mv.setViewName("error");
        }
        
        return mv;
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/{sid}")
    public ModelAndView showComputer(@PathVariable String sid) {
        
	boolean idOk = false;
	
        ModelAndView mv = new ModelAndView();
        
	if(!StringUtils.isEmptyOrWhitespaceOnly(sid)) {
	
            try {
                int id = Integer.parseInt(sid);

                if(id > 0) {

                    try {
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
                    catch (DBException e) {
                        mv.addObject("exception", e);
                        mv.setViewName("error");
                    }

                }

            }
            catch (NumberFormatException e){
            }

        }

        if(!idOk) {
            mv.setViewName("redirect:/x/computer");
        }
        
        return mv;
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/{sid}")
    public ModelAndView doUpdate(@PathVariable String sid,
                                @RequestParam String name,
                                @RequestParam(value = "introduced", required = false) String sintroduced,
                                @RequestParam(value = "discontinued", required = false) String sdiscontinued,
                                @RequestParam(value = "company", required = false) String scompany) {
        
        ModelAndView mv = new ModelAndView();
        int id = 0;
		
        try {
            id = Integer.parseInt(sid);
        }
        catch(NumberFormatException e) {
        }

        if(id == 0) {
            mv.setViewName("redirect:/x/home");
        }
        else {
            
            // Check each parameter
			
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

                try {
                    mv.addObject("cies", companyService.getCompanies());
                    mv.addObject("err", error);
                    
                    mv.addObject("cpu", cpu);
                    mv.addObject("introduced", sintroduced);
                    mv.addObject("discontinued", sdiscontinued);
                    mv.addObject("company", scompany);
                    
                    mv.setViewName("update");

                } catch (DBException e) {
                    mv.addObject("exception", e);
                    mv.setViewName("error");
                }

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

//                req.getSession().setAttribute("info", sb.toString());
                
                mv.setViewName("redirect:/x/computer?search="+cpu.getName());
            }
            
        }
        
        return mv;
    }
    
    @RequestMapping(value = "/{sid}/delete")
    public ModelAndView doDelete(@PathVariable String sid) {
        
        ModelAndView mv = new ModelAndView();
        
        boolean success = false;
		
        try {
            int id = Integer.parseInt(sid);
            success = computerService.deleteComputer(id);
        } catch (NumberFormatException e){}

//        if(success) {
//                req.getSession().setAttribute("info", "Done ! Computer has been deleted");
//        }
//        else {
//                req.getSession().setAttribute("info", "Error : Delete operation failed");
//        }

        mv.setViewName("redirect:/x/computer");
        
        return mv;
    }
    
    @RequestMapping(value = "/new")
    public ModelAndView showNew() {
        
        ModelAndView mv = new ModelAndView();
        
        try {
            mv.addObject("cies", companyService.getCompanies());
            mv.addObject("cpu", new Computer(-1, ""));
        
            mv.setViewName("update");
            
        } catch (DBException e) {
            mv.addObject("exception", e);
            mv.setViewName("error");
            
        }
        
        return mv;
    }
    
}
