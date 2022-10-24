package it.prova.municipioabitantejpa.dao.municipio;

import java.util.List;

import it.prova.municipioabitantejpa.dao.IBaseDAO;
import it.prova.municipioabitantejpa.model.Municipio;

public interface MunicipioDAO extends IBaseDAO<Municipio> {
	
	public Municipio getEagerAbitanti(Long id) throws Exception;

	public List<Municipio> findAllByAbitantiMinorenni() throws Exception;

	public List<Municipio> findAllByDescrizioneIniziaCon(String iniziale) throws Exception;

}
