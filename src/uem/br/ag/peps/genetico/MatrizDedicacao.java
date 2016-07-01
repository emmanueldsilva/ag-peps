package uem.br.ag.peps.genetico;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.newHashSet;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.math.MathContext.DECIMAL32;
import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.math.NumberUtils.DOUBLE_ZERO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import uem.br.ag.peps.entidade.Employee;
import uem.br.ag.peps.entidade.Skill;
import uem.br.ag.peps.entidade.Task;
import uem.br.ag.peps.problema.ProblemaBuilder;
import uem.br.ag.peps.utils.RandomFactory;

import com.google.common.collect.Lists;

public class MatrizDedicacao {
	
	private GrauDedicacao[][] matrizDedicacao = null;
	
	private List<FaseProjeto> fasesProjeto = newArrayList();
	
	private List<TaskScheduling> escalaTarefas = newArrayList();
	
	private Double custoTotalProjeto;
	
	private Double duracaoTotalProjeto;
	
	private int tarefasNaoRealizadas;
	
	private int habilidadesNecessarias;
	
	private Double trabalhoExtra;

	public MatrizDedicacao() {
		int numeroEmpregados = ProblemaBuilder.getInstance().getEmployees().size();
		int numeroTarefas = ProblemaBuilder.getInstance().getTasks().size();
		
		this.matrizDedicacao = new GrauDedicacao[numeroEmpregados][numeroTarefas];
	}
	
	public void addGrauDedicacao(Employee employee, Task task, Double value) {
		addGrauDedicacao(employee, task, new GrauDedicacao(employee, task, value));
	}
	
	private void addGrauDedicacao(Employee employee, Task task, GrauDedicacao grauDedicacao) {
		this.setGrauDedicacao(employee.getCodigo(), task.getNumero(), grauDedicacao);
	}
	
	public void setGrauDedicacao(int codigoEmployee, int numeroTask, GrauDedicacao grauDedicacao) {
		this.matrizDedicacao[codigoEmployee][numeroTask] = grauDedicacao;
	}
	
	public void efetuaCalculosProjeto() {
		calculaDuracoesTasks();
		calculaTaskScheduling();
		calculaFasesRealizacaoProjeto();
		calculaCustoProjeto();
		calculaEsforcoExtraTotalProjeto();
	}
	
	/**
	 * tjduracao
	 */
	public void calculaDuracoesTasks() {
		escalaTarefas = Lists.newArrayList();
		for (Task task : ProblemaBuilder.getInstance().getTasks()) {
			final BigDecimal esforcoTask = BigDecimal.valueOf(task.getCusto());
			
			final BigDecimal somatorioDedicaoTask = calculaSomatorioDedicacaoTask(task);
			
			BigDecimal duracaoTask = ZERO;
			if (somatorioDedicaoTask.compareTo(ZERO) > 0) {
				duracaoTask = esforcoTask.divide(somatorioDedicaoTask, DECIMAL32);
			}			
			
			escalaTarefas.add(task.getNumero(), new TaskScheduling(task, duracaoTask.doubleValue()));
		}
	}
	
	public void calculaTaskScheduling() {
		for (Task task : ProblemaBuilder.getInstance().getTasks()) {
			calculaTaskScheduling(task);
		}
	}

	public BigDecimal calculaSomatorioDedicacaoTask(Task task) {
		BigDecimal somatorioDedicacaoTask = BigDecimal.ZERO;
		for (Employee employee : ProblemaBuilder.getInstance().getEmployees()) {
			final GrauDedicacao grauDedicacao = getGrauDedicacao(employee, task);
			somatorioDedicacaoTask = somatorioDedicacaoTask.add(BigDecimal.valueOf(grauDedicacao.getValor()));
		}
		
		return somatorioDedicacaoTask;
	}
	
