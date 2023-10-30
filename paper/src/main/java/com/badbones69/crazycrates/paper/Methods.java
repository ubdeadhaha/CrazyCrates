package com.badbones69.crazycrates.paper;

import com.badbones69.crazycrates.api.enums.Permissions;
import com.badbones69.crazycrates.paper.api.enums.settings.Messages;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.ItemBuilder;
import com.badbones69.crazycrates.paper.listeners.FireworkDamageListener;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Matcher.quoteReplacement;

@SuppressWarnings("deprecation")
public class Methods {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    public String color(String message) {
        Matcher matcher = Pattern.compile("#[a-fA-F\\d]{6}").matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group()).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public void sendMessage(CommandSender commandSender, String message, boolean prefixToggle) {
        if (message == null || message.isEmpty()) return;

        String prefix = getPrefix();

        if (commandSender instanceof Player player) {
            if (!prefix.isEmpty() && prefixToggle) player.sendMessage(color(message.replaceAll("%prefix%", quoteReplacement(prefix))).replaceAll("%Prefix%", quoteReplacement(prefix))); else player.sendMessage(color(message));

            return;
        }

        if (!prefix.isEmpty() && prefixToggle) commandSender.sendMessage(color(message.replaceAll("%prefix%", quoteReplacement(prefix))).replaceAll("%Prefix%", quoteReplacement(prefix))); else commandSender.sendMessage(color(message));
    }

    public long pickNumber(long min, long max) {
        max++;

        try {
            // new Random() does not have a nextLong(long bound) method.
            return min + ThreadLocalRandom.current().nextLong(max - min);
        } catch (IllegalArgumentException e) {
            return min;
        }
    }

    public void sendCommand(String command) {
        ConsoleCommandSender console = plugin.getServer().getConsoleSender();

        plugin.getServer().dispatchCommand(console, command);
    }

    public String sanitizeColor(String msg) {
        return sanitizeFormat(color(msg));
    }

    public String removeColor(String msg) {
        return ChatColor.stripColor(msg);
    }

