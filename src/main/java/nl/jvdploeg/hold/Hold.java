// The author disclaims copyright to this source code.
package nl.jvdploeg.hold;

import java.util.List;

/** Hold containing {@link Id}s. */
public interface Hold {

  /** Add {@link Id}. */
  void add(Id<?> container);

  /** Get {@link Id} by container id. */
  <T extends Id<?>> T getContainer(String containerId);

  /** Get {@link Id}s that implement a {@link Service} type. */
  <T extends Id<?>> List<T> getContainers(Class<?> serviceType);

  /** Remove {@link Id} by container id. */
  <T extends Id<?>> T remove(String id);
}
