package uem.br.ag.peps.genetico;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import uem.br.ag.peps.entidade.Employee;
import uem.br.ag.peps.entidade.Skill;
import uem.br.ag.peps.entidade.Task;
import uem.br.ag.peps.problema.ProblemaBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class MatrizDedicacao {
	
	private GrauDedicacao[][] matrizDedicacao;
	
	private List<Double> duracoesTask = new ArrayList<Double>();

	public MatrizDedicacao() {
		int numeroEmpregados = ProblemaBuilder.getInstance().getEmployees().size();
		int numeroTarefas = ProblemaBuilder.getInstance().getTasks().size();
		
		this.matrizDedicacao = new GrauDedicacao[numeroEmpregados][numeroTarefas];
	}
	
	public void addGrauDedicacao(Employee employee, Task task, Double value) {
		final GrauDedicacao grauDedicacao = new GrauDedicacao();
		grauDedicacao.setEmployee(employee);
		grauDedicacao.setTask(task);
		grauDedicacao.setValor(value);
		
		this.matrizDedicacao[employee.getCodigo()][task.getNumero()] = grauDedicacao;
	}
	
	/**
	 * tjduracao
	 */
	public void calculaDuracoesTasks() {
		for (int task = 0; task < matrizDedicacao.length; task++) {
			final BigDecimal esforcoTask = BigDecimal.valueOf(ProblemaBuilder.getInstance().getTask(task).getCusto());
			final BigDecimal duracaoTask = esforcoTask.divide(calculaSomatorioDedicacaoTask(task), RoundingMode.HALF_EVEN);
			duracoesTask.add(task, duracaoTask.doubleValue());
		}
	}

	private BigDecimal calculaSomatorioDedicacaoTask(int task) {
		BigDecimal somatorioDedicacaoTask = BigDecimal.ZERO;
		
		for (int employee = 0; employee < matrizDedicacao[task].length; employee++) {
			final GrauDedicacao grauDedicacao = matrizDedicacao[employee][task];
			somatorioDedicacaoTask = somatorioDedicacaoTask.add(BigDecimal.valueOf(grauDedicacao.getValor()));
		}
		
		return somatorioDedicacaoTask;
	}
	
	public Double calculaCustoProjeto() {
		BigDecimal custoProjeto = BigDecimal.ZERO;
		
		for (int task = 0; task < matrizDedicacao.length; task++) {
			final BigDecimal duracaoTask = BigDecimal.valueOf(duracoesTask.get(0));
			
			for (int employee = 0; employee < matrizDedicacao[task].length; employee++) {
				final BigDecimal salario = BigDecimal.valueOf(ProblemaBuilder.getInstance().getEmployee(employee).getSalario());
				final GrauDedicacao grauDedicacao = matrizDedicacao[employee][task];

				final BigDecimal dedicacao = BigDecimal.valueOf(grauDedicacao.getValor());
				custoProjeto.add(salario.multiply(dedicacao).multiply(duracaoTask));
			}
		}
		
		return custoProjeto.doubleValue();
	}
	
	/**
	 * Restrição 1: Não pode haver tarefa sem ser realizada por algum empregado. Ou seja, não deve haver grau de dedicação <= 0 para uma dada tarefa.
	 * @return
	 */
	public boolean isSolucaoValidaPeranteRestricao1() {
		for (int task = 0; task < matrizDedicacao.length; task++) {
			if (calculaSomatorioDedicacaoTask(task).compareTo(ZERO) <= 0) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isSolucaoValidaPeranteRestricao2() {
		for (int task = 0; task < matrizDedicacao.length; task++) {
			
			
		}
		
		return false;
	}
	
	private List<GrauDedicacao> getGrausDedicacao(int task) {
		final List<GrauDedicacao> grausDedicacao = Lists.newArrayList();
		for (int employee = 0; employee < matrizDedicacao[task].length; employee++) {
			grausDedicacao.add(employee, matrizDedicacao[employee][task]);
		}
		
		return grausDedicacao;
	}
	
	private List<GrauDedicacao> getGrausDedicaoEfetivos(int task) {
		final List<GrauDedicacao> grausDedicacao = getGrausDedicacao(task);

		List<Skill> list = Lists.newArrayList();
		Set<Skill> set = Sets.newHashSet();
		grausDedicacao.stream()
					 .filter(g -> g.getValor() > 0.0)
					 .map(g -> g.getEmployee())
					 .map(e -> e.getSkills())
					 .forEach(s -> set.addAll(s));
		list.addAll(set);
		
		return null;
	}
}