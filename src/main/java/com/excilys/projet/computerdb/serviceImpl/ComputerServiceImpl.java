package com.excilys.projet.computerdb.serviceImpl;

import com.excilys.projet.computerdb.service.ComputerService;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.projet.computerdb.dao.Dao.Order;
import com.excilys.projet.computerdb.dao.Dao.Sort;
import com.excilys.projet.computerdb.daoImpl.ComputerDao;
import com.excilys.projet.computerdb.exception.DBException;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.model.Computer;
import com.excilys.projet.computerdb.model.Page;
import com.excilys.projet.computerdb.utils.CompaniesList;

@Service
@Transactional(readOnly=true)
public class ComputerServiceImpl implements ComputerService {

    @Autowired
    private ComputerDao computerDao;

    @Autowired
    private CompaniesList companiesList;

    private final static Logger logger = LoggerFactory.getLogger(ComputerServiceImpl.class);


    /* 
     * Méthode utilisée par UpdateComputer pour récupérer les informations du computer à éditer
     */
    @Override
    public Computer getComputer(int id) throws DBException {
        Computer cpu = null;

        try {
            cpu = computerDao.get(id);
        } catch (DataAccessException e) {
            logger.warn("Service - get computer:"+e.getMessage());
            logger.warn("Service - get "+ id);

            throw new DBException("Problème lors du chargement d'un computer : accès aux données impossible.");
        }

        return cpu;
    }


    /* 
     * Méthode utilisée par UpdateComputer pour l'insertion et la mise-à-jour du computer
     */
    @Transactional(readOnly=false)
    @Override
    public Computer updateComputer(Computer cpu) {
        checkCompany(cpu);

        if(cpu.getId() > 0) {
            
            try {
                cpu = computerDao.update(cpu);
            } catch (DataAccessException e) {
                logger.warn("Service - update computer:"+e.getMessage());
                logger.warn("Service - update "+ cpu);
            }
            
        }
        else {
            
            try {
                cpu = computerDao.insert(cpu);
            } catch (DataAccessException e) {
                logger.warn("Service - insert computer:"+e.getMessage());
                logger.warn("Service - insert "+ cpu);
            }
            
        }

        return cpu;
    }


    /* 
     * Méthode utilisée lors de l'ajout ou la mise-à-jour de computers 
     * pour valider la company associée
     */
    private Company checkCompany(Computer o) {
        if(o.getCompany() != null) {
            Company c = null;
            
            try {
                for(Company e : companiesList.getList()) {
                    if(e.getId() == o.getCompany().getId()) {
                        c = e;
                        break;
                    }
                }
            } catch (DBException e) {}
            
            o.setCompany(c);
        }

        return o.getCompany();
    }


    /* 
     * Méthode utilisée par DeleteComputer pour la suppression du computer
     */
    @Transactional(readOnly=false)
    @Override
    public boolean deleteComputer(int id) {
        boolean result = false;

        try {
            result = computerDao.delete(new Computer(id, null));
        } catch (DataAccessException e) {
            logger.warn("Service - delete computer:"+e.getMessage());
            logger.warn("Service - delete "+ id);
        }

        return result;
    }


    /*
     * Méthode utilisée par ListComputers pour obtenir des computers à afficher
     */
    @Override
    public Page loadPage(int number, int sort, String search) throws DBException {

        Page p = null;

        int count = 0;

        try {
            count = computerDao.count(search);
        } catch (DataAccessException e) {
            logger.warn("Service - load page:"+e.getMessage());
            logger.warn("Service - count total");

            throw new DBException("Problème lors du compte des computers : accès aux données impossible.");
        }

        int maxPage = count / Page.SIZE;

        if(count > 0 && count % Page.SIZE == 0) {
            maxPage--;
        }

        if(number > maxPage) {
            number = maxPage;
        }

        p = new Page(number, search);

        p.setTotal(count);

        p.setNext(number == maxPage ? new Page(maxPage) : new Page(number + 1));
        p.setPrevious(number == 0 ? new Page(0) : new Page(number -1));

        p.setStart(count == 0 ? 0 : number * Page.SIZE + 1);

        int last = ((number + 1) * Page.SIZE);
        p.setEnd(last > count ? count : last);

        if(sort < 0) {
                p.setOrder(Order.DESC);
        }
        else {
                p.setOrder(Order.ASC);
        }

        sort = Math.abs(sort);

        Sort[] sorts = {Sort.NAME, Sort.INTRODUCED, Sort.DISCONTINUED, Sort.COMPANY};
        p.setSort(sorts[sort-1]);

        try {
                p.setCpus(computerDao.getFromTo(p.getStart(), p.getEnd(), p.getSort(), p.getOrder(), p.getSearch()));
        } catch (DataAccessException e) {
                logger.warn("Service - load page:"+e.getMessage());
                logger.warn("Service - load "+ p);

                throw new DBException("Problème lors du chargement des computers : accès aux données impossible.");
        }

        return p;
    }

}
