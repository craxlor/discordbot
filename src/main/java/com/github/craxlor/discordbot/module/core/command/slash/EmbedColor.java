package com.github.craxlor.discordbot.module.core.command.slash;

import java.awt.Color;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SCAdmin;
import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.reply.Reply;
import com.github.craxlor.discordbot.reply.Status;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

public class EmbedColor extends SCAdmin {

	private static final String RGB_NAME = "rgb";
	private static final String RGB_DESCRIPTION = "Sets the color for all embedded messages from the bot by using rgb code.";
	private static final String RGB_OPT_RED_NAME = "red";
	private static final String RGB_OPT_RED_DESCRIPTION = "Red value.";
	private static final String RGB_OPT_GREEN_NAME = "green";
	private static final String RGB_OPT_GREEN_DESCRIPTION = "Green value.";
	private static final String RGB_OPT_BLUE_NAME = "blue";
	private static final String RGB_OPT_BLUE_DESCRIPTION = "Blue value.";

	private static final String HEX_NAME = "hex";
	private static final String HEX_DESCRIPTION = "Sets the color for all embedded messages from the bot by using hex code.";
	private static final String HEX_OPT_NAME = "hex";
	private static final String HEX_OPT_DESCRIPTION = "Insert a hex code. Example: #c7b299";

	private static final String SET_NAME = "set";
	private static final String SET_DESCRIPTION = "Configure the color for all embedded messages from the bot";
	private static final String REMOVE_NAME = "remove";
	private static final String REMOVE_DESCRIPTION = "Removes the set color for all embedded messages from the bot.";

	public EmbedColor() {
		// rgb
		SubcommandData colorRGB = new SubcommandData(RGB_NAME, RGB_DESCRIPTION);
		colorRGB.addOption(OptionType.INTEGER, RGB_OPT_RED_NAME, RGB_OPT_RED_DESCRIPTION, true);
		colorRGB.addOption(OptionType.INTEGER, RGB_OPT_GREEN_NAME, RGB_OPT_GREEN_DESCRIPTION, true);
		colorRGB.addOption(OptionType.INTEGER, RGB_OPT_BLUE_NAME, RGB_OPT_BLUE_DESCRIPTION, true);
		// hex
		SubcommandData colorHEX = new SubcommandData(HEX_NAME, HEX_DESCRIPTION);
		colorHEX.addOption(OptionType.STRING, HEX_OPT_NAME, HEX_OPT_DESCRIPTION, true);
		// set
		SubcommandGroupData set = new SubcommandGroupData(SET_NAME, SET_DESCRIPTION);
		set.addSubcommands(colorRGB, colorHEX);
		// remove
		SubcommandData remove = new SubcommandData(REMOVE_NAME, REMOVE_DESCRIPTION);

		commandData.addSubcommandGroups(set);
		commandData.addSubcommands(remove);
	}

	@Override
	@Nonnull
	public String getName() {
		return "color";
	}

	@Override
	@Nonnull
	public String getDescription() {
		return "configure color for all embedded messages from the bot";
	}

	@Override
	@SuppressWarnings("null")
	public Reply execute(SlashCommandInteractionEvent event) throws Exception {
		String subcommandName = event.getSubcommandName();
		String subcommandGroup = event.getSubcommandGroup();
		String statusDetail = "";
		if (subcommandGroup != null && subcommandGroup.equals(SET_NAME)) {
			int red = 0, green = 0, blue = 0;
			Color color;
			// RGB
			switch (subcommandName) {
				case RGB_NAME -> {
					red = event.getOption(RGB_OPT_RED_NAME).getAsInt();
					green = event.getOption(RGB_OPT_GREEN_NAME).getAsInt();
					blue = event.getOption(RGB_OPT_BLUE_NAME).getAsInt();
				}
				case HEX_NAME -> {
					String hex = event.getOption(HEX_OPT_NAME).getAsString();
					color = Color.decode(hex);
					red = color.getRed();
					green = color.getGreen();
					blue = color.getBlue();
				}
			}
			GuildManager.getGuildManager(event.getGuild()).getGuildConfig().setEmbedColor(red, green, blue);
			color = new Color(red, green, blue);
			statusDetail = "The color for embedded messages has been set to #."
					+ Integer.toHexString(color.getRGB()).substring(2);
		}
		if (subcommandName.equals(REMOVE_NAME)) {
			GuildManager.getGuildManager(event.getGuild()).getGuildConfig().removeEmbedColor();
			statusDetail = "The color for embedded messages will be random now.";
		}
		return new Reply(event.deferReply(), false).onCommand(event, Status.SUCCESS, statusDetail);
	}

	@Override
	public boolean isGuildOnly() {
		return false;
	}

}
