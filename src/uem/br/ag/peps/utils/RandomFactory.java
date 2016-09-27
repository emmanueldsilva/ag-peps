package uem.br.ag.peps.utils;

import static java.math.MathContext.DECIMAL32;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import uem.br.ag.peps.entidade.Employee;
import uem.br.ag.peps.entidade.Skill;
import uem.br.ag.peps.entidade.Task;
import uem.br.ag.peps.problema.ProblemaBuilder;
 
public class RandomFactory {
     
    public static final int HORAS_EXPEDIENTE = 8;

	private final Random random;
     
    private static RandomFactory instance = null;
        
    private RandomFactory() { 
        this.random = new Random();
    }
    
    public synchronized static RandomFactory getInstance() {
        if (instance == null) {
            instance = new RandomFactory();
        }
         
        return instance;
    }
 
    public int nextInt(int n) {
        return this.random.nextInt(n);
    }
    
    public void setSeed(long seed) {
    	this.random.setSeed(seed);
    }
    
    public Double randomGrauDedicacao() {
    	return getValorGrauDedicacao(nextInt(HORAS_EXPEDIENTE));
    }
    
    public Double randomGrauDedicacaoPositivo() {
    	return getValorGrauDedicacaoSemZero(nextInt(HORAS_EXPEDIENTE));
    }

	public Double getValorGrauDedicacao(int index) {
		switch (index) {
		case 0:
			return new BigDecimal(1).divide(new BigDecimal(7), DECIMAL32).doubleValue();
		case 1:
			return new BigDecimal(2).divide(new BigDecimal(7), DECIMAL32).doubleValue();
		case 2:
			return new BigDecimal(3).divide(new BigDecimal(7), DECIMAL32).doubleValue();
		case 3:
			return new BigDecimal(4).divide(new BigDecimal(7), DECIMAL32).doubleValue();
		case 4:
			return new BigDecimal(5).divide(new BigDecimal(7), DECIMAL32).doubleValue();
		case 5:
			return new BigDecimal(6).divide(new BigDecimal(7), DECIMAL32).doubleValue();
		case 6:
		default:
			return new BigDecimal(7).divide(new BigDecimal(7), DECIMAL32).doubleValue();
		}
	}
	
	public Double getValorGrauDedicacaoSemZero(int index) {
		switch (index) {
		case 0:
			return new BigDecimal(1).divide(new BigDecimal(7), DECIMAL32).doubleValue();
		case 1:
			return new BigDecimal(2).divide(new BigDecimal(7), DECIMAL32).doubleValue();
		case 2:
			return new BigDecimal(3).divide(new BigDecimal(7), DECIMAL32).doubleValue();
		case 3:
			return new BigDecimal(4).divide(new BigDecimal(7), DECIMAL32).doubleValue();
		case 4:
			return new BigDecimal(5).divide(new BigDecimal(7), DECIMAL32).doubleValue();
		case 5:
			return new BigDecimal(6).divide(new BigDecimal(7), DECIMAL32).doubleValue();
		case 6:
		default:
			return new BigDecimal(7).divide(new BigDecimal(7), DECIMAL32).doubleValue();
		}
	}
    
    public Double randomDoubleRange1() {
    	return this.random.nextDouble();
    }
    
    public Double randomDoubleRange2() {
    	return this.random.nextDouble() + nextInt(2);
    }
    
    public Employee randomEmployee() {
    	return randomElement(ProblemaBuilder.getInstance().getEmployees());
    }
    
    public Employee randomEmployeeComSkill(Skill skill) {
    	return randomElement(ProblemaBuilder.getInstance().getEmployeesComSkill(skill));
    }
    
    public Task randomTask() {
    	return randomElement(ProblemaBuilder.getInstance().getTasks());
    }
    
	public <T> T randomElement(List<T> elements) {
		return elements.get(this.random.nextInt(elements.size()));
    }
    
}