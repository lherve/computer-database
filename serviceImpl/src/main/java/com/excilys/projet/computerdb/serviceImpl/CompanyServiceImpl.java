package com.excilys.projet.computerdb.serviceImpl;

import com.excilys.projet.computerdb.service.CompanyService;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.projet.computerdb.dao.Dao;
import com.excilys.projet.computerdb.exception.DBException;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.utils.CompaniesList;

@Service
@Transactional(readOnly=true)
public class CompanyServiceImpl implements CompanyService {

	@Autowired
	private Dao<Company> companyDao;
	
	@Autowired
	private CompaniesList companiesList;
	
	private final static Logger logger = LoggerFactory.getLogger(CompanyServiceImpl.class);
	
	@Override
	public List<Company> getCompanies() throws DBException {
		return companiesList.getList();
	}
	
	@Transactional(readOnly=false)
	@Override
	public boolean updateCompany(Company cie) {
        boolean result = false;
		
		if(cie != null) {

            if(cie.getId() >= 0) {

                try {
                	companyDao.update(cie);
                } catch (DataAccessException e) {
                    logger.warn("Service - update company:"+e.getMessage());
                    logger.warn("Service - update "+ cie);
                }

            }
            else {
                
                try {
                	companyDao.insert(cie);
                } catch (DataAccessException e) {
                    logger.warn("Service - insert company:"+e.getMessage());
                    logger.warn("Service - insert "+ cie);
                }
                
            }

        }

        return result;
	}
	
	@Transactional(readOnly=false)
	@Override
	public boolean deleteCompany(int id) {
            boolean result = false;

            try {
                result = companyDao.delete(new Company(id, null));
            } catch (DataAccessException e) {
                logger.warn("Service - delete company:"+e.getMessage());
                logger.warn("Service - delete "+ id);
            }

            return result;
	}
	
}
