package uem.br.ag.peps.tests;

import static org.apache.commons.lang3.math.NumberUtils.DOUBLE_ZERO;
import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uem.br.ag.peps.entidade.Employee;
import uem.br.ag.peps.entidade.Task;
import uem.br.ag.peps.genetico.GrauDedicacao;
import uem.br.ag.peps.genetico.MatrizDedicacao;
import uem.br.ag.peps.problema.ProblemaBuilder;
import uem.br.ag.peps.utils.RandomFactory;

public class MatrizDedicacaoTest {

	private final ProblemaBuilder problemaBuilder = ProblemaBuilder.getInstance();
	
	@Before
	public void before() {
		problemaBuilder.clear();
		problemaBuilder.setParametrosPath("/home/emmanuel/projetos/ag-peps/test-resources/test-intances/peps-4-tasks-2-employees.conf");
		problemaBuilder.readParametrosArquivo();
	}
	
	@Test
	public void testMontagemMatrizDedicacao() {
		final MatrizDedicacao matrizDedicacao = new MatrizDedicacao();
		final GrauDedicacao[][] matriz = matrizDedicacao.getMatrizDedicacao();
		Assert.assertEquals(2, matriz.length);
		Assert.assertEquals(4, matriz[0].length);
		
		final Task task0 = problemaBuilder.getTask(0);
		final Task task1 = problemaBuilder.getTask(1);
		final Task task2 = problemaBuilder.getTask(2);
		final Task task3 = problemaBuilder.getTask(3);
		
		final Employee employee0 = problemaBuilder.getEmployee(0);
		matrizDedicacao.addGrauDedicacao(employee0, task0, 0.4);
		matrizDedicacao.addGrauDedicacao(employee0, task1, 1.0);
		matrizDedicacao.addGrauDedicacao(employee0, task2, 0.3);
		matrizDedicacao.addGrauDedicacao(employee0, task3, 0.7);
		
		final Employee employee1 = problemaBuilder.getEmployee(1);
		matrizDedicacao.addGrauDedicacao(employee1, task0, 0.6);
		matrizDedicacao.addGrauDedicacao(employee1, task1, 0.0);
		matrizDedicacao.addGrauDedicacao(employee1, task2, 0.7);
		matrizDedicacao.addGrauDedicacao(employee1, task3, 0.3);
		
		Assert.assertEquals(2, matriz.length);
		Assert.assertEquals(4, matriz[0].length);
		
		Assert.assertEquals(Double.valueOf(1.0), matrizDedicacao.getGrauDedicacao(employee0, task1).getValor());
		Assert.assertEquals(Double.valueOf(0.7), matrizDedicacao.getGrauDedicacao(employee0, task3).getValor());
		Assert.assertEquals(Double.valueOf(0.6), matrizDedicacao.getGrauDedicacao(employee1, task0).getValor());
		Assert.assertEquals(Double.valueOf(0.7), matrizDedicacao.getGrauDedicacao(employee1, task2).getValor());
	}
	
	@Test
	public void testSomatorioDedicacaoPorTask() {
		final MatrizDedicacao matrizDedicacao = new MatrizDedicacao();
		
		final Task task0 = problemaBuilder.getTask(0);
		final Task task1 = problemaBuilder.getTask(1);
		final Task task2 = problemaBuilder.getTask(2);
		final Task task3 = problemaBuilder.getTask(3);
		
		final Employee employee0 = problemaBuilder.getEmployee(0);
		matrizDedicacao.addGrauDedicacao(employee0, task0, 0.4);
		matrizDedicacao.addGrauDedicacao(employee0, task1, 1.0);
		matrizDedicacao.addGrauDedicacao(employee0, task2, 0.3);
		matrizDedicacao.addGrauDedicacao(employee0, task3, 0.8);
		
		final Employee employee1 = problemaBuilder.getEmployee(1);
		matrizDedicacao.addGrauDedicacao(employee1, task0, 0.2);
		matrizDedicacao.addGrauDedicacao(employee1, task1, 0.0);
		matrizDedicacao.addGrauDedicacao(employee1, task2, 0.7);
		matrizDedicacao.addGrauDedicacao(employee1, task3, 0.6);
		
		assertEquals(0.6, matrizDedicacao.calculaSomatorioDedicacaoTask(task0).doubleValue(), DOUBLE_ZERO);
		assertEquals(1.0, matrizDedicacao.calculaSomatorioDedicacaoTask(task1).doubleValue(), DOUBLE_ZERO);
		assertEquals(1.0, matrizDedicacao.calculaSomatorioDedicacaoTask(task2).doubleValue(), DOUBLE_ZERO);
		assertEquals(1.4, matrizDedicacao.calculaSomatorioDedicacaoTask(task3).doubleValue(), DOUBLE_ZERO);
	}
	
