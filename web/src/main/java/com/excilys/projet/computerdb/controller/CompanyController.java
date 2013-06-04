package com.excilys.projet.computerdb.controller;

import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.service.CompanyService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.ModelAttribute;
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
        
        model.addAttribute("companies", companyService.getCompanies());
        model.addAttribute("company", new Company());
        
        if(info != null && !info.trim().isEmpty()) {
        	model.addAttribute("info", info);
        }
        
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String doUpdate(@ModelAttribute("company") Company company,
								BindingResult result, ModelMap model,
								RedirectAttributes redirectAttributes) {
        
    	String viewname;
    	
    	if(result.hasErrors()) {
    		model.addAttribute("result", result);
            model.addAttribute("companies", companyService.getCompanies());
            viewname = "company";
    	}
    	else {
    		
    		StringBuilder info = new StringBuilder();

            if(!companyService.updateCompany(company)) {
                info.append("Error : Update operation failed");
            }
            else {
                info.append("Done ! Company ").append(company.getName()).append(" has been ");

                if(company.getId() > 0) {
                    info.append("updated");
                }
                else {
                    info.append("created");
                }
            }

            redirectAttributes.addFlashAttribute("info", info.toString());

            viewname = "redirect:/x/company";

    	}
    	
    	return viewname;
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
