package org.poo.platform.commands;

public final class CommandExecutor {
    private Command command;

    public void setCommand(final Command command) {
        this.command = command;
    }

    public void startExecution() {
        command.operation();
    }
}
