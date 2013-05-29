package com.excilys.projet.computerdb.model;

import java.util.List;

//import com.excilys.projet.computerdb.dao.Dao;

public class Page {
	
	public static final int SIZE = 10;
	
	public enum Sort {
		ID,
		NAME,
		INTRODUCED,
		DISCONTINUED,
		COMPANY;
	}
	
	public enum Order {
		ASC,
		DESC;
	}
	
	private List<Computer> cpus;
	
	private int number, start, end, total;

	private Page previous;
	private Page next;
	
	private Sort sort;
	private Order order;
	
	private String search;
	
	public Page(int number) {
		this.number = number;
	}
	
	public Page(int number, String search) {
		this.number = number;
		this.search = search;
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
	
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	
	public int getSize() {
		return SIZE;
	}

	public Sort getSort() {
		return sort;
	}

	public void setSort(Sort sort) {
		this.sort = sort;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Page ");
		
		sb.append(this.number).append(" [").append(this.start).append(",").append(this.end).append("] /");
		
//		if(this.sort != null) {
//			sb.append(this.sort);
//		}
//		
//		sb.append(" ");
//		
//		if(this.order != null) {
//			sb.append(this.order);
//		}
		
		sb.append("/ cpus:").append(cpus != null ? cpus.size() : null);
		
		return sb.toString();
	}
	
}
