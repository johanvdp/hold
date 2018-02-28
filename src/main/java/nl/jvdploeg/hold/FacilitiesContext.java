// The author disclaims copyright to this source code.
package nl.jvdploeg.hold;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import nl.jvdploeg.context.Context;
import nl.jvdploeg.exception.Checks;

@SuppressWarnings("rawtypes")
public final class FacilitiesContext extends Context<Facilities> implements Facilities {

  /** Execute {@link Command} as {@link Runnable}. */
  private static final class RunnableCommand<T> implements Runnable {

    private final T target;
    private final Command command;

    private RunnableCommand(final T target, final Command command) {
      this.target = target;
      this.command = command;
    }

    @Override
    public void run() {
      command.execute(target);
    }
  }

  /** Executor for {@link Id}s without {@link HasExecutor}. */
  private Executor defaultExecutor;

  /** Hold {@link Id}s by id. */
  private Hold hold;

  public FacilitiesContext() {
  }

  @Override
  public Hold getHold() {
    return hold;
  }

  @Override
  public <T, U extends T> CompletableFuture<Void> send(final Id<U> id, final Command<T> command) {
    Checks.ARGUMENT.notNull(id, "id");
    Checks.ARGUMENT.notNull(command, "command");
    final U container = hold.getContainer(id.getId());
    final Executor executor = getExecutor(container);
    final RunnableCommand<T> runnableCommand = new RunnableCommand<>(container, command);
    final CompletableFuture<Void> future = CompletableFuture.runAsync(runnableCommand, executor);
    return future;
  }

  @Override
  public <T, U extends T> CompletableFuture<Void> send(final Id<U> id, final Command<T> command, final long delay, final TimeUnit unit) {
    Checks.ARGUMENT.notNull(id, "id");
    Checks.ARGUMENT.notNull(command, "command");
    Checks.ARGUMENT.ge(Long.valueOf(delay), "delay", Long.valueOf(0L));
    Checks.ARGUMENT.notNull(unit, "unit");
    final U container = hold.getContainer(id.getId());
    final Executor executor = getExecutor(container);
    final RunnableCommand<T> runnableCommand = new RunnableCommand<>(container, command);
    final CompletableFuture<Void> future = CompletableFuture.runAsync(runnableCommand, CompletableFuture.delayedExecutor(delay, unit, executor));
    return future;
  }

  @Override
  public <T> CompletableFuture<Void> sendAll(final Class<T> serviceType, final Command<T> command) {
    Checks.ARGUMENT.notNull(serviceType, "serviceType");
    Checks.ARGUMENT.notNull(command, "command");
    final List<Id> containers = hold.getContainers(serviceType);
    final List<CompletableFuture<Void>> futures = new ArrayList<>();
    for (final Id container : containers) {
      final Executor executor = getExecutor(container);
      final RunnableCommand<Id> runnableCommand = new RunnableCommand<>(container, command);
      futures.add(CompletableFuture.runAsync(runnableCommand, executor));
    }
    final CompletableFuture<?>[] futuresArray = futures.toArray(new CompletableFuture<?>[futures.size()]);
    final CompletableFuture<Void> allFutures = CompletableFuture.allOf(futuresArray);
    return allFutures;
  }

  @Override
  public <T> CompletableFuture<Void> sendAll(final Class<T> serviceType, final Command<T> command, final long delay, final TimeUnit unit) {
    Checks.ARGUMENT.notNull(serviceType, "serviceType");
    Checks.ARGUMENT.notNull(command, "command");
    Checks.ARGUMENT.ge(Long.valueOf(delay), "delay", Long.valueOf(0L));
    Checks.ARGUMENT.notNull(unit, "unit");
    final List<Id> containers = hold.getContainers(serviceType);
    final List<CompletableFuture<Void>> futures = new ArrayList<>();
    for (final Id container : containers) {
      final Executor executor = getExecutor(container);
      final RunnableCommand<Id> runnableCommand = new RunnableCommand<>(container, command);
      futures.add(CompletableFuture.runAsync(runnableCommand, CompletableFuture.delayedExecutor(delay, unit, executor)));
    }
    final CompletableFuture<?>[] futuresArray = futures.toArray(new CompletableFuture<?>[futures.size()]);
    final CompletableFuture<Void> allFutures = CompletableFuture.allOf(futuresArray);
    return allFutures;
  }

  public void setDefaultExecutor(final Executor defaultExecutor) {
    this.defaultExecutor = defaultExecutor;
  }

  public void setHold(final Hold hold) {
    this.hold = hold;
  }

  private Executor getExecutor(final Object container) {
    Checks.STATE.notNull(container, "container");
    if (container instanceof HasExecutor) {
      return ((HasExecutor) container).getExecutor();
    }
    return defaultExecutor;
  }
}
