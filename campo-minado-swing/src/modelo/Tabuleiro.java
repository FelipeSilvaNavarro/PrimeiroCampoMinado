package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Tabuleiro implements CampoObservador {

	private final int colunas;
	private final int linhas;
	private int minas;

	private final List<Campo> campos = new ArrayList<>();
	private final List<Consumer<ResultadoEvento>> observadores = new ArrayList<>();

	public void paraCadaCampo(Consumer<Campo> funcao) {
		campos.forEach(funcao);
	}

	public void registrarObservador(Consumer<ResultadoEvento> observador) {
		observadores.add(observador);
	}

	private void notificarObservador(boolean resultado) {
		observadores.stream().forEach(o -> o.accept(new ResultadoEvento(resultado)));
	}

	public Tabuleiro(int colunas, int linhas, int minas) {
		this.colunas = colunas;
		this.linhas = linhas;
		this.minas = minas;
		gerarCampos();
		associarOsVizinhos();
		sortearMinas();
	}

	private void gerarCampos() {
		for (int linha = 0; linha < linhas; linha++) {
			for (int coluna = 0; coluna < colunas; coluna++) {
				Campo campo = new Campo(linha, coluna);
				campo.registrarObservador(this);
				campos.add(campo);
			}
		}
	}

	private void associarOsVizinhos() {
		for (Campo c1 : campos) {
			for (Campo c2 : campos) {
				c1.adicionarVizinho(c2);
			}
		}
	}

	private void sortearMinas() {
		long minasArmadas = 0;
		Predicate<Campo> minado = c -> c.isMinado();
		do {
			int aleatorio = (int) (Math.random() * campos.size());
			/*
			 * Parenteses usado para dar prioridade ao calculo e // depois transformar em
			 * inteiro, caso contrario n�o ir� funcionar
			 */
			campos.get(aleatorio).minar();
			minasArmadas = campos.stream().filter(minado).count();

		} while (minasArmadas < minas);
	}

	public int getColunas() {
		return colunas;
	}

	public int getLinhas() {
		return linhas;
	}

	public boolean objetivoAlcancado() {
		return campos.stream().allMatch(c -> c.objetivoAlcancado());
	}

	public void reiniciar() {
		campos.stream().forEach(c -> c.reiniciar());
		sortearMinas();
	}

	public void abrir(int linha, int coluna) {
		campos.parallelStream().filter(c -> c.getLinha() == linha && c.getColuna() == coluna).findFirst()
				.ifPresent(c -> c.abrir());
	}

	public void marcar(int linha, int coluna) {
		campos.parallelStream().filter(c -> c.getLinha() == linha && c.getColuna() == coluna).findFirst()
				.ifPresent(c -> c.alternarMarcacao());
	}

	@Override
	public void eventoOcorreu(Campo campo, CampoEvento evento) {
		if (evento == CampoEvento.EXPLODIR) {
			mostrarMinas();
			notificarObservador(false);
		} else if (objetivoAlcancado()) {
			notificarObservador(true);
		}

	}

	private void mostrarMinas() {
		campos.stream().filter(c -> c.isMinado()).filter(c -> !c.isMarcado()).forEach(c -> c.setAberto(true));
	}

}
