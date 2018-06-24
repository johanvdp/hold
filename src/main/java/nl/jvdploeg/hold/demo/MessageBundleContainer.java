// The author disclaims copyright to this source code.
package nl.jvdploeg.hold.demo;

import nl.jvdploeg.hold.Id;
import nl.jvdploeg.message.ResourceMessageBundle;

public final class MessageBundleContainer implements Id<MessageBundleContainer> {

  private final String id;
  private final ResourceMessageBundle messageBundle;

  public MessageBundleContainer(final String id, final ResourceMessageBundle messageBundle) {
    this.id = id;
    this.messageBundle = messageBundle;

  }

  @Override
  public String getId() {
    return id;
  }

  public ResourceMessageBundle getMessageBundle() {
    return messageBundle;
  }
}
