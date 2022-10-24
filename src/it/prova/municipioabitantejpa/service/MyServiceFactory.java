package it.prova.municipioabitantejpa.service;

import it.prova.municipioabitantejpa.dao.MyDaoFactory;
import it.prova.municipioabitantejpa.service.abitante.AbitanteService;
import it.prova.municipioabitantejpa.service.abitante.AbitanteServiceImpl;
import it.prova.municipioabitantejpa.service.municipio.MunicipioService;
import it.prova.municipioabitantejpa.service.municipio.MunicipioServiceImpl;

public class MyServiceFactory {

	// rendiamo le istanze restituite SINGLETON
	private static AbitanteService abitanteServiceInstance = null;
	private static MunicipioService municipioServiceInstance = null;

	public static AbitanteService getAbitanteServiceInstance() {
		if (abitanteServiceInstance == null) {
			abitanteServiceInstance = new AbitanteServiceImpl();
			abitanteServiceInstance.setAbitanteDAO(MyDaoFactory.getAbitanteDAOInstance());
		}
		return abitanteServiceInstance;
	}

	public static MunicipioService getMunicipioServiceInstance() {
		if (municipioServiceInstance == null) {
			municipioServiceInstance = new MunicipioServiceImpl();
			municipioServiceInstance.setMunicipioDAO(MyDaoFactory.getMunicipioDAOInstance());
		}
		return municipioServiceInstance;
	}

}
