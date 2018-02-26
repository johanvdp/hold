// The author disclaims copyright to this source code.
package nl.jvdploeg.hold.demo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.Executor;

import nl.jvdploeg.context.Context;
import nl.jvdploeg.hold.Command;
import nl.jvdploeg.hold.Container;
import nl.jvdploeg.hold.Facilities;
import nl.jvdploeg.hold.HasExecutor;
import nl.jvdploeg.hold.Service;

@Service(type = RequestInputService.class)
@Service(type = InputService.class)
public final class UserInterface implements Container, HasExecutor, RequestInputService, InputService {

  public static final String PROPERTY_REQUEST = "request";
  public static final String PROPERTY_INPUT = "input";

  private final String id;
  private final Executor executor;
  private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

  private Boolean request = Boolean.FALSE;
  private String input = "";

  public UserInterface(final String id, final Executor executor) {
    this.id = id;
    this.executor = executor;
  }

  public void addPropertyChangeListener(final PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  @Override
  public void cancelRequest() {
    final Boolean oldValue = request;
    if (!oldValue.equals(Boolean.FALSE)) {
      request = Boolean.FALSE;
      propertyChangeSupport.firePropertyChange(PROPERTY_REQUEST, oldValue, request);
    }
  }

  @Override
  public Executor getExecutor() {
    return executor;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void input(final String newValue) {
    Context.get(Facilities.class).sendAll(RequestInputService.class, (Command<RequestInputService>) c -> c.cancelRequest());

    final String oldValue = input;
    input = newValue;
    propertyChangeSupport.firePropertyChange(PROPERTY_INPUT, oldValue, newValue);
  }

  @Override
  public void requestInput() {
    final Boolean oldValue = request;
    if (!oldValue.equals(Boolean.TRUE)) {
      request = Boolean.TRUE;
      propertyChangeSupport.firePropertyChange(PROPERTY_REQUEST, oldValue, request);
    }
  }

  public void send(final String newInput) {
    Context.get(Facilities.class).sendAll(InputService.class, (Command<InputService>) c -> c.input(newInput));
  }

  public void sendRequest() {
    Context.get(Facilities.class).sendAll(RequestInputService.class, (Command<RequestInputService>) c -> c.requestInput());
  }
}
