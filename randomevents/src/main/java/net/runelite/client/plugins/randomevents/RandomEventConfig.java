/*
 *  Copyright (c) 2018, trimbe <github.com/trimbe>
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.runelite.client.plugins.randomevents;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("randomevents")
public interface RandomEventConfig extends Config
{
	@ConfigItem(
		keyName = "removeMenuOptions",
		name = "Remove others' menu options",
		description = "Remove menu options from random events for other players.",
		position = -3
	)
	default boolean removeMenuOptions()
	{
		return true;
	}

	@ConfigItem(
		keyName = "notifyAll",
		name = "Notify for all events",
		description = "",
		position = -2
	)
	default boolean notifyAllEvents()
	{
		return false;
	}

	@ConfigItem(
		keyName = "notifyDunce",
		name = "Notify on Surprise Exam",
		description = ""
	)
	default boolean notifyDunce()
	{
		return false;
	}

	@ConfigItem(
		keyName = "notifyGenie",
		name = "Notify on Genie",
		description = ""
	)
	default boolean notifyGenie()
	{
		return false;
	}

	@ConfigItem(
		keyName = "notifyDemon",
		name = "Notify on Drill Demon",
		description = ""
	)
	default boolean notifyDemon()
	{
		return false;
	}

	@ConfigItem(
		keyName = "notifyForester",
		name = "Notify on Freaky Forester",
		description = ""
	)
	default boolean notifyForester()
	{
		return false;
	}

	@ConfigItem(
		keyName = "notifyFrog",
		name = "Notify on Kiss the Frog",
		description = ""
	)
	default boolean notifyFrog()
	{
		return false;
	}

	@ConfigItem(
		keyName = "notifyGravedigger",
		name = "Notify on Gravedigger",
		description = ""
	)
	default boolean notifyGravedigger()
	{
		return false;
	}

	@ConfigItem(
		keyName = "notifyMoM",
		name = "Notify on Mysterious Old Man",
		description = ""
	)
	default boolean notifyMoM()
	{
		return false;
	}

	@ConfigItem(
		keyName = "notifyBob",
		name = "Notify on Evil Bob",
		description = ""
	)
	default boolean notifyBob()
	{
		return false;
	}

	@ConfigItem(
		keyName = "notifyQuiz",
		name = "Notify on Quiz Master",
		description = ""
	)
	default boolean notifyQuiz()
	{
		return false;
	}
	
	@ConfigItem(
		keyName = "notifyJekyll",
		name = "Notify on Jekyll & Hyde",
		description = ""
	)
	default boolean notifyJekyll()
	{
		return false;
	}

	@ConfigItem(
		keyName = "notifyBeekeeper",
		name = "Notify on Beekeeper",
		description = ""
	)
	default boolean notifyBeekeeper()
	{
		return false;
	}

	@ConfigItem(
		keyName = "notifySandwich",
		name = "Notify on Sandwich Lady",
		description = ""
	)
	default boolean notifySandwich()
	{
		return false;
	}
}
