package nl.jvdploeg.hold.demo;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.jvdploeg.hold.Command;
import nl.jvdploeg.hold.ContextThreadFactory;
import nl.jvdploeg.hold.FacilitiesContext;
import nl.jvdploeg.hold.Id;
import nl.jvdploeg.hold.Memory;
import nl.jvdploeg.hold.demo.Request.Priority;
import nl.jvdploeg.hold.demo.RequestInputRecorder.RecordedRequest;
import nl.jvdploeg.hold.demo.RequestInputRecorder.RequestType;
import nl.jvdploeg.message.MessageBuilder;
import nl.jvdploeg.message.MessageDefinition;

/**
 * Test {@link RequestQueue}.<br>
 * Sending and expecting the following requests:
 * <ul>
 * <li>L: begin low priority request
 * <li>N: begin normal priority request
 * <li>H: begin high priority request
 * <li>l: end low priority request
 * <li>n: end normal priority request
 * <li>h: end high priority request
 */
public class RequestQueueTest {

  private static RequestType getRequestType(final char requestCode) {
    switch (requestCode) {
      case 'L':
      case 'N':
      case 'H':
        return RequestType.BEGIN;
      case 'l':
      case 'n':
      case 'h':
        return RequestType.END;
      default:
        throw new IllegalArgumentException("unknown request code:" + requestCode);
    }
  }

  private InputRecorder source;
  private RequestInputRecorder target;
  private FacilitiesContext facilitiesContext;
  private Memory hold;
  private Id<InputService> sourceId;
  private Id<RequestInputService> targetId;
  private RequestQueue queue;
  private Id<RequestQueue> queueId;
  private Request high;
  private Request normal;
  private Request low;

  @After
  public void after() {
    facilitiesContext.exit();
  }

  @Before
  public void before() {
    hold = new Memory();
    facilitiesContext = new FacilitiesContext();
    facilitiesContext.enter();
    final ThreadFactory defaultContextThreadFactory = new ContextThreadFactory("default", facilitiesContext);
    final Executor executor = new ScheduledThreadPoolExecutor(1, defaultContextThreadFactory);
    facilitiesContext.setHold(hold);
    facilitiesContext.setDefaultExecutor(executor);
    source = new InputRecorder("source");
    sourceId = facilitiesContext.getHold().add(source);
    target = new RequestInputRecorder("target");
    targetId = facilitiesContext.getHold().add(target);
    queue = new RequestQueue("queue", targetId);
    queueId = facilitiesContext.getHold().add(queue);
    high = Request.createRequest(sourceId, Priority.HIGH, new MessageBuilder(new MessageDefinition("key")).build());
    normal = Request.createRequest(sourceId, Priority.NORMAL, new MessageBuilder(new MessageDefinition("key")).build());
    low = Request.createRequest(sourceId, Priority.LOW, new MessageBuilder(new MessageDefinition("key")).build());
  }

  private void test(final String send, final String expect) throws Exception {
    // given
    // when
    sendRequests(send);
    // then
    assertRequests(expect);
  }

  @Test
  public void testEmpty_HNLhnl_HhNnLl() throws Exception {
    test("HNLhnl", "HhNnLl");
  }

  @Test
  public void testEmpty_HNLlnh_Hh() throws Exception {
    test("HNLlnh", "Hh");
  }

  @Test(expected = ExecutionException.class)
  public void testEmpty_Nnh_Nn() throws Exception {
    // can not end request h without begin
    test("Nnh", "Nn");
  }

  @Test
  public void testEmpty_LNHhnl_LlNnHhNnLl() throws Exception {
    test("LNHhnl", "LlNnHhNnLl");
  }

  @Test
  public void testEmpty_Nn_Nn() throws Exception {
    test("Nn", "Nn");
  }

  private void assertRequests(final String expectedRequestCodes) throws Exception {
    final int numberOfExpectedRequests = expectedRequestCodes.length();
    final List<RecordedRequest> actualRequests = target.getRequests();
    // make sure no unexpected requests arrive
    Thread.sleep(100);
    Assert.assertEquals(numberOfExpectedRequests, actualRequests.size());
    for (int i = 0; i < numberOfExpectedRequests; i++) {
      final char expectedRequestCode = expectedRequestCodes.charAt(i);
      final RequestType expectedRequestType = getRequestType(expectedRequestCode);
      final Request expectedRequest = getRequest(expectedRequestCode);
      final RecordedRequest actualRequest = actualRequests.get(i);
      Assert.assertEquals(expectedRequestType, actualRequest.getType());
      Assert.assertEquals(expectedRequest, actualRequest.getRequest());
    }
  }

  private Request getRequest(final char requestCode) {
    switch (requestCode) {
      case 'L':
        return low;
      case 'N':
        return normal;
      case 'H':
        return high;
      case 'l':
        return low;
      case 'n':
        return normal;
      case 'h':
        return high;
      default:
        throw new IllegalArgumentException("unknown request code:" + requestCode);
    }
  }

  private void sendRequests(final String requestCodes) throws Exception {
    final int numberOfRequests = requestCodes.length();
    for (int i = 0; i < numberOfRequests; i++) {
      final char requestCode = requestCodes.charAt(i);
      Command<RequestQueue> request = null;
      switch (requestCode) {
        case 'L':
          request = c -> c.begin(low);
          break;
        case 'N':
          request = c -> c.begin(normal);
          break;
        case 'H':
          request = c -> c.begin(high);
          break;
        case 'l':
          request = c -> c.end(low);
          break;
        case 'n':
          request = c -> c.end(normal);
          break;
        case 'h':
          request = c -> c.end(high);
          break;
        default:
          throw new IllegalArgumentException("unknown request code:" + requestCode);
      }
      final CompletableFuture<Void> result = facilitiesContext.send(queueId, request);
      // await the result, to see exceptions
      result.get();
    }
  }
}
