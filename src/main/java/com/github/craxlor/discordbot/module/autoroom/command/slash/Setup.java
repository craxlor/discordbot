package com.github.craxlor.discordbot.module.autoroom.command.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SCAdmin;
import com.github.craxlor.discordbot.manager.GuildManager;
import com.github.craxlor.discordbot.manager.json.GuildConfig;
import com.github.craxlor.discordbot.reply.Reply;
import com.github.craxlor.discordbot.reply.Status;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class Setup extends SCAdmin {

	private static final String CREATE_NAME = "create";
	private static final String CREATE_DESCRIPTION = "define an autoroom trigger, which can be used to create Autorooms with specifc settings";
	private static final String EDIT_NAME = "edit";
	private static final String EDIT_DESCRIPTION = "edit a previously defined autoroom trigger";
	private static final String OPT_NAME_NAME = "name";
	private static final String OPT_NAME_DESCRIPTION = "set the naming pattern for autorooms created with this trigger\n Variables: username, number";
	private static final String OPT_TRIGGER_NAME = "trigger";
	private static final String OPT_TRIGGER_DESCRIPTION = "select the VoiceChannel that will act as the trigger";
	private static final String OPT_CATEGORY_NAME = "category";
	private static final String OPT_CATEGORY_DESCRIPTION = "select a category in which the created autorooms should remain";
	private static final String OPT_PARENT_NAME = "parent";
	private static final String OPT_PARENT_DESCRIPTION = "select a parent object which determines the permissions of the autorooms";
	public static final String CHOICE_TRIGGER = "trigger";
	public static final String CHOICE_CATEGORY = "category";
	private static final String REMOVE_NAME = "remove";
	private static final String REMOVE_DESCRIPTION = "remove the autoroom trigger configuration for a specific channel";
	private static final String REMOVE_OPT_CHANNEL_NAME = "channel";
	private static final String REMOVE_OPT_CHANNEL_DESCRIPTION = "select the autoroom trigger to be removed";
	private static final String REMOVE_OPT_DELETE_NAME = "delete";
	private static final String REMOVE_OPT_DELETE_DESCRIPTION = "should the channel be deleted or remain?";

	public Setup() {
		// CREATE
		SubcommandData create = new SubcommandData(CREATE_NAME, CREATE_DESCRIPTION);
		OptionData createOptionName = new OptionData(OptionType.STRING, OPT_NAME_NAME,
				OPT_NAME_DESCRIPTION, true);
		OptionData createOptionTrigger = new OptionData(OptionType.CHANNEL, OPT_TRIGGER_NAME,
				OPT_TRIGGER_DESCRIPTION, true);
		OptionData createOptionCategory = new OptionData(OptionType.CHANNEL, OPT_CATEGORY_NAME,
				OPT_CATEGORY_DESCRIPTION, true);
		OptionData createOptionParent = new OptionData(OptionType.STRING, OPT_PARENT_NAME,
				OPT_PARENT_DESCRIPTION, true);
		createOptionParent.addChoice(CHOICE_TRIGGER, CHOICE_TRIGGER);
		createOptionParent.addChoice(CHOICE_CATEGORY, CHOICE_CATEGORY);
		create.addOptions(createOptionName, createOptionTrigger, createOptionCategory, createOptionParent);
		// EDIT
		SubcommandData edit = new SubcommandData(EDIT_NAME, EDIT_DESCRIPTION);
		OptionData editOptionChannel = new OptionData(OptionType.CHANNEL, OPT_TRIGGER_NAME, OPT_TRIGGER_DESCRIPTION,
				true);
		OptionData editOptionName = new OptionData(OptionType.STRING, OPT_NAME_NAME, OPT_NAME_DESCRIPTION);
		OptionData editOptionCategory = new OptionData(OptionType.CHANNEL, OPT_CATEGORY_NAME, OPT_CATEGORY_DESCRIPTION);
		OptionData editOptionParent = new OptionData(OptionType.STRING, OPT_PARENT_NAME, OPT_PARENT_DESCRIPTION);
		editOptionParent.addChoice(CHOICE_TRIGGER, CHOICE_TRIGGER);
		editOptionParent.addChoice(CHOICE_CATEGORY, CHOICE_CATEGORY);
		edit.addOptions(editOptionChannel, editOptionName, editOptionCategory, editOptionParent);
		// REMOVE
		SubcommandData remove = new SubcommandData(REMOVE_NAME, REMOVE_DESCRIPTION);
		OptionData removeOptionTrigger = new OptionData(OptionType.CHANNEL, REMOVE_OPT_CHANNEL_NAME,
				REMOVE_OPT_CHANNEL_DESCRIPTION,
				true);
		OptionData removeOptionDelete = new OptionData(OptionType.BOOLEAN, REMOVE_OPT_DELETE_NAME,
				REMOVE_OPT_DELETE_DESCRIPTION);
		remove.addOptions(removeOptionTrigger, removeOptionDelete);

		commandData.addSubcommands(create, edit, remove);
	}

	@Override
	@Nonnull
	public String getName() {
		return "autoroom";
	}

	@Override
	@Nonnull
	public String getDescription() {
		return "autoroom";
	}

	@Override
	@SuppressWarnings("null")
	public Reply execute(SlashCommandInteractionEvent event) throws Exception {
		String subcommandName = event.getSubcommandName();
		String statusDetail = "";
		Guild guild = event.getGuild();
		GuildManager guildManager = GuildManager.getGuildManager(guild);
		GuildConfig config = guildManager.getGuildConfig();
		Reply reply = new Reply(event.deferReply(), false);
		switch (subcommandName) {
			case CREATE_NAME -> {
				try { // try catch to validate trigger & category options
					String name = event.getOption(OPT_NAME_NAME).getAsString();
					VoiceChannel trigger = event.getOption(OPT_TRIGGER_NAME)
							.getAsChannel().asVoiceChannel();
					// prevent double bindings
					if (config.isAutoroomTrigger(trigger.getIdLong()))
						return new Reply(event.deferReply(), false).onCommand(event, Status.FAIL,
								"the selceted channel is already an autoroom trigger");
					Category category = event.getOption(OPT_CATEGORY_NAME)
							.getAsChannel().asCategory();
					String parent = event.getOption(OPT_PARENT_NAME).getAsString();
					config.addAutoroomTrigger(name, trigger.getIdLong(), category.getIdLong(), parent);
					statusDetail = """
							set %s as an autoroomTrigger.
							The autorooms will be named: %s""".formatted(trigger.getAsMention(), name);
					return reply.onCommand(event, Status.SUCCESS, statusDetail);
				} catch (IllegalStateException e) {
					return reply.onCommand(event, Status.FAIL,
							"either the specified VoiceChannel is not a VoiceChannel or the specified category is not a category");
				}
			}
			case EDIT_NAME -> {
				VoiceChannel trigger = event.getOption(OPT_TRIGGER_NAME).getAsChannel().asVoiceChannel();
				// check if the provided channel is a trigger
				if (config.isAutoroomTrigger(trigger.getIdLong()) == false)
					return reply.onCommand(event, Status.FAIL,
							"the selceted channel is not an autoroom trigger");
				OptionMapping option = event.getOption(OPT_NAME_NAME);
				String name = null, parent = null;
				long categoryID = -1;
				// check the optional parameters
				// name
				if (option != null) {
					name = option.getAsString();
					statusDetail = """
							changed the **naming pattern** for autorooms created by %s to: %s"""
							.formatted(trigger.getAsMention(), name);
				}
				// category
				if ((option = event.getOption(OPT_CATEGORY_NAME)) != null) {
					try {
						Category category = option.getAsChannel().asCategory();
						categoryID = category.getIdLong();
						statusDetail += """

								changed the **category** where autorooms created by %s will be placed to &s"""
								.formatted(trigger.getAsMention(), category.getAsMention());
					} catch (IllegalStateException e) {
						return reply.onCommand(event, Status.FAIL, "the selected channel has to be a **category**");
					}
				}
				// parent
				if ((option = event.getOption(OPT_PARENT_NAME)) != null) {
					parent = option.getAsString();
					statusDetail += """

							changed the parent object that determines the permissions of the autorooms to the %s
								""".formatted(parent);
				}
				// apply changes
				config.editAutoroomtrigger(trigger.getIdLong(), name, categoryID, parent);
				return reply.onCommand(event, Status.SUCCESS, statusDetail);
			}
			case REMOVE_NAME -> {
				VoiceChannel trigger = event.getOption(REMOVE_OPT_CHANNEL_NAME).getAsChannel().asVoiceChannel();
				if (config.removeAutoroomTrigger(trigger.getIdLong())) {
					statusDetail = "removed " + trigger.getAsMention();
					OptionMapping optionMapping = event.getOption(REMOVE_OPT_DELETE_NAME);
					if (optionMapping != null && optionMapping.getAsBoolean())
						trigger.delete().queue();
					return reply.onCommand(event, Status.SUCCESS, statusDetail);
				} else
					return reply.onCommand(event, Status.FAIL,
							"couldn't identify " + trigger.getAsMention() + " as an autoroomTrigger");
			}
		}
		return reply.onCommand(event, Status.ERROR, "fatal error\ncontact the dev");
	}

	@Override
	public boolean isGuildOnly() {
		return true;
	}

}
