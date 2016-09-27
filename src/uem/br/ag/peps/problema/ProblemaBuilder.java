package uem.br.ag.peps.problema;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Integer.compare;
import static java.util.Collections.emptyList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import uem.br.ag.peps.entidade.Employee;
import uem.br.ag.peps.entidade.Skill;
import uem.br.ag.peps.entidade.Task;
import uem.br.ag.peps.parametros.ParametroEmployeeNumber;
import uem.br.ag.peps.parametros.ParametroEmployeeSalary;
import uem.br.ag.peps.parametros.ParametroEmployeeSkill;
import uem.br.ag.peps.parametros.ParametroGraphArc;
import uem.br.ag.peps.parametros.ParametroLinha;
import uem.br.ag.peps.parametros.ParametroReader;
import uem.br.ag.peps.parametros.ParametroSkillNumber;
import uem.br.ag.peps.parametros.ParametroTaskCost;
import uem.br.ag.peps.parametros.ParametroTaskNumber;
import uem.br.ag.peps.parametros.ParametroTaskSkill;

public class ProblemaBuilder {

	private String parametrosPath;
	
	private List<ParametroLinha> parametros = emptyList();
	
	private List<Skill> skills = newArrayList();
	
	private List<Employee> employees = newArrayList();
	
	private List<Task> tasks = newArrayList();
	
	public static ProblemaBuilder instance;
	
	private synchronized static void newInstance() {
		instance = new ProblemaBuilder();
	}

	public static ProblemaBuilder getInstance() {
		if (instance == null) {
			newInstance();
		}
		
		return instance;
	}

	private ProblemaBuilder() {	}

	public void readParametrosArquivo() {
		try {
			ParametroReader parametroReader = new ParametroReader();
			parametroReader.setFilePath(parametrosPath);
			parametroReader.readParametrosFile();
			parametros = parametroReader.getParametros();
			
			readSkills();
			readEmployees();
			readTasks();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Erro ao ler o arquivo de parâmetros");
		}
	}

	private void readSkills() {
		final ParametroSkillNumber parametroSkillNumber = (ParametroSkillNumber) parametros.stream()
																						   .filter(p -> p instanceof ParametroSkillNumber)
																						   .findFirst().get();
		int quantidadeSkills = parametroSkillNumber.getValue();
		for (int i = 0; i < quantidadeSkills; i++) {
			skills.add(new Skill(i));
		}
	}
	
	private void readEmployees() {
		final ParametroEmployeeNumber parametroEmployeeNumber = (ParametroEmployeeNumber) parametros.stream()
																								    .filter(p -> p instanceof ParametroEmployeeNumber)
																								    .findFirst().get();
		
		int quantidadeEmployees = parametroEmployeeNumber.getValue();
		for (int i = 0; i < quantidadeEmployees; i++) {
			final Employee employee = new Employee();
			employee.setCodigo(i);
			employee.setSalario(readSalario(i));
			employee.setSkills(readEmployeeSkills(i));
			employees.add(employee);
		}
	}
	
	private void readTasks() {
		final ParametroTaskNumber parametroTaskNumber = (ParametroTaskNumber) parametros.stream()
																						.filter(p -> p instanceof ParametroTaskNumber)
																						.findFirst().get();
		
		int quantidadeTasks = parametroTaskNumber.getValue();
		for (int i = 0; i < quantidadeTasks; i++) {
			final Task task = new Task();
			task.setNumero(i);
			task.setCusto(readCustoTask(i));
			task.setSkills(readTaskSkills(i));
			tasks.add(task);
		}
		
		buildHierarquiaEntreTasks();
	}

	private void buildHierarquiaEntreTasks() {
		parametros.stream()
				  .filter(p -> (p instanceof ParametroGraphArc))
				  .sorted((p1, p2)-> compare(((ParametroGraphArc) p1).getNumeroArco(), ((ParametroGraphArc) p2).getNumeroArco()))
				  .forEach(p -> buildRelacionamentoEntreTasks((ParametroGraphArc) p));
	}

	private void buildRelacionamentoEntreTasks(ParametroGraphArc p) {
		final Task taskInicial = tasks.get(p.getNo1());
		final Task taskFinal = tasks.get(p.getNo2());
		taskInicial.addNextTask(taskFinal);
		taskFinal.addPreviousTask(taskInicial);
	}

	private List<Skill> readTaskSkills(int numero) {
		final List<Skill> skills = new ArrayList<Skill>();
		parametros.stream()
				  .filter(p -> (p instanceof ParametroTaskSkill) && ((ParametroTaskSkill) p).getTask() == numero)
				  .sorted((p1, p2)-> compare(((ParametroTaskSkill) p1).getSkill(), ((ParametroTaskSkill) p2).getSkill()))
				  .forEach(p -> skills.add(this.skills.get(((ParametroTaskSkill) p).getValue())));
		
		return skills;
	}

	private List<Skill> readEmployeeSkills(int codigo) {
		final List<Skill> skills = new ArrayList<Skill>();
		parametros.stream()
				  .filter(p -> (p instanceof ParametroEmployeeSkill) && ((ParametroEmployeeSkill) p).getEmployee() == codigo)
				  .sorted((p1, p2) -> compare(((ParametroEmployeeSkill) p1).getSkill(), ((ParametroEmployeeSkill) p2).getSkill()))
				  .forEach(p -> skills.add(this.skills.get(((ParametroEmployeeSkill) p).getValue())));
		
		return skills;
	}

	private Double readSalario(int codigo) {
		return ((ParametroEmployeeSalary) parametros.stream()
													.filter(p -> p instanceof ParametroEmployeeSalary && ((ParametroEmployeeSalary) p).getEmployee() == codigo)
													.findFirst().get()).getValue();
	}
	
	private Double readCustoTask(int numero) {
		return ((ParametroTaskCost) parametros.stream()
											  .filter(p -> p instanceof ParametroTaskCost && ((ParametroTaskCost) p).getTask() == numero)
											  .findFirst().get()).getValue();
	}
	
	public Task getTask(int numero) {
		return tasks.stream()
					.filter(t -> t.getNumero() == numero)
					.findFirst().get();
	}
	
	public Employee getEmployee(int codigo) {
		return employees.stream()
				.filter(e -> e.getCodigo() == codigo)
				.findFirst().get();
	}
	
	public Employee getEmployees(int codigo) {
		return employees.stream()
				.filter(e -> e.getCodigo() == codigo)
				.findFirst().get();
	}
	
	
	public List<Employee> getEmployeesComSkill(Skill skill) {
		return employees.stream()
				 .filter(e -> e.hasSkill(skill))
				 .collect(Collectors.toList());
                        
	}
	
	public Skill getSkill(int codigo) {
		return skills.stream()
				.filter(s -> s.getCodigo() == codigo)
				.findFirst().get();
	}
	
	public void clear() {
		this.parametrosPath = null;
		this.employees = newArrayList();
		this.tasks = newArrayList();
		this.skills = newArrayList();
	}
	
	public void setParametrosPath(String parametrosPath) {
		this.parametrosPath = parametrosPath;
	}

	public List<ParametroLinha> getParametros() {
		return parametros;
	}

	public List<Skill> getSkills() {
		return skills;
	}

	public List<Employee> getEmployees() {
		return employees;
	}
	
	public int getNumeroEmployees() {
		return employees.size();
	}

	public List<Task> getTasks() {
		return tasks;
	}
	
	public int getNumeroTasks() {
		return tasks.size();
	}

}
