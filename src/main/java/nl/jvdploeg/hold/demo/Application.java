// The author disclaims copyright to this source code.
package nl.jvdploeg.hold.demo;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.swing.SwingUtilities;

import nl.jvdploeg.context.Context;
import nl.jvdploeg.hold.ContextThreadFactory;
import nl.jvdploeg.hold.Facilities;
import nl.jvdploeg.hold.FacilitiesContext;
import nl.jvdploeg.hold.Hold;
import nl.jvdploeg.hold.Id;
import nl.jvdploeg.hold.Memory;
import nl.jvdploeg.message.MessageBundle;

public class Application {

  public static final Id<MessageBundle> MESSAGE_BUNDLE_ID = () -> "messageBundle";

  public static void main(final String[] args) {
    final Application application = new Application();
    application.start();
  }

  private static BarcodeScannerPanel createBarcodeScanner(final FacilitiesContext facilitiesContext) {
    final ContextThreadFactory threadFactory = new ContextThreadFactory("scanner", facilitiesContext);
    final Executor executor = new ScheduledThreadPoolExecutor(1, threadFactory);
    final BarcodeScannerPanel scanner = new BarcodeScannerPanel("barcodeScanner", executor);
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

  private static UserInterfacePanel createUserInterface(final FacilitiesContext facilitiesContext) {
    final ContextThreadFactory threadFactory = new ContextThreadFactory("userInterface", facilitiesContext);
    final Executor executor = new ScheduledThreadPoolExecutor(1, threadFactory);
    final UserInterfacePanel userInterface = new UserInterfacePanel("userInterface", executor);
    return userInterface;
  }

  public Application() {
  }

  private void start() {
    final FacilitiesContext facilitiesContext = createFacilitiesContext();
    final BarcodeScannerPanel barcodeScanner = createBarcodeScanner(facilitiesContext);
    final UserInterfacePanel userInterface = createUserInterface(facilitiesContext);
    createMessageBundle(facilitiesContext);

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

  private static UniversalContainer<MessageBundle> createMessageBundle(final FacilitiesContext facilitiesContext) {
    final ResourceBundle resourceBundle = ResourceBundle.getBundle("nl.jvdploeg.hold.demo.ApplicationBundle");
    final MessageBundle messageBundle = new MessageBundle(resourceBundle);
    final UniversalContainer<MessageBundle> messageBundleContainer = new UniversalContainer<>(MESSAGE_BUNDLE_ID.getId(), messageBundle);
    facilitiesContext.getHold().add(messageBundleContainer);
    return messageBundleContainer;
  }

  public static String translate(final nl.jvdploeg.message.Message message) {
    final UniversalContainer<MessageBundle> container = Context.get(Facilities.class).getHold().getContainer(MESSAGE_BUNDLE_ID);
    return container.getContent().translate(message);
  }
}
