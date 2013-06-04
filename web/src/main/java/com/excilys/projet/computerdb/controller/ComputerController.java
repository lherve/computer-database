package com.excilys.projet.computerdb.controller;

import com.excilys.projet.computerdb.exception.DBException;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.model.Computer;
import com.excilys.projet.computerdb.model.Page;
import com.excilys.projet.computerdb.service.CompanyService;
import com.excilys.projet.computerdb.service.ComputerService;
import com.excilys.projet.computerdb.utils.CompanyConverter;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SuppressWarnings("unused")
@Controller
@RequestMapping("/computer")
public class ComputerController {
    
    @Autowired
    private ComputerService computerService;
    
    @Autowired
    private CompanyService companyService;
    
    @RequestMapping(method = RequestMethod.GET)
    public void listComputers(ModelMap model,
    							@RequestParam(required = false) String search, 
                                @RequestParam(value = "page", required = false) String spage,
                                @RequestParam(value = "s", required = false) String sort,
                                @RequestParam(required = false) String info) {
        
        int pageNumber = 0;
		
        if(spage != null && !spage.trim().isEmpty()) {
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
		
        if(sort != null && !sort.trim().isEmpty()) {
            try {
                s = Integer.parseInt(sort);
                if(s > 4 || s < -4 || s == 0) {
                    s = 1;
                }
            }
            catch(NumberFormatException e) {
            }
        }
        
        
        Page page = computerService.loadPage(pageNumber, s, search);

        model.addAttribute("page", page);
        model.addAttribute("s", s);

        if(info != null && !info.trim().isEmpty()) {
        	model.addAttribute("info", info);
        }
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public String showComputer(ModelMap model, @PathVariable int id) {
        
		String viewname = "redirect:/x/computer";
		
        if(id > 0) {

            Computer cpu = computerService.getComputer(id);

            if(cpu != null) {

                model.addAttribute("computer", cpu);
                model.addAttribute("companies", companyService.getCompanies());

                viewname = "update";
                
            }

        }

        return viewname;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{id}")
    public String doUpdate(@ModelAttribute("computer") Computer computer,
    						BindingResult result, ModelMap model,
							RedirectAttributes redirectAttributes) {
    	
		String viewname;
		
		if(result.hasErrors()) {
			model.addAttribute("result", result);
			model.addAttribute("companies", companyService.getCompanies());
			viewname = "update";
		}
		else {
			
			StringBuilder sb = new StringBuilder("");

            if(!computerService.updateComputer(computer)) {
                sb.append("Update operation failed");
            }
            else {
                sb.append("Computer ").append(computer.getName()).append(" has been ");

                if(computer.getId() > 0) {
                    sb.append("updated");
                }
                else {
                    sb.append("created");
                }
            }

            redirectAttributes.addFlashAttribute("info", sb.toString());
            
            viewname = "redirect:/x/computer?search="+computer.getName();
			
		}
		
		return viewname;
    }
    
    @RequestMapping(value = "/{id}/delete")
    public ModelAndView doDelete(@PathVariable int id, RedirectAttributes redirectAttributes) {
        
        ModelAndView mv = new ModelAndView();
        
        boolean success = false;
		
        success = computerService.deleteComputer(id);

        if(success) {
        	redirectAttributes.addFlashAttribute("info", "Done ! Computer has been deleted");
        }
        else {
        	redirectAttributes.addFlashAttribute("info", "Error : Delete operation failed");
        }

        mv.setViewName("redirect:/x/computer");
        
        return mv;
    }
    
    @RequestMapping(value = "/new")
    public ModelAndView showNew() {
        
        ModelAndView mv = new ModelAndView();
        
        mv.addObject("companies", companyService.getCompanies());
        mv.addObject("computer", new Computer(-1, ""));
    
        mv.setViewName("update");
            
        return mv;
    }
    
    @InitBinder
    public void initBinderCompany(WebDataBinder binder) {
    	binder.registerCustomEditor(Company.class, new CompanyConverter(companyService));
    }
    
    @ExceptionHandler({TypeMismatchException.class})
    public ModelAndView handleWrongParameterType() {
    	return new ModelAndView("error", "exception", new Exception("Impossible de trouver l'ordinateur associé. L'identifiant renseigné n'est pas valide."));
    }
    
}
