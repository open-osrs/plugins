/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.chathistory;

import com.google.common.base.Strings;
import com.google.common.collect.EvictingQueue;
import com.google.inject.Provides;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.ScriptID;
import net.runelite.api.VarClientInt;
import net.runelite.api.VarClientStr;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.util.Text;
import net.runelite.api.vars.InputType;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import static net.runelite.api.widgets.WidgetInfo.TO_CHILD;
import static net.runelite.api.widgets.WidgetInfo.TO_GROUP;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Chat History",
	description = "Retain your chat history when logging in/out or world hopping",
	tags = {"chat", "history", "retain", "cycle", "pm"},
	type = PluginType.MISCELLANEOUS
)
public class ChatHistoryPlugin extends Plugin implements KeyListener
{
	private static final String WELCOME_MESSAGE = "Welcome to Old School RuneScape";
	private static final String CLEAR_HISTORY = "Clear history";
	private static final String CLEAR_PRIVATE = "<col=ffff00>Private:";
	private static final String COPY_TO_CLIPBOARD = "Copy to clipboard";
	private static final int CYCLE_HOTKEY = KeyEvent.VK_TAB;
	private static final int FRIENDS_MAX_SIZE = 5;

	private Queue<QueuedMessage> messageQueue;
	private Deque<String> friends;

	private String currentMessage = null;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ChatHistoryConfig config;

	@Inject
	private KeyManager keyManager;

	@Inject
	private ChatMessageManager chatMessageManager;

	private boolean retainChatHistory;
	private boolean pmTargetCycling;

	/**
	 * Small hack to prevent plugins checking for specific messages to match. This works because the "—" character
	 * cannot be seen in-game. This replacement preserves wrapping on chat history messages.
	 *
	 * @param message message
	 * @return message with invisible character before every space
	 */
	private static String tweakSpaces(final String message)
	{
		if (message != null)
		{
			// First replacement cleans up prior applications of this so as not to keep extending the message
			return message.replace("— ", " ").replace(" ", "— ");
		}

		return null;
	}

