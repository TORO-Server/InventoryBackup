package marumasa.inventory_backup;

import java.util.Locale;

// /clear コマンドの文字列の解釈
public class ClearCommand {

    // /clear の対象を返す。対象の指定がないときは空文字、/clear でないときは null を返す
    public static String parseTarget(String commandLine) {
        final String[] parts = commandLine.trim().split("\\s+");

        String label = parts[0].toLowerCase(Locale.ROOT);
        if (label.startsWith("/")) label = label.substring(1);
        if (!label.equals("clear") && !label.equals("minecraft:clear")) return null;

        return parts.length >= 2 ? parts[1] : "";
    }
}
