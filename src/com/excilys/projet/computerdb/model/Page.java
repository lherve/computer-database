package com.excilys.projet.computerdb.model;

import java.util.List;

import com.excilys.projet.computerdb.dao.Dao;
import com.excilys.projet.computerdb.dao.Dao.Order;
import com.excilys.projet.computerdb.dao.Dao.Sort;
import com.excilys.projet.computerdb.daoImpl.ComputerDao;

public class Page {

	public static final int SIZE = 10;
	
	private List<Computer> cpus;
	
	private int number, start, end;
	
	private Page previous;
	private Page next;
	
	public Page(int number, boolean toLoad, int s, String search) {
		if(toLoad){
			
			if(number > 0)
				previous = new Page(number - 1);
			else if(number < 0)
				previous = new Page(-(number + 1));
			else
				previous = new Page(number);
			
			if(number < 0)
				next = new Page(-number);
			else
				next = new Page(number + 1);
			
			number = Math.abs(number);
			
			this.start = (number * SIZE) + 1;
			this.end = (number + 1) * SIZE;
			
			Dao.Sort sort = Sort.NAME;
			Dao.Order order = Order.ASC;
			
			if(s < 0) {
				order = Order.DESC;
			}
			
			s = Math.abs(s);
			
			switch(s) {
			case 2:
				sort = Sort.INTRODUCED;
				break;
			case 3:
				sort = Sort.DISCONTINUED;
				break;
			case 4:
				sort = Sort.COMPANY;
				break;
			}
			
			cpus = ComputerDao.I.getFromTo(this.start, this.end, sort, order, search);
		}
		else {
			previous = null;
			next = null;
			
			cpus = null;
		}
		
		this.number = number;
	}
	
	public Page(int number) {
		this.number = number;
	}

	public List<Computer> getCpus() {
		return cpus;
	}

	public void setCpus(List<Computer> cpus) {
		this.cpus = cpus;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Page getPrevious() {
		return previous;
	}

	public void setPrevious(Page previous) {
		this.previous = previous;
	}

	public Page getNext() {
		return next;
	}

	public void setNext(Page next) {
		this.next = next;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
	
	public int getSize() {
		return SIZE;
	}
}