	@Test
	public void testCalculoDuracaoTasks() {
		final MatrizDedicacao matrizDedicacao = new MatrizDedicacao();
		
		final Task task0 = problemaBuilder.getTask(0);
		final Task task1 = problemaBuilder.getTask(1);
		final Task task2 = problemaBuilder.getTask(2);
		final Task task3 = problemaBuilder.getTask(3);
		
		final Employee employee0 = problemaBuilder.getEmployee(0);
		matrizDedicacao.addGrauDedicacao(employee0, task0, 0.4);
		matrizDedicacao.addGrauDedicacao(employee0, task1, 1.0);
		matrizDedicacao.addGrauDedicacao(employee0, task2, 0.3);
		matrizDedicacao.addGrauDedicacao(employee0, task3, 0.8);
		
		final Employee employee1 = problemaBuilder.getEmployee(1);
		matrizDedicacao.addGrauDedicacao(employee1, task0, 0.2);
		matrizDedicacao.addGrauDedicacao(employee1, task1, 0.0);
		matrizDedicacao.addGrauDedicacao(employee1, task2, 0.7);
		matrizDedicacao.addGrauDedicacao(employee1, task3, 0.6);
		
		matrizDedicacao.efetuaCalculosProjeto();

		assertEquals(Double.valueOf(6.666667), matrizDedicacao.getTaskScheduling(task0).getDuracao());
		assertEquals(Double.valueOf(3.0), matrizDedicacao.getTaskScheduling(task1).getDuracao());
		assertEquals(Double.valueOf(5.0), matrizDedicacao.getTaskScheduling(task2).getDuracao());
		assertEquals(Double.valueOf(0.7142857), matrizDedicacao.getTaskScheduling(task3).getDuracao());
	}
	
	@Test
	public void testCalculoCustoTotalProjeto() {
		final MatrizDedicacao matrizDedicacao = new MatrizDedicacao();
		
		final Task task0 = problemaBuilder.getTask(0);
		final Task task1 = problemaBuilder.getTask(1);
		final Task task2 = problemaBuilder.getTask(2);
		final Task task3 = problemaBuilder.getTask(3);
		
		final Employee employee0 = problemaBuilder.getEmployee(0);
		matrizDedicacao.addGrauDedicacao(employee0, task0, 0.4);
		matrizDedicacao.addGrauDedicacao(employee0, task1, 1.0);
		matrizDedicacao.addGrauDedicacao(employee0, task2, 0.3);
		matrizDedicacao.addGrauDedicacao(employee0, task3, 0.8);
		
		final Employee employee1 = problemaBuilder.getEmployee(1);
		matrizDedicacao.addGrauDedicacao(employee1, task0, 0.2);
		matrizDedicacao.addGrauDedicacao(employee1, task1, 0.0);
		matrizDedicacao.addGrauDedicacao(employee1, task2, 0.7);
		matrizDedicacao.addGrauDedicacao(employee1, task3, 0.6);
		
		// 		t0		t1		t2		t3	
		// e0 [	0.4		1.0		0.3		0.8	]
		// e1 [	0.2		0.0		0.7		0.6	]
		
		matrizDedicacao.efetuaCalculosProjeto();
		
		assertEquals(Double.valueOf(33738.09572), matrizDedicacao.getCustoTotalProjeto());
	}
	