    public void firework(Location loc) {
        final Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fm = fw.getFireworkMeta();
        fm.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.RED).withColor(Color.AQUA).withColor(Color.ORANGE).withColor(Color.YELLOW).trail(false).flicker(false).build());
        fm.setPower(0);
        fw.setFireworkMeta(fm);
        FireworkDamageListener.addFirework(fw);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, fw :: detonate, 2);
    }

    public void firework(Location loc, Color color) {
        final Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fm = fw.getFireworkMeta();
        fm.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL).withColor(color).withColor(color).trail(false).flicker(false).build());
        fm.setPower(0);
        fw.setFireworkMeta(fm);
        FireworkDamageListener.addFirework(fw);

        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, fw::detonate, 2);
    }

    public Player getPlayer(String name) {
        return this.plugin.getServer().getPlayerExact(name);
    }

    public void removeItem(ItemStack item, Player player) {
        try {
            if (item.getAmount() <= 1) {
                player.getInventory().removeItem(item);
            } else {
                item.setAmount(item.getAmount() - 1);
            }
        } catch (Exception ignored) {}
    }

    public boolean permCheck(CommandSender sender, Permissions permissions, boolean tabComplete) {
        if (sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender) return true;

        Player player = (Player) sender;

        if (player.hasPermission(permissions.getPermission("command"))) {
            return true;
        } else {
            if (!tabComplete) {
                player.sendMessage(Messages.NO_PERMISSION.getMessage());
                return false;
            }

            return false;
        }
    }

    public String getPrefix() {
        return color(plugin.getCrazyHandler().getConfigManager().getPluginConfig().getProperty(PluginConfig.command_prefix));
    }

    public String getPrefix(String msg) {
        return color(getPrefix() + msg);
    }

    public boolean isInventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    public Integer randomNumber(int min, int max) {
        return min + new Random().nextInt(max - min);
    }

    public boolean isSimilar(ItemStack itemStack, Crate crate) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return itemStack.isSimilar(crate.getKey()) || itemStack.isSimilar(crate.getKeyNoNBT()) ||
                itemStack.isSimilar(crate.getAdminKey()) || stripNBT(itemStack).isSimilar(crate.getKeyNoNBT()) ||
                isSimilarCustom(crate.getKeyNoNBT(), itemStack) || (nbtItem.hasTag("CrazyCrates-Crate") && crate.getName().equals(nbtItem.getString("CrazyCrates-Crate")));
    }

    private boolean isSimilarCustom(ItemStack one, ItemStack two) {
        if (one != null && two != null) {
            if (one.getType() == two.getType()) {
                if (one.hasItemMeta() && two.hasItemMeta()) {
                    if (one.getItemMeta().hasDisplayName() && two.getItemMeta().hasDisplayName()) {
                        if (one.getItemMeta().getDisplayName().equalsIgnoreCase(two.getItemMeta().getDisplayName())) {
                            if (one.getItemMeta().hasLore() && two.getItemMeta().hasLore()) {
                                if (one.getItemMeta().getLore().size() == two.getItemMeta().getLore().size()) {
                                    int i = 0;

                                    for (String lore : one.getItemMeta().getLore()) {
                                        if (!lore.equals(two.getItemMeta().getLore().get(i))) {
                                            return false;
                                        }

                                        i++;
                                    }

                                    return true;
                                }
                            } else return !one.getItemMeta().hasLore() && !two.getItemMeta().hasLore();
                        }
                    } else if (!one.getItemMeta().hasDisplayName() && !two.getItemMeta().hasDisplayName()) {
                        if (one.getItemMeta().hasLore() && two.getItemMeta().hasLore()) {
                            if (one.getItemMeta().getLore().size() == two.getItemMeta().getLore().size()) {
                                int i = 0;

                                for (String lore : one.getItemMeta().getLore()) {
                                    if (!lore.equals(two.getItemMeta().getLore().get(i))) {
                                        return false;
                                    }

                                    i++;
                                }

                                return true;
                            } else {
                                return false;
                            }
                        } else return !one.getItemMeta().hasLore() && !two.getItemMeta().hasLore();
                    }
                } else return !one.hasItemMeta() && !two.hasItemMeta();
            }
        }

        return false;
    }

    private ItemStack stripNBT(ItemStack item) {
        try {
            NBTItem nbtItem = new NBTItem(item.clone());

            if (nbtItem.hasNBTData()) {
                if (nbtItem.hasTag("CrazyCrates-Crate")) {
                    nbtItem.removeKey("CrazyCrates-Crate");
                }
            }

            return nbtItem.getItem();
        } catch (Exception e) {
            return item;
        }
    }

    public Enchantment getEnchantment(String enchantmentName) {
        HashMap<String, String> enchantments = getEnchantmentList();
        enchantmentName = stripEnchantmentName(enchantmentName);

        for (Enchantment enchantment : Enchantment.values()) {
            try {
                if (stripEnchantmentName(enchantment.getKey().getKey()).equalsIgnoreCase(enchantmentName)) {
                    return enchantment;
                }

                if (stripEnchantmentName(enchantment.getName()).equalsIgnoreCase(enchantmentName) || (enchantments.get(enchantment.getName()) != null &&
                stripEnchantmentName(enchantments.get(enchantment.getName())).equalsIgnoreCase(enchantmentName))) {
                    return enchantment;
                }
            } catch (Exception ignore) {}
        }

        return null;
    }

    private String stripEnchantmentName(String enchantmentName) {
        return enchantmentName != null ? enchantmentName.replace("-", "").replace("_", "").replace(" ", "") : null;
    }

    private HashMap<String, String> getEnchantmentList() {
        HashMap<String, String> enchantments = new HashMap<>();
        enchantments.put("ARROW_DAMAGE", "Power");
        enchantments.put("ARROW_FIRE", "Flame");
        enchantments.put("ARROW_INFINITE", "Infinity");
        enchantments.put("ARROW_KNOCKBACK", "Punch");
        enchantments.put("DAMAGE_ALL", "Sharpness");
        enchantments.put("DAMAGE_ARTHROPODS", "Bane_Of_Arthropods");
        enchantments.put("DAMAGE_UNDEAD", "Smite");
        enchantments.put("DEPTH_STRIDER", "Depth_Strider");
        enchantments.put("DIG_SPEED", "Efficiency");
        enchantments.put("DURABILITY", "Unbreaking");
        enchantments.put("FIRE_ASPECT", "Fire_Aspect");
        enchantments.put("KNOCKBACK", "KnockBack");
        enchantments.put("LOOT_BONUS_BLOCKS", "Fortune");
        enchantments.put("LOOT_BONUS_MOBS", "Looting");
        enchantments.put("LUCK", "Luck_Of_The_Sea");
        enchantments.put("LURE", "Lure");
        enchantments.put("OXYGEN", "Respiration");
        enchantments.put("PROTECTION_ENVIRONMENTAL", "Protection");
        enchantments.put("PROTECTION_EXPLOSIONS", "Blast_Protection");
        enchantments.put("PROTECTION_FALL", "Feather_Falling");
        enchantments.put("PROTECTION_FIRE", "Fire_Protection");
        enchantments.put("PROTECTION_PROJECTILE", "Projectile_Protection");
        enchantments.put("SILK_TOUCH", "Silk_Touch");
        enchantments.put("THORNS", "Thorns");
        enchantments.put("WATER_WORKER", "Aqua_Affinity");
        enchantments.put("BINDING_CURSE", "Curse_Of_Binding");
        enchantments.put("MENDING", "Mending");
        enchantments.put("FROST_WALKER", "Frost_Walker");
        enchantments.put("VANISHING_CURSE", "Curse_Of_Vanishing");
        enchantments.put("SWEEPING_EDGE", "Sweeping_Edge");
        enchantments.put("RIPTIDE", "Riptide");
        enchantments.put("CHANNELING", "Channeling");
        enchantments.put("IMPALING", "Impaling");
        enchantments.put("LOYALTY", "Loyalty");
        return enchantments;
    }

    public ItemBuilder getRandomPaneColor() {
        List<String> colors = Arrays.asList(
        Material.WHITE_STAINED_GLASS_PANE.toString(),
        Material.ORANGE_STAINED_GLASS_PANE.toString(),
        Material.MAGENTA_STAINED_GLASS_PANE.toString(),
        Material.LIGHT_BLUE_STAINED_GLASS_PANE.toString(),
        Material.YELLOW_STAINED_GLASS_PANE.toString(),
        Material.LIME_STAINED_GLASS_PANE.toString(),
        Material.PINK_STAINED_GLASS_PANE.toString(),
        Material.GRAY_STAINED_GLASS_PANE.toString(),
        Material.CYAN_STAINED_GLASS_PANE.toString(),
        Material.PURPLE_STAINED_GLASS_PANE.toString(),
        Material.BLUE_STAINED_GLASS_PANE.toString(),
        Material.BROWN_STAINED_GLASS_PANE.toString(),
        Material.GREEN_STAINED_GLASS_PANE.toString(),
        Material.RED_STAINED_GLASS_PANE.toString(),
        Material.BLACK_STAINED_GLASS_PANE.toString(),
        Material.LIGHT_GRAY_STAINED_GLASS_PANE.toString());
        return new ItemBuilder().setMaterial(colors.get(new Random().nextInt(colors.size())));
    }

    /**
     * Decides when the crate should start to slow down.
     */
    public ArrayList<Integer> slowSpin() {
        ArrayList<Integer> slow = new ArrayList<>();
        int full = 46;
        int cut = 9;

        for (int i = 46; cut > 0; full--) {
            if (full <= i - cut || full >= i - cut) {
                slow.add(i);
                i -= cut;
                cut--;
            }
        }

        return slow;
    }

    public void failedToTakeKey(CommandSender player, Crate crate) {
        this.plugin.getServer().getLogger().warning("An error has occurred while trying to take a physical key from a player");
        this.plugin.getServer().getLogger().warning("Player: " + player.getName());
        this.plugin.getServer().getLogger().warning("Crate: " + crate.getName());

        player.sendMessage(getPrefix("&cAn issue has occurred when trying to take a key."));
        player.sendMessage(getPrefix("&cCommon reasons includes not having enough keys."));
    }

    public String sanitizeFormat(String string) {
        return TextComponent.toLegacyText(TextComponent.fromLegacyText(string));
    }

    // Thanks ElectronicBoy
    public HashMap<Integer, ItemStack> removeItemAnySlot(Inventory inventory, ItemStack... items) {
        if (items != null) {
            HashMap<Integer, ItemStack> leftover = new HashMap<>();

            // TODO: optimization

            for (int i = 0; i < items.length; i++) {
                ItemStack item = items[i];
                int toDelete = item.getAmount();

                while (true) {
                    // Paper start - Allow searching entire contents
                    ItemStack[] toSearch = inventory.getContents();
                    int first = firstFromInventory(item, false, toSearch);
                    // Paper end

                    // Drat! we don't have this type in the inventory
                    if (first == -1) {
                        item.setAmount(toDelete);
                        leftover.put(i, item);
                        break;
                    } else {
                        ItemStack itemStack = inventory.getItem(first);
                        int amount = itemStack.getAmount();

                        if (amount <= toDelete) {
                            toDelete -= amount;
                            // clear the slot, all used up
                            inventory.clear(first);
                        } else {
                            // split the stack and store
                            itemStack.setAmount(amount - toDelete);
                            inventory.setItem(first, itemStack);
                            toDelete = 0;
                        }
                    }

                    // Bail when done
                    if (toDelete <= 0) {
                        break;
                    }
                }
            }

            return leftover;
        } else {
            this.plugin.getLogger().info("Items cannot be null.");
        }

        return null;
    }

    private int firstFromInventory(ItemStack item, boolean withAmount, ItemStack[] inventory) {
        if (item == null) {
            return -1;
        }

        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) continue;

            if (withAmount ? item.equals(inventory[i]) : item.isSimilar(inventory[i])) {
                return i;
            }
        }

        return -1;
    }
}