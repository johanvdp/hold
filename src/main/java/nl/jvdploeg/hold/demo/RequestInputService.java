// The author disclaims copyright to this source code.
package nl.jvdploeg.hold.demo;

public interface RequestInputService {

  void end(Request request);

  void begin(Request request);
}
