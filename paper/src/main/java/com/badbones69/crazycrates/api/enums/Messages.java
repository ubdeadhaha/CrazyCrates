package com.badbones69.crazycrates.api.enums;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.properties.Property;
import com.ryderbelserion.vital.common.util.StringUtil;
import com.ryderbelserion.vital.enums.Support;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.messages.CommandKeys;
import com.badbones69.crazycrates.config.impl.messages.CrateKeys;
import com.badbones69.crazycrates.config.impl.messages.ErrorKeys;
import com.badbones69.crazycrates.config.impl.messages.MiscKeys;
import com.badbones69.crazycrates.config.impl.messages.PlayerKeys;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Messages {

    no_teleporting(MiscKeys.no_teleporting),
    no_commands_while_in_crate(MiscKeys.no_commands_while_using_crate),
    feature_disabled(MiscKeys.feature_disabled),
    unknown_command(MiscKeys.unknown_command),
    correct_usage(MiscKeys.correct_usage),
    no_keys(MiscKeys.no_keys),
    no_virtual_key(MiscKeys.no_virtual_key),
    internal_error(ErrorKeys.internal_error),
    no_schematics_found(ErrorKeys.no_schematics_found),
    no_prizes_found(ErrorKeys.no_prizes_found),
    prize_error(ErrorKeys.prize_error),
    cannot_be_empty(ErrorKeys.cannot_be_empty),
    cannot_be_air(ErrorKeys.cannot_be_air),
    not_online(PlayerKeys.not_online),

    no_permission(PlayerKeys.no_permission),
    inventory_not_empty(PlayerKeys.inventory_not_empty),
    too_close_to_another_player(PlayerKeys.too_close_to_another_player),
    must_be_a_player(PlayerKeys.must_be_a_player),
    must_be_console_sender(PlayerKeys.must_be_console_sender),
    same_player(PlayerKeys.same_player),
    must_be_looking_at_block(PlayerKeys.must_be_looking_at_block),
    obtaining_keys(PlayerKeys.obtaining_keys),

    cannot_set_type(CrateKeys.cannot_set_type),
    not_on_block(CrateKeys.not_on_block),
    already_opening_crate(CrateKeys.already_opening_crate),
    crate_in_use(CrateKeys.crate_in_use),
    world_disabled(CrateKeys.world_disabled),
    no_crate_permission(CrateKeys.no_crate_permission),
    cant_be_a_virtual_crate(CrateKeys.cant_be_a_virtual_crate),
    needs_more_room(CrateKeys.needs_more_room),
    out_of_time(CrateKeys.out_of_time),
    not_a_crate(CrateKeys.not_a_crate),
    not_a_key(CrateKeys.not_a_key),
    not_a_number(CrateKeys.not_a_number),
    preview_disabled(CrateKeys.preview_disabled),
    required_keys(CrateKeys.required_keys),
    created_physical_crate(CrateKeys.created_physical_crate, true),
    removed_physical_crate(CrateKeys.removed_physical_crate),
    crate_locations(CrateKeys.crate_locations, true),
    crate_locations_format(CrateKeys.crate_location_format),
    reloaded_forced_out_of_preview(CrateKeys.reloaded_forced_out_of_preview),

    gave_a_player_keys(CommandKeys.gave_a_player_keys),
    cannot_give_player_keys(CommandKeys.cannot_give_player_keys),
    given_everyone_keys(CommandKeys.given_everyone_keys),
    given_offline_player_keys(CommandKeys.given_offline_player_keys),
    take_player_keys(CommandKeys.take_players_keys),
    reloaded_plugin(CommandKeys.reloaded_plugin),
    cannot_take_keys(CommandKeys.cannot_take_keys),
    take_offline_player_keys(CommandKeys.take_offline_player_keys),
    opened_a_crate(CommandKeys.opened_a_crate),
    no_item_in_hand(CommandKeys.no_item_in_hand),
    added_item_with_editor(CommandKeys.added_item_with_editor),
    transfer_not_enough_keys(CommandKeys.transfer_not_enough_keys),
    transfer_sent_keys(CommandKeys.transfer_sent_keys),
    transfer_received_keys(CommandKeys.transfer_received_keys),
    no_virtual_keys(CommandKeys.no_virtual_keys),
    virtual_keys_header(CommandKeys.virtual_keys_header, true),
    other_player_no_keys(CommandKeys.other_player_no_keys),
    other_player_no_keys_header(CommandKeys.other_player_header, true),
    per_crate(CommandKeys.per_crate),

    help(CommandKeys.help, true),
    admin_help(CommandKeys.admin_help, true);

    private Property<String> property;

    private Property<List<String>> properties;
    private boolean isList = false;

    Messages(@NotNull final Property<String> property) {
        this.property = property;
    }

    Messages(@NotNull final Property<List<String>> properties, final boolean isList) {
        this.properties = properties;
        this.isList = isList;
    }

    private final SettingsManager config = ConfigManager.getConfig();

    private final SettingsManager messages = ConfigManager.getMessages();

    private boolean isList() {
        return this.isList;
    }

    public String getString() {
        return this.messages.getProperty(this.property);
    }

    public List<String> getList() {
        return this.messages.getProperty(this.properties);
    }

    public String getMessage() {
        return getMessage(null, new HashMap<>());
    }

    public String getMessage(@Nullable final CommandSender sender) {
        if (sender instanceof Player player) {
            return getMessage(player, new HashMap<>());
        }

        return getMessage(null, new HashMap<>());
    }

    public String getMessage(@NotNull final Map<String, String> placeholders) {
        return getMessage(null, placeholders);
    }

    public String getMessage(@NotNull final String placeholder, @NotNull final String replacement) {
        return getMessage(null, placeholder, replacement);
    }

    public String getMessage(@Nullable final CommandSender sender, @NotNull final String placeholder, @NotNull final String replacement) {
        Map<String, String> placeholders = new HashMap<>() {{
            put(placeholder, replacement);
        }};

        if (sender instanceof Player player) {
            return getMessage(player, placeholders);
        }

        return getMessage(null, placeholders);
    }

    public String getMessage(@Nullable final CommandSender sender, @NotNull final Map<String, String> placeholders) {
        if (sender instanceof Player player) {
            return getMessage(player, placeholders);
        }

        return getMessage(null, placeholders);
    }

    public String getMessage(@Nullable final Player player, @NotNull final Map<String, String> placeholders) {
        String prefix = this.config.getProperty(ConfigKeys.command_prefix);

        String message = parse(placeholders);

        if (Support.placeholder_api.isEnabled() && player != null) {
            return PlaceholderAPI.setPlaceholders(player, message.replaceAll("\\{prefix}", prefix));
        }

        return message.replaceAll("\\{prefix}", prefix);
    }

    private @NotNull String parse(@NotNull final Map<String, String> placeholders) {
        String message;

        if (isList()) {
            message = StringUtils.chomp(StringUtil.convertList(getList()));
        } else {
            message = getString();
        }

        if (!placeholders.isEmpty()) {
            for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
                message = message.replace(placeholder.getKey(), placeholder.getValue()).replace(placeholder.getKey().toLowerCase(), placeholder.getValue());
            }
        }

        return message;
    }
}