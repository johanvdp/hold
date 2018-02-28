// The author disclaims copyright to this source code.
package nl.jvdploeg.hold;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface Facilities {

  Hold getHold();

  /** Send {@link Command} to target. */
  <T, U extends T> CompletableFuture<Void> send(Id<U> target, Command<T> command);

  /** Send {@link Command} to target, later. */
  <T, U extends T> CompletableFuture<Void> send(Id<U> target, Command<T> command, long delay, TimeUnit unit);

  /**
   * Send {@link Command} to all targets implementing the {@link Service} type.
   */
  <T> CompletableFuture<Void> sendAll(Class<T> serviceType, Command<T> command);

  /**
   * Send {@link Command} to all targets implementing the {@link Service} type,
   * later.
   */
  <T> CompletableFuture<Void> sendAll(Class<T> serviceType, Command<T> command, long delay, TimeUnit unit);
}