	public Double calculaCustoProjeto() {
		BigDecimal custoProjeto = BigDecimal.ZERO;
		for (Employee employee : ProblemaBuilder.getInstance().getEmployees()) {
			final BigDecimal salario = BigDecimal.valueOf(employee.getSalario());
			
			for (Task task : ProblemaBuilder.getInstance().getTasks()) {
				final TaskScheduling taskScheduling = getTaskScheduling(task);
				final BigDecimal duracaoTask = BigDecimal.valueOf(taskScheduling.getDuracao());
				final GrauDedicacao grauDedicacao = getGrauDedicacao(employee, task);

				final BigDecimal dedicacao = BigDecimal.valueOf(grauDedicacao.getValor());
				custoProjeto = custoProjeto.add(salario.multiply(dedicacao).multiply(duracaoTask));
			}
		}
		
		return this.custoTotalProjeto = custoProjeto.doubleValue();
	}
	
	/**
	 * Restrição 1: Não pode haver tarefa sem ser realizada por algum empregado. Ou seja, não deve haver grau de dedicação <= 0 para uma dada tarefa.
	 * @return
	 */
	public boolean isSolucaoValidaPeranteRestricao1() {
		return calculaNumeroTarefasNaoRealizadas() == 0;
	}

	private int calculaNumeroTarefasNaoRealizadas() {
		int numeroTarefasNaoRealizadas = 0; 
		for (Task task: ProblemaBuilder.getInstance().getTasks()) {
			if (calculaSomatorioDedicacaoTask(task).compareTo(ZERO) <= 0) {
				numeroTarefasNaoRealizadas++;
			}
		}
		
		return this.tarefasNaoRealizadas = numeroTarefasNaoRealizadas;
	}
	
	/**
	 * Restrição 2: Toda tarefa deve ter funcionários aptos a realizá-la. Ou seja, deve haver pelo menos um funcionário atuando sobre essa tarefa que 
	 * possua as habilidades necessárias para realizá-la.
	 * @return
	 */
	public boolean isSolucaoValidaPeranteRestricao2() {
		habilidadesNecessarias = 0;
		for (Task task : ProblemaBuilder.getInstance().getTasks()) {
			final List<Skill> employeesSkills = getSkillsDosEmployeesQueAtuamNaTask(task);
			final List<Skill> taskSkills = task.getSkills();
			employeesSkills.retainAll(taskSkills);
			
			habilidadesNecessarias += difference(newHashSet(taskSkills), newHashSet(employeesSkills)).size();
		}
		
		return habilidadesNecessarias == 0;
	}
	
	/**
	 * Restrição 3: não pode haver trabalho extra no projeto. Trabalho extra ocorre quando um ou mais empregados tem um nível de dedicação
	 * maior que seu nível de dedicação máximo.
	 * @return
	 */
	public boolean isSolucaoValidaPeranteRestricao3() {
		final BigDecimal esforcoExtraTotalProjeto = valueOf(calculaEsforcoExtraTotalProjeto());
		
		return esforcoExtraTotalProjeto.compareTo(ZERO) == 0;
	}

	private Double calculaEsforcoExtraTotalProjeto() {
		BigDecimal esforcoExtraTotalProjeto = ZERO;
		for (Employee employee : ProblemaBuilder.getInstance().getEmployees()) {
			esforcoExtraTotalProjeto = esforcoExtraTotalProjeto.add(calculaEsforcoExtraFuncionario(employee));
		}
		
		return this.trabalhoExtra = esforcoExtraTotalProjeto.doubleValue();
	}

	private BigDecimal calculaEsforcoExtraFuncionario(Employee employee) {
		BigDecimal esforcoExtraFuncionario = ZERO;
		for (FaseProjeto faseProjeto : fasesProjeto) {
			esforcoExtraFuncionario = esforcoExtraFuncionario.add(calculaEsforcoExtraFuncionarioFase(employee, faseProjeto));
		}
		
		return esforcoExtraFuncionario;
	}

	private BigDecimal calculaEsforcoExtraFuncionarioFase(Employee employee, FaseProjeto faseProjeto) {
		final BigDecimal esforcoFase = calculaEsforcoFuncionarioFase(employee, faseProjeto);
		if (esforcoFase.compareTo(ONE) > 0) {
			return calculaEsforcoExtraFuncionarioProjeto(faseProjeto, esforcoFase);
		}
		
		return ZERO;
	}

