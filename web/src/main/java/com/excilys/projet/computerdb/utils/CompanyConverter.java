package com.excilys.projet.computerdb.utils;

import java.beans.PropertyEditorSupport;

import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.service.CompanyService;

public class CompanyConverter extends PropertyEditorSupport {

	private CompanyService companyService;
	
	public CompanyConverter(CompanyService companyService) {
		this.companyService = companyService;
	}
	
	public void setCompanyService(CompanyService companyService) {
		this.companyService = companyService;
	}
	
	@Override
	public String getAsText() {
		Company company = (Company) getValue();
		if(company == null) {
			return null;
		}
		return String.valueOf(company.getId());
	}
	
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if(text != null && !text.trim().isEmpty()) {
			try {
				int companyId = Integer.parseInt(text);
				for(Company cie : companyService.getCompanies()) {
					if(cie.getId() == companyId) {
						setValue(cie);
						break;
					}
				}
			} catch(NumberFormatException e) {}
		}
	}
	
	
}
