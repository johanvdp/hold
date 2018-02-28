// The author disclaims copyright to this source code.
package nl.jvdploeg.hold;

/**
 * Typed identifier.<br>
 * Allow reference to the type, but gives no access to the implementation.<br>
 * Makes sure that facilities are used to send commands instead invoking methods
 * directly.
 */
public interface Id<T> {

  String getId();
}