	@Test
	public void testCalculoTempoTotalProjeto() {
		final MatrizDedicacao matrizDedicacao = new MatrizDedicacao();
		
		final Task task0 = problemaBuilder.getTask(0);
		final Task task1 = problemaBuilder.getTask(1);
		final Task task2 = problemaBuilder.getTask(2);
		final Task task3 = problemaBuilder.getTask(3);
		
		final Employee employee0 = problemaBuilder.getEmployee(0);
		matrizDedicacao.addGrauDedicacao(employee0, task0, 0.4);
		matrizDedicacao.addGrauDedicacao(employee0, task1, 1.0);
		matrizDedicacao.addGrauDedicacao(employee0, task2, 0.3);
		matrizDedicacao.addGrauDedicacao(employee0, task3, 0.8);
		
		final Employee employee1 = problemaBuilder.getEmployee(1);
		matrizDedicacao.addGrauDedicacao(employee1, task0, 0.2);
		matrizDedicacao.addGrauDedicacao(employee1, task1, 0.0);
		matrizDedicacao.addGrauDedicacao(employee1, task2, 0.7);
		matrizDedicacao.addGrauDedicacao(employee1, task3, 0.6);
		
		// 		t0		t1		t2		t3	
		// e0 [	0.4		1.0		0.3		0.8	]
		// e1 [	0.2		0.0		0.7		0.6	]
		
		matrizDedicacao.efetuaCalculosProjeto();
		
		assertEquals(Double.valueOf(15.3809527), matrizDedicacao.getDuracaoTotalProjeto());
	}
	
	@Test
	public void testMatrizDedicacaoBinaryString() {
		final MatrizDedicacao matrizDedicacao = new MatrizDedicacao();
		
		final Task task0 = problemaBuilder.getTask(0);
		final Task task1 = problemaBuilder.getTask(1);
		final Task task2 = problemaBuilder.getTask(2);
		final Task task3 = problemaBuilder.getTask(3);
		
		final Employee employee0 = problemaBuilder.getEmployee(0);
		matrizDedicacao.addGrauDedicacao(employee0, task0, 0.43);
		matrizDedicacao.addGrauDedicacao(employee0, task1, 1.0);
		matrizDedicacao.addGrauDedicacao(employee0, task2, 0.29);
		matrizDedicacao.addGrauDedicacao(employee0, task3, 0.86);
		
		final Employee employee1 = problemaBuilder.getEmployee(1);
		matrizDedicacao.addGrauDedicacao(employee1, task0, 0.29);
		matrizDedicacao.addGrauDedicacao(employee1, task1, 0.0);
		matrizDedicacao.addGrauDedicacao(employee1, task2, 0.71);
		matrizDedicacao.addGrauDedicacao(employee1, task3, 0.57);
		
		assertEquals("011111010110010000101100", matrizDedicacao.toBinaryString());
	}
	
	@Test
	public void testSolucaoFactivelParaTodasRestricoes() {
		final MatrizDedicacao matrizDedicacao = new MatrizDedicacao();
		
		final Task task0 = problemaBuilder.getTask(0);
		final Task task1 = problemaBuilder.getTask(1);
		final Task task2 = problemaBuilder.getTask(2);
		final Task task3 = problemaBuilder.getTask(3);
		
		final Employee employee0 = problemaBuilder.getEmployee(0);
		matrizDedicacao.addGrauDedicacao(employee0, task0, 0.4);
		matrizDedicacao.addGrauDedicacao(employee0, task1, 1.0);
		matrizDedicacao.addGrauDedicacao(employee0, task2, 0.3);
		matrizDedicacao.addGrauDedicacao(employee0, task3, 0.8);
		
		final Employee employee1 = problemaBuilder.getEmployee(1);
		matrizDedicacao.addGrauDedicacao(employee1, task0, 0.2);
		matrizDedicacao.addGrauDedicacao(employee1, task1, 0.0);
		matrizDedicacao.addGrauDedicacao(employee1, task2, 0.7);
		matrizDedicacao.addGrauDedicacao(employee1, task3, 0.6);
		
		matrizDedicacao.efetuaCalculosProjeto();
		
		Assert.assertTrue("Deve ser uma solução factível, pois todas as tarefas possuem certa dedicação", 
				matrizDedicacao.isSolucaoValidaPeranteRestricao1());
		
		Assert.assertTrue("Deve ser uma solução factível, pois todas as tarefas possuem empregados qualificados para realizá-las", 
				matrizDedicacao.isSolucaoValidaPeranteRestricao2());
		
		Assert.assertTrue("Deve ser uma solução factível, pois não há esforço extra para realização das tarefas do projeto", 
				matrizDedicacao.isSolucaoValidaPeranteRestricao3());
	}
	
