FELocalizations
===============

Localizations for the ForgeEssentials project.
These are extremely subject to change. You have been warned.

To submit new localizations, make a pull request. You may be given commit access to this repo if you have enough pulls to make.

## Using Localization
If you ever find yourself writing a message in code that will be seen by a player, you will need to localize it. When creating new localization keys, please follow lower camel case for key names. For example, `noHomeSet` is easier to read than `nohomeset`. Function names follow the same convention.

If you need to insert data into a string, don't assume that all languages place these extra bits into the same spot as English does - use the string format specifiers, detailed in the Formatting Guide on the bottom of this page.

## Creating new Languages
If you are **fluent** (could hold a conversation) in a language that ForgeEssentials does not yet have, you are encouraged to create a locale file containing translations with all the current strings and submit a Pull Request to us. We will try and get it rolled into ForgeEssentials as quickly as possible.

While writing your new locale file, please make sure you read our Formatting Guide on this page, and make sure you understand what the crazy symbols in the strings are doing. If you do not understand the guide, come join us in #forgeessentials on irc.esper.net and someone should be able to explain it. If you do not follow the guide correctly, we will not accept your PR.

## Editing existing translations
If you discover an error in one of our localization strings, submit a new issue and make sure that you tag it with "Localization". Make sure your bug report follows this format:

`Localization file <language>.xml key "<key in XML file>" is incorrect. Should be <correct string>`

For example:

`Localization file en_US.xml key "message.error.permdenied" should have the entry: "You do not have permission to do that!"`

If the locale file is _very_ wrong and contains multiple errors, the better approach would be to submit a PR to us containing all the corrections.

## Formatting Guide

Since it is necessary to include numbers and/or strings in messages, and not all languages place them in the same place within a message, we use Java's String.format function to splice numbers and strings (like block or player names) into base localized strings. For a full, gory explanation of how they work, read the [Java Formatter docpage](http://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html#syntax). The basics are provided here in case you need a quick fix.

A _format specifier_ is denoted by a `%` in the string. (If you need to insert a percent symbol into the string, use `%%`) It may be followed by a `x$` where x is a number corresponding to the parameter that is being passed in code and will always have another letter denoting how the parameter should be interpreted. This is usually either `d` for numbers with no decimal places, `f` for numbers with decimal places, or `s` for other strings. Most often, things like `%d` or `%s` will appear in a localization entry. These are strings that accept one parameter, an integer and a string respectively. For example, you may see:

`<entry key="message.wc.invalidBlockId">%d is not a valid block ID!</entry>`

This entry contains a string that will have a number inserted into it at the front.

Unfortunatly in some cases, we need to place more than one extra piece of data into the same string. This is where the `x$` notation comes in. For example:

`<entry key="command.modlist.header">--- Showing the modlist page %1$d of %2$d ---</entry>`

This string accepts two integers, the current page being viewed in the first slot (denoted by the `1$`) the total number of pages being passed in the second slot (denoted by the `2$`), producing output like `modlist page 1 of 6`. If we wanted to swap the two, and make the output look like: `Of 6 pages, you are on page 1`, we would have to make the localization entry: `Of %2$d pages, you are on page %1$d`. This is because the parameter order in the _code_ is being referenced by the `x$` part of the format specifier, and remains the same across all languages. If you get them mixed up, you will end up with the wrong output.

If none of this made sense to you, it might be easier to let someone else do the localizing for your language.
