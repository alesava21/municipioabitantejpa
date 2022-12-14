package it.prova.municipioabitantejpa.test;

import java.util.List;

import org.hibernate.LazyInitializationException;

import it.prova.municipioabitantejpa.dao.EntityManagerUtil;
import it.prova.municipioabitantejpa.model.Abitante;
import it.prova.municipioabitantejpa.model.Municipio;
import it.prova.municipioabitantejpa.service.MyServiceFactory;
import it.prova.municipioabitantejpa.service.abitante.AbitanteService;
import it.prova.municipioabitantejpa.service.municipio.MunicipioService;

public class TestMunicipioAbitante {

	public static void main(String[] args) {

		MunicipioService municipioService = MyServiceFactory.getMunicipioServiceInstance();
		AbitanteService abitanteService = MyServiceFactory.getAbitanteServiceInstance();

		try {

			// ora con il service posso fare tutte le invocazioni che mi servono
			System.out.println(
					"In tabella Municipio ci sono " + municipioService.listAllMunicipi().size() + " elementi.");

			testInserisciMunicipio(municipioService);
			System.out.println(
					"In tabella Municipio ci sono " + municipioService.listAllMunicipi().size() + " elementi.");

			testInserisciAbitante(municipioService, abitanteService);
			System.out.println(
					"In tabella Municipio ci sono " + municipioService.listAllMunicipi().size() + " elementi.");

			testRimozioneAbitante(municipioService, abitanteService);
			System.out.println(
					"In tabella Municipio ci sono " + municipioService.listAllMunicipi().size() + " elementi.");

			testCercaTuttiGliAbitantiConNome(municipioService, abitanteService);
			System.out.println(
					"In tabella Municipio ci sono " + municipioService.listAllMunicipi().size() + " elementi.");

			testLazyInitExc(municipioService, abitanteService);

			testDescrizioneConIniziale(municipioService);

			testAbitanteCheIniziaConCognomeIniziale(abitanteService, municipioService);
			System.out.println(
					"In tabella Municipio ci sono " + municipioService.listAllMunicipi().size() + " elementi.");

			testCodiceMunicipioIniziaCon(municipioService, abitanteService);
			
			testAbitantiMinorenni(abitanteService, municipioService);

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			// questa ?? necessaria per chiudere tutte le connessioni quindi rilasciare il
			// main
			EntityManagerUtil.shutdown();
		}

	}

	private static void testInserisciMunicipio(MunicipioService municipioService) throws Exception {
		System.out.println(".......testInserisciMunicipio inizio.............");
		// creo nuovo municipio
		Municipio nuovoMunicipio = new Municipio("Municipio III", "III", "Via dei Nani");
		if (nuovoMunicipio.getId() != null)
			throw new RuntimeException("testInserisciMunicipio fallito: record gi?? presente ");

		// salvo
		municipioService.inserisciNuovo(nuovoMunicipio);
		// da questa riga in poi il record, se correttamente inserito, ha un nuovo id
		// (NOVITA' RISPETTO AL PASSATO!!!)
		if (nuovoMunicipio.getId() == null)
			throw new RuntimeException("testInserisciMunicipio fallito ");

		System.out.println(".......testInserisciMunicipio fine: PASSED.............");
	}

	private static void testInserisciAbitante(MunicipioService municipioService, AbitanteService abitanteService)
			throws Exception {
		System.out.println(".......testInserisciAbitante inizio.............");

		// creo nuovo abitante ma prima mi serve un municipio
		List<Municipio> listaMunicipiPresenti = municipioService.listAllMunicipi();
		if (listaMunicipiPresenti.isEmpty())
			throw new RuntimeException("testInserisciAbitante fallito: non ci sono municipi a cui collegarci ");

		Abitante nuovoAbitante = new Abitante("Pluto", "Plutorum", 77, "Via Lecce");
		// lo lego al primo municipio che trovo
		nuovoAbitante.setMunicipio(listaMunicipiPresenti.get(0));

		// salvo il nuovo abitante
		abitanteService.inserisciNuovo(nuovoAbitante);

		// da questa riga in poi il record, se correttamente inserito, ha un nuovo id
		// (NOVITA' RISPETTO AL PASSATO!!!)
		if (nuovoAbitante.getId() == null)
			throw new RuntimeException("testInserisciAbitante fallito ");

		// il test fallisce anche se non ?? riuscito a legare i due oggetti
		if (nuovoAbitante.getMunicipio() == null)
			throw new RuntimeException("testInserisciAbitante fallito: non ha collegato il municipio ");

		System.out.println(".......testInserisciAbitante fine: PASSED.............");
	}

