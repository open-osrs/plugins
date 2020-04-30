package net.runelite.client.plugins.itemcombiner;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyListener;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static net.runelite.api.ObjectID.DWARF_MULTICANNON;

public class ItemCombinerHotkeyListener extends MouseAdapter implements KeyListener {
    private Client client;

    private ItemCombinerPlugin plugin;

    private ItemCombinerConfig config;

    private Instant lastPress;

    @Inject
    private ItemManager itemManager;

    private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1);
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 25, TimeUnit.SECONDS, queue,
            new ThreadPoolExecutor.DiscardPolicy());

    @Inject
    private ItemCombinerHotkeyListener(final Client client, final ItemCombinerConfig config, final ItemCombinerPlugin plugin) {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
    }

    @Override
    public void keyTyped(final KeyEvent e) {
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        if (client.getGameState() != GameState.LOGGED_IN)
            return;

        if (executor == null)
            return;

        if (lastPress != null && Duration.between(lastPress, Instant.now()).getNano() > 1000) {
            lastPress = null;
        }

        if (lastPress != null)
            return;

        if (e.getExtendedKeyCode() == config.useItemsKeybind().getKeyCode()) {
            executor.submit(() -> {
                for (int i = config.iterations(); i > 0; i--) {
                    useItems();
                }
            });
        }
    }

    @Override
    public void keyReleased(final KeyEvent e) {
    }

    private void useItems() {
        try {
            final int itemId = config.itemId();
            final int itemId2 = config.itemId2();
            final Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

            final WidgetItem firstItem = inventory.getWidgetItems().stream().filter(inventoryItem -> inventoryItem.getId() == itemId).findFirst().orElse(null);

            if (firstItem == null) {
                return;
            }

            final WidgetItem secondItem = inventory.getWidgetItems().stream().filter(inventoryItem -> inventoryItem.getId() == itemId2).findFirst().orElse(null);

            if (secondItem == null) {
                return;
            }

            plugin.entry = new MenuEntry("Use", "<col=ff9040>" + itemManager.getItemDefinition(firstItem.getId()).getName(), firstItem.getId(), MenuOpcode.ITEM_USE.getId(), firstItem.getIndex(), 9764864, false);
            InputHandler.click(client);
            Thread.sleep(config.clickDelay());

            plugin.entry = new MenuEntry("Use", "<col=ff9040>" + itemManager.getItemDefinition(firstItem.getId()).getName() + "<col=ffffff> -> <col=ff9040>" + itemManager.getItemDefinition(secondItem.getId()).getName(), secondItem.getId(), MenuOpcode.ITEM_USE_ON_WIDGET_ITEM.getId(), secondItem.getIndex(), 9764864, false);
            InputHandler.click(client);
            Thread.sleep(config.clickDelay());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
