import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CounterScraper {

    public static void fetchAndSaveCounters(String enemyChamp) {
        String slug = checkChampion.getLolalyticsSlug(enemyChamp);

        if (slug == null || slug.isEmpty()) {
            System.err.println("⚠️ 「" + enemyChamp + "」のスラグ取得に失敗しました。");
            return;
        }

        // Lolalytics の内部 API またはデータ取得用エンドポイント
        // HTTPヘッダーをブラウザと完全に一致させてリクエスト
        String targetUrl = "https://lolalytics.com/lol/" + slug + "/counters/";

        try {
            URL url = URI.create(targetUrl).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);

            if (conn.getResponseCode() != 200) {
                System.err.println("⚠️ アクセスエラー HTTP Status: " + conn.getResponseCode());
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            parseAndSave(sb.toString(), enemyChamp, slug);

        } catch (Exception e) {
            System.err.println("❌ データ取得エラー: " + e.getMessage());
        }
    }

    private static void parseAndSave(String html, String enemyChamp, String enemySlug) {
        String sql = "INSERT INTO global_counters (enemy_champion, counter_champion, win_rate) " +
                     "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE win_rate = VALUES(win_rate)";

        try (Connection dbConn = checkChampion.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {

            int count = 0;

            // Lolalytics の Next.js __NEXT_DATA__ JSON 埋め込みノードから抽出
            // またはスクリプトブロック内の "cid":... や "wr":... を抽出
            Pattern pattern = Pattern.compile("\"name\":\"([a-zA-Z0-9]+)\"[^}]*?\"wr\":([0-9]{2}\\.[0-9]{1,2})");
            Matcher matcher = pattern.matcher(html);

            while (matcher.find() && count < 5) {
                String counterSlug = matcher.group(1).toLowerCase();
                double winRate = Double.parseDouble(matcher.group(2));

                if (counterSlug.equalsIgnoreCase(enemySlug) || counterSlug.equalsIgnoreCase(enemyChamp)) {
                    continue;
                }

                // 勝率のフィルタリング（カウンター勝率として妥当な 40.0% 〜 65.0%）
                if (winRate < 40.0 || winRate > 65.0) continue;

                String counterName = counterSlug.substring(0, 1).toUpperCase() + counterSlug.substring(1);

                stmt.setString(1, enemyChamp);
                stmt.setString(2, counterName);
                stmt.setDouble(3, winRate);
                stmt.addBatch();
                count++;
            }

            if (count > 0) {
                stmt.executeBatch();
                System.out.println("✅ [Lolalytics] 「" + enemyChamp + "」のカウンターデータを " + count + " 件DBに保存しました！");
            } else {
                System.out.println("⚠️ 「" + enemyChamp + "」のデータ抽出数が0件でした。");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}