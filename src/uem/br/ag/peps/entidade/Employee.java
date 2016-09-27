package uem.br.ag.peps.entidade;

import static java.util.Collections.emptyList;

import java.util.List;

public class Employee {

	private int codigo;
	
	private Double salario;
	
	private List<Skill> skills = emptyList();

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public Double getSalario() {
		return salario;
	}

	public void setSalario(Double salario) {
		this.salario = salario;
	}

	public List<Skill> getSkills() {
		return skills;
	}

	public void setSkills(List<Skill> skills) {
		this.skills = skills;
	}
	
	public void addSkill(Skill skill) {
		if (!hasSkill(skill)) {
			skills.add(skill);
		}
	}
	
	public boolean hasSkill(Skill skill) {
		return skills.contains(skill);
	}
}
