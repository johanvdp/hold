// The author disclaims copyright to this source code.
package nl.jvdploeg.hold.demo;

import java.util.UUID;

import nl.jvdploeg.hold.Id;
import nl.jvdploeg.message.Message;

public interface Request extends Id<Request> {

  enum Priority {
    HIGH(30), NORMAL(20), LOW(10);

    private final int code;

    Priority(final int code) {
      this.code = code;
    }

    public int getCode() {
      return code;
    }
  }

  static Request createRequest(final Id<? extends InputService> target, final Priority priority, final Message message) {
    final String id = UUID.randomUUID().toString();
    final Request request = new Request() {

      @Override
      public String getId() {
        return id;
      }

      @Override
      public Id<? extends InputService> getTarget() {
        return target;
      }

      @Override
      public Priority getPriority() {
        return priority;
      }

      @Override
      public Message getMessage() {
        return message;
      }
    };
    return request;
  }

  Id<? extends InputService> getTarget();

  Message getMessage();

  Priority getPriority();
}
