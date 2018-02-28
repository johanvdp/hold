package nl.jvdploeg.hold;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestContainerWithExecutorTest extends FacilitiesContextTest {

  private Executor executor;
  private TestContainerWithExecutor container;
  private Id<TestContainer> reference;

  @Before
  public void addContainerWithExecutor() throws Exception {
    final ThreadFactory containerContextThreadFactory = new ContextThreadFactory("container", getFacilitiesContext());
    executor = new ScheduledThreadPoolExecutor(1, containerContextThreadFactory);
    container = new TestContainerWithExecutor("container", executor);
    reference = getFacilities().getHold().add(container);
  }

  @After
  public void removeContainerWithExecutor() throws Exception {
    getFacilities().getHold().remove(reference);
    container = null;
    executor = null;
  }

  @Test
  public void testConstructor() throws Exception {
    // then
    Assert.assertEquals(0, container.getTouchCount());
    Assert.assertNotNull(container.getExecutor());
    Assert.assertNull(container.getLastTouchedBy());
  }

  @Test
  public void testSend() throws Exception {
    // given
    // when
    final Future<?> result = getFacilities().send(container, c -> c.touch());
    // then
    result.get();
    Assert.assertEquals(1, container.getTouchCount());
    Assert.assertEquals("container-1", container.getLastTouchedBy());
  }

  @Test
  public void testSendWithDelay() throws Exception {
    // given
    // when
    final Future<?> result = getFacilities().send(container, c -> c.touch(), 20, TimeUnit.MILLISECONDS);
    // then
    Assert.assertNull(container.getLastTouchedBy());
    result.get();
    Assert.assertEquals(1, container.getTouchCount());
    Assert.assertEquals("container-1", container.getLastTouchedBy());
  }
}
