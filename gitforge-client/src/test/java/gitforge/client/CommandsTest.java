package gitforge.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.sun.net.httpserver.HttpServer;

/** Runs commands against a fake server (JDK's built-in HttpServer): no real GitForge server or database needed. */
class CommandsTest {

    /** A fresh empty folder per test, used as the "home" folder; JUnit deletes it afterwards. */
    @TempDir
    Path home;

    HttpServer server;
    Commands commands;
    List<String> requestBodies = new ArrayList<>();

    @BeforeEach
    void startFakeServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/api/repos", exchange -> {
            var body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            requestBodies.add(body);
            int status = body.contains("\"taken\"") ? 409 : body.contains("\"../x\"") ? 400 : 201;
            exchange.sendResponseHeaders(status, -1);
            exchange.close();
        });
        server.start();
        commands = new Commands(new ApiClient("http://localhost:" + server.getAddress().getPort()), home);
    }

    @AfterEach
    void stopFakeServer() {
        server.stop(0);
    }

    @Test
    void branchInitCreatesRepository() throws Exception {
        assertEquals("Repository 'demo' created.", commands.run("gitforge branch init demo"));
        assertEquals("Repository 'demo2' created.", commands.run("  branch   init demo2 "));
        assertEquals(List.of("{\"name\":\"demo\"}", "{\"name\":\"demo2\"}"), requestBodies);
    }

    @Test
    void branchInitReportsDuplicateAndInvalidNames() throws Exception {
        assertTrue(commands.run("gitforge branch init taken").contains("already exists"));
        assertTrue(commands.run("gitforge branch init ../x").contains("Invalid name"));
    }

    @Test
    void wrongInputIsExplainedAndNotSent() throws Exception {
        assertEquals("Usage: gitforge branch init <name>", commands.run("gitforge branch init"));
        assertEquals("Usage: gitforge branch init <name>", commands.run("gitforge branch init a b"));
        assertTrue(commands.run("gitforge push").startsWith("Unknown command"));
        assertEquals(Commands.HELP, commands.run("help"));
        assertEquals(Commands.HELP, commands.run("gitforge"));
        assertTrue(requestBodies.isEmpty());
    }

    @Test
    void reportsUnreachableServer() throws Exception {
        int freePort;
        try (var socket = new ServerSocket(0)) {
            freePort = socket.getLocalPort();
        }
        var offline = new Commands(new ApiClient("http://localhost:" + freePort), home);
        assertTrue(offline.run("gitforge branch init demo").startsWith("Cannot reach the GitForge server"));
    }

    @Test
    void lsListsFoldersFirstAndHidesHiddenFiles() throws Exception {
        Files.createDirectory(home.resolve("src"));
        Files.createDirectory(home.resolve("Docs"));
        Files.writeString(home.resolve("readme.txt"), "hi");
        Files.writeString(home.resolve(".hidden"), "x");

        assertEquals("  Docs/\n  src/\n  readme.txt", commands.run("ls"));
    }

    @Test
    void lsOnEmptyFolder() throws Exception {
        assertEquals("(empty)", commands.run("ls"));
    }

    @Test
    void cdMovesAroundAndPromptFollows() throws Exception {
        Files.createDirectories(home.resolve("projects/My App"));

        assertEquals("~", commands.promptPath());
        assertEquals(home.toString(), commands.run("pwd"));

        assertEquals("", commands.run("cd projects"));
        assertEquals("~" + File.separator + "projects", commands.promptPath());

        commands.run("cd My App");   // folder name with a space
        assertEquals(home.resolve("projects/My App").toString(), commands.run("pwd"));

        commands.run("cd ..");
        assertEquals(home.resolve("projects").toString(), commands.run("pwd"));

        commands.run("cd ~");
        assertEquals("~", commands.promptPath());

        commands.run("cd ~/projects");
        assertEquals(home.resolve("projects").toString(), commands.run("pwd"));

        commands.run("cd " + home.resolve("projects/My App"));   // absolute path
        assertEquals(home.resolve("projects/My App").toString(), commands.run("pwd"));

        commands.run("cd");   // no argument = home, like a real terminal
        assertEquals("~", commands.promptPath());
    }

    @Test
    void cdRejectsMissingFoldersAndFiles() throws Exception {
        Files.writeString(home.resolve("notes.txt"), "x");

        assertEquals("No such folder: nope", commands.run("cd nope"));
        assertEquals("Not a folder: notes.txt", commands.run("cd notes.txt"));
        assertEquals("~", commands.promptPath(), "a failed cd stays where it was");
    }

    @Test
    void promptShowsFullPathOutsideHome() throws Exception {
        var outside = home.getRoot();
        commands.run("cd " + outside);
        assertEquals(outside.toString(), commands.promptPath());
    }

    @Test
    void jsonEscapesQuotesAndBackslashes() {
        assertEquals("\"a\\\"b\\\\c\"", ApiClient.json("a\"b\\c"));
    }
}
