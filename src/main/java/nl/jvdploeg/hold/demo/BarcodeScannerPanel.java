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
import nl.jvdploeg.hold.Facilities;
import nl.jvdploeg.hold.HasExecutor;
import nl.jvdploeg.hold.Id;
import nl.jvdploeg.hold.Service;

@Service(type = RequestInputService.class)
public final class BarcodeScannerPanel extends JPanel implements Id<BarcodeScannerPanel>, HasExecutor, RequestInputService {

  private static final long serialVersionUID = 1L;

  private final String id;
  private final Executor executor;
  private JTextField inputText;
  private JTextField messageText;
  private JButton sendButton;

  private Id<? extends InputService> target;

  public BarcodeScannerPanel(final String id, final Executor executor) {
    this.id = id;
    this.executor = executor;
  }

  @Override
  public void end(final Request request) {
    target = null;
    SwingUtilities.invokeLater(() -> {
      sendButton.setEnabled(false);
      inputText.setText("");
      messageText.setText("");
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
    setBorder(new TitledBorder("Barcode scanner"));
    setLayout(new GridBagLayout());
    final GridBagConstraints gbc = new GridBagConstraints();

    // row: message
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 0.0d;
    gbc.weighty = 0.0d;
    gbc.fill = GridBagConstraints.NONE;
    add(new JLabel("message:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0d;
    gbc.weighty = 0.0d;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    messageText = new JTextField(10);
    messageText.setEnabled(true);
    messageText.setEditable(false);
    add(messageText, gbc);

    // row: input
    gbc.gridx = 0;
    gbc.gridy++;
    gbc.weightx = 0.0d;
    gbc.weighty = 0.0d;
    gbc.fill = GridBagConstraints.NONE;
    add(new JLabel("input:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0d;
    gbc.weighty = 0.0d;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    inputText = new JTextField(10);
    inputText.setEnabled(false);
    add(inputText, gbc);

    gbc.gridx = 2;
    gbc.weightx = 0.0d;
    gbc.weighty = 0.0d;
    gbc.fill = GridBagConstraints.NONE;
    sendButton = new JButton("Send");
    sendButton.setEnabled(false);
    sendButton.addActionListener(a -> sendInput());
    add(sendButton, gbc);
  }

  @Override
  public void begin(final Request newRequest) {
    target = newRequest.getTarget();
    final String requestMessage = Application.translate(newRequest.getMessage());
    SwingUtilities.invokeLater(() -> {
      sendButton.setEnabled(true);
      inputText.setEnabled(true);
      inputText.setText("");
      messageText.setText(requestMessage);
    });
  }

  private void sendInput() {
    final String input = inputText.getText();
    inputText.setText("");
    Context.get(Facilities.class).send(target, (Command<InputService>) c -> c.input(this, input));
  }
}
