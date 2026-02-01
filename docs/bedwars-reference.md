# Bed Wars reference checklist

This checklist helps compare our implementation with the public Bed Wars rules
and tuning used by Hypixel (reference: https://hypixel.fandom.com/wiki/Bed_Wars),
and calls out the current plugin defaults so you can verify they align.

## Gameplay flow

- Confirm bed destruction rules and final-kill behavior match our expectations.
- Check respawn delay, spawn protection, and spectator join policies.
- Verify win conditions and any sudden-death/endgame phases.

### Current plugin defaults

- Respawn delay: `respawnDelaySeconds: 5` (`config/config.yml`).
- Spawn protection: `spawnProtectionSeconds: 3` (`config/config.yml`).
- Spectator join: `enableSpectatorJoin: false` (`config/config.yml`).

## Match formats

- Compare supported modes (Solo/Doubles/Threes/Fours).
- Confirm team sizes, island counts, and map scaling expectations.

### Current plugin defaults

- Supported modes are defined per-map in `map.yml` (see `maps/example_map/map.yml`).
- Minimum players: `minPlayers: 2` (`config/config.yml`).

## Economy & generators

- Compare forge tiers and drop intervals (iron/gold/diamond/emerald).
- Validate currency drop behavior on death.
- Ensure generator upgrade pacing aligns with intended match length.

### Current plugin defaults

- Forge tiers: 4 levels, `dropIntervalTicks` 40/30/20/15 with iron/gold drops
  (`config/config.yml`).
- Drop on death: IRON/GOLD true, DIAMOND/EMERALD false (`config/config.yml`).

## Shops & upgrades

- Compare item availability, tiering, and pricing defaults.
- Validate team upgrades and trap ordering rules.
- Check permanent vs. per-life upgrades.

### Current plugin defaults

- Shop items and prices are defined in `config/shop.yml`.
- Team upgrades and costs are defined in `config/upgrades.yml`.

## UI & messaging

- Review scoreboard, actionbar, and chat messaging expectations.
- Ensure victory/defeat and bed destruction messaging are clear.

### Current plugin defaults

- Chat and match messaging templates are defined in `config/messages.yml`.
