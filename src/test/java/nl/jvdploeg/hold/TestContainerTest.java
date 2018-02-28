package nl.jvdploeg.hold;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestContainerTest extends FacilitiesContextTest {

  private TestContainer container;

  @Before
  public void addContainer() {
    container = new TestContainer("container");
    getFacilities().getHold().add(container);
  }

  @After
  public void removeContainer() {
    getFacilities().getHold().remove("container");
    container = null;
  }

  @Test
  public void testConstructor() throws Exception {
    // then
    Assert.assertEquals(0, container.getTouchCount());
  }

  @Test
  public void testSend() throws Exception {
    // given
    // when
    final Future<?> result = getFacilities().send(container, c -> c.touch());
    // then
    result.get();
    Assert.assertEquals(1, container.getTouchCount());
    Assert.assertEquals("default-1", container.getLastTouchedBy());
  }

  @Test
  public void testSendMethodWithParameters() throws Exception {
    // given
    // when
    final Future<?> result = getFacilities().send(container, c -> c.methodWithParameters(new String[] { "A", "B" }));
    // then
    result.get();
    Assert.assertArrayEquals(new String[] { "A", "B" }, container.getParameters());
  }

  @Test
  public void testSendWithDelay() throws Exception {
    // given
    // when
    final Future<?> result = getFacilities().send(container, (Command<TestContainer>) c -> c.touch(), 20, TimeUnit.MILLISECONDS);
    // then
    Assert.assertNull(container.getLastTouchedBy());
    result.get();
    Assert.assertEquals(1, container.getTouchCount());
    Assert.assertEquals("default-1", container.getLastTouchedBy());
  }

}
