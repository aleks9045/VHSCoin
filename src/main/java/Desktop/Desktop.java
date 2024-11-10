package Desktop;

import javafx.application.*;
import javafx.beans.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;

public class Desktop extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Загружаем шрифт прямо в Java
        Font font = Font.loadFont(getClass().getResource("/vhs.ttf").toExternalForm(), 24);  // Устанавливаем размер шрифта 24px

        // Создаем layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);

        // Устанавливаем фон для grid (всего окна)
        grid.setStyle("-fx-background-color: #1B201C;");  // Устанавливаем цвет фона для grid

        // Первая колонка
        VBox firstColumn = new VBox();
        firstColumn.setSpacing(20);  // Отступы между элементами в колонке
        firstColumn.setPadding(new Insets(20));  // Добавляем отступы внутри колонки
        VBox.setVgrow(firstColumn, Priority.ALWAYS);  // Растягиваем колонку по высоте

        // Создаем блок для заголовка и добавляем в него стилизованный label
        VBox headerBlock = new VBox(10);
        Label label1 = createStyledLabel("HISTORY", font, true);
        headerBlock.getChildren().add(label1);

        // Добавляем блок с обводкой в первую колонку
        VBox borderedHeaderBlock = createBorderedBlock(headerBlock.getChildren());
        firstColumn.getChildren().add(borderedHeaderBlock);

        // Добавляем первую колонку в сетку
        grid.add(firstColumn, 0, 0);

        // Вторая колонка
        VBox secondColumn = new VBox();
        secondColumn.setSpacing(20);  // Отступы между элементами
        secondColumn.setPadding(new Insets(20));  // Добавляем отступы внутри колонки
        VBox.setVgrow(secondColumn, Priority.ALWAYS);  // Растягиваем колонку по высоте
        grid.add(secondColumn, 1, 0);

        // Третья колонка
        VBox thirdColumn = new VBox();
        thirdColumn.setSpacing(20);  // Отступы между элементами
        thirdColumn.setPadding(new Insets(20));  // Добавляем отступы внутри колонки
        VBox.setVgrow(thirdColumn, Priority.ALWAYS);  // Растягиваем колонку по высоте

        // Создаем блок для заголовка третьей колонки и добавляем в него стилизованный label
        Label label3 = createStyledLabel("MINER", font, true);
        thirdColumn.getChildren().add(label3);

        // Добавляем блок с обводкой в третью колонку
        VBox borderedThirdColumn = createBorderedBlock(thirdColumn.getChildren());
        thirdColumn.getChildren().clear();  // Очистим старые элементы, чтобы добавить только обернутые
        thirdColumn.getChildren().add(borderedThirdColumn);

        // Добавляем третью колонку в сетку
        grid.add(thirdColumn, 2, 0);

        // Настройка ширины колонок
        grid.getColumnConstraints().addAll(
                new javafx.scene.layout.ColumnConstraints() {{
                    setPercentWidth(25); // Первая колонка займет 25% ширины
                }},
                new javafx.scene.layout.ColumnConstraints() {{
                    setPercentWidth(25); // Вторая колонка займет 25% ширины
                }},
                new javafx.scene.layout.ColumnConstraints() {{
                    setPercentWidth(50); // Третья колонка займет 50% ширины
                }}
        );

        // Создаем сцену
        Scene scene = new Scene(grid, 872, 492);

        // Настройка окна
        primaryStage.setTitle("JavaFX Приложение с тремя колонками");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);  // Запрещаем изменять размер окна
        primaryStage.show();
    }

    // Функция для создания стилизованного текста
    private Label createStyledLabel(String text, Font font, boolean glowing) {
        Label label = new Label(text);
        label.setFont(font);  // Применяем шрифт
        label.setTextFill(Color.WHITE);  // Устанавливаем белый цвет текста

        // Если glowing == true, добавляем эффект свечения
        if (glowing) {
            DropShadow dropShadow = new DropShadow();
            dropShadow.setColor(Color.rgb(255, 255, 255, 0.5));  // Белый цвет с 50% прозрачностью
            dropShadow.setRadius(50);  // Радиус свечения
            dropShadow.setOffsetX(0);
            dropShadow.setOffsetY(0);
            label.setEffect(dropShadow);  // Применяем эффект
        }

        return label;
    }

    // Функция для создания блока с обводкой
    private VBox createBorderedBlock(ObservableList<Node> children) {
        VBox borderedBlock = new VBox(20);  // Устанавливаем отступ 20px между элементами
        borderedBlock.setPadding(new Insets(20));  // Устанавливаем отступы внутри блока

        // Устанавливаем обводку для блока
        borderedBlock.setBorder(new Border(
                new BorderStroke(
                        Color.rgb(235, 255, 242, 0.1),  // Цвет обводки
                        BorderStrokeStyle.DASHED,  // Стиль обводки (пунктирная линия)
                        new CornerRadii(2),  // Радиусы углов (плоские)
                        BorderWidths.DEFAULT)  // Ширина обводки
        ));

        // Добавляем дочерние элементы в контейнер
        borderedBlock.getChildren().addAll(children);

        return borderedBlock;  // Возвращаем готовый контейнер
    }

    public static void main(String[] args) {
        launch(args);  // Запуск приложения
    }
}
