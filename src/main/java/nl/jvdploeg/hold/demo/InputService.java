// The author disclaims copyright to this source code.
package nl.jvdploeg.hold.demo;

import nl.jvdploeg.hold.Id;

public interface InputService {

  void input(Id<? extends RequestInputService> source, String input);
}
