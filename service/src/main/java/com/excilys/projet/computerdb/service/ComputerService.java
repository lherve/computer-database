package com.excilys.projet.computerdb.service;

import com.excilys.projet.computerdb.exception.DBException;
import com.excilys.projet.computerdb.model.Computer;
import com.excilys.projet.computerdb.model.Page;
import org.springframework.transaction.annotation.Transactional;

public interface ComputerService {

    /*
     * Méthode utilisée par DeleteComputer pour la suppression du computer
     */
    @Transactional(readOnly = false)
    boolean deleteComputer(int id);

    /*
     * Méthode utilisée par UpdateComputer pour récupérer les informations du computer à éditer
     */
    Computer getComputer(int id) throws DBException;

    /*
     * Méthode utilisée par ListComputers pour obtenir des computers à afficher
     */
    Page loadPage(int number, int sort, String search) throws DBException;

    /*
     * Méthode utilisée par UpdateComputer pour l'insertion et la mise-à-jour du computer
     */
    @Transactional(readOnly = false)
    Computer updateComputer(Computer cpu);
    
}
