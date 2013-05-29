package com.excilys.projet.computerdb.controller;

import com.excilys.projet.computerdb.exception.DBException;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.service.CompanyService;

import com.mysql.jdbc.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/company")
public class CompanyController {
    
    @Autowired
    private CompanyService companyService;
    
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showCompanies() {
        
        ModelAndView mv = new ModelAndView();
        
        try {
            mv.addObject("cies", companyService.getCompanies());
            
            // gestion des messages d'information (insert/update/delete)
            
//            String info = (String) req.getSession().getAttribute("info");
//            
//            if(!StringUtils.isEmptyOrWhitespaceOnly(info)) {
//                    req.setAttribute("info", info);
//                    req.getSession().setAttribute("info", null);
//            }

            mv.setViewName("companies");

        } catch (DBException e) {
            mv.addObject("exception", e);
            mv.setViewName("error");
        }
        
        return mv;
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView doUpdate(@RequestParam(value = "id") String sid,
                                @RequestParam String name) {
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
        
            int error = 0;

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
                
                /*Company cie = */
            	companyService.updateCompany(new Company(id, name));

//                StringBuilder info = new StringBuilder();
//
//                if(cie == null || cie.getId() < 0) {
//                    info.append("Error : Update operation failed");
//                }
//                else {
//                    info.append("Done ! Company ").append(cie.getName()).append(" has been ");
//
//                    if(cie.getId() > 0) {
//                        info.append("updated");
//                    }
//                    else {
//                        info.append("created");
//                    }
//                }

//                req.getSession().setAttribute("info", info.toString());

                mv.setViewName("redirect:/x/company");

            }
            else {
                
                try {
                    mv.addObject("cies", companyService.getCompanies());
                    mv.addObject("err", error);
                    
                    mv.setViewName("companies");

                } catch (DBException e) {
                    mv.addObject("exception", e);
                mv.setViewName("error");
                }
            }
            
        }
        
        return mv;
    }
    
    @RequestMapping(value = "/delete")
    public ModelAndView doDelete(@RequestParam(value = "id") String sid) {
        
        ModelAndView mv = new ModelAndView();
        
        //boolean success = false;

        try {
            int id = Integer.parseInt(sid);
            /*success =*/ companyService.deleteCompany(id);
        } catch (NumberFormatException e){}

//        if(success) {
//                req.getSession().setAttribute("info", "Done ! Company has been deleted");
//        }
//        else {
//                req.getSession().setAttribute("info", "Error : Delete operation failed");
//        }

        mv.setViewName("redirect:/x/company");
        
        return mv;
    }
    
}
