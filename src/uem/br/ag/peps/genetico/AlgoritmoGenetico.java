package uem.br.ag.peps.genetico;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import uem.br.ag.peps.problema.ProblemaBuilder;
import uem.br.ag.peps.utils.PrintFactory;
import uem.br.ag.peps.utils.StatisticsPrintFactory;

import com.google.common.collect.Lists;

public class AlgoritmoGenetico {
	
	private List<Individuo> populacao = Lists.newArrayList();
	
	private ParametrosAlgoritmo parametrosAlgoritmo;
	
	private Integer hitRate = 0;
	
	private List<Double> melhoresFitness = Lists.newArrayList();
	
	private List<Double> melhoresCustosProjeto = Lists.newArrayList();
	
	private List<Double> melhoresDuracaoProjeto = Lists.newArrayList();
	
	public AlgoritmoGenetico(ParametrosAlgoritmo parametrosAlgoritmo) {
		this.parametrosAlgoritmo = parametrosAlgoritmo;
	}
	
    public void inicializaDadosProblema() {
    	ProblemaBuilder.getInstance().clear();
    	ProblemaBuilder.getInstance().setParametrosPath(parametrosAlgoritmo.getPathBenchmark());
    	ProblemaBuilder.getInstance().readParametrosArquivo();
    }

    public void executarAlgoritmo() {
        for (int cont = 0; cont < parametrosAlgoritmo.getNumeroExecucoes(); cont++) {
        	final PrintFactory printFactory = new PrintFactory(parametrosAlgoritmo, cont + 1);
            long start = System.currentTimeMillis();  
             
            final Populacao populacao = new Populacao(parametrosAlgoritmo.getTamanhoPopulacao());
            populacao.gerarIndividuos();
            
            for (int i = 0; i < parametrosAlgoritmo.getNumeroGeracoes(); i++) {
				populacao.avaliarIndividuos();
				populacao.selecionarMaisAptosPorTorneio();
				printFactory.geraEstatisticas(populacao, i);
				
				List<Individuo> individuosFilhos = populacao.efetuarCruzamento(parametrosAlgoritmo.getPercentualCruzamento());
				populacao.efetuarMutacao(individuosFilhos, parametrosAlgoritmo.getPercentualMutacao());
            }

            populacao.avaliarIndividuos();
            populacao.selecionarMaisAptosPorTorneio();

            final Individuo melhorIndividuo = populacao.getMelhorIndividuo();
            
            printFactory.geraEstatisticas(populacao, parametrosAlgoritmo.getNumeroGeracoes());
			printFactory.printIndividuo(melhorIndividuo);
            populacao.getIndividuos().sort((i1, i2) -> i1.getValorFitness().compareTo(i2.getValorFitness()));
            
            if (melhorIndividuo.isFactivel()) {
            	hitRate++;
            	melhoresFitness.add(melhorIndividuo.getValorFitness());
            	melhoresCustosProjeto.add(melhorIndividuo.getCustoTotalProjeto());
            	melhoresDuracaoProjeto.add(melhorIndividuo.getDuracaoTotalProjeto());
            }
             
            printFactory.printEstatisticaExecucao(calculaTempoExecucao(start), parametrosAlgoritmo);
            printFactory.plotaGraficos(populacao);
        }
        
        final StatisticsPrintFactory statisticsPrintFactory = new StatisticsPrintFactory(parametrosAlgoritmo, hitRate, melhoresFitness, melhoresCustosProjeto, melhoresDuracaoProjeto);
        statisticsPrintFactory.printEstatisticasExecucoes();
    }

	private Double calculaTempoExecucao(long start) {
		BigDecimal delay = new BigDecimal(System.currentTimeMillis() - start);
		delay = delay.divide(new BigDecimal(1000), MathContext.DECIMAL32);
		delay = delay.multiply(new BigDecimal(10000000));
		delay = delay.divide(new BigDecimal(10000000), MathContext.DECIMAL32);
		
		return delay.doubleValue();
	}

	public List<Individuo> getPopulacao() {
		return populacao;
	}

	public ParametrosAlgoritmo getParametrosAlgoritmo() {
		return parametrosAlgoritmo;
	}
	
}

