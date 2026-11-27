package messenger.server;

import messenger.exception.InvalidCommandException;

public class CommandParser {
    public static final int DEFAULT_HISTORY_LIMIT = 20;
    public static final int MAX_HISTORY_LIMIT = 100;


    public Command parse(String rawLine) {


        if (rawLine == null) {
            throw new InvalidCommandException("empty input");
        }


        String line = rawLine.trim();
        if (line.isEmpty()) {
            throw new InvalidCommandException("empty command");
        }



        if (!line.startsWith("/")) {
            return new Command.Msg(line);
        }


        String[] firstSplit = line.split("\\s+", 2);
        String cmd = firstSplit[0].toLowerCase();
        String rest = firstSplit.length > 1 ? firstSplit[1].trim() : "";


        return switch (cmd) {

            case "/register" -> parseRegister(rest);
            case "/login" -> parseLogin(rest);
            case "/join" -> parseSingleToken(rest, "/join <room>");
            case "/leave" -> parseLeave(rest);
            case "/rooms" -> new Command.Rooms();
            case "/who" -> new Command.Who();
            case "/msg" -> parseMsg(rest);
            case "/pm" -> parsePm(rest);
            case "/history" -> parseHistory(rest);
            case "/quit" -> new Command.Quit();

            default -> throw new InvalidCommandException("unknown command: " + firstSplit[0]);

        };
    }


    private Command parseRegister(String rest) {

        String[] parts = rest.split("\\s+", 2);

        if (parts.length < 2 || parts[0].isEmpty() || parts[1].isEmpty()) {

            throw new InvalidCommandException("Usage: /register <username> <password>");

        }

        return new Command.Register(parts[0], parts[1]);

    }

    private Command parseLogin(String rest) {

        String[] parts = rest.split("\\s+", 2);

        if (parts.length < 2 || parts[0].isEmpty() || parts[1].isEmpty()) {

            throw new InvalidCommandException("usage: /login <username> <password>");
        }

        return new Command.Login(parts[0], parts[1]);
    }

    private Command.Join parseSingleToken(String rest, String usage) {

        if (rest.isEmpty() || rest.contains(" ")) {
            throw new InvalidCommandException("usage: " + usage);
        }

        return new Command.Join(rest);

    }

    private Command.Leave parseLeave(String rest) {

        if (rest.contains(" ")) {

            throw new InvalidCommandException("usage: /leave [room]");

        }

        return new Command.Leave(rest.isEmpty() ? null : rest);

    }

    private Command.Msg parseMsg(String rest) {

        if (rest.isEmpty()) {

            throw new InvalidCommandException("usage: /msg <text>");

        }

        return new Command.Msg(rest);

    }

    private Command.Pm parsePm(String rest) {
        String[] parts = rest.split("\\s+", 2);
        if (parts.length < 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            throw new InvalidCommandException("Usage: /pm <username> <text>");
        }

        return new Command.Pm(parts[0], parts[1]);

    }

    private Command.History parseHistory(String rest) {

        if (rest.isEmpty()) {

            throw new InvalidCommandException("usage: /history <room|@user> [limit]");

        }

        String[] parts = rest.split("\\s+");

        if (parts.length > 2) {

            throw new InvalidCommandException("usage: /history <room|@user> [limit]");

        }
        String target = parts[0];

        int limit = DEFAULT_HISTORY_LIMIT;

        if (parts.length == 2) {

            try {
                limit = Integer.parseInt(parts[1]);

            } catch (NumberFormatException e) {

                throw new InvalidCommandException("limit must be a number");

            }

            if (limit <= 0 || limit > MAX_HISTORY_LIMIT) {

                throw new InvalidCommandException("limit must be between 1 and " + MAX_HISTORY_LIMIT);

            }
        }

        return new Command.History(target, limit);

    }
}