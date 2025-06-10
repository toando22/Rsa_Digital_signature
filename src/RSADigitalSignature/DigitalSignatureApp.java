package RSADigitalSignature;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;

public class DigitalSignatureApp extends Application {

    private ComboBox<Integer> keySizeComboBox;
    private TextArea privateKeyArea;
    private TextArea publicKeyArea;
    private TextField senderFileField;
    private TextField receiverFileField;
    private TextArea senderHashArea;
    private TextArea receiverHashArea;
    private TextArea signatureArea;
    private TextField verificationResultField;
    private ProgressBar progressBar;
    
    private final RSAUtils rsa = new RSAUtils();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Digital Signature Software - Enhanced");

        // Tạo layout chính
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Phần tiêu đề
        HBox titleBox = createTitleBox();
        root.setTop(titleBox);

        // Phần nội dung chính
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(15));

        // Phần khóa
        GroupBox keyGroup = createKeyGroup();
        
        // Phần người gửi
        GroupBox senderGroup = createSenderGroup();
        
        // Phần người nhận
        GroupBox receiverGroup = createReceiverGroup();
        
        // Nút reset và thanh tiến trình
        HBox bottomBox = createBottomBox();
        
        // Footer
        HBox footerBox = createFooterBox();

        mainContent.getChildren().addAll(keyGroup, senderGroup, receiverGroup, bottomBox, footerBox);
        root.setCenter(mainContent);

        // Hiển thị cửa sổ
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);  // Thiết lập kích thước tối thiểu
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }

    private HBox createTitleBox() {
        Label titleLabel = new Label("DIGITAL SIGNATURE SOFTWARE");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKBLUE);
        
        Label principleLabel = new Label("Bản quyền thuộc về Đỗ Duy Toàn");
        principleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        principleLabel.setTextFill(Color.GRAY);
        
        VBox titleText = new VBox(titleLabel, principleLabel);
        titleText.setAlignment(Pos.CENTER);
        
        HBox titleBox = new HBox(titleText);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(10));
        titleBox.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
        
        return titleBox;
    }
    
    private static class GroupBox extends VBox {
        private final HBox titleBox;
        
        public GroupBox(String title, String iconPath) {
            super();
            
            ImageView icon = createDefaultIcon();
            if (iconPath != null && !iconPath.isEmpty()) {
                try {
                    InputStream is = DigitalSignatureApp.class.getResourceAsStream(iconPath);
                    if (is != null) {
                        icon = new ImageView(new Image(is));
                        icon.setFitHeight(20);
                        icon.setFitWidth(20);
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi khi load ảnh: " + e.getMessage());
                }
            }
            
            Label titleLabel = new Label(title);
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            titleLabel.setTextFill(Color.DARKBLUE);
            
            titleBox = new HBox(8);
            titleBox.getChildren().add(icon);
            titleBox.getChildren().add(titleLabel);
            titleBox.setAlignment(Pos.CENTER_LEFT);
            titleBox.setPadding(new Insets(0, 10, 0, 10));
            
            StackPane contentPane = new StackPane();
            contentPane.setStyle("-fx-border-color: #bdbdbd; -fx-border-radius: 5; -fx-border-width: 1;");
            contentPane.setPadding(new Insets(15, 15, 15, 15));
            
            getChildren().addAll(titleBox, contentPane);
            setSpacing(-5);
            setPadding(new Insets(0, 0, 15, 0));
        }
        
        private ImageView createDefaultIcon() {
            WritableImage defaultImage = new WritableImage(20, 20);
            PixelWriter writer = defaultImage.getPixelWriter();
            for (int y = 0; y < 20; y++) {
                for (int x = 0; x < 20; x++) {
                    writer.setArgb(x, y, 0xFFAAAAAA);
                }
            }
            ImageView defaultIcon = new ImageView(defaultImage);
            defaultIcon.setFitHeight(20);
            defaultIcon.setFitWidth(20);
            return defaultIcon;
        }
        
        public void setContent(javafx.scene.Node content) {
            ((StackPane)getChildren().get(1)).getChildren().add(content);
        }
    }
    
    private GroupBox createKeyGroup() {
        GroupBox keyGroup = new GroupBox("KHÓA", "/khoa.png.jpg");
        
        HBox keySizeBox = new HBox(10);
        keySizeBox.setAlignment(Pos.CENTER_LEFT);
        Label keySizeLabel = new Label("Kích thước:");
        keySizeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        keySizeComboBox = new ComboBox<>();
        keySizeComboBox.getItems().addAll(256, 512, 1024, 2048);
        keySizeComboBox.setValue(256);
        keySizeComboBox.setStyle("-fx-background-color: white; -fx-border-color: #ccc;");
        
        keySizeBox.getChildren().addAll(keySizeLabel, keySizeComboBox);

        Button generateKeyButton = new Button("Tạo khóa");
        generateKeyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        generateKeyButton.setOnAction(e -> generateKeys());

        Label privateKeyLabel = new Label("Private Key:");
        privateKeyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        privateKeyArea = new TextArea();
        privateKeyArea.setPrefRowCount(3);
        privateKeyArea.setWrapText(true);
        privateKeyArea.setStyle("-fx-control-inner-background: #fff8e1; -fx-border-color: #ffd54f;");

        Label publicKeyLabel = new Label("Public Key:");
        publicKeyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        publicKeyArea = new TextArea();
        publicKeyArea.setPrefRowCount(3);
        publicKeyArea.setWrapText(true);
        publicKeyArea.setStyle("-fx-control-inner-background: #e8f5e9; -fx-border-color: #81c784;");

        VBox keyContent = new VBox(10);
        keyContent.setPadding(new Insets(15));
        keyContent.getChildren().addAll(keySizeBox, generateKeyButton, privateKeyLabel, privateKeyArea, publicKeyLabel, publicKeyArea);
        
        keyGroup.setContent(keyContent);
        return keyGroup;
    }

    private GroupBox createSenderGroup() {
        GroupBox senderGroup = new GroupBox("NGƯỜI GỬI", "/guitin.png.jpg");
        
        HBox senderFileBox = new HBox(10);
        senderFileBox.setAlignment(Pos.CENTER_LEFT);
        
        Label senderFileLabel = new Label("Đầu vào:");
        senderFileLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        senderFileField = new TextField();
        senderFileField.setEditable(false);
        senderFileField.setStyle("-fx-background-color: white; -fx-border-color: #ccc;");
        
        Button senderFileButton = new Button("Chọn tài liệu");
        senderFileButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        senderFileButton.setOnAction(e -> chooseFile(senderFileField, senderHashArea));
        
        senderFileBox.getChildren().addAll(senderFileLabel, senderFileField, senderFileButton);

        Label senderHashLabel = new Label("Băm SHA-1 đầu vào:");
        senderHashLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        senderHashArea = new TextArea();
        senderHashArea.setPrefRowCount(2);
        senderHashArea.setWrapText(true);
        senderHashArea.setEditable(false);
        senderHashArea.setStyle("-fx-control-inner-background: #f5f5f5;");

        Button createSignatureButton = new Button("Tạo chữ ký");
        createSignatureButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");
        createSignatureButton.setOnAction(e -> createSignature());

        Label signatureLabel = new Label("Chữ ký được tạo:");
        signatureLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        signatureArea = new TextArea();
        signatureArea.setPrefRowCount(4);
        signatureArea.setWrapText(true);
        signatureArea.setStyle("-fx-control-inner-background: #fff3e0; -fx-border-color: #ffb74d;");

        VBox senderContent = new VBox(10);
        senderContent.setPadding(new Insets(15));
        senderContent.getChildren().addAll(senderFileBox, senderHashLabel, senderHashArea, 
                                          createSignatureButton, signatureLabel, signatureArea);
        
        senderGroup.setContent(senderContent);
        return senderGroup;
    }

    private GroupBox createReceiverGroup() {
        GroupBox receiverGroup = new GroupBox("NGƯỜI NHẬN", "/nhantin.png");
        
        HBox receiverFileBox = new HBox(10);
        receiverFileBox.setAlignment(Pos.CENTER_LEFT);
        
        Label receiverFileLabel = new Label("Đầu vào:");
        receiverFileLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        receiverFileField = new TextField();
        receiverFileField.setEditable(false);
        receiverFileField.setStyle("-fx-background-color: white; -fx-border-color: #ccc;");
        
        Button receiverFileButton = new Button("Chọn tài liệu");
        receiverFileButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        receiverFileButton.setOnAction(e -> chooseFile(receiverFileField, receiverHashArea));
        
        receiverFileBox.getChildren().addAll(receiverFileLabel, receiverFileField, receiverFileButton);

        Label receiverHashLabel = new Label("Băm SHA-1 đầu vào:");
        receiverHashLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        receiverHashArea = new TextArea();
        receiverHashArea.setPrefRowCount(2);
        receiverHashArea.setWrapText(true);
        receiverHashArea.setEditable(false);
        receiverHashArea.setStyle("-fx-control-inner-background: #f5f5f5;");

        Button verifyButton = new Button("Kiểm tra");
        verifyButton.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-weight: bold;");
        verifyButton.setOnAction(e -> verifySignature());

        Label verificationResultLabel = new Label("Kết quả kiểm tra:");
        verificationResultLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        verificationResultField = new TextField();
        verificationResultField.setEditable(false);
        verificationResultField.setStyle("-fx-font-weight: bold;");

        VBox receiverContent = new VBox(10);
        receiverContent.setPadding(new Insets(15));
        receiverContent.getChildren().addAll(receiverFileBox, receiverHashLabel, receiverHashArea, 
                                            verifyButton, verificationResultLabel, verificationResultField);
        
        receiverGroup.setContent(receiverContent);
        return receiverGroup;
    }

    private HBox createBottomBox() {
        Button resetButton = new Button("RESET");
        resetButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        resetButton.setOnAction(e -> resetFields());
        
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);//default 200
        progressBar.setStyle("-fx-accent: #4CAF50;");
        
        HBox bottomBox = new HBox(20, resetButton, progressBar);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));
        
        return bottomBox;
    }

    private HBox createFooterBox() {
        Label footerLabel = new Label("Bản quyền thuộc về Đỗ Duy Toàn - CNTT02_K17__HAUI");
        footerLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
        footerLabel.setTextFill(Color.GRAY);
        
        HBox footerBox = new HBox(footerLabel);
        footerBox.setAlignment(Pos.CENTER);
        footerBox.setPadding(new Insets(10, 0, 0, 0));
        
        return footerBox;
    }

    private void chooseFile(TextField fileField, TextArea hashArea) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn tài liệu");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Tất cả files", "*.*"),
            new FileChooser.ExtensionFilter("Tài liệu", "*.docx", "*.pdf", "*.txt"),
            new FileChooser.ExtensionFilter("Ảnh", "*.jpg", "*.png", "*.jpeg")
        );
        
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            progressBar.setProgress(0.3);
            fileField.setText(selectedFile.getAbsolutePath());
            
            try {
                byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());
                progressBar.setProgress(0.6);
                String hash = RSAUtils.calculateSHA1(fileBytes);
                hashArea.setText(hash);
                progressBar.setProgress(1.0);
            } catch (Exception ex) {
                showAlert("Lỗi", "Không thể đọc file: " + ex.getMessage());
                progressBar.setProgress(0);
            }
        }
    }

    private void generateKeys() {
        int bitLength = keySizeComboBox.getValue();
        progressBar.setProgress(0.1);
        
        if (bitLength < 1024) {
            showAlert("Cảnh báo", "Khóa có kích thước nhỏ hơn 1024 bit có thể không an toàn!");
        }
        
        try {
            progressBar.setProgress(0.3);
            rsa.generateKeys(bitLength);
            
            progressBar.setProgress(0.9);
            privateKeyArea.setText("n: " + rsa.getN() + "\nd: " + rsa.getD());
            publicKeyArea.setText("n: " + rsa.getN() + "\ne: " + rsa.getE());
            progressBar.setProgress(1.0);
            
            showAlert("Thành công", "Đã tạo khóa thành công với kích thước " + bitLength + " bit");
        } catch (Exception ex) {
            showAlert("Lỗi", "Không thể tạo khóa: " + ex.getMessage());
            progressBar.setProgress(0);
        }
    }

    private void createSignature() {
        if (senderFileField.getText().isEmpty()) {
            showAlert("Lỗi", "Vui lòng chọn tài liệu để ký");
            return;
        }
        
        if (privateKeyArea.getText().isEmpty()) {
            showAlert("Lỗi", "Vui lòng tạo khóa trước");
            return;
        }
        
        progressBar.setProgress(0.2);
        
        try {
            byte[] fileBytes = Files.readAllBytes(new File(senderFileField.getText()).toPath());
            progressBar.setProgress(0.5);
            
            BigInteger signature = rsa.sign(fileBytes);
            signatureArea.setText(signature.toString());
            progressBar.setProgress(1.0);
            
            showAlert("Thành công", "Đã tạo chữ ký số thành công!");
        } catch (Exception ex) {
            showAlert("Lỗi", "Không thể tạo chữ ký: " + ex.getMessage());
            progressBar.setProgress(0);
        }
    }

    private void verifySignature() {
        if (receiverFileField.getText().isEmpty()) {
            showAlert("Lỗi", "Vui lòng chọn tài liệu để kiểm tra");
            return;
        }
        
        if (signatureArea.getText().isEmpty()) {
            showAlert("Lỗi", "Không có chữ ký để kiểm tra");
            return;
        }
        
        if (publicKeyArea.getText().isEmpty()) {
            showAlert("Lỗi", "Không có khóa công khai để kiểm tra");
            return;
        }
        
        progressBar.setProgress(0.2);
        
        try {
            byte[] fileBytes = Files.readAllBytes(new File(receiverFileField.getText()).toPath());
            progressBar.setProgress(0.4);
            
            BigInteger signature = new BigInteger(signatureArea.getText());
            boolean isValid = rsa.verify(fileBytes, signature);
            
            progressBar.setProgress(0.9);
            if (isValid) {
                verificationResultField.setText("Hợp lệ - Tài liệu toàn vẹn");
                verificationResultField.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            } else {
                verificationResultField.setText("Không hợp lệ - Tài liệu đã bị thay đổi");
                verificationResultField.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
            progressBar.setProgress(1.0);
        } catch (Exception ex) {
            showAlert("Lỗi", "Không thể kiểm tra chữ ký: " + ex.getMessage());
            progressBar.setProgress(0);
        }
    }

    private void resetFields() {
        privateKeyArea.clear();
        publicKeyArea.clear();
        senderFileField.clear();
        receiverFileField.clear();
        senderHashArea.clear();
        receiverHashArea.clear();
        signatureArea.clear();
        verificationResultField.clear();
        verificationResultField.setStyle("");
        progressBar.setProgress(0);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}