// The author disclaims copyright to this source code.
package nl.jvdploeg.hold.demo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import nl.jvdploeg.context.Context;
import nl.jvdploeg.hold.Command;
import nl.jvdploeg.hold.Facilities;
import nl.jvdploeg.hold.HasExecutor;
import nl.jvdploeg.hold.Id;

public final class Scenario implements Id<Scenario>, HasExecutor {

  public enum State {
    STARTED, WAITING_FOR_INPUT, ENDED;
  }

  private final String id;
  private volatile State state = State.STARTED;
  private volatile String input;
  private CompletableFuture<Void> timeout;
  private volatile boolean inputRequested;
  private final Executor executor;

  public Scenario(final String id, final Executor executor) {
    this.id = id;
    this.executor = executor;
  }

  @Override
  public Executor getExecutor() {
    return executor;
  }

  @Override
  public String getId() {
    return id;
  }

  public String getInput() {
    return input;
  }

  public State getState() {
    return state;
  }

  public void input(final String theInput) {
    switch (state) {
      case WAITING_FOR_INPUT:
        state = State.ENDED;
        cancelTimeout();
        input = theInput;
        return;
      case STARTED:
      case ENDED:
      default:
        throw new IllegalStateException("state:" + state);
    }
  }

  public boolean isInputRequested() {
    return inputRequested;
  }

  public void start() {
    switch (state) {
      case STARTED:
        state = State.WAITING_FOR_INPUT;
        requestInput();
        startTimeout();
        return;
      case WAITING_FOR_INPUT:
      case ENDED:
      default:
        throw new IllegalStateException("state:" + state);
    }
  }

  public void timeout() {
    switch (state) {
      case WAITING_FOR_INPUT:
        state = State.ENDED;
        return;
      case STARTED:
      case ENDED:
      default:
        throw new IllegalStateException("state:" + state);
    }
  }

  private void cancelTimeout() {
    if (timeout != null) {
      timeout.cancel(false);
      timeout = null;
    }
  }

  private void requestInput() {
    inputRequested = true;
  }

  private void startTimeout() {
    final Facilities facilities = Context.get(Facilities.class);
    timeout = facilities.send(this, (Command<Scenario>) c -> c.timeout(), 10, TimeUnit.SECONDS);
  }
}
