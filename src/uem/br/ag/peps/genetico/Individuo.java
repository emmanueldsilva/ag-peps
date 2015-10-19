package uem.br.ag.peps.genetico;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.math.MathContext.DECIMAL32;

import java.math.BigDecimal;

public class Individuo {

	private MatrizDedicacao matrizDedicacao;
	
	private Double valorFitness;
	
	private Boolean factivel;
	
	public Individuo() {
		this(new MatrizDedicacao());
	}
	
	public Individuo(MatrizDedicacao matrizDedicacao) {
		this.matrizDedicacao = matrizDedicacao;
		this.valorFitness = null;
		this.factivel = null;
	}
	
	public void verificaFactibilidade() {
		this.factivel = matrizDedicacao.isSolucaoValidaPeranteRestricao1() && 
						matrizDedicacao.isSolucaoValidaPeranteRestricao2() && 
						matrizDedicacao.isSolucaoValidaPeranteRestricao3();
	}
	
	public void calculaValorFitness(ParametrosPesos parametrosPesos) {
		BigDecimal valorFitness = ZERO;
		if (factivel) {
			valorFitness = calculaValorFitnessSolucaoFactivel(parametrosPesos);
		} else {
			valorFitness = calculaValorFitnessSolucaoNaoFactivel(parametrosPesos);
		}
		
		this.valorFitness = valorFitness.doubleValue();
	}

	private BigDecimal calculaValorFitnessSolucaoFactivel(ParametrosPesos parametrosPesos) {
		return ONE.divide(calculaCusto(parametrosPesos), DECIMAL32);
	}
	
	private BigDecimal calculaValorFitnessSolucaoNaoFactivel(ParametrosPesos parametrosPesos) {
		return ONE.divide(calculaCusto(parametrosPesos).add(calculaPenalidade(parametrosPesos)), DECIMAL32);
	}

	private BigDecimal calculaCusto(ParametrosPesos parametrosPesos) {
		final BigDecimal pesoCustoProjeto = valueOf(parametrosPesos.getPesoCustoProjeto());
		final BigDecimal pesoDuracaoProjeto = valueOf(parametrosPesos.getPesoDuracaoProjeto());
		final BigDecimal custoProjeto = valueOf(matrizDedicacao.getCustoTotalProjeto());
		final BigDecimal duracaoProjeto = valueOf(matrizDedicacao.getDuracaoTotalProjeto());
		
		return pesoCustoProjeto.multiply(custoProjeto).add(pesoDuracaoProjeto.multiply(duracaoProjeto));
	}
	
	private BigDecimal calculaPenalidade(ParametrosPesos parametrosPesos) {
		BigDecimal penalidade = valueOf(parametrosPesos.getPesoPenalidade());
		penalidade = penalidade.add(valueOf(parametrosPesos.getPesoTrabalhoNaoRealizado()).multiply(valueOf(matrizDedicacao.getTarefasNaoRealizadas())));
		penalidade = penalidade.add(valueOf(parametrosPesos.getPesoHabilidadesNecessarias()).multiply(valueOf(matrizDedicacao.getHabilidadesNecessarias())));
		penalidade = penalidade.add(valueOf(parametrosPesos.getPesoTrabalhoExtra()).multiply(valueOf(matrizDedicacao.getTrabalhoExtra())));
		
		return penalidade;
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

	public boolean isFactivel() {
		return factivel;
	}

	public void setFactivel(boolean isFactivel) {
		this.factivel = isFactivel;
	}
	
}
