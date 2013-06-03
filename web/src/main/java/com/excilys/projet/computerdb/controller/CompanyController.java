package com.excilys.projet.computerdb.controller;

import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.service.CompanyService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/company")
public class CompanyController {
    
    @Autowired
    private CompanyService companyService;
    
    @RequestMapping(method = RequestMethod.GET)
    public void showCompanies(ModelMap model,
							@RequestParam(required = false) String info) {
        
        model.addAttribute("cies", companyService.getCompanies());
        
        if(info != null && !info.trim().isEmpty()) {
        	model.addAttribute("info", info);
        }
        
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView doUpdate(@RequestParam(value = "id") String sid,
                                @RequestParam String name,
                                RedirectAttributes redirectAttributes) {
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

            if(name == null || name.trim().isEmpty()) {
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
                
                Company cie = companyService.updateCompany(new Company(id, name));

                StringBuilder info = new StringBuilder();

                if(cie == null || cie.getId() < 0) {
                    info.append("Error : Update operation failed");
                }
                else {
                    info.append("Done ! Company ").append(cie.getName()).append(" has been ");

                    if(cie.getId() > 0) {
                        info.append("updated");
                    }
                    else {
                        info.append("created");
                    }
                }

                redirectAttributes.addFlashAttribute("info", info.toString());

                mv.setViewName("redirect:/x/company");

            }
            else {
                mv.addObject("cies", companyService.getCompanies());
                mv.addObject("err", error);
                
                mv.setViewName("company");

            }
            
        }
        
        return mv;
    }
    
    @RequestMapping(value = "/delete")
    public ModelAndView doDelete(@RequestParam(value = "id") String sid, RedirectAttributes redirectAttributes) {

    	ModelAndView mv = new ModelAndView();
        
        boolean success = false;

        try {
            int id = Integer.parseInt(sid);
            success = companyService.deleteCompany(id);
        } catch (NumberFormatException e){}

        if(success) {
        	redirectAttributes.addFlashAttribute("info", "Done ! Company has been deleted");
        }
        else {
        	redirectAttributes.addFlashAttribute("info", "Error : Delete operation failed");
        }

        mv.setViewName("redirect:/x/company");
        
        return mv;
    }

}