	private BigDecimal calculaEsforcoExtraFuncionarioProjeto(FaseProjeto faseProjeto, BigDecimal esforcoFase) {
		//(esforcoFase - 1.0) * faseProjeto.getDuracaoFase() 
		//(esforcoFase - 1.0) pois o valor limite do esforço é 1.0, acima disso é esforço extra
		BigDecimal duracaoFase = BigDecimal.valueOf(faseProjeto.getDuracaoFase());
 		
		return (esforcoFase.subtract(ONE)).multiply(duracaoFase);
	}

	private BigDecimal calculaEsforcoFuncionarioFase(Employee employee, FaseProjeto faseProjeto) {
		BigDecimal esforcoFase = BigDecimal.ZERO;
		for (TaskScheduling taskScheduling : faseProjeto.getEscalasConcomitantes()) {
			final Double valorGrauDedicacao = getGrauDedicacao(employee, taskScheduling.getTask()).getValor();
			esforcoFase = esforcoFase.add(valueOf(valorGrauDedicacao));
		}
		
		return esforcoFase;
	}
	
	public void calculaFasesRealizacaoProjeto() {
		this.duracaoTotalProjeto = calculaTempoTotalProjeto();

		Double inicioFase = DOUBLE_ZERO;
		fasesProjeto = newArrayList();
		while (inicioFase < duracaoTotalProjeto) {
			final TaskScheduling taskMaisCedoASerConcluida = getPrimeiraEscalaTarefaASerConcluida(inicioFase);
			final List<TaskScheduling> taskSchedulingConcomitantes = getTaskSchedulingConcomitantes(taskMaisCedoASerConcluida);
			taskSchedulingConcomitantes.add(taskMaisCedoASerConcluida);
			
			fasesProjeto.add(new FaseProjeto(inicioFase, taskMaisCedoASerConcluida.getTempoFim(), taskSchedulingConcomitantes));

			inicioFase = taskMaisCedoASerConcluida.getTempoFim();
		}
	}

	private TaskScheduling getPrimeiraEscalaTarefaASerConcluida(Double tempoInicioFase) {
		if (DOUBLE_ZERO.equals(tempoInicioFase)) {
			return escalaTarefas.stream()
                                .filter(rt -> DOUBLE_ZERO.equals(rt.getTempoInicio()) && rt.getTempoFim() > DOUBLE_ZERO)
                                .min(comparingDouble(TaskScheduling::getTempoFim))
                                .get();
		}
		
		return escalaTarefas.stream()
                            .filter(rt -> rt.getTempoFim() > tempoInicioFase)
                            .min(comparingDouble(TaskScheduling::getTempoFim))
                            .get();
	}

	private List<TaskScheduling> getTaskSchedulingConcomitantes(TaskScheduling taskMaisCedoASerConcluida) {
		Double tempoFim = taskMaisCedoASerConcluida.getTempoFim();
		return escalaTarefas.stream()
                            .filter(rt -> rt.getTempoInicio() < tempoFim && tempoFim < rt.getTempoFim())
                            .collect(toList());
	}

	public Double calculaTempoTotalProjeto() {
		return escalaTarefas.stream()
                            .max((rt1, rt2) -> rt1.getTempoFim().compareTo(rt2.getTempoFim()))
                            .get()
                            .getTempoFim();
	}

	private TaskScheduling calculaTaskScheduling(Task task) {
		if (!task.hasPreviousTasks()) {
			return buildSchedulingTaskInicial(task);
		} 

		return buildSchedulingTaskDependente(task);
	}

	private TaskScheduling buildSchedulingTaskInicial(Task task) {
		return getTaskScheduling(task).buildSchedulingTaskInicial();
	}
	
