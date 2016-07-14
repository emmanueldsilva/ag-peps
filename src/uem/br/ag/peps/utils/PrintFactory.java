package uem.br.ag.peps.utils;

import static org.apache.commons.io.FileUtils.forceDelete;
import static org.apache.commons.io.FileUtils.forceMkdir;
import static org.apache.commons.io.FileUtils.getUserDirectoryPath;
import static org.apache.commons.io.FileUtils.write;
import static org.jfree.chart.ChartFactory.createLineChart;
import static org.jfree.chart.ChartUtilities.saveChartAsPNG;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

import uem.br.ag.peps.entidade.Employee;
import uem.br.ag.peps.entidade.Task;
import uem.br.ag.peps.genetico.Individuo;
import uem.br.ag.peps.genetico.MatrizDedicacao;
import uem.br.ag.peps.genetico.ParametrosAlgoritmo;
import uem.br.ag.peps.genetico.Populacao;
import uem.br.ag.peps.genetico.TaskScheduling;
import uem.br.ag.peps.problema.ProblemaBuilder;

public class PrintFactory {

	private final String MELHOR_FITNESS = "Melhor Fitness";
	private final String MEDIA_FITNESS = "Média Fitness";
	private final String PIOR_FITNESS = "Pior Fitness";
	
	private final String MELHOR_CUSTO_PROJETO = "Melhor Custo Projeto";
	private final String MEDIA_CUSTO_PROJETO = "Média Custo Projeto";
	private final String PIOR_CUSTO_PROJETO = "Pior Custo Projeto";
	private final String CUSTO_MELHOR_INDIVIDUO = "Custo Projeto Melhor Indivíduo";
	
	private final String MELHOR_DURACAO_PROJETO = "Melhor Duração Projeto";
	private final String MEDIA_DURACAO_PROJETO = "Média Duração Projeto";
	private final String PIOR_DURACAO_PROJETO = "Pior Duração Projeto";
	private final String DURACAO_MELHOR_INDIVIDUO = "Duração Projeto Melhor Indivíduo";
	
	private static final NumberFormat CURRENCY_INSTANCE = NumberFormat.getCurrencyInstance(Locale.US);
	
	private DefaultCategoryDataset dataSetFitness = new DefaultCategoryDataset();
	
	private DefaultCategoryDataset dataSetCustoProjeto = new DefaultCategoryDataset();
	
	private DefaultCategoryDataset dataSetDuracaoProjeto = new DefaultCategoryDataset();
	
	private ParametrosAlgoritmo parametrosAlgoritmo;
	
	private Integer execucao;
	
	public PrintFactory(ParametrosAlgoritmo parametrosAlgoritmo, Integer execucao) {
		this.parametrosAlgoritmo = parametrosAlgoritmo;
		this.execucao = execucao;
		
		deleteRegistrosAnteriores();
	}
	
