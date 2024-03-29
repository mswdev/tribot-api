package scripts.tribotapi.game.inventory;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.*;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by Sphiinx on 1/10/2016.
 * Re-written by Sphiinx on 7/8/2016.
 */
public class Inventory07 {

    /**
     * Checks if the inventory has changed from the given inventory cache.
     *
     * @param inventory_cache The inventory cache to check.
     * @return True if the inventory has changed; false otherwise.
     */
    public static boolean hasInventoryChanged(RSItem[] inventory_cache) {
        if (inventory_cache == null)
            return false;

        final RSItem[] current_inventory_cache = Inventory.getAll();
        if (current_inventory_cache == null)
            return false;

        if (inventory_cache.length != current_inventory_cache.length) {
            return true;
        } else {
            for (RSItem cache_item : inventory_cache) {
                for (RSItem current_cache_item : current_inventory_cache)
                    if (cache_item.getStack() != current_cache_item.getStack())
                        return true;
            }
        }

        return false;
    }

    /**
     * Gets the number of free space in the RSPlayers inventory.
     *
     * @return How many free spaces is in the RSPlayers inventory.
     */
    public static int getAmountOfSpace() {
        return 28 - Inventory.getAll().length;
    }

    /**
     * Finds the specified RSItems in the RSPlayers inventory.
     *
     * @param id The IDs of the RSItems.
     * @return The RSItem; Null if no RSItems are found.
     */
    public static RSItem getItem(int... id) {
        final RSItem[] items = Inventory.find(Filters.Items.idEquals(id));
        return items.length > 0 ? items[0] : null;
    }

    /**
     * Finds the specified RSItems in the RSPlayers inventory.
     *
     * @param name The names of the RSItems.
     * @return The RSItem; Null if no RSItems are found.
     */
    public static RSItem getItem(String... name) {
        if (name == null)
            return null;

        final RSItem[] items = Inventory.find(Filters.Items.nameEquals(name));
        return items.length > 0 ? items[0] : null;
    }

    /**
     * Finds the specified RSItems in the RSPlayers inventory.
     *
     * @param filter The filter.
     * @return The RSItem; Null if no RSItems were found.
     */
    public static RSItem getItem(Filter<RSItem> filter) {
        if (filter == null)
            return null;

        final RSItem[] items = Inventory.find(filter);
        return items.length > 0 ? items[0] : null;
    }

    /**
     * Emulates mouse keys and drops all the inventory items except the specified ids.
     *
     * @param ignore The ids of the items that should not be dropped.
     * @return True if all items were dropped except for those specified, false otherwise.
     */
    public static boolean mouseKeysDropAllExcept(int... ignore) {
        return mouseKeysDropAllExcept(2, ignore);
    }

    /**
     * Emulates mouse keys and drops all the inventory items except the specified ids.
     *
     * @param sleep_mod A multiplier for the delay in between dropping items. The lower the number, the shorter the delay.
     * @param ignore    The ids of the items that should not be dropped.
     * @return True if all items were dropped except for those specified, false otherwise.
     */
    public static boolean mouseKeysDropAllExcept(int sleep_mod, int... ignore) {
        if (!GameTab.TABS.INVENTORY.isOpen())
            GameTab.TABS.INVENTORY.open();

        final RSItem[] items = convertTo28(Inventory.find(Filters.Items.idNotEquals(ignore)));
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 7; j++) {
                if (deselectedItem()) {
                    if (j > 0) {
                        --j;
                        continue;
                    } else {
                        --i;
                        j = 6;
                        continue;
                    }
                }
                if (items[4 * j + i] == null) continue;
                Rectangle r = new RSItem(4 * j + i, 0, 0, RSItem.TYPE.INVENTORY).getArea();
                mouseKeysDropItem(r, sleep_mod);
                waitForInventoryFullMessage();
            }
        }
        return Timing.waitCondition(itemsDropped(ignore), General.random(800, 1200));
    }

    private static RSItem[] convertTo28(RSItem[] items) {
        RSItem[] out = new RSItem[28];
        for (RSItem item : items) {
            out[item.getIndex()] = item;
        }
        return out;
    }

    private static Condition itemsDropped(int... ignore) {
        return new Condition() {
            @Override
            public boolean active() {
                General.sleep(100);
                return Inventory.find(Filters.Items.idNotEquals(ignore)).length == 0;
            }
        };
    }

    private static Condition itemNotSelected = new Condition() {
        @Override
        public boolean active() {
            General.sleep(100);
            return Game.getItemSelectionState() != 1;
        }
    };

    private static boolean deselectedItem() {
        if (Game.getItemSelectionState() == 1) {
            Mouse.click(3);
            if (Timing.waitMenuOpen(General.random(450, 550))) {
                if (ChooseOption.isOptionValid("Cancel")) {
                    if (ChooseOption.select("Cancel")) {
                        return Timing.waitCondition(itemNotSelected, General.random(700, 1000));
                    }
                }
            }
        }
        return false;
    }

    private static void mouseKeysDropItem(Rectangle r, int sleepMod) {
        if (!r.contains(Mouse.getPos())) {
            General.sleep(50, 100);
            Mouse.move(new Point((int) r.getCenterX() + General.random(-3, 3), (int) r.getCenterY() + General.random(-3, 3)));
        }
        if (r.contains(Mouse.getPos())) {
            Mouse.click(3);
            if (sleepMod != 0) General.sleep(General.random(20, 25) * sleepMod);
            int y = getOptionMenuY();
            if (y == -1) {
                Mouse.click(1);
                General.sleep(50, 100);
            } else {
                Keyboard.sendPress(KeyEvent.CHAR_UNDEFINED, Keyboard.getKeyCode((char) KeyEvent.VK_CONTROL));
                General.sleep(30, 60);
                Mouse.hop(new Point((int) Mouse.getPos().getX(), y));
                General.sleep(30, 60);
                Keyboard.sendRelease(KeyEvent.CHAR_UNDEFINED, Keyboard.getKeyCode((char) KeyEvent.VK_CONTROL));
                Mouse.click(1);
                General.sleep(50, 100);
            }
        }
    }

    private static int getOptionMenuY() {
        String[] actions = ChooseOption.getOptions();
        for (int i = 0; i < actions.length; i++) {
            if (actions[i].toLowerCase().contains("drop")) {
                Point p = ChooseOption.getPosition();
                if (p != null) return (int) (p.getY() + 21 + 16 * i);
            }
        }
        return -1;
    }

    private static void waitForInventoryFullMessage() {
        if (!General.isLookingGlass()) {
            String message = NPCChat.getMessage();
            if (message != null && (message.contains("You don't have") || message.contains("You can't carry"))) {
                for (int i = 0; i < 10 && NPCChat.getMessage() != null; i++)
                    General.sleep(90, 110);
            }
        }
    }

}