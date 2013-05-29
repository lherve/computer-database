package com.excilys.projet.computerdb.service;

import com.excilys.projet.computerdb.exception.DBException;
import com.excilys.projet.computerdb.model.Company;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface CompanyService {

    @Transactional(readOnly = false)
    boolean deleteCompany(int id);

    List<Company> getCompanies() throws DBException;

    @Transactional(readOnly = false)
    Company updateCompany(Company cie);
    
}
