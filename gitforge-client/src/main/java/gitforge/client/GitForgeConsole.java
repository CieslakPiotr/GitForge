package gitforge.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.nio.file.Path;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * The window. Looks like a terminal: output text on top, a "gitforge ~>" input line at the bottom.
 * Built with Swing, which ships with Java, so it runs the same on Mac and Windows with nothing to install.
 * What each command does lives in Commands; this class only shows text and reads what you type.
 */
public class GitForgeConsole {

    // Colors and font: dark background, light text, green prompt, monospaced like a real terminal.
    private static final Color BACKGROUND = new Color(0x1E1E1E);
    private static final Color TEXT = new Color(0xD4D4D4);
    private static final Color PROMPT = new Color(0x6A9955);
    private static final Font FONT = new Font(Font.MONOSPACED, Font.PLAIN, 14);

    /** Program start. Runs when you do "java -jar gitforge.jar" or double-click the jar. */
    public static void main(String[] args) {
        // Server address: from the GITFORGE_URL environment variable, or the local default.
        var env = System.getenv("GITFORGE_URL");
        var url = env != null && !env.isBlank() ? env : "http://localhost:8080";

        // The console starts in your home folder (/Users/piotr on Mac, C:\Users\piotr on Windows).
        var commands = new Commands(new ApiClient(url), Path.of(System.getProperty("user.home")));

        // Swing rule: windows must be created on Swing's own thread (the "event dispatch thread").
        SwingUtilities.invokeLater(() -> open(commands, url));
    }

    private static void open(Commands commands, String url) {
        // --- Output area: everything printed so far. Read-only; wraps long lines. ---
        var output = new JTextArea();
        output.setEditable(false);
        output.setLineWrap(true);
        output.setFont(FONT);
        output.setBackground(BACKGROUND);
        output.setForeground(TEXT);
        output.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        output.append("GitForge console  (server: " + url + ")\nType 'help' to see the commands.\n\n");

        // --- Prompt label on the left of the input line, e.g. "gitforge ~/Desktop> ". Updated after every command. ---
        var prompt = new JLabel(promptText(commands));
        prompt.setFont(FONT);
        prompt.setForeground(PROMPT);

        // --- Input line: where you type. Enter runs the command (see addActionListener below). ---
        var input = new JTextField();
        input.setFont(FONT);
        input.setBackground(BACKGROUND);
        input.setForeground(TEXT);
        input.setCaretColor(TEXT);
        input.setBorder(null);
        input.getAccessibleContext().setAccessibleName("Command");   // name read out by screen readers

        // --- Bottom row: prompt label + input line side by side. ---
        var inputRow = new JPanel(new BorderLayout());
        inputRow.setBackground(BACKGROUND);
        inputRow.setBorder(BorderFactory.createEmptyBorder(6, 8, 8, 8));
        inputRow.add(prompt, BorderLayout.WEST);
        inputRow.add(input, BorderLayout.CENTER);

        // --- The window itself: output (scrollable) in the middle, input row at the bottom. ---
        var frame = new JFrame("GitForge");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   // closing the window ends the program
        var scroll = new JScrollPane(output);
        scroll.setBorder(null);
        frame.add(scroll, BorderLayout.CENTER);
        frame.add(inputRow, BorderLayout.SOUTH);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);   // center on screen

        // --- What happens when you press Enter in the input line. ---
        input.addActionListener(event -> {
            var line = input.getText();
            input.setText("");
            if (line.isBlank()) return;

            // Echo the typed line into the output, with the prompt, like a terminal does.
            output.append(promptText(commands) + line + "\n");

            switch (line.strip()) {
                // These two only affect the window, so they are handled here, not in Commands.
                case "exit" -> frame.dispose();
                case "clear" -> output.setText("");
                default -> {
                    // Everything else goes to Commands. It runs on a background thread, because a server call
                    // can take a moment and the window would freeze if Swing's thread waited for it.
                    input.setEnabled(false);   // no typing while a command runs
                    Thread.startVirtualThread(() -> {
                        String result;
                        try {
                            result = commands.run(line);
                        } catch (InterruptedException e) {
                            result = "Interrupted.";
                        }
                        var text = result;
                        // Back on Swing's thread to update the window (Swing components are not thread-safe).
                        SwingUtilities.invokeLater(() -> {
                            if (!text.isEmpty()) output.append(text + "\n");
                            output.append("\n");
                            output.setCaretPosition(output.getDocument().getLength());   // scroll to the bottom
                            prompt.setText(promptText(commands));   // "cd" may have changed the folder
                            input.setEnabled(true);
                            input.requestFocusInWindow();
                        });
                    });
                }
            }
        });

        frame.setVisible(true);
        input.requestFocusInWindow();   // cursor in the input line, ready to type
    }

    /** "gitforge ~/Desktop> ": the prompt shows which folder the console is in. */
    private static String promptText(Commands commands) {
        return "gitforge " + commands.promptPath() + "> ";
    }
}
