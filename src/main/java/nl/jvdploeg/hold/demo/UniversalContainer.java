// The author disclaims copyright to this source code.
package nl.jvdploeg.hold.demo;

import nl.jvdploeg.hold.Id;

public final class UniversalContainer<T> implements Id<UniversalContainer<T>> {

  private final String id;
  private final T content;

  public UniversalContainer(final String id, final T content) {
    this.id = id;
    this.content = content;

  }

  @Override
  public String getId() {
    return id;
  }

  public T getContent() {
    return content;
  }
}
