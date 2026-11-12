# ChatFilter
Chat Filter is a chat management plugin for reducing spam, swearing, advertisement of IPs and URLs. Stop repetition in chat and add as many swear words, IPs and URLs as you want to make sure your server chat is controlled! This plugin also filters books, signs, commands and anvils. More details below!

## Features
- **Regex filtering:** Utilizes the use of regex to prevent bypassing of the filter.
- **Multiple Filters:** Filter words, IPs and URLs covering chat, anvils, signs, commands and books.
- **Unicode support:** Prevents the use of certain Unicode common with hacked clients. (hacker font)
- **Anti Spam:** Stop repetitive or similar messages aswell as excessive CAPS and character spam. (Player names exempt)
- **Punishments:** Execute commands when a player is caught by the filter.
- **Commands:** In-game regex generator for words, whitelisting of words, pause and clear chat.
- **Notify staff:** Alerts and highlights swearing and advertising to players with the correct permission.
- **Customizability:** All messages are fully modifiable - Hex compatible. 
- **Preset words:** Over 55+ preset English words.
- **No bloat features:** ChatFilter has been made to stay simple and basic.
- **Languages files:** ChatFilter currently supports English, Danish, Chinese (Thanks to Zhaomengran) and Spanish.

## Commands
- `/clearchat (/cf clear)` –  Clears the chat.
- `/cf help` – Displays a list of the plugin’s commands.
- `/cf reload` – Reloads the plugin config.
- `/cf blacklist (ip/word) list/add/remove <args/word/IP>` – Blacklists a chosen word or IP. (Will not allow this word/IP to go throught the filter)
- `/cf whitelist (ip/word) list/add/remove <args/word/IP>` – Whitelists a chosen word or IP. (Will allow a string of characters/a word or IP to go through the filter)
- `/cf import` - Import plain text words 
- `/cf pause` – Pause chat for players that do not have the bypass perm.

## Permissions
- `chatfilter.reload` – Allows players to use /cf reload
- `chatfilter.blacklist` – Allows players to use /cf blacklist
- `chatfilter.whitelist` – Allows players to use /cf whitelist
- `chatfilter.blacklist.remove` – Allows players to use /cf whitelist remove
- `chatfilter.whitelist.remove` – Allows players to use /cf whitelist remove
- `chatfilter.view` – Allows players to to view what gets caught in the filter
- `chatfilter.pause` – Allows players to pause the chat
- `chatfilter.bypass` – Allows the player to bypass all filters(Chat, Signs, Books, Anvils, Decaps, pause chat and repeat messages)
- `chatfilter.bypass.chat` – Allows the player to bypass in chat
- `chatfilter.bypass.sign` – Allows the player to bypass on a sign
- `chatfilter.bypass.anvil` – Allows the player to bypass in a anvil
- `chatfilter.bypass.book` – Allows the player to bypass in books
- `chatfilter.bypass.command` – Allows the player to bypass in commands
- `chatfilter.bypass.repeat` – Allows the player to bypass repeat messages 
- `chatfilter.bypass.caps` –Allows the player to bypass chat decapping
- `chatfilter.bypass.pause` – Allows the player to bypass paused chat
- `chatfilter.bypass.swear` – Allows the player to bypass all swear filters (chat ,books, commands etc)
- `chatfilter.bypass.swear.<config entry>` – Allows the player to bypass set config entries (chat ,books, commands etc)
- `chatfilter.bypass.ip.<config entry>` – Allows the player to bypass set config entries (chat ,books, commands etc)
- `chatfilter.bypass.ip` – Allows the player to bypass all ip filters (chat ,books, commands etc)

# DISCLAIMER
This fork implements Folia with minimal code changes.