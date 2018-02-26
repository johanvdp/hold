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
public final class BarcodeScanner implements Container, HasExecutor, RequestInputService {

  public static final String PROPERTY_REQUEST = "request";

  private final String id;
  private final Executor executor;
  private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  private Boolean request = Boolean.FALSE;

  public BarcodeScanner(final String id, final Executor executor) {
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
  public void requestInput() {
    final Boolean oldValue = request;
    if (!oldValue.equals(Boolean.TRUE)) {
      request = Boolean.TRUE;
      propertyChangeSupport.firePropertyChange(PROPERTY_REQUEST, oldValue, request);
    }
  }

  public void send(final String input) {
    Context.get(Facilities.class).sendAll(InputService.class, (Command<InputService>) c -> c.input(input));
  }
}
