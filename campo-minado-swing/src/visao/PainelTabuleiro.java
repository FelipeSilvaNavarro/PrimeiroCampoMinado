package visao;

import java.awt.GridLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import modelo.Tabuleiro;

@SuppressWarnings("serial")
public class PainelTabuleiro extends JPanel {

	public PainelTabuleiro(Tabuleiro tabuleiro) {
		setLayout(new GridLayout(tabuleiro.getLinhas(), tabuleiro.getColunas()));

		tabuleiro.paraCadaCampo(c -> add(new BotaoCampo(c)));

		tabuleiro.registrarObservador(e -> {
			SwingUtilities.invokeLater(() -> {
				if (e.isGanhou()) {
					JOptionPane.showConfirmDialog(this, "Voc� ganhou!");
				} else {
					JOptionPane.showConfirmDialog(this, "Voc� perdeu!");
				}
				tabuleiro.reiniciar();
			});
		});
	}
}
