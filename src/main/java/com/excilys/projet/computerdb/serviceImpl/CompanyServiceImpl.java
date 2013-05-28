package com.excilys.projet.computerdb.serviceImpl;

import com.excilys.projet.computerdb.service.CompanyService;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.projet.computerdb.daoImpl.CompanyDao;
import com.excilys.projet.computerdb.exception.DBException;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.utils.CompaniesList;

@Service
@Transactional(readOnly=true)
public class CompanyServiceImpl implements CompanyService {

	@Autowired
	private CompanyDao companyDao;
	
	@Autowired
	private CompaniesList companiesList;
	
	private final static Logger logger = LoggerFactory.getLogger(CompanyServiceImpl.class);
	
    @Override
	public List<Company> getCompanies() throws DBException {
		List<Company> list;
		try {
                    list = companiesList.getList();
		} catch (DBException e) {
                    throw e;
		}
		return list;
	}
	
	@Transactional(readOnly=false)
    @Override
	public Company updateCompany(Company cie) {
            if(cie != null) {

                if(cie.getId() >= 0) {

                    try {
                        cie = companyDao.update(cie);
                    } catch (DataAccessException e) {
                        logger.warn("Service - update company:"+e.getMessage());
                        logger.warn("Service - update "+ cie);
                    }

                }
                else {
                    
                    try {
                        cie = companyDao.insert(cie);
                    } catch (DataAccessException e) {
                        logger.warn("Service - insert company:"+e.getMessage());
                        logger.warn("Service - insert "+ cie);
                    }
                    
                }

            }

            return cie;
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
