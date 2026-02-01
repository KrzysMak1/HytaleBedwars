# HytaleBedWars

Production-grade BedWars foundation for Hytale.

## Quickstart

1. Place maps in `maps/example_map` (see `maps/example_map/map.yml`).
2. Start the server.
3. Run `/bw admin start example_map SOLO`.

## Build

```bash
./gradlew test spotlessCheck
```

## Commands

- `/bw admin start <mapId> <mode>`
- `/bw admin stop <matchId>`
- `/bw admin forceteam <player> <teamColor>`
- `/bw admin reload`
- `/bw admin dumpstate <matchId>`

## Permissions

- `bedwars.admin`
- `bedwars.play`

## Config Formats

### config.yml

```yaml
configVersion: 2
minPlayers: 2
startingCountdownSeconds: 10
respawnDelaySeconds: 5
spawnProtectionSeconds: 3
assistWindowSeconds: 5
enableSpectatorJoin: false
actionbarEnabled: true
maxItemsOnGround: 64
mergeRadius: 2
cleanupItemsIntervalSeconds: 30
blockPlacePerSecondLimit: 8
breakableMapBlocks:
  - { x: 0, y: 64, z: 0 }
bedProtectionEnabled: true
forgeLevels:
  1:
    dropIntervalTicks: 40
    drops:
      - IRON
  2:
    dropIntervalTicks: 30
    drops:
      - IRON
      - GOLD
currencyDropOnDeath:
  IRON: true
  GOLD: true
  DIAMOND: false
  EMERALD: false
```

### map.yml

```yaml
id: "example"
name: "Example"
author: "BedWars Team"
supportedModes: ["SOLO", "DOUBLES", "FOURS"]
teams:
  - color: RED
    name: "Red"
    spawn: { x: 0, y: 64, z: 0, yaw: 0, pitch: 0 }
    bed: { x: 2, y: 64, z: 0 }
    bedRegion:
      min: { x: 1, y: 64, z: -1 }
      max: { x: 3, y: 65, z: 1 }
    baseRegion:
      min: { x: -5, y: 60, z: -5 }
      max: { x: 5, y: 70, z: 5 }
    shopNpc: { x: 1, y: 64, z: 2 }
    upgradeNpc: { x: -1, y: 64, z: 2 }
generators:
  - type: IRON
    location: { x: 0, y: 64, z: 0 }
    teamColor: RED
  - type: DIAMOND
    location: { x: 25, y: 64, z: 25 }
    teamColor: null
voidY: 20
buildRegion:
  min: { x: -100, y: 0, z: -100 }
  max: { x: 100, y: 256, z: 100 }
```

### shop.yml

```yaml
categories:
  BLOCKS:
    - id: wool
      name: "Wool"
      currency: IRON
      amount: 4
      quantity: 16
      tier: 1
  WEAPONS:
    - id: sword_stone
      name: "Stone Sword"
      currency: IRON
      amount: 10
      tier: 1
```

### upgrades.yml

```yaml
upgrades:
  SHARPENED_SWORDS:
    maxLevel: 1
    cost:
      currency: DIAMOND
      amount: 4
  REINFORCED_ARMOR:
    maxLevel: 4
    costs:
      1: { currency: DIAMOND, amount: 2 }
      2: { currency: DIAMOND, amount: 4 }
      3: { currency: DIAMOND, amount: 8 }
      4: { currency: DIAMOND, amount: 16 }
```

### messages.yml

```yaml
prefix: "&7[&bBedWars&7] "
match:
  join: "{prefix}&a{player} joined {mode}"
  leave: "{prefix}&c{player} left"
  startCountdown: "{prefix}&eMatch starting in {time}s"
  cancelled: "{prefix}&cMatch cancelled"
  winner: "{prefix}&6Team {team} wins on {map}!"
  bedDestroyedGlobal: "{prefix}&c{player} destroyed {team} bed!"
  bedDestroyedTeam: "{prefix}&cYour bed was destroyed!"
  finalKill: "{prefix}&cFinal kill! {player}"
respawn:
  pending: "{prefix}&eRespawning in {time}s"
  blocked: "{prefix}&cYou can no longer respawn"
shop:
  purchaseSuccess: "{prefix}&aPurchased {item}"
  purchaseFail: "{prefix}&cNot enough {currency}"
admin:
  start: "{prefix}&aStarted match {map} {mode}"
  stop: "{prefix}&cStopped match {match}"
```
