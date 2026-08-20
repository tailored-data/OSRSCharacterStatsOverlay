# Character Stats Overlay

A [RuneLite](https://runelite.net) plugin that shows the numbers from the in-game
**Equip Your Character** screen without opening it — as a sidebar panel, as a
movable on-screen overlay, or both.

## What it shows

Both views mirror the game's own groupings. Every value can be switched on or
off individually, and whole groups can be hidden at once. In the sidebar each
value gets its own icon.

| Group | Values |
| --- | --- |
| Attack bonus | Stab, Slash, Crush, Magic, Range |
| Defence bonus | Stab, Slash, Crush, Magic, Range |
| Other bonuses | Melee STR, Ranged STR, Magic DMG, Prayer |
| Target-specific | Undead, Slayer |
| Weapon speed | Base, Actual |

## The two views

**Sidebar panel** — click the shield icon on the right-hand toolbar. The panel
updates live as your gear and attack style change; you do not have to have it
open for the plugin to keep tracking.

**Overlay** — a movable panel drawn over the game. Move and resize it the normal
RuneLite way: hold `Alt` (or use overlay drag mode) and drag it, or drag its
edge to resize.

Both are fed from the same snapshot, so they can never disagree. Either can be
turned off on its own under **Display**.

## Configuration

**Display**

- **Show sidebar panel** — whether the plugin's toolbar icon appears.
- **Show overlay** — whether the on-screen overlay is drawn.
- **Show icons in sidebar** — the per-value icons.
- **Show group headers** — the headings above each group, in both views.
- **Weapon speed units** — seconds, matching the game, or game ticks.

**Overlay appearance**

- **Transparency** — how opaque the overlay is drawn, from 0% (invisible) to
  100%. This fades the background *and* the text together.
- **Background colour** — with its own alpha channel, which stacks with the
  transparency slider.
- **Show overlay title**, **Overlay header colour**.

**Attack bonus / Defence bonus / Other bonuses / Target-specific / Weapon speed**

Each group has its own section containing a master toggle for the group plus one
toggle per value. These apply to both views.

## How the values are worked out

Most values are summed from the stats of your worn items and update the moment
your equipment changes — no interface needs to be open. The weapon speed's
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
./gradlew build   # compile and run the tests
```

To log in with a Jagex account from the development client, follow
[Using Jagex Accounts](https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts).

## Licence

[BSD 2-Clause](LICENSE).
