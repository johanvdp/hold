package nl.jvdploeg.hold;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ServiceTest extends FacilitiesContextTest {

  private TestContainer containerA;
  private Id<TestContainer> referenceA;
  private Executor executor;
  private TestContainerWithExecutor containerB;
  private Id<TestContainer> referenceB;

  @Before
  public void addContainers() throws Exception {
    containerA = new TestContainer("a");
    referenceA = getFacilities().getHold().add(containerA);

    final ThreadFactory containerContextThreadFactory = new ContextThreadFactory("b", getFacilitiesContext());
    executor = new ScheduledThreadPoolExecutor(1, containerContextThreadFactory);
    containerB = new TestContainerWithExecutor("b", executor);
    referenceB = getFacilities().getHold().add(containerB);
  }

  @After
  public void removeContainers() throws Exception {
    getFacilities().getHold().remove(referenceA);
    containerA = null;

    getFacilities().getHold().remove(referenceB);
    containerB = null;
    executor = null;
  }

  @Test
  public void testSendAll() throws Exception {
    // given
    // when
    final CompletableFuture<Void> result = getFacilities().sendAll(Sensor.class, c -> c.touch());
    // then
    result.get();
    Assert.assertEquals(1, containerA.getTouchCount());
    Assert.assertEquals("default-1", containerA.getLastTouchedBy());
    Assert.assertEquals(1, containerB.getTouchCount());
    Assert.assertEquals("b-1", containerB.getLastTouchedBy());
  }

  @Test
  public void testSendAllWithDelay() throws Exception {
    // given
    // when
    final CompletableFuture<Void> result = getFacilities().sendAll(Sensor.class, c -> c.touch(), 20, TimeUnit.MILLISECONDS);
    // then
    Assert.assertNull(containerA.getLastTouchedBy());
    Assert.assertNull(containerB.getLastTouchedBy());
    result.get();
    Assert.assertEquals(1, containerA.getTouchCount());
    Assert.assertEquals("default-1", containerA.getLastTouchedBy());
    Assert.assertEquals(1, containerB.getTouchCount());
    Assert.assertEquals("b-1", containerB.getLastTouchedBy());
  }
}
