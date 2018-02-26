// The author disclaims copyright to this source code.
package nl.jvdploeg.hold;

/** Command for type. */
public interface Command<T> {

  /** Execute command against target. */
  void execute(T target);
}
