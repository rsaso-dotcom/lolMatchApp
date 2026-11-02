import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MatchupAppFx extends Application {

    private Label myChampLabel;
    private ComboBox<String> enemyCombo; // 敵ピック済みの全チャンピオン（最大5体）
    private FlowPane counterContainer;
    private TextArea noteTextArea;
    private Label statusLabel;

    private String currentMyChamp = "未選択";
    private String myLane = "top"; // 自分割り当てレーン

    private LcuService lcuService = new LcuService();
    private boolean isLcuConnected = false;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("LoL Matchup Notes Pro");

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #091428;");

        HBox vsCard = createVsHeader();
        VBox counterSection = createCounterSection();
        VBox noteSection = createNoteSection();
        HBox actionBar = createActionBar();

        root.getChildren().addAll(vsCard, counterSection, noteSection, actionBar);

        setupEvents();
        startLcuLoop();

        Scene scene = new Scene(root, 750, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createVsHeader() {
        HBox hbox = new HBox(15);
        hbox.setAlignment(Pos.CENTER);
        hbox.setPadding(new Insets(15));
        hbox.setStyle("-fx-background-color: #0A1428; -fx-border-color: #C89B3C; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");

        myChampLabel = new Label("自分: " + currentMyChamp + " (" + myLane.toUpperCase() + ")");
        myChampLabel.setTextFill(Color.web("#F0E6D2"));
        myChampLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        Label vsLabel = new Label("VS");
        vsLabel.setTextFill(Color.web("#C89B3C"));
        vsLabel.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 18));

        Label enemyLabel = new Label("敵チーム:");
        enemyLabel.setTextFill(Color.web("#F0E6D2"));
        enemyLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        enemyCombo = new ComboBox<>();
        enemyCombo.setPromptText("敵ピック待機中...");
        enemyCombo.setStyle("-fx-background-color: #1E232A; -fx-text-fill: #F0E6D2; -fx-font-size: 14px; -fx-border-color: #785A28; -fx-border-radius: 4;");

        Button refreshBtn = new Button("🔄 再検知");
        refreshBtn.setStyle("-fx-background-color: #C89B3C; -fx-text-fill: #091428; -fx-font-weight: bold; -fx-cursor: hand;");
        refreshBtn.setOnAction(e -> pollLcuOnce());

        hbox.getChildren().addAll(myChampLabel, vsLabel, enemyLabel, enemyCombo, refreshBtn);
        return hbox;
    }

    private VBox createCounterSection() {
        VBox box = new VBox(10);

        Label title = new Label("💡 選択中対面に対するカウンター (勝率上位)");
        title.setTextFill(Color.web("#C89B3C"));
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        counterContainer = new FlowPane(10, 10);
        counterContainer.setAlignment(Pos.CENTER_LEFT);

        box.getChildren().addAll(title, counterContainer);
        return box;
    }

    private VBox createNoteSection() {
        VBox box = new VBox(8);

        Label label = new Label("📝 対面メモ / 立ち回りノート");
        label.setTextFill(Color.web("#F0E6D2"));
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        noteTextArea = new TextArea();
        noteTextArea.setPromptText("ここにレベル1～6の立ち回りや注意アイテム、CD管理メモを記入...");
        noteTextArea.setPrefRowCount(10);
        noteTextArea.setWrapText(true);
        noteTextArea.setStyle(
            "-fx-control-inner-background: #1E232A; " +
            "-fx-text-fill: #F0E6D2; " +
            "-fx-font-family: 'Consolas', 'Yu Gothic'; " +
            "-fx-font-size: 14px; " +
            "-fx-border-color: #3C3C41; " +
            "-fx-border-radius: 5; " +
            "-fx-background-radius: 5;"
        );

        box.getChildren().addAll(label, noteTextArea);
        return box;
    }

    private HBox createActionBar() {
        HBox hbox = new HBox(15);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Button saveBtn = new Button("💾 メモを保存");
        saveBtn.setStyle("-fx-background-color: #2CD67D; -fx-text-fill: #091428; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 20; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> saveNote());

        Button deleteBtn = new Button("🗑️ 削除");
        deleteBtn.setStyle("-fx-background-color: #E84057; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 20; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> deleteNote());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        statusLabel = new Label("状態: LCU接続確認中...");
        statusLabel.setTextFill(Color.web("#A0A5B5"));

        hbox.getChildren().addAll(saveBtn, deleteBtn, spacer, statusLabel);
        return hbox;
    }

    // --- LCU 自動監視 ＆ 対面最有力判定 ---

    private void startLcuLoop() {
        Thread lcuThread = new Thread(() -> {
            while (true) {
                try {
                    pollLcuOnce();
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        lcuThread.setDaemon(true);
        lcuThread.start();
    }

    private void pollLcuOnce() {
        if (!isLcuConnected) {
            isLcuConnected = lcuService.connect();
        }

        if (isLcuConnected) {
            String json = lcuService.getChampSelectSession();
            if (json != null && !json.contains("未接続") && !json.contains("API呼び出しエラー")) {
                
                // 1. 自分のピックとレーン取得
                int myChampId = lcuService.getMyChampionId(json);
                String detectedLane = lcuService.getMyPosition(json);
                if (detectedLane != null && !detectedLane.isEmpty()) {
                    myLane = detectedLane;
                }

                String myChampName = getChampNameById(myChampId);

                // 2. 敵チームの全ピック/ホバーID（最大5体）を取得
                List<Integer> enemyChampIds = lcuService.getEnemyChampionIds(json);

                // 全5体の日本語名リストを生成
                List<String> enemyNames = new ArrayList<>();
                for (Integer id : enemyChampIds) {
                    String name = getChampNameById(id);
                    if (name != null && !enemyNames.contains(name)) {
                        enemyNames.add(name);
                    }
                }

                // 3. 自分と同じレーン（myLane）に所属する最も対面確率が高いチャンピオンを特定
                String bestMatchEnemy = findBestLaneMatch(enemyChampIds, myLane);

                Platform.runLater(() -> {
                    if (myChampName != null && !myChampName.equals(currentMyChamp)) {
                        currentMyChamp = myChampName;
                    }
                    myChampLabel.setText("自分: " + currentMyChamp + " (" + myLane.toUpperCase() + ")");

                    // ドロップダウンリストの内容が変更された場合のみ更新
                    if (!enemyNames.equals(enemyCombo.getItems())) {
                        String currentSelected = enemyCombo.getValue();
                        enemyCombo.getItems().clear();
                        enemyCombo.getItems().addAll(enemyNames);

                        // 優先順位: 1. ユーザーが手動で選んでいた対面 -> 2. メインレーン一致チャンピオン -> 3. 1体目
                        if (currentSelected != null && enemyNames.contains(currentSelected)) {
                            enemyCombo.setValue(currentSelected);
                        } else if (bestMatchEnemy != null && enemyNames.contains(bestMatchEnemy)) {
                            enemyCombo.setValue(bestMatchEnemy);
                        } else if (!enemyNames.isEmpty()) {
                            enemyCombo.setValue(enemyNames.get(0));
                        }

                        onMatchupChanged();
                    }

                    statusLabel.setText("状態: 敵チーム " + enemyNames.size() + " 体を取得完了 (最有力対面: " + (bestMatchEnemy != null ? bestMatchEnemy : "判定中") + ")");
                });
            } else {
                isLcuConnected = false;
                Platform.runLater(() -> statusLabel.setText("状態: チャンプセレクト外"));
            }
        } else {
            Platform.runLater(() -> statusLabel.setText("状態: LoLクライアント未起動"));
        }
    }

    // 敵IDの中から、メインレーンが自分の割り当てレーン（myLane）に一致するチャンピオン名を優先的に探索
    private String findBestLaneMatch(List<Integer> enemyIds, String lane) {
        if (enemyIds.isEmpty()) return null;

        StringBuilder inClause = new StringBuilder();
        for (int i = 0; i < enemyIds.size(); i++) {
            inClause.append("?");
            if (i < enemyIds.size() - 1) inClause.append(",");
        }

        String sql = "SELECT champion_name FROM champions WHERE champion_id IN (" + inClause + ") AND main_lane = ? LIMIT 1";

        try (Connection conn = checkChampion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int index = 1;
            for (Integer id : enemyIds) {
                stmt.setInt(index++, id);
            }
            stmt.setString(index, lane.toLowerCase());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("champion_name");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getChampNameById(int champId) {
        if (champId <= 0) return null;
        String sql = "SELECT champion_name FROM champions WHERE champion_id = ?";
        try (Connection conn = checkChampion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, champId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("champion_name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- DB操作 & UI連動 ---

    private void setupEvents() {
        enemyCombo.setOnAction(e -> onMatchupChanged());
    }

    private void onMatchupChanged() {
        String enemyChamp = enemyCombo.getValue();
        if (enemyChamp == null) return;

        List<String> counters = getCountersFromDb(enemyChamp);
        updateCounterBadges(counters);

        String note = getNoteFromDb(currentMyChamp, enemyChamp);
        noteTextArea.setText(note);
    }

    private List<String> getCountersFromDb(String enemy) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT counter_champion, win_rate FROM global_counters WHERE enemy_champion = ? ORDER BY win_rate DESC LIMIT 5";

        try (Connection conn = checkChampion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, enemy);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String counter = rs.getString("counter_champion");
                double wr = rs.getDouble("win_rate");
                list.add(counter + " (" + String.format("%.1f", wr) + "%)");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private String getNoteFromDb(String myChamp, String enemy) {
        String sql = "SELECT note_text FROM matchup_notes WHERE my_champion = ? AND enemy_champion = ?";
        try (Connection conn = checkChampion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, myChamp);
            stmt.setString(2, enemy);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("note_text");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void saveNote() {
        String enemy = enemyCombo.getValue();
        String text = noteTextArea.getText();

        if (enemy == null) return;

        String sql = "INSERT INTO matchup_notes (my_champion, enemy_champion, note_text) " +
                     "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE note_text = VALUES(note_text)";

        try (Connection conn = checkChampion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, currentMyChamp);
            stmt.setString(2, enemy);
            stmt.setString(3, text);
            stmt.executeUpdate();

            statusLabel.setText("状態: メモを保存しました (" + currentMyChamp + " vs " + enemy + ")");

        } catch (Exception e) {
            statusLabel.setText("状態: メモ保存失敗");
            e.printStackTrace();
        }
    }

    private void deleteNote() {
        String enemy = enemyCombo.getValue();
        if (enemy == null) return;

        String sql = "DELETE FROM matchup_notes WHERE my_champion = ? AND enemy_champion = ?";

        try (Connection conn = checkChampion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, currentMyChamp);
            stmt.setString(2, enemy);
            stmt.executeUpdate();

            noteTextArea.clear();
            statusLabel.setText("状態: メモを削除しました (" + currentMyChamp + " vs " + enemy + ")");

        } catch (Exception e) {
            statusLabel.setText("状態: メモ削除失敗");
            e.printStackTrace();
        }
    }

    public void updateCounterBadges(List<String> counters) {
        counterContainer.getChildren().clear();

        if (counters.isEmpty()) {
            Label emptyLabel = new Label("カウンターデータがありません");
            emptyLabel.setTextFill(Color.web("#A0A5B5"));
            counterContainer.getChildren().add(emptyLabel);
            return;
        }

        for (String counter : counters) {
            HBox badge = new HBox(8);
            badge.setPadding(new Insets(8, 12, 8, 12));
            badge.setStyle("-fx-background-color: #1E282D; -fx-border-color: #0396A6; -fx-border-radius: 15; -fx-background-radius: 15;");

            Label textLabel = new Label(counter);
            textLabel.setTextFill(Color.web("#00F0FF"));
            textLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));

            badge.getChildren().add(textLabel);
            counterContainer.getChildren().add(badge);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}