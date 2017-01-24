package cc.arduino.adk.processing;

import java.util.List;

public interface MyDeviceListener {
  void stackTrace(final List<String> trace);

  void sketchStopped();
}
