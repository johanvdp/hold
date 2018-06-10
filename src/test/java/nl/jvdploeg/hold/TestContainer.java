package nl.jvdploeg.hold;

import java.util.concurrent.atomic.AtomicInteger;

@Service(type = Sensor.class)
public class TestContainer implements Id<TestContainer>, Sensor {

  private final String id;
  private final AtomicInteger counter = new AtomicInteger(0);
  private String touchedBy;
  private String[] parameters;

  public TestContainer(final String id) {
    this.id = id;
  }

  @Override
  public final String getId() {
    return id;
  }

  @Override
  public final String getLastTouchedBy() {
    return touchedBy;
  }

  public final String[] getParameters() {
    return parameters;
  }

  @Override
  public final int getTouchCount() {
    return counter.get();
  }

  public final void methodWithParameters(final String[] theParameters) {
    parameters = theParameters;
  }

  @Override
  public final void touch() {
    counter.incrementAndGet();
    touchedBy = Thread.currentThread().getName();
  }
}
