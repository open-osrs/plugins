/*
 * Copyright (c) 2018, Andrew EP | ElPinche256 <https://github.com/ElPinche256>
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
package com.openosrs.classic377;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("JavaExampleConfig")

public interface RSClassic377Config extends Config
{
	@ConfigItem(
		keyName = "confirm",
		name = "Confirm agreement",
		description = "Accept the agreement of username/password privacy",
		position = 0
	)
	default boolean confirm()
	{
		return false;
	}

	@ConfigItem(
		keyName = "codebase",
		name = "Codebase",
		description = "URL that the embedded client will connect to",
		position = 1
	)
	default String codebase()
	{
		return "rsclassic377.ddns.net";
	}

	// Must be declared before plugin will start, auto entered for you during startup.
	@ConfigItem(
		keyName = "username",
		name = "Username (12 chars max)",
		description = "Name you will use in Classic",
		position = 2
	)
	default String username()
	{
		return "";
	}

	// Randomly generated 18 char password, auto entered for you during startup.
	@ConfigItem(
		keyName = "password",
		name = "Password",
		description = "Hidden and sensitive, backup your config and do not share if you value your Classic account.",
		hidden = true
	)
	default String password()
	{
		return Util.generatePassword();
	}
}