package it.prova.municipioabitantejpa.dao.abitante;

import java.util.List;

import it.prova.municipioabitantejpa.dao.IBaseDAO;
import it.prova.municipioabitantejpa.model.Abitante;

public interface AbitanteDAO extends IBaseDAO<Abitante> {
	
	public List<Abitante> findAllByNome(String nome) throws Exception;

	public List<Abitante> findAllByCognome(String cognome) throws Exception;

	public List<Abitante> findAllByCodiceMunicipioIniziaCon(String codice) throws Exception;
	
}
