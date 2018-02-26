// The author disclaims copyright to this source code.
package nl.jvdploeg.hold;

import java.util.concurrent.Executor;

/** Declare that a type has its own {@link Executor}. */
public interface HasExecutor {

  Executor getExecutor();
}
