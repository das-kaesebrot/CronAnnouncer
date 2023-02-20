# CronAnnouncer

![build](https://github.com/github/docs/actions/workflows/main.yml/badge.svg?branch=main)

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
  # A name for your message. Only used for display purposes.
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

## Open Source License Attribution

This application uses Open Source components. You can find the source code of their open source projects along with license information below. We acknowledge and are grateful to these developers for their contributions to open source.
### [cron-utils](https://github.com/jmrozanec/cron-utils)
- Copyright (c) 2014 [jmrozanec](https://github.com/jmrozanec) and contributors
- [Apache License 2.0](https://github.com/jmrozanec/cron-utils/blob/master/LICENSE)
