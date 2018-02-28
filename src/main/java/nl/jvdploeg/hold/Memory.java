// The author disclaims copyright to this source code.
package nl.jvdploeg.hold;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.jvdploeg.exception.Checks;
import nl.jvdploeg.exception.IllegalArgumentExceptionBuilder;
import nl.jvdploeg.exception.IllegalStateExceptionBuilder;

/**
 * In-memory implementation of {@link Hold}.
 */
public final class Memory implements Hold {

  /** {@link Id}s by container id. */
  private final Map<String, Id<?>> containers = new HashMap<>();
  /** {@link Id}s by {@link Service} type. */
  private final Map<Class<?>, List<Id<?>>> services = new HashMap<>();

  public Memory() {
  }

  @Override
  public synchronized void add(final Id<?> container) {
    Checks.ARGUMENT.notNull(container, "container");
    addContainer(container);
    addServices(container);
  }

  @SuppressWarnings("unchecked")
  @Override
  public synchronized <T extends Id<?>> T getContainer(final String containerId) {
    Checks.ARGUMENT.notNull(containerId, "containerId");
    final T container = (T) containers.get(containerId);
    if (container == null) {
      throw new IllegalArgumentExceptionBuilder() //
          .method("get") //
          .message("hold does not contain") //
          .field("container", containerId) //
          .build();
    }
    return container;
  }

  @SuppressWarnings("unchecked")
  @Override
  public synchronized <T extends Id<?>> List<T> getContainers(final Class<?> serviceType) {
    Checks.ARGUMENT.notNull(serviceType, "serviceType");
    final List<T> list = (List<T>) services.get(serviceType);
    if (list == null) {
      throw new IllegalStateExceptionBuilder() //
          .method("getContainers") //
          .message("hold does not contain") //
          .field("serviceType", serviceType.toString()) //
          .build();

    }
    return Collections.unmodifiableList(list);
  }

  @SuppressWarnings("unchecked")
  @Override
  public synchronized <T extends Id<?>> T remove(final String id) {
    Checks.ARGUMENT.notNull(id, "id");
    final T container = (T) removeContainer(id);
    removeServices(container);
    return container;
  }

  private void addContainer(final Id<?> container) {
    Checks.ARGUMENT.notNull(container, "container");
    final String id = container.getId();
    Checks.ARGUMENT.notNull(id, "container.id");
    if (containers.containsKey(id)) {
      throw new IllegalArgumentExceptionBuilder() //
          .method("addContainer") //
          .message("hold already contains") //
          .field("container", id) //
          .build();
    }
    containers.put(id, container);
  }

  private void addServices(final Id<?> container) {
    final Service[] serviceAnnotations = getServices(container);
    for (final Service serviceAnnotation : serviceAnnotations) {
      final Class<?> serviceClass = serviceAnnotation.type();
      List<Id<?>> list = services.get(serviceClass);
      if (list == null) {
        list = new ArrayList<>();
        services.put(serviceClass, list);
      }
      list.add(container);
    }
  }

  private Service[] getServices(final Id<?> container) {
    final Service[] list = container.getClass().getAnnotationsByType(Service.class);
    return list;
  }

  private Id<?> removeContainer(final String id) {
    final Id<?> container = containers.remove(id);
    if (container == null) {
      throw new IllegalStateExceptionBuilder() //
          .method("removeContainer") //
          .message("hold does not contain") //
          .field("container.id", id) //
          .build();
    }
    return container;
  }

  private void removeServices(final Id<?> container) {
    final Service[] serviceAnnotations = getServices(container);
    for (final Service serviceAnnotation : serviceAnnotations) {
      final Class<?> serviceType = serviceAnnotation.type();
      final List<Id<?>> list = services.get(serviceType);
      final boolean removed = list.remove(container);
      if (!removed) {
        throw new IllegalStateExceptionBuilder() //
            .method("removeService") //
            .message("hold does not contain") //
            .field("service.type", serviceType.toString()) //
            .identity("container", container) //
            .field("container.id", container.getId()) //
            .build();
      }
    }
  }
}
