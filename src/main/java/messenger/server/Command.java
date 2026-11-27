package messenger.server;

public sealed interface Command {
    record Register(String username, String password) implements Command {}
    record Login(String username, String password) implements Command {}
    record Join(String room) implements Command {}
    record Leave(String room) implements Command {}
    record Rooms() implements Command {}
    record Who() implements Command {}
    record Msg(String text) implements Command {}
    record Pm(String targetUser, String text) implements Command {}
    record History(String target, int limit) implements Command {}
    record Quit() implements Command {}
}