// File  : freesell/UIFreeSell.java
// Description: Freecell solitaire program.
//         Main program / JFrame.  Adds a few components and the 
//         main graphics area, UICardPanel, that handles the mouse and painting.
// Author: Fred Swartz - Feb 20 2007 - Placed in public domain.

package freecell.view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import freecell.model.GameModel;
import javax.swing.event.*;

/////////////////////////////////////////////////////////////// class UIFreeSell
public class UIFreeCell extends JFrame {
    private static final long serialVersionUID = 1L;
    private static JLabel jogador;
    private static JLabel jogador1pontosDisplay;
    private static JLabel jogador2pontosDisplay;
    private static int player1points = 0;
    private static int player2points = 0;

    public static void Changeturn() {
        String texto = jogador.getText();
        if (texto == "jogador 1") {
            jogador.setText("jogador 2");
        } else {
            jogador.setText("jogador 1");
        }
    }

    public static void incrementaPontos(int value) {
        String texto = jogador.getText();
        if (texto == "jogador 1") {
            player1points = player1points + value;
            jogador1pontosDisplay.setText(Integer.toString(player1points));

        } else {
            player2points = player2points + value;
            jogador2pontosDisplay.setText(Integer.toString(player2points));

        }
    }

    // ========================================== ========================= fields
    private GameModel _model = new GameModel();

    private UICardPanel _boardDisplay;
    private JCheckBox _autoCompleteCB = new JCheckBox("Auto Complete");

    // ===================================================================== main
    public static void main(String[] args) {
        // ... Do all GUI initialization on Event Dispatching Thread. This is the
        // correct way, but is often omitted because the other
        // almost always(!) works.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new UIFreeCell();
            }
        });
    }

    // ============================================================== constructor
    public UIFreeCell() {

        _boardDisplay = new UICardPanel(_model);
        // ... Create button and check box.
        JButton newGameBtn = new JButton("New Game");
        newGameBtn.addActionListener(new ActionNewGame());

        _autoCompleteCB.setSelected(true);
        _autoCompleteCB.addActionListener(new ActionAutoComplete());
        _boardDisplay.setAutoCompletion(_autoCompleteCB.isSelected());

        // ... Do layout
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("jogador 1:"));
        jogador1pontosDisplay = new JLabel("0");
        controlPanel.add(jogador1pontosDisplay);

        controlPanel.add(new JLabel("jogador 2:"));
        jogador2pontosDisplay = new JLabel("0");

        controlPanel.add(jogador2pontosDisplay);
        controlPanel.add(newGameBtn);
        controlPanel.add(_autoCompleteCB);
        // adicionando um label:
        JLabel turno = new JLabel("turno:");
        controlPanel.add(turno);
        jogador = new JLabel("jogador 1");
        controlPanel.add(jogador);

        // ... Create content pane with graphics area in center (so it expands)
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(controlPanel, BorderLayout.NORTH);
        content.add(_boardDisplay, BorderLayout.CENTER);

        // ... Set this window's characteristics.
        setContentPane(content);
        setTitle("Free Cell");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

    }

    ////////////////////////////////////////////////////////////// ActionNewGame
    class ActionNewGame implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            player1points = 0;
            player2points = 0;
            jogador1pontosDisplay.setText("0");
            jogador2pontosDisplay.setText("0");

            _model.reset();
        }
    }

    ///////////////////////////////////////////////////////// ActionAutoComplete
    class ActionAutoComplete implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            _boardDisplay.setAutoCompletion(_autoCompleteCB.isSelected());
        }
    }

}
