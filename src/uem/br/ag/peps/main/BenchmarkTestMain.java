package uem.br.ag.peps.main;

import static java.util.Arrays.asList;

import java.io.File;

import uem.br.ag.peps.genetico.AlgoritmoGenetico;
import uem.br.ag.peps.genetico.ParametrosAlgoritmo;
import uem.br.ag.peps.genetico.ParametrosPesos;

public class BenchmarkTestMain {

	private static final Double PESO_CUSTO_PROJETO = 0.0000005;
	private static final Double PESO_DURACAO_PROJETO = 0.045;
	private static final Double PESO_PENALIDADE = 100.0;
	private static final Double PESO_TRABALHO_EXTRA = 1.0;
	private static final Double PESO_TRABALHO_NAO_REALIZADO = 1.0;
	private static final Double PESO_HABILIDADES_NECESSARIAS = 1.0;
	
	private static final int NUMERO_EXECUCOES = 10;
	
	public static void main(String[] args) {
		System.out.println("Iniciando AG-PEPS");
		
		ParametrosPesos.getInstance().atribuiParametros(PESO_CUSTO_PROJETO, 
													    PESO_DURACAO_PROJETO, 
													    PESO_PENALIDADE, 
													    PESO_TRABALHO_NAO_REALIZADO, 
													    PESO_HABILIDADES_NECESSARIAS, 
													    PESO_TRABALHO_EXTRA);
		
		for (String pathBenchmark: asList("/home/emmanuel/projetos/ag-peps/resources/problem-generator/inst10-5-10-5.conf",
										  "/home/emmanuel/projetos/ag-peps/resources/problem-generator/inst20-10-10-5.conf",
										  "/home/emmanuel/projetos/ag-peps/resources/problem-generator/inst30-15-10-5.conf")) {
			for (Integer numeroGeracoes : asList(50, 75, 100, 125, 150, 200, 300, 500, 1000, 1500, 2000)) {
				for (Integer tamanhoPopulacao: asList(30, 50, 75, 100, 125, 150, 200, 250, 300, 400, 500, 750, 1000)) {
					for (Double percentualCruzamento : asList(1.0, 3.0, 5.0, 10.0, 15.0, 20.0, 25.0, 30.0, 40.0, 50.0)) {
						for (Double percentualMutacao : asList(1.0, 2.0, 3.0, 4.0, 5.0, 10.0, 15.0, 20.0, 25.0)) {
							System.out.println("Iniciando execução: " + new File(pathBenchmark).getName() + "/"
									  + NUMERO_EXECUCOES + "/"
									  + numeroGeracoes + "/" 
									  + tamanhoPopulacao + "/"
									  + percentualCruzamento + "/"
									  + percentualMutacao);
							
							final ParametrosAlgoritmo parametrosAlgoritmo = new ParametrosAlgoritmo();
							parametrosAlgoritmo.setNumeroExecucoes(NUMERO_EXECUCOES);
							parametrosAlgoritmo.setNumeroGeracoes(numeroGeracoes);
							parametrosAlgoritmo.setTamanhoPopulacao(tamanhoPopulacao);
							parametrosAlgoritmo.setPercentualCruzamento(percentualCruzamento);
							parametrosAlgoritmo.setPercentualMutacao(percentualMutacao);
							parametrosAlgoritmo.setPathBenchmark(pathBenchmark);

							final AlgoritmoGenetico algoritmoGenetico = new AlgoritmoGenetico(parametrosAlgoritmo);
							algoritmoGenetico.inicializaDadosProblema();
							algoritmoGenetico.executarAlgoritmo();
							
							System.out.println("Fim da execução");
							System.gc();
						} 
					}
				}
			}
		}
	}
	
}
