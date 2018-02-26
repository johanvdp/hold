// The author disclaims copyright to this source code.
package nl.jvdploeg.hold.demo;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.swing.SwingUtilities;

import nl.jvdploeg.hold.ContextThreadFactory;
import nl.jvdploeg.hold.FacilitiesContext;
import nl.jvdploeg.hold.Hold;
import nl.jvdploeg.hold.Memory;

public class Application {

  public static void main(final String[] args) {
    final Application application = new Application();
    application.start();
  }

  private static BarcodeScanner createBarcodeScanner(final FacilitiesContext facilitiesContext) {
    final ContextThreadFactory threadFactory = new ContextThreadFactory("scanner", facilitiesContext);
    final Executor executor = new ScheduledThreadPoolExecutor(1, threadFactory);
    final BarcodeScanner scanner = new BarcodeScanner("barcodeScanner", executor);
    facilitiesContext.getHold().add(scanner);
    return scanner;
  }

  private static FacilitiesContext createFacilitiesContext() {
    final FacilitiesContext facilitiesContext = new FacilitiesContext();
    final ContextThreadFactory threadFactory = new ContextThreadFactory("default", facilitiesContext);
    final Hold hold = new Memory();
    facilitiesContext.setHold(hold);
    final int availableProcessors = Runtime.getRuntime().availableProcessors();
    final Executor defaultExecutor = new ScheduledThreadPoolExecutor(availableProcessors, threadFactory);
    facilitiesContext.setDefaultExecutor(defaultExecutor);
    return facilitiesContext;
  }

  private static UserInterface createUserInterface(final FacilitiesContext facilitiesContext) {
    final ContextThreadFactory threadFactory = new ContextThreadFactory("userInterface", facilitiesContext);
    final Executor executor = new ScheduledThreadPoolExecutor(1, threadFactory);
    final UserInterface userInterface = new UserInterface("userInterface", executor);
    facilitiesContext.getHold().add(userInterface);
    return userInterface;
  }

  public Application() {
  }

  private void start() {
    final FacilitiesContext facilitiesContext = createFacilitiesContext();
    final BarcodeScanner barcodeScanner = createBarcodeScanner(facilitiesContext);
    final UserInterface userInterface = createUserInterface(facilitiesContext);

    final MainFrame mainFrame = new MainFrame(userInterface, barcodeScanner);
    // give AWT thread facilities context
    // perform initialization on AWT thread
    SwingUtilities.invokeLater(() -> {
      facilitiesContext.enter();
      mainFrame.initialize();
    });
    mainFrame.addWindowListener(new WindowAdapter() {

      @Override
      public void windowClosed(final WindowEvent ev) {
        super.windowClosed(ev);
        facilitiesContext.exit();
        System.exit(0);
      }
    });
  }
}
