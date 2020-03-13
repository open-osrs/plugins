package net.runelite.client.plugins.pktools.ScriptCommand;

public class ScriptCommandFactory
{
	public static ScriptCommand builder(final String scriptCommand)
	{
		switch (scriptCommand.toLowerCase())
		{
			case "rigour":
				return new RigourCommand();
			case "augury":
				return new AuguryCommand();
			case "piety":
				return new PietyCommand();
			case "incrediblereflexes":
				return new IncredibleReflexesCommand();
			case "ultimatestrength":
				return new UltimateStrengthCommand();
			case "steelskin":
				return new SteelSkinCommand();
			case "eagleeye":
				return new EagleEyeCommand();
			case "mysticmight":
				return new MysticMightCommand();
			case "clickenemy":
				return new ClickEnemyCommand();
			case "freeze":
				return new FreezeCommand();
			case "vengeance":
				return new VengeanceCommand();
			case "teleblock":
				return new TeleBlockCommand();
			case "entangle":
				return new EntangleCommand();
			case "spec":
				return new SpecCommand();
			case "doublespec":
				return new DoubleSpecCommand();
			case "wait":
				return new WaitCommand();
			case "group1":
				return new Group1Command();
			case "group2":
				return new Group2Command();
			case "group3":
				return new Group3Command();
			case "group4":
				return new Group4Command();
			case "protectitem":
				return new ProtectItemCommand();
			default:
				return new ExceptionCommand();
		}
	}
}