	@Test
	public void testSolucaoNaoFactivelParaRestricao1() {
		final MatrizDedicacao matrizDedicacao = new MatrizDedicacao();
		
		final Task task0 = problemaBuilder.getTask(0);
		final Task task1 = problemaBuilder.getTask(1);
		final Task task2 = problemaBuilder.getTask(2);
		final Task task3 = problemaBuilder.getTask(3);
		
		final Employee employee0 = problemaBuilder.getEmployee(0);
		matrizDedicacao.addGrauDedicacao(employee0, task0, 0.4);
		matrizDedicacao.addGrauDedicacao(employee0, task1, 0.0);
		matrizDedicacao.addGrauDedicacao(employee0, task2, 0.3);
		matrizDedicacao.addGrauDedicacao(employee0, task3, 0.8);
		
		final Employee employee1 = problemaBuilder.getEmployee(1);
		matrizDedicacao.addGrauDedicacao(employee1, task0, 0.2);
		matrizDedicacao.addGrauDedicacao(employee1, task1, 0.0);
		matrizDedicacao.addGrauDedicacao(employee1, task2, 0.7);
		matrizDedicacao.addGrauDedicacao(employee1, task3, 0.6);
		
		Assert.assertFalse("Não deve ser uma solução factível, pois a tarefa 2 está sem dedicação de nenhum empregado.", 
				matrizDedicacao.isSolucaoValidaPeranteRestricao1());

		Assert.assertEquals(1, matrizDedicacao.getNumeroTarefasNaoRealizadas());
	}
	
	@Test
	public void testSolucaoNaoFactivelParaRestricao2() {
		final MatrizDedicacao matrizDedicacao = new MatrizDedicacao();
		
		final Task task0 = problemaBuilder.getTask(0);
		final Task task1 = problemaBuilder.getTask(1);
		final Task task2 = problemaBuilder.getTask(2);
		final Task task3 = problemaBuilder.getTask(3);
		
		final Employee employee0 = problemaBuilder.getEmployee(0);
		matrizDedicacao.addGrauDedicacao(employee0, task0, 0.4);
		matrizDedicacao.addGrauDedicacao(employee0, task1, 0.0);
		matrizDedicacao.addGrauDedicacao(employee0, task2, 0.3);
		matrizDedicacao.addGrauDedicacao(employee0, task3, 0.8);
		
		final Employee employee1 = problemaBuilder.getEmployee(1);
		matrizDedicacao.addGrauDedicacao(employee1, task0, 0.2);
		matrizDedicacao.addGrauDedicacao(employee1, task1, 1.0);
		matrizDedicacao.addGrauDedicacao(employee1, task2, 0.7);
		matrizDedicacao.addGrauDedicacao(employee1, task3, 0.6);
		
		Assert.assertFalse("Não deve ser uma solução factível, pois tarefa 1 não possui empregados qualificados para realizá-las", 
				matrizDedicacao.isSolucaoValidaPeranteRestricao2());
		
		Assert.assertEquals(1, matrizDedicacao.getNumeroHabilidadesNecessarias());
	}
	
