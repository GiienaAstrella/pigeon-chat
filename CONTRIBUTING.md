# Contribution Guidelines

Pigeon Chat is an open source project.
We welcome all contributions.

These guidelines are subject to change

## Reporting Bugs

Please use the [Bug Report][brf] form.
Using this form assigns the appropriate labels to your ticket.
Answering the questions correctly and concisely allows us to fix the bug faster.

## Requesting new features

Please use the [Feature Request][frf] form.
Using this form assigns the appropriate labels to your ticket.
Answering the questions clearly and concisely allows us to spend time deliberating on your request,
rather than asking obvious questions.

## Requesting support for Minecraft version

Please use the [Feature Request][frf] form and notate that you are requesting for a Minecraft
version to be supported.

## Contributing Code

Please start your contribution as either a [bug report][brf] or [feature request][frf].
This allows us to discuss implementation details before any work is done.
We don't want you to waste your time on something we won't accept.
We will close **all** PRs without an accompanying issue ticket.

When contributing code, please ensure you are authorized to do so.
In other words, don't submit code you don't own the copyright to.

Also, please make sure you agree to the terms of the [MIT License][license].
By submitting your contribution, you are agreeing to the terms of the MIT License.

Next, your contribution must also be documented in the `UNRELEASED` section of
[CHANGELOG.md][changelog].
We will not merge your PR until it contains documentation entry to the changelog.
However, please only document things the player *should* care about.
Examples:
- You changed the recipe for Pen (document this).
- You refactored the way `MessengerAnimal` gathers valid delivery targets **without** changing
  the already-documented behavior (don't document this).

Lastly, please submit your contribution against the `main` branch.
We will `cherry-pick` your contribution (and adjust to version-specific semantics) to all supported
port branches as necessary.
Exceptions:
- You are submitting a bug fix that is only applicable in specific Minecraft versions.
  Please target the appropriate port branch for that version.
- You are contributing a port a Minecraft version we do not yet support.
  Please target the closest port branch to that version.
  If the closest branch is `main`, it is fine to target that branch.
  During the review process, a maintainer will create the appropriate port branch, and you will be
  asked to rebase your PR to that branch.

[license]: LICENSE
[changelog]: CHANGELOG.md
[brf]: https://github.com/GiienaAstrella/pigeon-chat/issues/new?template=1-bug.yaml
[frf]: https://github.com/GiienaAstrella/pigeon-chat/issues/new?template=2-feature.yaml