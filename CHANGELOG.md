# Changelog

All notable changes in Changelog will be documented in this file.

The format is based on [Keep a Changelog].

## [UNRELEASED]

### Added

- Added localization for common config key `pigeon.allow_self_delivery`. ([#8])

### Changed

- Right-clicking the front side of a Bird Cage containing a Pigeon now releases the Pigeon.

### Deprecated

### Removed

### Fixed

- Fixed an issue where Pigeon Chat loads in an environment where GCL cannot load. ([#16])
- Fixed an issue where common configuration is still editable from the client side edit screen
  when connected to a dedicated server. ([#17])
- Delivery assignment that began when the Bird Cage is unblocked now correctly cancels the
  assignment if the cage is blocked after target selection. ([#14])
- Capturing Pigeon with a Bird Cage in creative mode now actually captures the Pigeon. ([#18])

### Security

## [0.2.1+26.2] - 2026-08-02

### Fixed

- Capturing Pigeon while holding multiple Bird Cage in a stack no longer captures to all held
  Bird Cage. ([#12])

## [0.2.0+26.2] - 2026-07-30

### Added

- Pigeons now have animations. ([#4])
- Bird Cage.
  Right-click a Pigeon to capture one.
  Sneak-right-click a placed down Bird Cage to retrieve it.
  Like with Pigeons in the wild, right-click a caged Pigeon to make deliveries.
  Breaking Bird Cage with a tool (as opposed to retrieving it), the captured entity will be
  released. ([#7])

### Changed

- Version numbering now includes the Minecraft version as build metadata (e.g. `+26.2` for
  Minecraft 26.2).
- Name Tags are not nameable by default through Anvil.
  This behavior is configurable.
- Pigeons will now fly to their delivery target if within pathfinding range.
  If their target falls outside of pathfinding range, Pigeons will fly towards their target's
  general direction before teleporting to within pathfinding range of the target.
  At that point, they will continue to fly to their target. ([#2])
- Self delivery is now configurable (`pigeon.allow_self_delivery`).
  This defaults to `false` outside of the development environment.

### Fixed

- Fixed an issue where config driven item durabilities (e.g. Pen, Quill, and Ink Bottle) shows the
  wrong durability when connected to a server.

## [0.1.0] - 2026-07-08

### Added

- Quill, a writing utensil created by dipping Feather-like items in ink.
- Pen, a refillable writing utensil.
- Ink Bottle, a refillable ink container.
- Letter, a communication media converted from Paper-like items.
- Pigeon, a passive entity that can deliver items for you.
  Pigeons spawns in groups of two to six.
- Raw Pigeon, dropped by Pigeons when killed.
- Cooked Pigeon.
- Item tags:
  - `pigeonchat:quill_materials` for items that can be converted to Quill when dipped in ink.
  - `pigeonchat:nib_materials` for metal nuggets that can be used as the nib when crafting a Pen.
  - `pigeonchat:writables` for items you can write on.
  - `pigeonchat:writables/letter` for items you can write on as a letter.
    These items will be converted to the Letter item.
  - `pigeonchat:writables/name_tag` for Name Tag items.
  - `pigeonchat:deliverables` for items that can be delivered by a Messenger Animal.
  - `pigeonchat:deliverables/pigeon` for items that can be delivered by a Pigeon.

### Changed

- Name Tags can now be named by right-clicking while holding a writing utensil with the other hand.
- Right-clicking a Name Tag without holding a writing utensil with the other hand displays its name.

[#12]: https://github.com/GiienaAstrella/pigeon-chat/issues/12
[#14]: https://github.com/GiienaAstrella/pigeon-chat/issues/14
[#16]: https://github.com/GiienaAstrella/pigeon-chat/issues/16
[#17]: https://github.com/GiienaAstrella/pigeon-chat/issues/17
[#18]: https://github.com/GiienaAstrella/pigeon-chat/issues/18
[#2]: https://github.com/GiienaAstrella/pigeon-chat/issues/2
[#4]: https://github.com/GiienaAstrella/pigeon-chat/issues/4
[#7]: https://github.com/GiienaAstrella/pigeon-chat/issues/7
[#8]: https://github.com/GiienaAstrella/pigeon-chat/issues/8
[0.1.0]: https://github.com/GiienaAstrella/pigeon-chat/compare/0f0e4a9c15de3a94691e7d807bed1eacdb5a48bb...v0.1.0
[0.2.0+26.2]: https://github.com/GiienaAstrella/pigeon-chat/releases/tag/v0.2.0+26.2
[0.2.1+26.2]: https://github.com/GiienaAstrella/pigeon-chat/releases/tag/v0.2.1+26.2
[Keep a Changelog]: https://keepachangelog.com/en/1.1.0/
