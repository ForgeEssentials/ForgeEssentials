package com.forgeessentialsclient.config;

import com.forgeessentialsclient.config.ValuesCached.ValueCachedBoolean;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class ClientConfig extends BaseConfig {
	private final ForgeConfigSpec configSpec;

	public final ValueCachedBoolean allowCUI;
	public final ValueCachedBoolean allowQRCodeRender;
	public final ValueCachedBoolean allowPermissionRender;
	public final ValueCachedBoolean allowQuestionerShortcuts;
	public final ValueCachedBoolean allowAuthAutoLogin;
	public final ValueCachedBoolean versioncheck;
	
	public ClientConfig() {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.comment("Configure ForgeEssentials Client addon features..")
				.push("General");
		allowCUI = ValueCachedBoolean.wrap(this, builder
				.comment("Set to false to disable graphical selections.")
				.define("allowCUI", true));
		allowQRCodeRender = ValueCachedBoolean.wrap(this, builder
				.comment("Set to false to disable QR code rendering when you enter /remote qr.")
				.define("allowQRCodeRender", true));
		allowPermissionRender = ValueCachedBoolean.wrap(this, builder
				.comment("Set to false to disable visual indication of block/item permissions.")
				.define("allowPermRender", true));
		allowQuestionerShortcuts = ValueCachedBoolean.wrap(this, builder
				.comment("Use shortcut buttons to answer questions. Defaults are F8 for yes and F9 for no, change in game options menu.")
				.define("allowQuestionerShortcuts", true));
		allowAuthAutoLogin = ValueCachedBoolean.wrap(this, builder
				.comment("Save tokens to automatically log in to servers using FE's Authentication Module.")
				.define("allowAuthAutoLogin", true));
		versioncheck = ValueCachedBoolean.wrap(this, builder
				.comment("Check for newer versions of ForgeEssentials on load?")
				.define("versionCheck", true));
		builder.pop();
		configSpec = builder.build();
	}
	@Override
	public String getFileName() {
		return "ForgeEssentialsClientConfig";
	}
	@Override
	public ForgeConfigSpec getConfigSpec() {
		return configSpec;
	}
	@Override
	public ModConfig.Type getConfigType() {
		return ModConfig.Type.CLIENT;
	}
}
