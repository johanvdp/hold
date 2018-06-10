package nl.jvdploeg.hold.demo;

import java.util.ArrayList;
import java.util.List;

import nl.jvdploeg.hold.Id;

public final class InputRecorder implements Id<InputService>, InputService {

  public static final class RecordedInput {

    private final Id<? extends RequestInputService> source;
    private final String input;

    public RecordedInput(final Id<? extends RequestInputService> source, final String input) {
      this.source = source;
      this.input = input;
    }

    public String getInput() {
      return input;
    }

    public Id<? extends RequestInputService> getSource() {
      return source;
    }
  }

  private final String id;
  private final List<RecordedInput> calls = new ArrayList<>();

  public InputRecorder(final String id) {
    this.id = id;
  }

  @Override
  public String getId() {
    return id;
  }

  public List<RecordedInput> getRequests() {
    return calls;
  }

  @Override
  public void input(final Id<? extends RequestInputService> source, final String input) {
    calls.add(new RecordedInput(source, input));
  }
}
