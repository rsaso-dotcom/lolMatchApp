import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class checkChampion {

    private static final Map<Integer, String> CHAMPION_MAP = new HashMap<>();

    // DB接続共通設定
    private static final String DB_URL = "jdbc:mysql://localhost:3306/lol_app?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "lol0161";

    // 共通のDB接続取得メソッド
    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public static void loadChampionData() {
        if (!CHAMPION_MAP.isEmpty()) return;
        try {
            URL url = URI.create("https://ddragon.leagueoflegends.com/cdn/14.1.1/data/ja_JP/champion.json").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            Pattern pattern = Pattern.compile("\"id\":\"([^\"]+)\".*?\"key\":\"(\\d+)\"");
            Matcher matcher = pattern.matcher(sb.toString());

            while (matcher.find()) {
                CHAMPION_MAP.put(Integer.parseInt(matcher.group(2)), matcher.group(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getChampionName(int championId) {
        if (championId <= 0) return "未選択";

        String query = "SELECT champion_name FROM champions WHERE champion_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, championId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("champion_name");
                }
            }
        } catch (Exception e) {
            System.err.println("DBエラー(getChampionName): " + e.getMessage());
            e.printStackTrace();
        }
        return "Unknown (" + championId + ")";
    }

    // 選択中の対面メモを取得する（存在しない場合は空文字 "" を返す）
    public static String getMatchupNoteText(String myChampion, List<String> enemyChampions) {
        if (myChampion == null || enemyChampions == null || enemyChampions.isEmpty()) {
            return "";
        }

        String query = "SELECT note_text FROM matchup_notes WHERE my_champion = ? AND enemy_champion = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, myChampion);
            pstmt.setString(2, enemyChampions.get(0));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String note = rs.getString("note_text");
                    return (note != null) ? note : "";
                }
            }
        } catch (Exception e) {
            System.err.println("DBエラー(getMatchupNoteText): " + e.getMessage());
            e.printStackTrace();
        }

        return "";
    }

    // メモの保存・更新（存在すればUPDATE、なければINSERT）
    public static boolean saveOrUpdateNote(String myChamp, String enemyChamp, String noteText) {
        String query = "INSERT INTO matchup_notes (my_champion, enemy_champion, note_text) " +
                       "VALUES (?, ?, ?) " +
                       "ON DUPLICATE KEY UPDATE note_text = VALUES(note_text)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, myChamp);
            pstmt.setString(2, enemyChamp);
            pstmt.setString(3, noteText);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // メモの削除
    public static boolean deleteNote(String myChampion, String enemyChampion) {
        String query = "DELETE FROM matchup_notes WHERE my_champion = ? AND enemy_champion = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, myChampion);
            pstmt.setString(2, enemyChampion);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // DBを参照して指定レーン適性の有無を判定するメソッド
    public static boolean isLaneMatch(String championName, String lane) {
        if (championName == null || lane == null || lane.isEmpty()) return false;

        String query = "SELECT COUNT(*) FROM champion_lanes WHERE LOWER(champion_name) = LOWER(?) AND LOWER(lane) = LOWER(?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, championName);
            pstmt.setString(2, lane);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 外部統計上位5体を取得するメソッド（データがない場合はスクレイピングを実行）
    public static List<String> getGlobalCounters(String enemyChamp) {
        List<String> counters = new ArrayList<>();

        if (enemyChamp == null || enemyChamp.trim().isEmpty() || "未選択".equals(enemyChamp)) {
            return counters;
        }

        String query = "SELECT counter_champion, win_rate FROM global_counters " +
                       "WHERE enemy_champion = ? " +
                       "ORDER BY win_rate DESC LIMIT 5";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, enemyChamp);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("counter_champion");
                    double winRate = rs.getDouble("win_rate");
                    counters.add(name + " (" + winRate + "%)");
                }
            }

            // ★ DBにデータが存在しなかった場合、Webから自動スクレイピングして再取得
            if (counters.isEmpty()) {
                System.out.println("ℹ️ DBに「" + enemyChamp + "」のデータがないため、Webから自動取得を試みます...");
                
                // カウンタースクレイピング呼び出し
                CounterScraper.fetchAndSaveCounters(enemyChamp);

                // 再度DBから読み込み（新しいPreparedStatementで確実に取得）
                try (PreparedStatement retryStmt = conn.prepareStatement(query)) {
                    retryStmt.setString(1, enemyChamp);
                    try (ResultSet rsRetry = retryStmt.executeQuery()) {
                        while (rsRetry.next()) {
                            String name = rsRetry.getString("counter_champion");
                            double winRate = rsRetry.getDouble("win_rate");
                            counters.add(name + " (" + winRate + "%)");
                        }
                    }
                }
            } else {
                System.out.println("✅ DBから「" + enemyChamp + "」のデータ（" + counters.size() + "件）を取得しました。");
            }

        } catch (Exception e) {
            System.err.println("DBエラー(getGlobalCounters): " + e.getMessage());
            e.printStackTrace();
        }

        return counters;
    }

    /**
     * 日本語チャンピオン名から DB の lolalytics_slug を取得する
     */
    public static String getLolalyticsSlug(String championName) {
        if (championName == null || championName.trim().isEmpty()) {
            return "";
        }

        String query = "SELECT lolalytics_slug FROM champions WHERE champion_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, championName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String slug = rs.getString("lolalytics_slug");
                    if (slug != null && !slug.trim().isEmpty()) {
                        return slug;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("DBエラー(getLolalyticsSlug): " + e.getMessage());
        }

        // DBに万が一未登録の場合のフォールバック（小文字・英数字のみ）
        return championName.toLowerCase().replaceAll("[^a-z0-9]", "");
    }
}