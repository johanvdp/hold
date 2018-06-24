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
import nl.jvdploeg.message.MessageBuilder;
import nl.jvdploeg.message.MessageDefinition;

public final class UserInterfacePanel extends JPanel {

  @Service(type = InputService.class)
  private final class Input implements Id<Input>, HasExecutor, InputService {

    private Input() {
    }

    @Override
    public Executor getExecutor() {
      return executor;
    }

    @Override
    public String getId() {
      return id + ".input";
    }

    @Override
    public void input(final Id<? extends RequestInputService> source, final String theInput) {
      SwingUtilities.invokeLater(() -> {
        inputText.setText(source.getId() + ":" + theInput);
      });
      sendCancelRequest();
    }

  }

  @Service(type = InputService.class)
  private final class Message implements Id<Input>, HasExecutor, InputService {

    private Message() {
    }

    @Override
    public Executor getExecutor() {
      return executor;
    }

    @Override
    public String getId() {
      return id + ".message";
    }

    @Override
    public void input(final Id<? extends RequestInputService> source, final String theInput) {
      SwingUtilities.invokeLater(() -> {
        messageText.setText(source.getId() + ":" + theInput);
      });
    }
  }

  @Service(type = RequestInputService.class)
  private final class RequestInput implements Id<RequestInput>, HasExecutor, RequestInputService {

    private RequestInput() {
    }

    @Override
    public Executor getExecutor() {
      return executor;
    }

    @Override
    public String getId() {
      return id + ".requestInput";
    }

    @Override
    public void begin(final Request newRequest) {
      target = newRequest.getTarget();
      final String requestMessage = Application.translate(newRequest.getMessage());
      SwingUtilities.invokeLater(() -> {
        sendButton.setEnabled(true);
        enteredText.setEnabled(true);
        enteredText.setText("");
        messageText.setText(requestMessage);
      });
    }

    @Override
    public void end(final Request newRequest) {
      target = null;
      SwingUtilities.invokeLater(() -> {
        sendButton.setEnabled(false);
        enteredText.setEnabled(false);
        enteredText.setText("");
        messageText.setText("");
      });
    }

  }

  private static final long serialVersionUID = 1L;

  private final String id;
  private final Executor executor;
  private JButton requestButton;
  private JButton sendButton;
  private JTextField enteredText;
  private JTextField inputText;
  private JTextField messageText;
  private Id<? extends InputService> target;
  private final Input input = new Input();
  private final Message message = new Message();
  private final RequestInput requestInput = new RequestInput();

  private Request request;

  public UserInterfacePanel(final String id, final Executor executor) {
    this.id = id;
    this.executor = executor;
  }

  private void sendInput() {
    final String text = enteredText.getText();
    enteredText.setText("");
    Context.get(Facilities.class).send(target, (Command<InputService>) c -> c.input(requestInput, text));
  }

  private void sendCancelRequest() {
    Context.get(Facilities.class).sendAll(RequestInputService.class, (Command<RequestInputService>) c -> c.end(request));
  }

  private void sendRequestInput() {
    final nl.jvdploeg.message.Message requestMessage = new MessageBuilder(new MessageDefinition("userInterface.request")).build();
    request = Request.createRequest(input, Request.Priority.NORMAL, requestMessage);
    Context.get(Facilities.class).sendAll(RequestInputService.class, (Command<RequestInputService>) c -> c.begin(request));
  }

  public void initialize() {

    setBorder(new TitledBorder("User interface"));
    setLayout(new GridBagLayout());
    final GridBagConstraints gbc = new GridBagConstraints();

    // row: message
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.weightx = 0.0d;
    gbc.weighty = 0.0d;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add(new JLabel("message:"), gbc);

    gbc.gridx = 1;
    gbc.gridwidth = 1;
    gbc.weightx = 1.0d;
    gbc.weighty = 0.0d;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    messageText = new JTextField(20);
    messageText.setEditable(false);
    add(messageText, gbc);

    // row: input
    gbc.gridx = 0;
    gbc.gridy++;
    gbc.gridwidth = 1;
    gbc.weightx = 0.0d;
    gbc.weighty = 0.0d;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add(new JLabel("input:"), gbc);

    gbc.gridx = 1;
    gbc.gridwidth = 1;
    gbc.weightx = 1.0d;
    gbc.weighty = 0.0d;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    inputText = new JTextField(20);
    inputText.setEditable(false);
    add(inputText, gbc);

    gbc.gridx = 2;
    gbc.gridwidth = 1;
    gbc.weightx = 0.0d;
    gbc.weighty = 0.0d;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    requestButton = new JButton("Request");
    requestButton.addActionListener(a -> sendRequestInput());
    add(requestButton, gbc);

    // row: data entry
    gbc.gridx = 0;
    gbc.gridy++;
    gbc.gridwidth = 1;
    gbc.weightx = 0.0d;
    gbc.weighty = 0.0d;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add(new JLabel("enter:"), gbc);

    gbc.gridx = 1;
    gbc.gridwidth = 1;
    gbc.weightx = 1.0d;
    gbc.weighty = 0.0d;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    enteredText = new JTextField(10);
    enteredText.setEnabled(false);
    add(enteredText, gbc);

    gbc.gridx = 2;
    gbc.gridwidth = 1;
    gbc.weightx = 0.0d;
    gbc.weighty = 0.0d;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    sendButton = new JButton("Send");
    sendButton.setEnabled(false);
    sendButton.addActionListener(a -> sendInput());
    add(sendButton, gbc);

    Context.get(Facilities.class).getHold().add(input);
    Context.get(Facilities.class).getHold().add(message);
    Context.get(Facilities.class).getHold().add(requestInput);
  }
}
