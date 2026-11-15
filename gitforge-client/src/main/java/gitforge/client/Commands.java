package gitforge.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The "brain" of the console: takes one line the user typed and returns the text to print.
 * It has no window code, so it can be tested without opening a window (see CommandsTest).
 */
final class Commands {

    /** Text shown for "help" (and when the user types only "gitforge"). */
    static final String HELP = """
            Commands:
              gitforge branch init <name>   Create a new repository on the server
              pwd                           Show the current folder
              ls                            List the current folder
              cd <folder>                   Go to a folder (cd .. = up, cd ~ = home)
              help                          Show list of commands
              clear                         Clear the screen
              exit                          Close the console""";

    /** Talks to the GitForge server over HTTP. */
    private final ApiClient api;

    /** Your home folder, e.g. /Users/piotr on Mac or C:\Users\piotr on Windows. "~" means this folder. */
    private final Path home;

    /** The folder the console is "in". Starts at home; "cd" changes it. Like the folder a terminal is open in. */
    private Path currentDir;

    Commands(ApiClient api, Path home) {
        this.api = api;
        this.home = home.toAbsolutePath().normalize();
        this.currentDir = this.home;
    }

    /** Runs one typed line and returns what the console should print. */
    String run(String line) throws InterruptedException {
        // Split the line into words on spaces: "gitforge branch init demo" -> [gitforge, branch, init, demo].
        List<String> words = new ArrayList<>(List.of(line.strip().split("\\s+")));

        // The "gitforge" prefix is optional, so "branch init demo" works too.
        if (words.getFirst().equals("gitforge")) words.removeFirst();
        if (words.isEmpty() || words.getFirst().equals("help")) return HELP;

        // Everything after the command word, joined back together, so "cd My Folder" works with spaces.
        // shortcut: several spaces in a row inside a folder name become one; add quoting if that matters.
        var argument = String.join(" ", words.subList(1, words.size()));

        switch (words.getFirst()) {
            case "pwd":
                return currentDir.toString();
            case "ls":
                return list();
            case "cd":
                return changeDir(argument);
        }

        // "branch init <name>": exactly 3 words, otherwise show how to use it.
        if (words.size() >= 2 && words.get(0).equals("branch") && words.get(1).equals("init")) {
            return words.size() == 3 ? init(words.get(2)) : "Usage: gitforge branch init <name>";
        }
        return "Unknown command: " + line.strip() + "\nType 'help' to see the commands.";
    }

    /** The current folder for the prompt, with your home folder shortened to "~": "~/Desktop/GitForge". */
    String promptPath() {
        if (currentDir.equals(home)) return "~";
        if (currentDir.startsWith(home)) return "~" + currentDir.toString().substring(home.toString().length());
        return currentDir.toString();
    }

    /** "ls": folders first (ending in /), then files, alphabetically. Hidden entries (".git", ".DS_Store") are skipped, like a normal ls. */
    private String list() {
        try (var entries = Files.list(currentDir)) {
            var lines = entries
                    .filter(path -> !path.getFileName().toString().startsWith("."))
                    .sorted(Comparator
                            .comparing((Path path) -> !Files.isDirectory(path))   // false (= folder) sorts first
                            .thenComparing(path -> path.getFileName().toString().toLowerCase()))
                    .map(path -> "  " + path.getFileName() + (Files.isDirectory(path) ? "/" : ""))
                    .collect(Collectors.joining("\n"));
            return lines.isEmpty() ? "(empty)" : lines;
        } catch (IOException e) {
            // E.g. no permission: macOS asks before an app may read Desktop/Documents/Downloads.
            return "Cannot read folder: " + currentDir;
        }
    }

    /** "cd <folder>": move to another folder, if it exists. */
    private String changeDir(String argument) {
        var target = resolve(argument);
        if (!Files.exists(target)) return "No such folder: " + argument;
        if (!Files.isDirectory(target)) return "Not a folder: " + argument;
        currentDir = target;
        return "";   // like a real terminal, a successful cd prints nothing
    }

    /** Turns what the user typed into a full path: "", "~" and "~/x" are relative to home, everything else to the current folder. */
    private Path resolve(String argument) {
        if (argument.isEmpty() || argument.equals("~")) return home;
        if (argument.startsWith("~/") || argument.startsWith("~\\")) return home.resolve(argument.substring(2)).normalize();
        // resolve() joins paths ("src" -> currentDir/src; an absolute path replaces it), normalize() removes "." and "..".
        return currentDir.resolve(argument).toAbsolutePath().normalize();
    }

    /** asks the server to create the repository and turns the HTTP status into a message. */
    private String init(String name) throws InterruptedException {
        try {
            var response = api.initRepo(name);
            return switch (response.statusCode()) {
                case 201 -> "Repository '" + name + "' created.";                     // 201 Created
                case 409 -> "Repository '" + name + "' already exists.";              // 409 Conflict
                case 400 -> "Invalid name '" + name + "'. Use letters, digits, '.', '_' or '-' (max 100)."; // 400 Bad Request
                default -> "Server error " + response.statusCode() + ": " + response.body();
            };
        } catch (IOException e) {
            // Thrown when nothing answers at the URL: server not started, wrong port, etc.
            return "Cannot reach the GitForge server at " + api.baseUrl() + ". Is it running?";
        }
    }
}
