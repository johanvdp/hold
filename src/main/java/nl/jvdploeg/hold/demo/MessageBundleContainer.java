// The author disclaims copyright to this source code.
package nl.jvdploeg.hold.demo;

import nl.jvdploeg.hold.Id;
import nl.jvdploeg.message.MessageBundle;

public final class MessageBundleContainer implements Id<MessageBundleContainer> {

  private final String id;
  private final MessageBundle messageBundle;

  public MessageBundleContainer(final String id, final MessageBundle messageBundle) {
    this.id = id;
    this.messageBundle = messageBundle;

  }

  @Override
  public String getId() {
    return id;
  }

  public MessageBundle getMessageBundle() {
    return messageBundle;
  }
}
