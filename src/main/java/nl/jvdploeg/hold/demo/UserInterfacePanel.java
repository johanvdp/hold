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

public final class UserInterfacePanel extends JPanel {

  private static final long serialVersionUID = 1L;

  private final UserInterface userInterface;
  private JButton requestButton;
  private JButton sendButton;
  private JTextField enteredText;
  private JTextField receivedText;
  private final PropertyChangeListener listener = ev -> {
    final String propertyName = ev.getPropertyName();
    if (UserInterface.PROPERTY_REQUEST.equals(propertyName)) {
      onRequest(((Boolean) ev.getNewValue()).booleanValue());
    } else if (UserInterface.PROPERTY_INPUT.equals(propertyName)) {
      onInput((String) ev.getNewValue());
    }
  };

  public UserInterfacePanel(final UserInterface userInterface) {
    this.userInterface = userInterface;
  }

  public void initialize() {
    setBorder(new TitledBorder("User interface"));
    setLayout(new GridBagLayout());
    final GridBagConstraints gbc = new GridBagConstraints();

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.weightx = 0.0d;
    gbc.weighty = 0.0d;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add(new JLabel("received:"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.weightx = 1.0d;
    gbc.weighty = 0.0d;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    receivedText = new JTextField(20);
    receivedText.setEditable(false);
    add(receivedText, gbc);

    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.weightx = 0.0d;
    gbc.weighty = 0.0d;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    requestButton = new JButton("Request");
    requestButton.addActionListener(a -> sendRequest());
    add(requestButton, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.weightx = 0.0d;
    gbc.weighty = 0.0d;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add(new JLabel("entered:"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.weightx = 1.0d;
    gbc.weighty = 0.0d;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    enteredText = new JTextField(10);
    enteredText.setEnabled(false);
    add(enteredText, gbc);

    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.weightx = 0.0d;
    gbc.weighty = 0.0d;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    sendButton = new JButton("Send");
    sendButton.setEnabled(false);
    sendButton.addActionListener(a -> sendInput());
    add(sendButton, gbc);

    Context.get(Facilities.class).send(userInterface, c -> c.addPropertyChangeListener(listener));
  }

  private void onInput(final String newValue) {
    SwingUtilities.invokeLater(() -> {
      receivedText.setText(newValue);
    });
  }

  private void onRequest(final boolean request) {
    SwingUtilities.invokeLater(() -> {
      sendButton.setEnabled(request);
      enteredText.setEnabled(request);
      if (!request) {
        enteredText.setText("");
      }
    });
  }

  private void sendInput() {
    final String input = enteredText.getText();
    enteredText.setText("");
    Context.get(Facilities.class).send(userInterface, c -> c.send(input));
  }

  private void sendRequest() {
    Context.get(Facilities.class).send(userInterface, c -> c.sendRequest());
  }
}
