package com.excilys.projet.computerdb.model;

import java.util.List;

import com.excilys.projet.computerdb.dao.Dao;
import com.excilys.projet.computerdb.dao.Dao.Order;
import com.excilys.projet.computerdb.dao.Dao.Sort;


public class Page {

	public static final int SIZE = 10;
	
	private List<Computer> cpus;
	
	private int number, start, end;
	
	private Page previous;
	private Page next;
	
	private Dao.Sort sort;
	private Dao.Order order;
	
	private String search;
	
	public Page(int number, int sort, String search) {
		
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
		
		this.sort = Sort.NAME;
		this.order = Order.ASC;
		
		if(sort < 0) {
			this.order = Order.DESC;
		}
		
		sort = Math.abs(sort);
		
		switch(sort) {
		case 2:
			this.sort = Sort.INTRODUCED;
			break;
		case 3:
			this.sort = Sort.DISCONTINUED;
			break;
		case 4:
			this.sort = Sort.COMPANY;
			break;
		}
		
		this.search = search;
		
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

	public Dao.Sort getSort() {
		return sort;
	}

	public void setSort(Dao.Sort sort) {
		this.sort = sort;
	}

	public Dao.Order getOrder() {
		return order;
	}

	public void setOrder(Dao.Order order) {
		this.order = order;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}
	
}
