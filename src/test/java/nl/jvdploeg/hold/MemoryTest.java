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
    hold.add(cargoA);
    hold.add(cargoB);
    // then
    Assert.assertEquals(cargoA, hold.getContainer(cargoA.getId()));
    Assert.assertEquals(cargoB, hold.getContainer(cargoB.getId()));
  }

  @Test
  public void testHoldEmpty_LoadOne_GetSame() {
    // given
    final Memory hold = new Memory();
    // when
    final TestContainer cargo = new TestContainer("a");
    hold.add(cargo);
    // then
    Assert.assertEquals(cargo, hold.getContainer("a"));
  }

  @Test
  public void testHoldWithOne_RemoveOne_Removed() {
    // given
    final Memory hold = new Memory();
    final TestContainer cargoA = new TestContainer("a");
    hold.add(cargoA);
    // when
    final TestContainer removed = hold.remove("a");
    // then
    Assert.assertEquals(cargoA, removed);
    try {
      hold.getContainer("a");
      Assert.fail("failed to remove");
    } catch (final Exception e) {
    }
  }
}
