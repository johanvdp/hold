// The author disclaims copyright to this source code.
package nl.jvdploeg.hold.demo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.concurrent.Executor;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import nl.jvdploeg.context.Context;
import nl.jvdploeg.hold.Command;
import nl.jvdploeg.hold.Container;
import nl.jvdploeg.hold.Facilities;
import nl.jvdploeg.hold.HasExecutor;
import nl.jvdploeg.hold.Service;

@Service(type = RequestInputService.class)
@Service(type = InputService.class)
public final class UserInterfacePanel extends JPanel implements Container, HasExecutor, RequestInputService, InputService {

  private static final long serialVersionUID = 1L;

  private final String id;
  private final Executor executor;
  private JButton requestButton;
  private JButton sendButton;
  private JTextField enteredText;
  private JTextField receivedText;

  private InputService target;

  public UserInterfacePanel(final String id, final Executor executor) {
    this.id = id;
    this.executor = executor;
  }

  @Override
  public void cancelRequest() {
    target = null;
    SwingUtilities.invokeLater(() -> {
      sendButton.setEnabled(false);
      enteredText.setEnabled(false);
      enteredText.setText("");
    });
  }

  @Override
  public Executor getExecutor() {
    return executor;
  }

  @Override
  public String getId() {
    return id;
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
    requestButton.addActionListener(a -> sendRequestInput());
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
  }

  @Override
  public void input(final String input) {
    SwingUtilities.invokeLater(() -> {
      receivedText.setText(input);
    });
    sendCancelRequest();
  }

  @Override
  public void requestInput(final InputService theTarget) {
    target = theTarget;
    SwingUtilities.invokeLater(() -> {
      sendButton.setEnabled(true);
      enteredText.setEnabled(true);
      enteredText.setText("");
    });
  }

  private void sendCancelRequest() {
    Context.get(Facilities.class).sendAll(RequestInputService.class, (Command<RequestInputService>) c -> c.cancelRequest());
  }

  private void sendInput() {
    final String input = enteredText.getText();
    enteredText.setText("");
    Context.get(Facilities.class).send(target, (Command<InputService>) c -> c.input(input));
  }

  private void sendRequestInput() {
    Context.get(Facilities.class).sendAll(RequestInputService.class, (Command<RequestInputService>) c -> c.requestInput(this));
  }
}
