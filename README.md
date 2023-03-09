# CronAnnouncer

![build](https://github.com/das-kaesebrot/CronAnnouncer/actions/workflows/gradle.yml/badge.svg?branch=main)

## About
CronAnnouncer is a Bukkit/Spigot plugin to allow scheduling broadcast/title messages to all users using a cron expression.

## Download and Installation
Download the latest `cron-announcer-x.x.x.xxx.jar` from the [releases](https://github.com/das-kaesebrot/CronAnnouncer/releases) tab, then place the downloaded .jar file in your Bukkit/Spigot/Paper server `plugins` subdirectory and restart the server.

## Configuration
Edit the default `config.yml`.
Once you've made your changes, you will have to reload the plugin or restart the server.

See http://www.sauronsoftware.it/projects/cron4j/api/it/sauronsoftware/cron4j/SchedulingPattern.html for valid cron patterns.

Example configuration:
```yaml
schedules:
  # A unique key for your message. Used to identify messages for in-game commands like /cronannouncer rm <message-key>.
  my-test-message:
    # The content of the message being sent, with color support.
    message: "&5My colorful message text running every 5 minutes"
    # Cron expression defining the interval the message should be sent in
    # See http://www.sauronsoftware.it/projects/cron4j/api/it/sauronsoftware/cron4j/SchedulingPattern.html for valid examples
    schedule: "*/5 * * * *"
    # supported types: broadcast/title
    #   broadcast: chat message
    #   title: title message (only using the big text for now)
    type: broadcast
```

Another example configuration with multiple entries:
```yaml
schedules:
  
  my-test-message-1:
    message: "&5My colorful message text running every 5 minutes"
    schedule: "*/5 * * * *"
    type: broadcast
  
  my-test-message-2:
    message: "Yet another message to be sent daily at 12:00 PM"
    schedule: "0 12 * * *"
    type: title
```
## Commands and permissions

Permissions node for all permissions:
`eu.kaesebrot.dev.cronannouncer.*`

### `/cronannouncer list`
Lists all scheduled messages.

Arguments: none

Example:
`/cronannouncer list`

Permissions node:
`eu.kaesebrot.dev.cronannouncer.list`

### `/cronannouncer add <message-key> <cron-expr> <message-text> <type>`
Adds a new message to the config file and reloads the plugin. All arguments may be quoted if spaces are needed.

Arguments:
- message-key: a unique key for the message. Cannont contain spaces.
- cron-expr: A valid cron pattern, has to be in quotes. See [Configuration](#configuration)
- message-text: The content of the message. Color codes using & and $ are supported. Has to be in quotes if it contains spaces.
- type: The type the message should be. Can be either `broadcast` or `title`.

Example:
`/cronannouncer add my-new-message "*/5 * * * *" "&2My message text being displayed every 5 minutes" broadcast`

Permissions node:
`eu.kaesebrot.dev.cronannouncer.add`

### `/cronannouncer rm <message-key>`
Removes the specified message as identified by the message key from the config file and reloads the plugin.

Arguments:
- message-key: the key of the message to remove (see `config.yml` reference)

Example:
`/cronannouncer rm my-message-to-be-removed`

Permissions node:
`eu.kaesebrot.dev.cronannouncer.remove`

### `/cronannouncer reload`
Reloads the config.yml file from disk and queues all scheduled messages.

Arguments: none

Example:
`/cronannouncer reload`

Permissions node:
`eu.kaesebrot.dev.cronannouncer.reload`

## Open Source License Attribution

This application uses Open Source components. You can find the source code of their open source projects along with license information below. We acknowledge and are grateful to these developers for their contributions to open source.
### [cron-utils](https://github.com/jmrozanec/cron-utils)
- Copyright (c) 2014 [jmrozanec](https://github.com/jmrozanec) and contributors
- [Apache License 2.0](https://github.com/jmrozanec/cron-utils/blob/master/LICENSE)
