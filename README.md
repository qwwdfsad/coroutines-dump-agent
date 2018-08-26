# Coroutines dump agent

[![works badge](https://cdn.rawgit.com/nikku/works-on-my-machine/v0.2.0/badge.svg)](https://github.com/nikku/works-on-my-machine)

PoC servisability agent which scans heap of the target process for `Continuation` objects and prints their completions.
`sa-jdi.jar` from dependencies should match target process Java version.

Use:
`mvn clean package && java -cp target/agent.jar:dependencies/sa-jdi.jar $PID"
On Mac OS X, attach should be ran with `sudo` (yay, servisability!)
