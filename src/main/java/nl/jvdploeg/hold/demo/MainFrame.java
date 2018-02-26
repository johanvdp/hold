// The author disclaims copyright to this source code.
package nl.jvdploeg.hold.demo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;

public final class MainFrame extends JFrame {

  private static final long serialVersionUID = 1L;

  private final UserInterface userInterface;
  private final BarcodeScanner barcodeScanner;

  public MainFrame(final UserInterface userInterface, final BarcodeScanner barcodeScanner) {
    this.userInterface = userInterface;
    this.barcodeScanner = barcodeScanner;
  }

  public void initialize() {
    setTitle("Main");
    setLayout(new GridBagLayout());
    final GridBagConstraints gbc = new GridBagConstraints();

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.weightx = 1.0d;
    gbc.weighty = 1.0d;
    gbc.fill = GridBagConstraints.BOTH;
    final UserInterfacePanel userInterfacePanel = new UserInterfacePanel(userInterface);
    userInterfacePanel.initialize();
    add(userInterfacePanel, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.weightx = 1.0d;
    gbc.weighty = 1.0d;
    gbc.fill = GridBagConstraints.BOTH;
    final BarcodeScannerPanel barcodeScannerPanel = new BarcodeScannerPanel(barcodeScanner);
    barcodeScannerPanel.initialize();
    add(barcodeScannerPanel, gbc);

    pack();
    setResizable(false);
    setVisible(true);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
  }
}
