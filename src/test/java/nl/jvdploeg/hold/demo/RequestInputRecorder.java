package nl.jvdploeg.hold.demo;

import java.util.ArrayList;
import java.util.List;

import nl.jvdploeg.hold.Id;

public final class RequestInputRecorder implements Id<RequestInputService>, RequestInputService {

  enum RequestType {
    BEGIN, END;
  }

  public static final class RecordedRequest {

    private final Request request;
    private final RequestType type;

    public RecordedRequest(final Request request, final RequestType type) {
      this.request = request;
      this.type = type;
    }

    public Request getRequest() {
      return request;
    }

    public RequestType getType() {
      return type;
    }
  }

  private final String id;
  private volatile List<RecordedRequest> requests = new ArrayList<>();

  public RequestInputRecorder(final String id) {
    this.id = id;
  }

  @Override
  public void end(final Request request) {
    requests.add(new RecordedRequest(request, RequestType.END));
  }

  @Override
  public void begin(final Request request) {
    requests.add(new RecordedRequest(request, RequestType.BEGIN));
  }

  @Override
  public String getId() {
    return id;
  }

  public List<RecordedRequest> getRequests() {
    return requests;
  }
}
