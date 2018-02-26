package nl.jvdploeg.hold;

import java.util.concurrent.Executor;

public class TestContainerWithExecutor extends TestContainer implements HasExecutor {

  private final Executor executor;

  public TestContainerWithExecutor(final String id, final Executor executor) {
    super(id);
    this.executor = executor;
  }

  @Override
  public final Executor getExecutor() {
    return executor;
  }
}
