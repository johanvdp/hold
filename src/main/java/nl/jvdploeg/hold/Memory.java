// The author disclaims copyright to this source code.
package nl.jvdploeg.hold;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.jvdploeg.exception.Checks;
import nl.jvdploeg.exception.ThrowableBuilder;

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
  public synchronized <T> Id<T> add(final Id<T> container) {
    Checks.ARGUMENT.notNull(container, "container");
    addContainer(container);
    addServices(container);
    return container;
  }

  @SuppressWarnings("unchecked")
  @Override
  public synchronized <T extends Id<?>> T getContainer(final Id<?> id) {
    Checks.ARGUMENT.notNull(id, "id");
    Checks.ARGUMENT.notNull(id.getId(), "id.id");
    final T container = (T) containers.get(id.getId());
    if (container == null) {
      throw ThrowableBuilder.createIllegalStateExceptionBuilder() //
          .method("get") //
          .message("hold does not contain") //
          .field("id", id.getId()) //
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
      throw ThrowableBuilder.createIllegalStateExceptionBuilder() //
          .method("getContainers") //
          .message("hold does not contain") //
          .field("serviceType", serviceType.toString()) //
          .build();

    }
    return Collections.unmodifiableList(list);
  }

  @SuppressWarnings("unchecked")
  @Override
  public synchronized <T extends Id<?>> T remove(final Id<?> id) {
    Checks.ARGUMENT.notNull(id, "id");
    final T container = (T) removeContainer(id);
    removeServices(container);
    return container;
  }

  private void addContainer(final Id<?> container) {
    final String id = container.getId();
    Checks.ARGUMENT.notNull(id, "container.id");
    if (containers.containsKey(id)) {
      throw ThrowableBuilder.createIllegalStateExceptionBuilder() //
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

  private Id<?> removeContainer(final Id<?> id) {
    Checks.ARGUMENT.notNull(id.getId(), "id.id");
    final Id<?> container = containers.remove(id.getId());
    if (container == null) {
      throw ThrowableBuilder.createIllegalStateExceptionBuilder() //
          .method("removeContainer") //
          .message("hold does not contain") //
          .field("id", id.getId()) //
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
        throw ThrowableBuilder.createIllegalStateExceptionBuilder() //
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
