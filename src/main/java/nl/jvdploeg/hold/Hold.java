// The author disclaims copyright to this source code.
package nl.jvdploeg.hold;

import java.util.List;

/**
 * Hold: the storage space in the hull of a ship.<br>
 * Holds containers referenced by their {@link Id}.
 */
public interface Hold {

  /** Add container with {@link Id}. */
  <T> Id<T> add(Id<T> container);

  /** Get container by {@link Id}. */
  <T extends Id<?>> T getContainer(Id<?> id);

  /** Get containers that implement a {@link Service} type. */
  <T extends Id<?>> List<T> getContainers(Class<?> serviceType);

  /** Remove container by {@link Id}. */
  <T extends Id<?>> T remove(Id<?> id);
}