	private void deleteRegistrosAnteriores() {
		try {
			File file = getRegistrosExecucaoFile();
			if (file.exists()) forceDelete(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void imprimePopulacao(List<Individuo> individuos) {
		individuos.forEach(i -> printIndividuo(i));
	}

	public void geraEstatisticas(Populacao populacao, Integer geracao) {
		populaDataSetFitness(populacao, geracao);
		populaDataSetCustoProjeto(populacao, geracao);
		populaDataSetDuracaoProjeto(populacao, geracao);
		
		printEstatisticaPopulacao(populacao, geracao);
	}

	private void printEstatisticaPopulacao(Populacao populacao, Integer geracao) {
		final CustomStringBuilder sb = new CustomStringBuilder();
		sb.appendLine("--------------------------------------------------------------");
		sb.appendLine("ESTATÍSTICAS GERAÇÃO " + geracao);
		sb.appendLine();
		sb.appendLine("MELHOR FITNESS: " + populacao.getMaiorValorFitness());
		sb.appendLine("MEDIA FITNESS: " + populacao.getMediaValorFitness());
		sb.appendLine("PIOR FITNESS: " + populacao.getMenorValorFitness());
		sb.appendLine("FITNESS DO MELHOR INDIVÍDUO: " + populacao.getMelhorIndividuo().getValorFitness());
		sb.appendLine("--------------------------------------------------------------");
		sb.appendLine("MAIOR CUSTO PROJETO: " + CURRENCY_INSTANCE.format(populacao.getMaiorValorCustoProjeto()));
		sb.appendLine("MEDIA CUSTO PROJETO: " + CURRENCY_INSTANCE.format(populacao.getMediaValorCustoProjeto()));
		sb.appendLine("MENOR CUSTO PROJETO: " + CURRENCY_INSTANCE.format(populacao.getMenorValorCustoProjeto()));
		sb.appendLine("CUSTO DO MELHOR INDIVÍDUO: " + CURRENCY_INSTANCE.format(populacao.getMelhorIndividuo().getCustoTotalProjeto()));
		sb.appendLine("--------------------------------------------------------------");
		sb.appendLine("MAIOR DURAÇÃO PROJETO: " + populacao.getMaiorDuracaoProjeto());
		sb.appendLine("MEDIA DURAÇÃO PROJETO: " + populacao.getMediaDuracaoProjeto());
		sb.appendLine("MENOR DURAÇÃO PROJETO: " + populacao.getMenorDuracaoProjeto());
		sb.appendLine("DURAÇÃO PROJETO DO MELHOR INDIVÍDUO: " + populacao.getMelhorIndividuo().getDuracaoTotalProjeto());
		sb.appendLine("--------------------------------------------------------------");
		sb.appendLine();
		sb.appendLine();
		
		appendToEnd(sb.toString());
	}
	
	public void printEstatisticaExecucao(Double tempoExecucao, ParametrosAlgoritmo parametrosAlgoritmo) {
		final CustomStringBuilder sb = new CustomStringBuilder();
		sb.appendLine("--------------------------------------------------------------");
		sb.appendLine("TEMPO DE EXECUÇÃO: " + tempoExecucao);
		sb.appendLine();
		sb.appendLine("NUMERO DE EMPLOYEES: " + ProblemaBuilder.getInstance().getNumeroEmployees());
		sb.appendLine("NUMERO DE TASKS: " + ProblemaBuilder.getInstance().getNumeroTasks());
		sb.appendLine();
		sb.appendLine("BENCHMARK: " + new File(parametrosAlgoritmo.getPathBenchmark()).getName());
		sb.appendLine("NÚMERO DE EXECUÇÕES: " + parametrosAlgoritmo.getNumeroExecucoes());
		sb.appendLine("NÚMERO DE GERAÇÕES: " + parametrosAlgoritmo.getNumeroGeracoes());
		sb.appendLine("TAMANHO POPULAÇÃO: " + parametrosAlgoritmo.getTamanhoPopulacao());
		sb.appendLine("PERCENTUAL CRUZAMENTO: " + parametrosAlgoritmo.getPercentualCruzamento());
		sb.appendLine("PERCENTUAL MUTAÇÃO: " + parametrosAlgoritmo.getPercentualMutacao());
		
		appendToEnd(sb.toString());
	}
	
	private void appendToEnd(String string) {
		try {
			write(getRegistrosExecucaoFile(), string, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File getRegistrosExecucaoFile() throws IOException {
		return new File(buildPathDiretorio() + "estatisticas" + execucao + ".txt");
	}

	private void populaDataSetFitness(Populacao populacao, Integer geracao) {
		dataSetFitness.addValue(populacao.getMaiorValorFitness(), MELHOR_FITNESS, geracao);
//		dataSetFitness.addValue(populacao.getMediaValorFitness(), MEDIA_FITNESS, geracao);
//		dataSetFitness.addValue(populacao.getMenorValorFitness(), PIOR_FITNESS, geracao);
	}
	
	private void populaDataSetDuracaoProjeto(Populacao populacao, Integer geracao) {
//		dataSetDuracaoProjeto.addValue(populacao.getMaiorDuracaoProjeto(), PIOR_DURACAO_PROJETO, geracao);
//		dataSetDuracaoProjeto.addValue(populacao.getMediaDuracaoProjeto(), MEDIA_DURACAO_PROJETO, geracao);
		dataSetDuracaoProjeto.addValue(populacao.getMenorDuracaoProjeto(), MELHOR_DURACAO_PROJETO, geracao);
		dataSetDuracaoProjeto.addValue(populacao.getMelhorIndividuo().getDuracaoTotalProjeto(), DURACAO_MELHOR_INDIVIDUO, geracao);
	}
	
	private void populaDataSetCustoProjeto(Populacao populacao, Integer geracao) {
//		dataSetCustoProjeto.addValue(populacao.getMaiorValorCustoProjeto(), PIOR_CUSTO_PROJETO, geracao);
//		dataSetCustoProjeto.addValue(populacao.getMediaValorCustoProjeto(), MEDIA_CUSTO_PROJETO, geracao);
		dataSetCustoProjeto.addValue(populacao.getMenorValorCustoProjeto(), MELHOR_CUSTO_PROJETO, geracao);
		dataSetCustoProjeto.addValue(populacao.getMelhorIndividuo().getCustoTotalProjeto(), CUSTO_MELHOR_INDIVIDUO, geracao);
	}
	
	public synchronized void plotaGraficos(Populacao populacao) {
		try {
			Individuo melhorIndividuo = populacao.getMelhorIndividuo();
			Individuo piorIndividuo = populacao.getPiorIndividuo();
			
			String pathDiretorio = buildPathDiretorio();
			buildGraficoFitness(pathDiretorio, melhorIndividuo, piorIndividuo);
//			buildGraficoCustoProjeto(pathDiretorio, melhorIndividuo, piorIndividuo);
			buildGraficoCustoProjeto(pathDiretorio, populacao.getMenorValorCustoProjeto(), populacao.getMaiorValorCustoProjeto());
			buildGraficoDuracaoProjeto(pathDiretorio);
			buildDiagramaGantt(pathDiretorio, melhorIndividuo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void buildGraficoFitness(String pathDiretorio, Individuo melhorIndividuo, Individuo piorIndividuo) throws IOException {
		JFreeChart graficoFitness = createLineChart("Valor de Fitness", "Gerações", "Fitness", dataSetFitness, 
				PlotOrientation.VERTICAL, true, true, false);
		
		CategoryPlot categoryPlot = graficoFitness.getCategoryPlot();
		categoryPlot.setDomainCrosshairVisible(true);
		categoryPlot.setRangeCrosshairVisible(true);
		
		saveChartAsPNG(new File(pathDiretorio + "grafico_fitness_" + execucao + ".png"), graficoFitness, 1000, 300);
	}
	
	private void buildGraficoCustoProjeto(String pathDiretorio, Individuo melhorIndividuo, Individuo piorIndividuo) throws IOException {
		JFreeChart graficoCustoProjeto = createLineChart("Custo do Projeto", "Gerações", "Custo", dataSetCustoProjeto, 
				PlotOrientation.VERTICAL, true, true, false);
		
		CategoryPlot categoryPlot = (CategoryPlot) graficoCustoProjeto.getPlot();
		categoryPlot.setDomainCrosshairVisible(true);
		categoryPlot.setRangeCrosshairVisible(true);
		
		saveChartAsPNG(new File(pathDiretorio + "grafico_custo_" + execucao + ".png"), graficoCustoProjeto, 1000, 300);
	}
	
	private void buildGraficoCustoProjeto(String pathDiretorio, Double menorCustoProjeto, Double maiorCustoProjeto) throws IOException {
		JFreeChart graficoCustoProjeto = createLineChart("Custo do Projeto", "Gerações", "Custo", dataSetCustoProjeto, 
				PlotOrientation.VERTICAL, true, true, false);
		
		CategoryPlot categoryPlot = (CategoryPlot) graficoCustoProjeto.getPlot();
		categoryPlot.setDomainCrosshairVisible(true);
		categoryPlot.setRangeCrosshairVisible(true);
		
		double CENTENA_MILHAR = 10000.00;
		NumberAxis range = (NumberAxis) categoryPlot.getRangeAxis();
		range.setRange(menorCustoProjeto - (2 * CENTENA_MILHAR), maiorCustoProjeto + (2 * CENTENA_MILHAR));
		range.setTickUnit(new NumberTickUnit(CENTENA_MILHAR));
		
		saveChartAsPNG(new File(pathDiretorio + "grafico_custo_" + execucao + ".png"), graficoCustoProjeto, 1000, 300);
	}
	
	private void buildGraficoDuracaoProjeto(String pathDiretorio) throws IOException {
		JFreeChart graficoDuracaoProjeto = createLineChart("Duração do Projeto", "Gerações", "Duração", dataSetDuracaoProjeto, 
				PlotOrientation.VERTICAL, true, true, false);
		
		saveChartAsPNG(new File(pathDiretorio + "grafico_duracao_" + execucao + ".png"), graficoDuracaoProjeto, 1000, 300);
	}
	
	private void buildDiagramaGantt(String pathDiretorio, Individuo melhorIndividuo) throws IOException {
		final List<TaskScheduling> escalaTarefas = melhorIndividuo.getMatrizDedicacao().getEscalaTarefas();
		
		final JFreeChart diagramaGantt = ChartFactory.createGanttChart("Diagrama de Gantt", "Tarefas", "Tempo", createDataset(escalaTarefas));
		
		saveChartAsPNG(new File(pathDiretorio + "diagrama_gantt_" + execucao + ".png"), diagramaGantt, 1000, 600);
	}
	
    private IntervalCategoryDataset createDataset(List<TaskScheduling> taskScheduling) {
        final TaskSeries taskSeries = new TaskSeries("Tarefas");

        taskScheduling.forEach((ts) -> {
        	taskSeries.add(new org.jfree.data.gantt.Task("Task " + ts.getTask().getNumero(), calculaData(ts.getTempoInicio()), calculaData(ts.getTempoFim())));
        });
        
        final TaskSeriesCollection taskSeriesCollection = new TaskSeriesCollection();
        taskSeriesCollection.add(taskSeries);
        
        return taskSeriesCollection;
    }

	private Date calculaData(Double tempo) {
		int meses = tempo.intValue();
		int dias = (int) ((tempo - meses) * (365.25/12));
		
		final Calendar calendarInicio = Calendar.getInstance();
		calendarInicio.set(2000, 01, 01);
		calendarInicio.add(Calendar.MONTH, meses);
		calendarInicio.add(Calendar.DAY_OF_MONTH, dias);
		return calendarInicio.getTime();
	}

	private String buildPathDiretorio() throws IOException {
		String pathDiretorio = getUserDirectoryPath() + "/ag-peps/execucoes/";
		pathDiretorio += new File(parametrosAlgoritmo.getPathBenchmark()).getName() + "/";
		pathDiretorio += "exe" + parametrosAlgoritmo.getNumeroExecucoes() + "/";
		pathDiretorio += "ger" + parametrosAlgoritmo.getNumeroGeracoes() + "/";
		pathDiretorio += "pop" + parametrosAlgoritmo.getTamanhoPopulacao() + "/";
		pathDiretorio += "cru" + parametrosAlgoritmo.getPercentualCruzamento().intValue() + "/";
		pathDiretorio += "mut" + parametrosAlgoritmo.getPercentualMutacao().intValue() + "/";
		
		File file = new File(pathDiretorio);
		if (!file.exists()) forceMkdir(file);
		return pathDiretorio;
	}

	public void printIndividuo(Individuo individuo) {
		final CustomStringBuilder sb = new CustomStringBuilder();
		sb.appendLine("==================================================================================");
		sb.appendLine("INDIVÍDUO");
		sb.appendLine("FITNESS: " + individuo.fitnessToString());
		
		final MatrizDedicacao matrizDedicacao = individuo.getMatrizDedicacao();
		if (individuo.isFactivel() != null) {
			sb.appendLine("FACTIVEL: " + individuo.isFactivel());
			sb.appendLine("TAREFAS NÃO REALIZADAS: " + matrizDedicacao.getTarefasNaoRealizadas());
			sb.appendLine("HABILIDADES NECESSARIAS: " + matrizDedicacao.getHabilidadesNecessarias());
			sb.appendLine("TRABALHO EXTRA: " + matrizDedicacao.getTrabalhoExtra());
			sb.appendLine("CUSTO PROJETO: " + CURRENCY_INSTANCE.format(matrizDedicacao.getCustoTotalProjeto()));
			sb.appendLine("DURAÇÃO PROJETO: " + matrizDedicacao.getDuracaoTotalProjeto());
		}

		for (Employee employee : ProblemaBuilder.getInstance().getEmployees()) {
			for (Task task: ProblemaBuilder.getInstance().getTasks()) {
				sb.append(matrizDedicacao.getGrauDedicacao(employee, task).getValor() + "\t");
			}
			
			sb.appendLine();
		}
		
		sb.appendLine("==================================================================================");
		sb.appendLine();
		sb.appendLine();
		
		appendToEnd(sb.toString());
	}
	
}
