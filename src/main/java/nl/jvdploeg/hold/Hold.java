// The author disclaims copyright to this source code.
package nl.jvdploeg.hold;

import java.util.List;

/** Hold containing {@link Container}s. */
public interface Hold {

  /** Add {@link Container}. */
  void add(Container container);

  /** Get {@link Container} by container id. */
  <T extends Container> T getContainer(String containerId);

  /** Get {@link Container}s that implement a {@link Service} type. */
  <T extends Container> List<T> getContainers(Class<?> serviceType);

  /** Remove {@link Container} by container id. */
  <T extends Container> T remove(String id);
}
