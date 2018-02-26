// The author disclaims copyright to this source code.
package nl.jvdploeg.hold.demo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import nl.jvdploeg.context.Context;
import nl.jvdploeg.hold.Facilities;

public final class BarcodeScannerPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  private final BarcodeScanner barcodeScanner;
  private JTextField inputText;
  private JButton sendButton;

  private final PropertyChangeListener listener = ev -> {
    final String propertyName = ev.getPropertyName();
    if (BarcodeScanner.PROPERTY_REQUEST.equals(propertyName)) {
      onRequest(((Boolean) ev.getNewValue()).booleanValue());
    }
  };

  public BarcodeScannerPanel(final BarcodeScanner barcodeScanner) {
    this.barcodeScanner = barcodeScanner;
  }

  public void initialize() {
    setBorder(new TitledBorder("Barcode scanner"));
    setLayout(new GridBagLayout());
    final GridBagConstraints gbc = new GridBagConstraints();

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 0.0d;
    gbc.weighty = 0.0d;
    gbc.fill = GridBagConstraints.NONE;
    add(new JLabel("scanned:"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0d;
    gbc.weighty = 0.0d;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    inputText = new JTextField(10);
    inputText.setEnabled(false);
    add(inputText, gbc);

    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weightx = 0.0d;
    gbc.weighty = 0.0d;
    gbc.fill = GridBagConstraints.NONE;
    sendButton = new JButton("Send");
    sendButton.setEnabled(false);
    sendButton.addActionListener(a -> sendInput());
    add(sendButton, gbc);

    Context.get(Facilities.class).send(barcodeScanner, c -> c.addPropertyChangeListener(listener));
  }

  private void onRequest(final boolean request) {
    SwingUtilities.invokeLater(() -> {
      sendButton.setEnabled(request);
      inputText.setEnabled(request);
      if (!request) {
        inputText.setText("");
      }
    });
  }

  private void sendInput() {
    final String input = inputText.getText();
    inputText.setText("");
    Context.get(Facilities.class).send(barcodeScanner, c -> c.send(input));
  }
}
