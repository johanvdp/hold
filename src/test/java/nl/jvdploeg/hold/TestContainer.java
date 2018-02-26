package nl.jvdploeg.hold;

@Service(type = Sensor.class)
public class TestContainer implements Container, Sensor {

  private final String id;
  private volatile int counter;
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
    return counter;
  }

  public final void methodWithParameters(final String[] theParameters) {
    parameters = theParameters;
  }

  @Override
  public final void touch() {
    counter++;
    touchedBy = Thread.currentThread().getName();
  }
}
