package desktop;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Desktop extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Загружаем шрифт прямо в Java
        Font font = Font.loadFont(getClass().getResource("/vhs.ttf").toExternalForm(), 16);

        // Создаем layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);

        // Первая колонка
        VBox firstColumn = new VBox(10);
        Label label1 = new Label("Заголовок 1");
        label1.setFont(font);  // Применяем шрифт
        firstColumn.getChildren().addAll(label1);
        grid.add(firstColumn, 0, 0);

        // Вторая колонка
        VBox secondColumn = new VBox(10);
        Label label2 = new Label("HISTORY");
        label2.setFont(font);  // Применяем шрифт
        Button btn1 = new Button("Кнопка 1");
        btn1.setFont(font);  // Применяем шрифт
        Button btn2 = new Button("Кнопка 2");
        btn2.setFont(font);  // Применяем шрифт
        Button btn3 = new Button("Кнопка 3");
        btn3.setFont(font);  // Применяем шрифт

        secondColumn.getChildren().addAll(label2, btn1, btn2, btn3);
        grid.add(secondColumn, 1, 0);

        // Третья колонка
        VBox thirdColumn = new VBox(10);
        Label label3 = new Label("Заголовок 3");
        label3.setFont(font);  // Применяем шрифт
        thirdColumn.getChildren().add(label3);
        grid.add(thirdColumn, 2, 0);

        // Настройка ширины колонок
        grid.getColumnConstraints().addAll(
                new javafx.scene.layout.ColumnConstraints(218),
                new javafx.scene.layout.ColumnConstraints(218),
                new javafx.scene.layout.ColumnConstraints(436)
        );

        // Создаем сцену
        Scene scene = new Scene(grid, 872, 492);

        // Подключаем CSS для других стилей (например, фон, отступы и т.д.)
        scene.getStylesheets().add(getClass().getResource("/main.css").toExternalForm());

        // Настройка окна
        primaryStage.setTitle("JavaFX Приложение с тремя колонками");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);  // Запрещаем изменять размер окна
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);  // Запуск приложения
    }
}