	private static void testRimozioneAbitante(MunicipioService municipioService, AbitanteService abitanteService)
			throws Exception {
		System.out.println(".......testRimozioneAbitante inizio.............");

		// inserisco un abitante che rimuover??
		// creo nuovo abitante ma prima mi serve un municipio
		List<Municipio> listaMunicipiPresenti = municipioService.listAllMunicipi();
		if (listaMunicipiPresenti.isEmpty())
			throw new RuntimeException("testRimozioneAbitante fallito: non ci sono municipi a cui collegarci ");

		Abitante nuovoAbitante = new Abitante("Pietro", "Mitraglia", 33, "Via del Mare");
		// lo lego al primo municipio che trovo
		nuovoAbitante.setMunicipio(listaMunicipiPresenti.get(0));

		// salvo il nuovo abitante
		abitanteService.inserisciNuovo(nuovoAbitante);

		Long idAbitanteInserito = nuovoAbitante.getId();
		abitanteService.rimuovi(idAbitanteInserito);
		// proviamo a vedere se ?? stato rimosso
		if (abitanteService.caricaSingoloAbitante(idAbitanteInserito) != null)
			throw new RuntimeException("testRimozioneAbitante fallito: record non cancellato ");
		System.out.println(".......testRimozioneAbitante fine: PASSED.............");
	}

	private static void testCercaTuttiGliAbitantiConNome(MunicipioService municipioService,
			AbitanteService abitanteService) throws Exception {
		System.out.println(".......testCercaTuttiGliAbitantiConNome inizio.............");

		// inserisco un paio di abitanti di test
		// prima mi serve un municipio
		List<Municipio> listaMunicipiPresenti = municipioService.listAllMunicipi();
		if (listaMunicipiPresenti.isEmpty())
			throw new RuntimeException(
					"testCercaTuttiGliAbitantiConNome fallito: non ci sono municipi a cui collegarci ");

		Abitante nuovoAbitante = new Abitante("Mariotto", "Bassi", 27, "Via Lucca");
		Abitante nuovoAbitante2 = new Abitante("Mariotto", "Nato", 37, "Via Roma");
		// lo lego al primo municipio che trovo
		nuovoAbitante.setMunicipio(listaMunicipiPresenti.get(0));
		nuovoAbitante2.setMunicipio(listaMunicipiPresenti.get(0));

		// salvo i nuovi abitante
		abitanteService.inserisciNuovo(nuovoAbitante);
		abitanteService.inserisciNuovo(nuovoAbitante2);

		// ora mi aspetto due 'Mario'
		if (abitanteService.cercaTuttiGliAbitantiConNome("Mariotto").size() != 2)
			throw new RuntimeException("testCercaTuttiGliAbitantiConNome fallito: numero record inatteso ");

		// clean up code
		abitanteService.rimuovi(nuovoAbitante.getId());
		abitanteService.rimuovi(nuovoAbitante2.getId());

		System.out.println(".......testCercaTuttiGliAbitantiConNome fine: PASSED.............");
	}

	private static void testLazyInitExc(MunicipioService municipioService, AbitanteService abitanteService)
			throws Exception {
		System.out.println(".......testLazyInitExc inizio.............");

		// prima mi serve un municipio
		List<Municipio> listaMunicipiPresenti = municipioService.listAllMunicipi();
		if (listaMunicipiPresenti.isEmpty())
			throw new RuntimeException("testLazyInitExc fallito: non ci sono municipi a cui collegarci ");

		Municipio municipioSuCuiFareIlTest = listaMunicipiPresenti.get(0);
		// se interrogo la relazione devo ottenere un'eccezione visto che sono LAZY
		try {
			municipioSuCuiFareIlTest.getAbitanti().size();
			// se la riga sovrastante non da eccezione il test fallisce
			throw new RuntimeException("testLazyInitExc fallito: eccezione non lanciata ");
		} catch (LazyInitializationException e) {
			// 'spengo' l'eccezione per il buon fine del test
		}
		// una LazyInitializationException in quanto il contesto di persistenza ?? chiuso
		// se usiamo un caricamento EAGER risolviamo...dipende da cosa ci serve!!!
		// municipioService.caricaSingoloMunicipioConAbitanti(...);
		System.out.println(".......testLazyInitExc fine: PASSED.............");
	}

