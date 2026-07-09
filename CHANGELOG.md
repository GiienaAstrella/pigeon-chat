# Changelog

All notable changes in Changelog will be documented in this file.

The format is based on [Keep a Changelog].

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

### Security

 [Keep a Changelog]: https://keepachangelog.com/en/1.1.0/
[UNRELEASED]: https://github.com/GiienaAstrella/pigeon-chat/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/GiienaAstrella/pigeon-chat/compare/0f0e4a9c15de3a94691e7d807bed1eacdb5a48bb...v0.1.0