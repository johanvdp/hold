package nl.jvdploeg.hold;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import org.junit.After;
import org.junit.Before;

import nl.jvdploeg.context.Context;

public abstract class FacilitiesContextTest {

  /** Thread specific context providing facilities. */
  private FacilitiesContext facilitiesContext;
  private Hold hold;
  private Facilities facilities;

  @After
  public final void after() throws Exception {
    facilities = null;
    facilitiesContext.exit();
  }

  @Before
  public final void before() throws Exception {
    hold = new Memory();
    facilitiesContext = new FacilitiesContext();
    facilitiesContext.enter();
    facilities = Context.get(Facilities.class);
    final ThreadFactory defaultContextThreadFactory = new ContextThreadFactory("default", facilitiesContext);
    final Executor executor = new ScheduledThreadPoolExecutor(1, defaultContextThreadFactory);
    facilitiesContext.setHold(hold);
    facilitiesContext.setDefaultExecutor(executor);
  }

  public final Facilities getFacilities() {
    return facilities;
  }

  public final FacilitiesContext getFacilitiesContext() {
    return facilitiesContext;
  }
}
