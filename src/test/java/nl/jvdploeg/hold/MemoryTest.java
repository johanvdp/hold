package nl.jvdploeg.hold;

import org.junit.Assert;
import org.junit.Test;

public class MemoryTest {

  @Test
  public void testHold_LoadTwo_UnloadTwo() {
    // given
    final Memory hold = new Memory();
    final TestContainer cargoA = new TestContainer("a");
    final TestContainer cargoB = new TestContainer("b");
    // when
    final Id<TestContainer> referenceA = hold.add(cargoA);
    final Id<TestContainer> referenceB = hold.add(cargoB);
    // then
    Assert.assertEquals(cargoA, hold.getContainer(referenceA));
    Assert.assertEquals(cargoB, hold.getContainer(referenceB));
  }

  @Test
  public void testHoldEmpty_LoadOne_GetSame() {
    // given
    final Memory hold = new Memory();
    // when
    final TestContainer cargo = new TestContainer("a");
    final Id<TestContainer> referenceA = hold.add(cargo);
    // then
    Assert.assertEquals(cargo, hold.getContainer(referenceA));
  }

  @Test
  public void testHoldWithOne_RemoveOne_Removed() {
    // given
    final Memory hold = new Memory();
    final TestContainer cargoA = new TestContainer("a");
    final Id<TestContainer> referenceA = hold.add(cargoA);
    // when
    final TestContainer removed = hold.remove(referenceA);
    // then
    Assert.assertEquals(cargoA, removed);
    try {
      hold.getContainer(referenceA);
      Assert.fail("failed to remove");
    } catch (final Exception e) {
    }
  }
}
