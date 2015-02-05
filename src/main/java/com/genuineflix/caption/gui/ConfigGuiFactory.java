/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */
package com.genuineflix.caption.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;

import com.genuineflix.caption.ClosedCaption;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.IConfigElement;

/**
 * Created by lukas on 29.06.14.
 */
public class ConfigGuiFactory implements IModGuiFactory {

	@Override
	public void initialize(final Minecraft minecraftInstance) {}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return ConfigGui.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(final RuntimeOptionCategoryElement element) {
		return null;
	}

	public static class ConfigGui extends GuiConfig {

		public ConfigGui(final GuiScreen parentScreen) {
			super(parentScreen, getConfigElements(), ClosedCaption.MODID, false, false, I18n.format("closedcaption.config.title"));
		}

		private static List<IConfigElement> getConfigElements() {
			final List<IConfigElement> list = new ArrayList<IConfigElement>();
			list.add(new DummyCategoryElement("closedcaption.config.general", "closedcaption.config.sub.general", GeneralEntry.class).setRequiresMcRestart(true));
			list.add(new DummyCategoryElement("closedcaption.config.graphic", "closedcaption.config.sub.graphic", GraphicEntry.class));
			return list;
		}

		public static class GeneralEntry extends GuiConfigEntries.CategoryEntry {

			public GeneralEntry(final GuiConfig owningScreen, final GuiConfigEntries owningEntryList, final IConfigElement prop) {
				super(owningScreen, owningEntryList, prop);
			}

			@Override
			protected GuiScreen buildChildScreen() {
				final ConfigElement element = new ConfigElement(ClosedCaption.config().getCategory("general"));
				return new GuiConfig(owningScreen, element.getChildElements(), owningScreen.modID, "general", configElement.requiresWorldRestart() || owningScreen.allRequireWorldRestart, configElement.requiresMcRestart() || owningScreen.allRequireMcRestart, GuiConfig.getAbridgedConfigPath(ClosedCaption.config().toString()));
			}
		}

		public static class GraphicEntry extends GuiConfigEntries.CategoryEntry {

			public GraphicEntry(final GuiConfig owningScreen, final GuiConfigEntries owningEntryList, final IConfigElement prop) {
				super(owningScreen, owningEntryList, prop);
			}

			@Override
			protected GuiScreen buildChildScreen() {
				final ConfigElement element = new ConfigElement(ClosedCaption.config().getCategory("graphic"));
				return new GuiConfig(owningScreen, element.getChildElements(), owningScreen.modID, "graphic", configElement.requiresWorldRestart() || owningScreen.allRequireWorldRestart, configElement.requiresMcRestart() || owningScreen.allRequireMcRestart, GuiConfig.getAbridgedConfigPath(ClosedCaption.config().toString()));
			}
		}
	}
}