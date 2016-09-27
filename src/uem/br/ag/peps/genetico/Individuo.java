package uem.br.ag.peps.genetico;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.math.MathContext.DECIMAL32;

import java.math.BigDecimal;

public class Individuo implements Cloneable {

	private MatrizDedicacao matrizDedicacao;
	
	private Double valorFitness;
	
	private Boolean factivel;
	
	public Individuo(MatrizDedicacao matrizDedicacao) {
		this.matrizDedicacao = matrizDedicacao;
		this.valorFitness = null;
		this.factivel = null;
	}
	
	public void verificaFactibilidade() {
		boolean factivelRestricao1 = matrizDedicacao.isSolucaoValidaPeranteRestricao1();
		boolean factivelRestricao2 = matrizDedicacao.isSolucaoValidaPeranteRestricao2();
		boolean factivelRestricao3 = matrizDedicacao.isSolucaoValidaPeranteRestricao3();
		
		this.factivel = factivelRestricao1 && factivelRestricao2 && factivelRestricao3;
	}
	
	public void calculaValorFitness() {
		BigDecimal valorFitness = ZERO;
		if (factivel) {
			valorFitness = calculaValorFitnessSolucaoFactivel();
		} else {
			valorFitness = calculaValorFitnessSolucaoNaoFactivel();
		}
		
		this.valorFitness = valorFitness.doubleValue();
	}

	private BigDecimal calculaValorFitnessSolucaoFactivel() {
		return ONE.divide(calculaCusto(), DECIMAL32);
	}
	
	private BigDecimal calculaValorFitnessSolucaoNaoFactivel() {
		return ONE.divide(calculaCusto().add(calculaPenalidade()), DECIMAL32);
	}

	private BigDecimal calculaCusto() {
		final ParametrosPesos parametrosPesos = ParametrosPesos.getInstance();
		
		final BigDecimal pesoCustoProjeto = valueOf(parametrosPesos.getPesoCustoProjeto());
		final BigDecimal pesoDuracaoProjeto = valueOf(parametrosPesos.getPesoDuracaoProjeto());
		final BigDecimal custoProjeto = valueOf(matrizDedicacao.getCustoTotalProjeto());
		final BigDecimal duracaoProjeto = valueOf(matrizDedicacao.getDuracaoTotalProjeto());
		
		return pesoCustoProjeto.multiply(custoProjeto).add(pesoDuracaoProjeto.multiply(duracaoProjeto));
	}
	
	private BigDecimal calculaPenalidade() {
		final ParametrosPesos parametrosPesos = ParametrosPesos.getInstance();
		
		BigDecimal penalidade = valueOf(parametrosPesos.getPesoPenalidade());
		penalidade = penalidade.add(valueOf(parametrosPesos.getPesoTrabalhoNaoRealizado()).multiply(valueOf(matrizDedicacao.getNumeroTarefasNaoRealizadas())));
		penalidade = penalidade.add(valueOf(parametrosPesos.getPesoHabilidadesNecessarias()).multiply(valueOf(matrizDedicacao.getNumeroHabilidadesNecessarias())));
		penalidade = penalidade.add(valueOf(parametrosPesos.getPesoTrabalhoExtra()).multiply(valueOf(matrizDedicacao.getTotalTrabalhoExtra())));
		
		return penalidade;
	}
	
	public void efetuarMutacao() {
		matrizDedicacao.efetuarMutacao();
	}

	public MatrizDedicacao getMatrizDedicacao() {
		return matrizDedicacao;
	}

	public void setMatrizDedicacao(MatrizDedicacao matrizDedicacao) {
		this.matrizDedicacao = matrizDedicacao;
	}

	public Double getValorFitness() {
		return valorFitness;
	}

	public void setValorFitness(Double valorFitness) {
		this.valorFitness = valorFitness;
	}

	public Boolean isFactivel() {
		return factivel;
	}

	public void setFactivel(boolean isFactivel) {
		this.factivel = isFactivel;
	}
	
	public Double getCustoTotalProjeto() {
		return matrizDedicacao.getCustoTotalProjeto();
	}
	
	public Double getDuracaoTotalProjeto() {
		return matrizDedicacao.getDuracaoTotalProjeto();
	}
	
	public String fitnessToString() {
		if (valorFitness == null) {
			return "NULL";
		}
		
		return new BigDecimal(getValorFitness(), DECIMAL32) + "";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((factivel == null) ? 0 : factivel.hashCode());
		result = prime * result + ((matrizDedicacao == null) ? 0 : matrizDedicacao.hashCode());
		result = prime * result + ((valorFitness == null) ? 0 : valorFitness.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Individuo other = (Individuo) obj;
		if (factivel == null) {
			if (other.factivel != null)
				return false;
		} else if (!factivel.equals(other.factivel))
			return false;
		if (matrizDedicacao == null) {
			if (other.matrizDedicacao != null)
				return false;
		} else if (!matrizDedicacao.equals(other.matrizDedicacao))
			return false;
		if (valorFitness == null) {
			if (other.valorFitness != null)
				return false;
		} else if (!valorFitness.equals(other.valorFitness))
			return false;
		return true;
	}
	
	@Override
	protected Individuo clone() {
		try {
			final Individuo clone = new Individuo(this.matrizDedicacao.clone());
			clone.setFactivel(this.factivel);
			clone.setValorFitness(this.valorFitness);
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