	@Test
	public void testSolucaoNaoFactivelParaRestricao3() {
		final MatrizDedicacao matrizDedicacao = new MatrizDedicacao();
		
		final Task task0 = problemaBuilder.getTask(0);
		final Task task1 = problemaBuilder.getTask(1);
		final Task task2 = problemaBuilder.getTask(2);
		final Task task3 = problemaBuilder.getTask(3);
		
		final Employee employee0 = problemaBuilder.getEmployee(0);
		matrizDedicacao.addGrauDedicacao(employee0, task0, 0.4);
		matrizDedicacao.addGrauDedicacao(employee0, task1, 1.2);
		matrizDedicacao.addGrauDedicacao(employee0, task2, 0.3);
		matrizDedicacao.addGrauDedicacao(employee0, task3, 0.8);
		
		final Employee employee1 = problemaBuilder.getEmployee(1);
		matrizDedicacao.addGrauDedicacao(employee1, task0, 0.2);
		matrizDedicacao.addGrauDedicacao(employee1, task1, 0.0);
		matrizDedicacao.addGrauDedicacao(employee1, task2, 0.7);
		matrizDedicacao.addGrauDedicacao(employee1, task3, 0.6);
		
		matrizDedicacao.efetuaCalculosProjeto();
		
		Assert.assertFalse("Não deve ser uma solução factível, pois o esforço para realizar a tarefa 2 ultrapassa o esforço limite.", 
				matrizDedicacao.isSolucaoValidaPeranteRestricao3());
		
		Assert.assertEquals(Double.valueOf(0.5), matrizDedicacao.getTotalTrabalhoExtra());
	}
	
	@Test
	public void testMutacaoMatrizDedicacao() {
		RandomFactory.getInstance().setSeed(1);
		
		final MatrizDedicacao matrizDedicacao = new MatrizDedicacao();
		
		final Task task0 = problemaBuilder.getTask(0);
		final Task task1 = problemaBuilder.getTask(1);
		final Task task2 = problemaBuilder.getTask(2);
		final Task task3 = problemaBuilder.getTask(3);
		
		final Employee employee0 = problemaBuilder.getEmployee(0);
		matrizDedicacao.addGrauDedicacao(employee0, task0, 0.43);
		matrizDedicacao.addGrauDedicacao(employee0, task1, 1.0);
		matrizDedicacao.addGrauDedicacao(employee0, task2, 0.29);
		matrizDedicacao.addGrauDedicacao(employee0, task3, 0.86);
		
		final Employee employee1 = problemaBuilder.getEmployee(1);
		matrizDedicacao.addGrauDedicacao(employee1, task0, 0.29);
		matrizDedicacao.addGrauDedicacao(employee1, task1, 0.0);
		matrizDedicacao.addGrauDedicacao(employee1, task2, 0.71);
		matrizDedicacao.addGrauDedicacao(employee1, task3, 0.57);
		
		matrizDedicacao.efetuarMutacao();
		
		Assert.assertEquals(Double.valueOf(0.43), matrizDedicacao.getGrauDedicacao(employee0, task0).getValor());
		Assert.assertEquals(Double.valueOf(1.0), matrizDedicacao.getGrauDedicacao(employee0, task1).getValor());
		Assert.assertEquals(Double.valueOf(0.29), matrizDedicacao.getGrauDedicacao(employee0, task2).getValor());
		Assert.assertEquals(Double.valueOf(0.29), matrizDedicacao.getGrauDedicacao(employee0, task3).getValor());
		Assert.assertEquals(Double.valueOf(0.29), matrizDedicacao.getGrauDedicacao(employee1, task0).getValor());
		Assert.assertEquals(Double.valueOf(0.0), matrizDedicacao.getGrauDedicacao(employee1, task1).getValor());
		Assert.assertEquals(Double.valueOf(0.71), matrizDedicacao.getGrauDedicacao(employee1, task2).getValor());
		Assert.assertEquals(Double.valueOf(0.57), matrizDedicacao.getGrauDedicacao(employee1, task3).getValor());
	}
	
}
