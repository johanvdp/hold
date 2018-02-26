package nl.jvdploeg.hold;

public interface Sensor {

  /** The name of the thread that last called the {@link #touch()} method. */
  String getLastTouchedBy();

  /** Get how often the {@link #touch()} method was called. */
  int getTouchCount();

  void touch();
}
