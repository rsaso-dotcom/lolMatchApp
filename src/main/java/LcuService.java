import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class LcuService {

    private String port = "";
    private String token = "";

   // lockfile を直接読み込む方式
    public boolean connect() {
        // LoLのデフォルトインストールパスにある lockfile
        File lockfile = new File("K:/Riot Games/League of Legends/lockfile");

        if (!lockfile.exists()) {
            System.err.println("❌ lockfileが見つかりません。LoLが起動していないか確認してください。");
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(lockfile))) {
            String line = br.readLine();
            if (line != null) {
                // lockfileの形式: プロセス名:PID:ポート:トークン:プロトコル
                // 例) LeagueClient:12345:51234:AuthTokenValue:https
                String[] tokens = line.split(":");
                if (tokens.length >= 4) {
                    this.port = tokens[2];
                    this.token = tokens[3];
                    System.out.println("✅ LoLクライアント検出成功!");
                    System.out.println("   Port: " + this.port);
                    System.out.println("   Token: " + this.token);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.err.println("❌ lockfileの読み込みに失敗しました。");
        return false;
    }

    // 2. LCU APIにリクエストを送信するメソッド
    public String getChampSelectSession() {
        if (port.isEmpty() || token.isEmpty()) {
            return "未接続です";
        }

        try {
            String auth = "riot:" + this.token;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpClient client = HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .build();

            URI uri = URI.create("https://127.0.0.1:" + this.port + "/lol-champ-select/v1/session");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Authorization", "Basic " + encodedAuth)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (Exception e) {
            return "API呼び出しエラー: " + e.getMessage();
        }
    }

    // 自分の championId を抽出
    public int getMyChampionId(String jsonResponse) {
        try {
            // myTeam セクションから自分の選択/ホバーIDを取得
            Pattern myTeamPattern = Pattern.compile("\"myTeam\":\\[(.*?)\\]");
            Matcher myTeamMatcher = myTeamPattern.matcher(jsonResponse);

            if (myTeamMatcher.find()) {
                String myTeamJson = myTeamMatcher.group(1);

                Pattern intentPattern = Pattern.compile("\"championPickIntent\":(\\d+)");
                Matcher intentMatcher = intentPattern.matcher(myTeamJson);
                if (intentMatcher.find()) {
                    int id = Integer.parseInt(intentMatcher.group(1));
                    if (id > 0) return id;
                }

                Pattern champPattern = Pattern.compile("\"championId\":(\\d+)");
                Matcher champMatcher = champPattern.matcher(myTeamJson);
                if (champMatcher.find()) {
                    return Integer.parseInt(champMatcher.group(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 敵チーム (theirTeam) の championId リストを抽出
    public List<Integer> getEnemyChampionIds(String jsonResponse) {
        List<Integer> enemyIds = new ArrayList<>();
        try {
            Pattern theirTeamPattern = Pattern.compile("\"theirTeam\":\\[(.*?)\\]");
            Matcher theirTeamMatcher = theirTeamPattern.matcher(jsonResponse);

            if (theirTeamMatcher.find()) {
                String theirTeamJson = theirTeamMatcher.group(1);

                // championId または championPickIntent をすべて抽出
                Pattern champPattern = Pattern.compile("\"championId\":(\\d+)|\"championPickIntent\":(\\d+)");
                Matcher champMatcher = champPattern.matcher(theirTeamJson);

                while (champMatcher.find()) {
                    String idStr = champMatcher.group(1) != null ? champMatcher.group(1) : champMatcher.group(2);
                    int id = Integer.parseInt(idStr);
                    if (id > 0 && !enemyIds.contains(id)) {
                        enemyIds.add(id);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enemyIds;
    }

    // 自分の割当レーン（top, jungle, middle, bottom, utility）を取得する
    public String getMyPosition(String json) {
        if (json == null || json.isEmpty()) return "";

        try {
            // 自分のcellId（プレイヤーID）を取得
            int myCellId = -1;
            Matcher cellMatcher = Pattern.compile("\"localPlayerCellId\":(\\d+)").matcher(json);
            if (cellMatcher.find()) {
                myCellId = Integer.parseInt(cellMatcher.group(1));
            }

            if (myCellId != -1) {
                // myTeam の配列の中から自分の cellId を探し、assignedPosition を抽出
                Pattern myTeamPattern = Pattern.compile("\"myTeam\":\\[(.*?)\\]");
                Matcher myTeamMatcher = myTeamPattern.matcher(json);
                if (myTeamMatcher.find()) {
                    String myTeamJson = myTeamMatcher.group(1);
                    // 各プレイヤーのオブジェクトを判定
                    Pattern playerPattern = Pattern.compile("\\{[^\\}]*\"cellId\":" + myCellId + "[^\\}]*\\}");
                    Matcher playerMatcher = playerPattern.matcher(myTeamJson);
                    if (playerMatcher.find()) {
                        String playerObj = playerMatcher.group(0);
                        Matcher posMatcher = Pattern.compile("\"assignedPosition\":\"([^\"]+)\"").matcher(playerObj);
                        if (posMatcher.find()) {
                            return posMatcher.group(1).toLowerCase(); //例: "top", "jungle", "middle", "bottom", "utility"
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}