	@Provides
	ChatHistoryConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ChatHistoryConfig.class);
	}

	@Override
	protected void startUp()
	{
		updateConfig();

		messageQueue = EvictingQueue.create(100);
		friends = new ArrayDeque<>(FRIENDS_MAX_SIZE + 1);
		keyManager.registerKeyListener(this);
	}

	@Override
	protected void shutDown()
	{
		messageQueue.clear();
		messageQueue = null;
		friends.clear();
		friends = null;
		currentMessage = null;
		keyManager.unregisterKeyListener(this);
	}

	@Subscribe
	private void onChatMessage(ChatMessage chatMessage)
	{
		// Start sending old messages right after the welcome message, as that is most reliable source
		// of information that chat history was reset
		ChatMessageType chatMessageType = chatMessage.getType();
		if (chatMessageType == ChatMessageType.WELCOME && StringUtils.startsWithIgnoreCase(chatMessage.getMessage(), WELCOME_MESSAGE))
		{
			if (!this.retainChatHistory)
			{
				return;
			}

			QueuedMessage queuedMessage;

			while ((queuedMessage = messageQueue.poll()) != null)
			{
				chatMessageManager.queue(queuedMessage);
			}

			return;
		}

		switch (chatMessageType)
		{
			case PRIVATECHATOUT:
			case PRIVATECHAT:
			case MODPRIVATECHAT:
				final String name = Text.removeTags(chatMessage.getName());
				// Remove to ensure uniqueness & its place in history
				if (!friends.remove(name) &&
					// If the friend didn't previously exist ensure deque capacity doesn't increase by adding them
					friends.size() >= FRIENDS_MAX_SIZE)
				{
					friends.remove();
				}
				friends.add(name);
				// intentional fall-through
			case PUBLICCHAT:
			case MODCHAT:
			case FRIENDSCHAT:
			case CONSOLE:
				final QueuedMessage queuedMessage = QueuedMessage.builder()
					.type(chatMessageType)
					.name(chatMessage.getName())
					.sender(chatMessage.getSender())
					.value(tweakSpaces(chatMessage.getMessage()))
					.runeLiteFormattedMessage(tweakSpaces(chatMessage.getMessageNode().getRuneLiteFormatMessage()))
					.timestamp(chatMessage.getTimestamp())
					.build();

				if (!messageQueue.contains(queuedMessage))
				{
					messageQueue.offer(queuedMessage);
				}
		}
	}

	@Subscribe
	public void onMenuOpened(MenuOpened event)
	{
		if (event.getMenuEntries().length < 2 || !config.copyToClipboard())
		{
			return;
		}

		// Use second entry as first one can be walk here with transparent chatbox
		final MenuEntry entry = event.getMenuEntries()[event.getMenuEntries().length - 2];

		if (entry.getOpcode() != MenuOpcode.CC_OP_LOW_PRIORITY.getId() && entry.getOpcode() != MenuOpcode.RUNELITE.getId())
		{
			return;
		}

		final int groupId = TO_GROUP(entry.getParam1());
		final int childId = TO_CHILD(entry.getParam1());

		if (groupId != WidgetInfo.CHATBOX.getGroupId())
		{
			return;
		}

		final Widget widget = client.getWidget(groupId, childId);
		final Widget parent = widget.getParent();

		if (WidgetInfo.CHATBOX_MESSAGE_LINES.getId() != parent.getId())
		{
			return;
		}

		// Get child id of first chat message static child so we can substract this offset to link to dynamic child
		// later
		final int first = WidgetInfo.CHATBOX_FIRST_MESSAGE.getChildId();

		// Convert current message static widget id to dynamic widget id of message node with message contents
		// When message is right clicked, we are actually right clicking static widget that contains only sender.
		// The actual message contents are stored in dynamic widgets that follow same order as static widgets.
		// Every first dynamic widget is message sender and every second one is message contents.
		final int dynamicChildId = (childId - first) * 2 + 1;

		// Extract and store message contents when menu is opened because dynamic children can change while right click
		// menu is open and dynamicChildId will be outdated
		final Widget messageContents = parent.getChild(dynamicChildId);
		if (messageContents == null)
		{
			return;
		}

		currentMessage = messageContents.getText();

		final MenuEntry menuEntry = new MenuEntry();
		menuEntry.setOption(COPY_TO_CLIPBOARD);
		menuEntry.setTarget(entry.getTarget());
		menuEntry.setOpcode(MenuOpcode.RUNELITE.getId());
		menuEntry.setParam0(entry.getParam0());
		menuEntry.setParam1(entry.getParam1());
		menuEntry.setIdentifier(entry.getIdentifier());
		client.setMenuEntries(ArrayUtils.insert(1, client.getMenuEntries(), menuEntry));
	}

	@Subscribe
	private void onMenuOptionClicked(MenuOptionClicked event)
	{
		String menuOption = event.getOption();

		if (menuOption.contains(CLEAR_HISTORY))
		{
			if (menuOption.startsWith(CLEAR_PRIVATE))
			{
				messageQueue.removeIf(e -> e.getType() == ChatMessageType.PRIVATECHAT ||
					e.getType() == ChatMessageType.PRIVATECHATOUT || e.getType() == ChatMessageType.MODPRIVATECHAT);
				friends.clear();
			}
			else
			{
				messageQueue.removeIf(e -> e.getType() == ChatMessageType.PUBLICCHAT || e.getType() == ChatMessageType.MODCHAT);
			}
		}
		else if (COPY_TO_CLIPBOARD.equals(menuOption) && !Strings.isNullOrEmpty(currentMessage))
		{
			final StringSelection stringSelection = new StringSelection(Text.removeTags(currentMessage));
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() != CYCLE_HOTKEY || !this.pmTargetCycling)
		{
			return;
		}

		if (client.getVar(VarClientInt.INPUT_TYPE) != InputType.PRIVATE_MESSAGE.getType())
		{
			return;
		}

		clientThread.invoke(() ->
		{
			final String target = findPreviousFriend();
			if (target == null)
			{
				return;
			}

			final String currentMessage = client.getVar(VarClientStr.INPUT_TEXT);

			client.runScript(ScriptID.OPEN_PRIVATE_MESSAGE_INTERFACE, target);

			client.setVar(VarClientStr.INPUT_TEXT, currentMessage);
			client.runScript(ScriptID.CHAT_TEXT_INPUT_REBUILD, "");
		});
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
	}

	private String findPreviousFriend()
	{
		final String currentTarget = client.getVar(VarClientStr.PRIVATE_MESSAGE_TARGET);
		if (currentTarget == null || friends.isEmpty())
		{
			return null;
		}

		for (Iterator<String> it = friends.descendingIterator(); it.hasNext(); )
		{
			String friend = it.next();
			if (friend.equals(currentTarget))
			{
				return it.hasNext() ? it.next() : friends.getLast();
			}
		}

		return friends.getLast();
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (!"chathistory".equals(event.getGroup()))
		{
			return;
		}

		updateConfig();
	}

	private void updateConfig()
	{
		this.retainChatHistory = config.retainChatHistory();
		this.pmTargetCycling = config.pmTargetCycling();
	}
}
