package com.excilys.projet.computerdb.test.dao;

import java.util.Calendar;
import java.util.Date;

import com.excilys.projet.computerdb.daoImpl.ComputerDao;
import com.excilys.projet.computerdb.model.Computer;

public class ComputerDaoTest {

	public static void getTest() {
		
		Object o = ComputerDao.I.get(577);
		
		if(o!=null) {
			if(o instanceof Computer) {
				Computer cpu = (Computer) o;
				if(cpu.getName().equals("Computer test")) {
					System.out.println("getTest suceed");
				}
				else {
					System.out.println("getTest / Mauvaise valeur retournée : '" + cpu.getName() + "' != 'Computer test'");
				}
			}
			else {
				System.out.println("getTest / Mauvais type de retour : "+ o.getClass() + " != Computer");
			}
		}
		else {
			System.out.println("getTest / Valeur de retour null");
		}
	}
	
	public static void deleteTest() {
		if(ComputerDao.I.delete(new Computer(576, ""))) {
			System.out.println("deleteTest suceed");
		}
		else {
			System.out.println("deleteTest failed");
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void insertTest() {
		Computer c = new Computer(0, "Computer test");
		c.setIntroduced(Calendar.getInstance());
		c.getIntroduced().setTime(new Date(80, 10, 10));
		//c.setDiscontinued(Calendar.getInstance());
		//c.getDiscontinued().setTime(new Date());
		c.setDiscontinued(null);
		c.setCompany(null);
		
		if(ComputerDao.I.insert(c).getId() > 0) {
			System.out.println("insertTest suceed");
		}
		else {
			System.out.println("insertTest failed");
		}
		
	}
	
	public static void countTest() {
		int count = ComputerDao.I.count();
		if(count > 0) {
			System.out.println("countTest suceed ("+count+")");
		}
		else {
			System.out.println("countTest failed");
		}
	}
	
	public static void updateTest() {
		Computer c = ComputerDao.I.get(577);
		c.setDiscontinued(Calendar.getInstance());
		c.getDiscontinued().setTime(new Date());
		ComputerDao.I.update(c);
		
		c = ComputerDao.I.get(577);
		if(c.getDiscontinued() != null) {
			System.out.println("updateTest suceed");
		}
		else {
			System.out.println("updateTest failed");
		}
	}
	
	public static void getAllTest() {
		for(Computer cpu : ComputerDao.I.getAll()) {
			System.out.println(cpu);
		}
	}
	
	public static void getFromToTest() {
		for(Computer cpu : ComputerDao.I.getFromTo(550, 560)) {
			System.out.println(cpu);
		}
	}
	
	public static void main(String[] args) {
		getFromToTest();
	}
	
}
