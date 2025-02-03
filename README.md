# Firefly

A collection of helpful features for Paper servers.

[![CodeFactor](https://www.codefactor.io/repository/github/fireml-dev/firefly/badge)](https://www.codefactor.io/repository/github/fireml-dev/firefly)

---

## Requirements

- Paper 1.21.4
- Java 21
- [DaisyLib](https://github.com/FireML-Dev/DaisyLib)
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) (Optional)
- [MiniPlaceholders](https://modrinth.com/plugin/miniplaceholders) (Optional)

---

## Features

* ### Modules
Every part of this plugin can be enabled/disabled via configs, and most support this without a server restart.

* ### Custom Commands
Register as many custom commands as you'd like. 
These can be configured to send messages and execute other commands, and can be added/removed without requiring a server restart.

* ### Elevators
Elevators can be placed in a vertical stack, and players can jump/sneak to navigate between floors. 
A crafting recipe can be provided to allow players to craft these blocks, or they can be awarded with `/elevator giveBlock [player]`.

* ### Block Protection
Protect blocks from being accidentally broken, like a budding amethyst or a loot chest.

* ### Kits
Award kits to players. Kits can be configured to provide all their contents or a single reward. 
Optionally, you can require players to have specific permissions to open a kit, preventing unwanted trading of kits.

This module uses DaisyLib's Reward system, meaning third party plugins can register their own rewards if they choose to.
To see all usable reward types, you can use `/daisylib rewardTypes`.

* ### Essentials-style Commands
Firefly offers modern alternatives to various Essentials commands. 
These commands exist in the "commands" module, and every configuration they need is in one place.

These configurations include:
- Enabled/Disabled Status
- Permission
- All Messages
- Command Name
- Command Aliases
- Any command-specific configuration, like the entity blacklist for the ride command.

---

## Download

Release Builds are currently not available.

Snapshot/Dev Builds are available on [Jenkins](https://ci.firedev.uk/job/Firefly/).
