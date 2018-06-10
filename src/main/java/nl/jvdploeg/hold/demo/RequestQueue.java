// The author disclaims copyright to this source code.
package nl.jvdploeg.hold.demo;

import java.util.LinkedList;

import nl.jvdploeg.context.Context;
import nl.jvdploeg.hold.Facilities;
import nl.jvdploeg.hold.Id;
import nl.jvdploeg.hold.Service;

@Service(type = RequestInputService.class)
public final class RequestQueue implements Id<RequestQueue>, RequestInputService {

  private final LinkedList<Request> requests = new LinkedList<>();
  private final String id;
  private final Id<RequestInputService> target;

  public RequestQueue(final String id, final Id<RequestInputService> target) {
    this.id = id;
    this.target = target;
  }

  /** Insert at end for {@link nl.jvdploeg.hold.demo.Request.Priority}. */
  private void add(final Request request) {
    final int priorityCode = request.getPriority().getCode();
    for (int i = 0; i < requests.size(); i++) {
      if (requests.get(i).getPriority().getCode() >= priorityCode) {
        // try next
        continue;
      }
      // place in the middle
      requests.add(i, request);
      return;
    }
    // place at end
    requests.addLast(request);
  }

  /** Remove */
  private void remove(final Request request) {
    final boolean removed = requests.remove(request);
    if (!removed) {
      throw new IllegalArgumentException("can not remove request, queue does not contain request");
    }
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void end(final Request request) {
    final Request oldRequest = requests.peek();
    remove(request);
    if (oldRequest == request) {
      sendEnd(oldRequest);
      if (requests.size() > 0) {
        final Request newRequest = requests.peek();
        sendBegin(newRequest);
      }
    }
  }

  @Override
  public void begin(final Request request) {
    final Request oldRequest = requests.peek();
    add(request);
    final Request newRequest = requests.peek();
    if (oldRequest != null && oldRequest != newRequest) {
      sendEnd(oldRequest);
    }
    if (oldRequest != newRequest) {
      sendBegin(newRequest);
    }
  }

  private void sendBegin(final Request newRequest) {
    Context.get(Facilities.class).send(target, c -> c.begin(newRequest));
  }

  private void sendEnd(final Request oldRequest) {
    Context.get(Facilities.class).send(target, c -> c.end(oldRequest));
  }
}