	private TaskScheduling buildSchedulingTaskDependente(Task task) {
		final List<TaskScheduling> schedulingPrecedentes = new ArrayList<TaskScheduling>();
		for (Task previousTask : task.getPreviousTasks()) {
			schedulingPrecedentes.add(calculaTaskScheduling(previousTask));
		}
		
		final TaskScheduling schedulingPrecedenteMaisTardio = schedulingPrecedentes.stream()
				.max(comparingDouble(TaskScheduling::getTempoFim))
				.get();
		
		final Double tempoInicio = schedulingPrecedenteMaisTardio.getTempoFim();
		final TaskScheduling taskScheduling = getTaskScheduling(task);
		return taskScheduling.buildTaskScheduling(tempoInicio);
	}
	
	public TaskScheduling getTaskScheduling(Task task) {
		return escalaTarefas.stream()
                            .filter(r -> r.getTask().equals(task))
                            .findFirst().get();
	}

	private List<GrauDedicacao> getGrausDedicacao(Task task) {
		final List<GrauDedicacao> grausDedicacao = Lists.newArrayList();
		for (Employee employee : ProblemaBuilder.getInstance().getEmployees()) {
			grausDedicacao.add(employee.getCodigo(), getGrauDedicacao(employee, task));
		}
		
		return grausDedicacao;
	}
	
	private List<Skill> getSkillsDosEmployeesQueAtuamNaTask(Task task) {
		return getGrausDedicacao(task).stream()
                                      .filter(g -> g.getValor() > DOUBLE_ZERO)
                                      .map(g -> g.getEmployee())
                                      .flatMap(e -> e.getSkills().stream())
                                      .distinct()
                                      .collect(Collectors.toList());
	}
	
	public GrauDedicacao getGrauDedicacao(Employee employee, Task task) {
		return getGrauDedicacao(employee.getCodigo(), task.getNumero());
	}
	
	public GrauDedicacao getGrauDedicacao(int codigoEmployee, int taskNumber) {
		return matrizDedicacao[codigoEmployee][taskNumber];
	}
	
	public void efetuarMutacao() {
		int posicaoGene = RandomFactory.getInstance().nextInt(toBinaryString().length());
		
		StringBuilder sb = new StringBuilder(toBinaryString());
		sb.setCharAt(posicaoGene, sb.charAt(posicaoGene) == '0'? '1' : '0');
		
		String novaMatriz = sb.toString();
		int contadorBits = 0;
		for (Employee employee : ProblemaBuilder.getInstance().getEmployees()) {
			for (Task task: ProblemaBuilder.getInstance().getTasks()) {
				final String valorDedicacao = novaMatriz.substring(contadorBits, contadorBits + 3);
				getGrauDedicacao(employee, task).setValor(valorDedicacao);
				
				contadorBits += 3;
			}
		}
	}
	
	public String toBinaryString() {
		String matrizDedicacaoBinaria = EMPTY;
		for (Employee employee : ProblemaBuilder.getInstance().getEmployees()) {
			for (Task task: ProblemaBuilder.getInstance().getTasks()) {
				final GrauDedicacao grauDedicacao = getGrauDedicacao(employee, task);
				
				matrizDedicacaoBinaria += grauDedicacao.getValorAsBinary();
			}
		}
		
		return matrizDedicacaoBinaria;
	}
	
	public void popularMatrizAleatoriamente() {
		for (Employee employee : ProblemaBuilder.getInstance().getEmployees()) {
			for (Task task: ProblemaBuilder.getInstance().getTasks()) {
				addGrauDedicacao(employee, task, RandomFactory.getInstance().randomGrauDedicacao());
			}
		}
	}
	
	public GrauDedicacao[][] getMatrizDedicacao() {
		return matrizDedicacao;
	}

	public List<TaskScheduling> getEscalaTarefas() {
		return escalaTarefas;
	}
	
	public List<FaseProjeto> getFasesProjeto() {
		return fasesProjeto;
	}

	public Double getCustoTotalProjeto() {
		return custoTotalProjeto;
	}

	public Double getDuracaoTotalProjeto() {
		return duracaoTotalProjeto;
	}

	public int getTarefasNaoRealizadas() {
		return tarefasNaoRealizadas;
	}

	public int getHabilidadesNecessarias() {
		return habilidadesNecessarias;
	}

	public Double getTrabalhoExtra() {
		return trabalhoExtra;
	}
	
}
