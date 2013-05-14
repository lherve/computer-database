package com.excilys.projet.computerdb.service;

import java.util.List;

import com.excilys.projet.computerdb.dao.Dao.Order;
import com.excilys.projet.computerdb.dao.Dao.Sort;
import com.excilys.projet.computerdb.daoImpl.CompanyDao;
import com.excilys.projet.computerdb.daoImpl.ComputerDao;
import com.excilys.projet.computerdb.model.Company;
import com.excilys.projet.computerdb.model.Computer;
import com.excilys.projet.computerdb.model.Page;

public enum ComputerService {

	I;

	public Computer updateComputer(Computer cpu) {
		Computer c = null;

		if(cpu.getId() > 0) {
			c = ComputerDao.I.update(cpu);
		}
		else {
			c = ComputerDao.I.insert(cpu);
		}
		
		return c;
	}
	
	public boolean deleteComputer(int id) {
		Computer cpu = new Computer(id, "");
		return ComputerDao.I.delete(cpu);
	}

	public List<Company> getCompanies() {
		return CompanyDao.I.getAll(Sort.NAME, Order.ASC);
	}

	public int count(String search) {
		return ComputerDao.I.count(search);
	}
	
	public Page loadPage(int number, int sort, String search) {
		Page p = new Page(number, search);

		if(number > 0) {
			p.setPrevious(new Page(number - 1));
			p.setNext(new Page(number + 1));
		}
		else {
			if(number < 0) {
				p.setPrevious(new Page(-(number + 1)));
			}
			else {
				p.setPrevious(new Page(number));
			}
			p.setNext(new Page(-number));
		}
		
		number = Math.abs(number);
		
		p.setStart(number * Page.SIZE + 1);
		p.setEnd((number + 1) * Page.SIZE);
		
		if(sort < 0) {
			p.setOrder(Order.DESC);
		}
		else {
			p.setOrder(Order.ASC);
		}
		
		sort = Math.abs(sort);
		
		switch(sort) {
		case 2:
			p.setSort(Sort.INTRODUCED);
			break;
		case 3:
			p.setSort(Sort.DISCONTINUED);
			break;
		case 4:
			p.setSort(Sort.COMPANY);
			break;
		default:
			p.setSort(Sort.NAME);
		}
		
		p.setCpus(ComputerDao.I.getFromTo(p.getStart(), p.getEnd(), p.getSort(), p.getOrder(), p.getSearch()));
	
		return p;
	}

}
