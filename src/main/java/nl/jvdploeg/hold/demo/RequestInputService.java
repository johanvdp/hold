// The author disclaims copyright to this source code.
package nl.jvdploeg.hold.demo;

public interface RequestInputService {

  void cancelRequest();

  void requestInput(InputService target);
}
