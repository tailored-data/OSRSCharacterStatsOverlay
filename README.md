# Character Stats Overlay

A [RuneLite](https://runelite.net) plugin that shows the numbers from the in-game
**Equip Your Character** screen as a movable, transparency-adjustable overlay, so
you can see your equipment bonuses without opening the equipment stats interface.

## What it shows

The overlay mirrors the game's own groupings. Every value can be switched on or
off individually, and whole groups can be hidden at once.

| Group | Values |
| --- | --- |
| Attack bonus | Stab, Slash, Crush, Magic, Range |
| Defence bonus | Stab, Slash, Crush, Magic, Range |
| Other bonuses | Melee STR, Ranged STR, Magic DMG, Prayer |
| Target-specific | Undead, Slayer |
| Weapon speed | Base, Actual |

## Configuration

**Appearance**

- **Transparency** — how opaque the whole overlay is drawn, from 0% (invisible)
  to 100% (fully opaque). This fades the background *and* the text together.
- **Background colour** — the panel background, with its own alpha channel. Its
  alpha stacks with the transparency slider.
- **Show plugin title** / **Show group headers** / **Header colour** — control the
  headings.
- **Weapon speed units** — seconds, matching the game, or game ticks.

**Attack bonus / Defence bonus / Other bonuses / Target-specific / Weapon speed**

Each group has its own section containing a master toggle for the group plus one
toggle per value.

The overlay itself is moved and resized the normal RuneLite way: hold `Alt` (or
use the overlay drag mode) and drag it, or drag its edge to resize.

## How the values are worked out

Most values are summed from the stats of your worn items and update the moment
your equipment changes — no need to open any interface. The weapon speed's
*Actual* row also accounts for your current attack style, which is one tick
faster than the base speed on **Rapid**.

**Target-specific is different.** The undead and slayer multipliers are derived
by the game from rules the RuneLite client API does not expose, so the plugin
reads them straight off the game's own equipment stats screen while it is open.
This means:

- They show `?` until you open **Worn Equipment → View stats** at least once.
- They are dropped back to `?` as soon as your equipment changes, rather than
  showing you a stale number. Reopen the stats screen to refresh them.
- Set **Hide until known** if you would rather the rows disappear than show `?`.

This is a deliberate trade-off: a value that is occasionally missing beats a
value that is quietly wrong.

## Building and running

Requires **JDK 11** (see the
[plugin hub setup guide](https://github.com/runelite/plugin-hub#setting-up-the-development-environment)).
The Gradle wrapper pins **Gradle 8.10**, the same version the plugin hub uses to
build submissions.

```bash
./gradlew run     # launch a development RuneLite client with the plugin loaded
./gradlew build   # compile and run checks
```

To log in with a Jagex account from the development client, follow
[Using Jagex Accounts](https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts).

## Licence

[BSD 2-Clause](LICENSE).