	public static void testDescrizioneConIniziale(MunicipioService municipioService) throws Exception {
		System.out.println(".......testDescrizioneConIniziale inizio.............");
		List<Municipio> listaMunicipio = municipioService.listAllMunicipi();
		if (listaMunicipio.isEmpty()) {
			throw new RuntimeException("testDescrizioneConIniziale fallito: non ci sono municipi a cui collegarci ");
		}
		if (municipioService.cercaTutteLeDescrizioniConIniziele("m").isEmpty()) {
			throw new RuntimeException(
					"testDescrizioneConIniziale fallito: non ci sono descrizioni dei municipi che inizino cosi. ");

		}

		System.out.println(".......testDescrizioneConIniziale fine: PASSED.............");
	}

	public static void testAbitanteCheIniziaConCognomeIniziale(AbitanteService abitanteService,
			MunicipioService municipioService) throws Exception {
		System.out.println(".......testAbitanteCheIniziaConCognomeIniziale inizio.............");

		List<Municipio> listaMunicipiPresenti = municipioService.listAllMunicipi();
		if (listaMunicipiPresenti.isEmpty())
			throw new RuntimeException(
					"testCercaTuttiGliAbitantiConNome fallito: non ci sono municipi a cui collegarci ");

		Abitante nuovoAbitante = new Abitante("Alessandro", "a", 27, "Via Lucca");
		Abitante nuovoAbitante2 = new Abitante("Andrea", "a", 37, "Via Roma");

		nuovoAbitante.setMunicipio(listaMunicipiPresenti.get(0));
		nuovoAbitante2.setMunicipio(listaMunicipiPresenti.get(0));

		abitanteService.inserisciNuovo(nuovoAbitante);
		abitanteService.inserisciNuovo(nuovoAbitante2);

		if (abitanteService.cercaTuttiGliAbitantiConCognome("a").size() != 2)
			throw new RuntimeException("testCercaTuttiGliAbitantiConCognome fallito: numero record inatteso ");

		abitanteService.rimuovi(nuovoAbitante.getId());
		abitanteService.rimuovi(nuovoAbitante2.getId());

		System.out.println(".......testCercaTuttiGliAbitantiConCognome fine: PASSED.............");

	}

	public static void testCodiceMunicipioIniziaCon(MunicipioService municipioService, AbitanteService abitanteService)
			throws Exception {
		System.out.println(".......testCodiceMunicipioIniziaCon inizio.............");

		List<Municipio> listaMunicipiPresenti = municipioService.listAllMunicipi();
		if (listaMunicipiPresenti.isEmpty())
			throw new RuntimeException("testCodiceMunicipioIniziaCon fallito: non ci sono municipi a cui collegarci ");

		Abitante nuovoAbitante = new Abitante("Mirko", "Bassi", 27, "Via Lucca");
		Abitante nuovoAbitante2 = new Abitante("Andrea", "Bassi", 37, "Via Roma");

		nuovoAbitante.setMunicipio(listaMunicipiPresenti.get(0));
		nuovoAbitante2.setMunicipio(listaMunicipiPresenti.get(0));

		abitanteService.inserisciNuovo(nuovoAbitante);
		abitanteService.inserisciNuovo(nuovoAbitante2);

		List<Abitante> test = abitanteService.CercaCodiceMunicipioIniziaPer("I");

		System.out.println(test);

		System.out.println(abitanteService.CercaCodiceMunicipioIniziaPer("I"));

		System.out.println(".......testCodiceMunicipioIniziaCon fine: PASSED.............");

	}

	public static void testAbitantiMinorenni(AbitanteService abitanteService, MunicipioService municipioService)
			throws Exception {
		System.out.println(".......testAbitantiMinorenni inizio.............");

		List<Municipio> listaMunicipiPresenti = municipioService.listAllMunicipi();
		if (listaMunicipiPresenti.isEmpty())
			throw new RuntimeException("testAbitantiMinorenni fallito: non ci sono municipi a cui collegarci ");

		Abitante nuovoAbitante = new Abitante("Mirko", "Bassi", 10, "Via Lucca");
		Abitante nuovoAbitante2 = new Abitante("Andrea", "Bassi", 2, "Via Roma");

		nuovoAbitante.setMunicipio(listaMunicipiPresenti.get(0));
		nuovoAbitante2.setMunicipio(listaMunicipiPresenti.get(0));

		abitanteService.inserisciNuovo(nuovoAbitante);
		abitanteService.inserisciNuovo(nuovoAbitante2);

		List<Municipio> test = municipioService.cercaTuttiAbitantiMinorenni();

		System.out.println(test);

		System.out.println(".......testAbitantiMinorenni fine: PASSED.............");
	}

}
