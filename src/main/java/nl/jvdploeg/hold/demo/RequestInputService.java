// The author disclaims copyright to this source code.
package nl.jvdploeg.hold.demo;

import nl.jvdploeg.hold.Id;

public interface RequestInputService {

  void cancelRequest();

  void requestInput(Id<? extends InputService> target);
}
