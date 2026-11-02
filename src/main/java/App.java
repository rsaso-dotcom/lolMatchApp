public class App {

    public static void main(String[] args) {
        System.out.println("=== LCU API 接続テスト ===");

        LcuService lcu = new LcuService();
        
        // 1. クライアントからのポート・トークン取得テスト
        if (lcu.connect()) {
            System.out.println("\n--- チャンプ選択APIを呼び出します ---");
            // 2. チャンプ選択情報の取得テスト
            String result = lcu.getChampSelectSession();
            System.out.println(result);
        }

        // ※以前作成したDBテストを動かしたいときは、下の行の「//」を外して実行できます
        // testDatabase();
    }

    // 第2章で作ったMySQL接続テストコード（退避用）
    public static void testDatabase() {
        String url = "jdbc:mysql://localhost:3306/lol_app?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String user = "root";
        String password = "YOUR_ROOT_PASSWORD"; // ご自身のパスワード

        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(url, user, password);
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery("SELECT * FROM matchup_notes")) {

            System.out.println("\n--- MySQLデータ取得テスト ---");
            while (rs.next()) {
                System.out.println("自分: " + rs.getString("my_champion") + " vs 相手: " + rs.getString("enemy_champion"));
                System.out.println("メモ: " + rs.getString("note"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}