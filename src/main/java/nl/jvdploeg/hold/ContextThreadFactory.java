// The author disclaims copyright to this source code.
package nl.jvdploeg.hold;

import java.util.concurrent.ThreadFactory;

import nl.jvdploeg.context.Context;

/** Creates {@link Thread}s including {@link Context}. */
public final class ContextThreadFactory implements ThreadFactory {

  private final Context<?> context;
  private final String name;
  private int number;

  public ContextThreadFactory(final String name, final Context<?> context) {
    this.name = name;
    this.context = context;
  }

  @Override
  public Thread newThread(final Runnable r) {
    final Runnable runInContext = () -> {
      try {
        context.enter();
        r.run();
      } finally {
        context.exit();
      }
    };
    number++;
    final String threadName = name + "-" + number;
    final Thread thread = new Thread(runInContext, threadName);
    return thread;
  }

}
