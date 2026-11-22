# The UnderVoid

*A new dimension, hidden beneath our world since the beginning of time.*

The UnderVoid is a NeoForge mod for Minecraft 1.21.1, made by the USSM team for a mod jam. Below the void lies the **Undervoid**: a dark, foggy world where your **sanity** slowly drains away. Survive it, find places to recover, and brew potions to keep your mind together.

## Requirements

| | Version | |
|---|---|---|
| Minecraft | 1.21.1 | required |
| NeoForge | 21.1.262 or newer 21.1.x | required |
| [GeckoLib](https://modrinth.com/mod/geckolib) | 4.8.4 or newer | required |
| [Simple Voice Chat](https://modrinth.com/plugin/simple-voice-chat) | 2.5.0 or newer | optional, see [Voice recording](#voice-recording-simple-voice-chat) |

The mod must be installed on both the client and the server.

## Getting into the Undervoid

Fall into the void of any other dimension and survive for 5 seconds. The fall deals damage, so eat **3 golden apples within one minute** before jumping: void damage is then divided by 4, which is enough to make it.

To leave, find a **Bloom Rift** in the Bloom caves, or climb above the top of the Undervoid. Going back to the Overworld resets your sanity to 100.

## What's inside

**Sanity.** A sanity bar goes down in the Gloom and the Miasma, and recovers in the Bloom, next to a hedgehog, or outside the dimension. The lower it gets, the stranger things become: hallucinations, then damage at zero. Too much sanity in the Undervoid is dangerous too.

**Biomes.**
- *Gloom*: a corrupted surface under an oppressive fog, with corrupted grass, dark deepslate and dead shrubs.
- *Bloom*: caves full of flowers, hedgehogs and rifts back to the Overworld.
- *Miasma*: purple fog zones that hurt and drain sanity very fast.

**Potions.** Ten new flowers are the ingredients of the sanity potions: *Restoration* and *Distress* (instant, +/- sanity), *Erosion* (faster sanity loss), *Serenity* and *Clarity* (slower sanity loss). Most come in a normal and a strong version. They are all in the *Sanity potions* creative tab.

**Gear.**
- *Hazmat suit + gas mask*: halves sanity changes and protects from the miasma. It cannot be combined with sanity potions.
- *Katana*: dash through enemies. One katana is quick, two (one in each hand) hit much harder. The off-hand is locked while holding a katana, except for a second katana or a totem.
- *Spawner Receptacle*: keep it in your off-hand and mine a spawner with a pickaxe to get it back as an item, with its mob inside.
- *Bedrock Breaker*: powered with redstone, it fires a beam through everything above and below it, bedrock included.

**Creatures.**
- *Hedgehog*: pet it to recover sanity.
- *Mimics*: in the Gloom, they copy players they have seen. They approach as a friend, then reveal themselves. The lower your sanity, the more often you meet them.
- *Herobrine*: a figure that watches from far away and vanishes when you approach. Rare. He haunts players who have been to the Undervoid, in both dimensions.
- *Enera*: a smoke demon with a katana, a dash and short teleports. She drains the sanity of everyone nearby and gets stronger when your sanity is low. She drops a Spawner Receptacle and a Blade Fragment.

## Voice recording (Simple Voice Chat)

If Simple Voice Chat is installed, Mimics can **record players' voices and replay them**. The clips are stored in the world save.

- This is **on by default** (`mimicRecordVoice` in the server config). Server owners should tell their players, or turn it off.
- Turning it off stops new recordings. Check the clips already stored in the world if you want them gone.
- Without Simple Voice Chat, nothing is recorded and the mod works normally.

## Server owners

All gameplay values (sanity gain and loss, dash damage and cooldown, Mimic frequency, miasma damage, void survival time, and more) are in the mod's server config file and can be changed without touching the code. The client config only has the hallucination intensity.

The `/herobrine` commands (operators only) are meant for testing. Leave `/herobrine debug` off on a live server: it affects every player.

## Building

```
./gradlew build          # builds the mod jar
./gradlew runClient      # starts a test client
./gradlew runServer      # starts a test server
./gradlew runData        # regenerates models, loot tables and tags
```

Requires Java 21. Mod ID: `the_undervoid`.

## Credits and licence

Made by the USSM team. Released under the MIT licence.

Parts of the code were produced with the help of an AI assistant (Claude). All assets were made by the USSM.

Built on [NeoForge](https://neoforged.net/) and [GeckoLib](https://github.com/bernie-g/geckolib). Mimics are inspired by the *mimic* mod by tgkowski.