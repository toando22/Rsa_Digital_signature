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
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class DigitalSignatureApp_updatevr1_22_5 extends Application {

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

    private BigInteger n, d, e;
    private final SecureRandom random = new SecureRandom();

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
        Scene scene = new Scene(root, 950, 650);
        primaryStage.setScene(scene);
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
            
            // Tạo ImageView với xử lý lỗi
            ImageView icon = null;
            try {
                // Sửa đường dẫn ảnh - bỏ "/btl_anm/resources/"
                String resourcePath = iconPath.replace("/btl_anm/deepseek_working", "/");
                InputStream is = DigitalSignatureApp_updatevr1_22_5.class.getResourceAsStream(resourcePath);
                if (is != null) {
                    icon = new ImageView(new Image(is));
                    icon.setFitHeight(20);
                    icon.setFitWidth(20);
                } else {
                    System.err.println("Không tìm thấy ảnh: " + resourcePath);
                    // Tạo ảnh mặc định nếu không tìm thấy
                    icon = createDefaultIcon();
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi load ảnh: " + e.getMessage());
                icon = createDefaultIcon();
            }
            
            Label titleLabel = new Label(title);
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            titleLabel.setTextFill(Color.DARKBLUE);
            
            titleBox = new HBox(8);
            if (icon != null) {
                titleBox.getChildren().add(icon);
            }
            titleBox.getChildren().add(titleLabel);
            titleBox.setAlignment(Pos.CENTER_LEFT);
            titleBox.setPadding(new Insets(0, 10, 0, 10));
            
            // Tạo nội dung
            StackPane contentPane = new StackPane();
            contentPane.setStyle("-fx-border-color: #bdbdbd; -fx-border-radius: 5; -fx-border-width: 1;");
            contentPane.setPadding(new Insets(15, 15, 15, 15));
            
            getChildren().addAll(titleBox, contentPane);
            setSpacing(-5);
            setPadding(new Insets(0, 0, 15, 0));
        }
        
        private ImageView createDefaultIcon() {
            // Tạo ảnh mặc định với màu xám
            WritableImage defaultImage = new WritableImage(20, 20);
            PixelWriter writer = defaultImage.getPixelWriter();
            for (int y = 0; y < 20; y++) {
                for (int x = 0; x < 20; x++) {
                    writer.setArgb(x, y, 0xFFAAAAAA); // Màu xám
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
        
        // Chọn kích thước khóa
        HBox keySizeBox = new HBox(10);
        keySizeBox.setAlignment(Pos.CENTER_LEFT);
        Label keySizeLabel = new Label("Kích thước:");
        keySizeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        keySizeComboBox = new ComboBox<>();
        keySizeComboBox.getItems().addAll(256, 512, 1024, 2048);
        keySizeComboBox.setValue(256);
        keySizeComboBox.setStyle("-fx-background-color: white; -fx-border-color: #ccc;");
        
        keySizeBox.getChildren().addAll(keySizeLabel, keySizeComboBox);

        // Nút tạo khóa
        Button generateKeyButton = new Button("Tạo khóa");
        generateKeyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        generateKeyButton.setOnAction(e -> generateKeys());

        // Khóa bí mật
        Label privateKeyLabel = new Label("Private Key:");
        privateKeyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        privateKeyArea = new TextArea();
        privateKeyArea.setPrefRowCount(3);
        privateKeyArea.setWrapText(true);
        privateKeyArea.setStyle("-fx-control-inner-background: #fff8e1; -fx-border-color: #ffd54f;");

        // Khóa công khai
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
        
        // Chọn tài liệu
        HBox senderFileBox = new HBox(10);
        senderFileBox.setAlignment(Pos.CENTER_LEFT);
        
        Label senderFileLabel = new Label("Đầu vào:");
        senderFileLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        senderFileField = new TextField();
        senderFileField.setEditable(false);
        senderFileField.setStyle("-fx-background-color: white; -fx-border-color: #ccc;");
        
        Button senderFileButton = new Button("Chọn tài liệu");
        senderFileButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        senderFileButton.setOnAction(e -> {
			try {
				chooseFile(senderFileField, senderHashArea);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        
        senderFileBox.getChildren().addAll(senderFileLabel, senderFileField, senderFileButton);

        // Băm SHA-1
        Label senderHashLabel = new Label("Băm SHA-1 đầu vào:");
        senderHashLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        senderHashArea = new TextArea();
        senderHashArea.setPrefRowCount(2);
        senderHashArea.setWrapText(true);
        senderHashArea.setEditable(false);
        senderHashArea.setStyle("-fx-control-inner-background: #f5f5f5;");

        // Nút tạo chữ ký
        Button createSignatureButton = new Button("Tạo chữ ký");
        createSignatureButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");
        createSignatureButton.setOnAction(e -> createSignature());

        // Chữ ký
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
        
        // Chọn tài liệu
        HBox receiverFileBox = new HBox(10);
        receiverFileBox.setAlignment(Pos.CENTER_LEFT);
        
        Label receiverFileLabel = new Label("Đầu vào:");
        receiverFileLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        receiverFileField = new TextField();
        receiverFileField.setEditable(false);
        receiverFileField.setStyle("-fx-background-color: white; -fx-border-color: #ccc;");
        
        Button receiverFileButton = new Button("Chọn tài liệu");
        receiverFileButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        receiverFileButton.setOnAction(e -> {
			try {
				chooseFile(receiverFileField, receiverHashArea);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        
        receiverFileBox.getChildren().addAll(receiverFileLabel, receiverFileField, receiverFileButton);

        // Băm SHA-1
        Label receiverHashLabel = new Label("Băm SHA-1 đầu vào:");
        receiverHashLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        receiverHashArea = new TextArea();
        receiverHashArea.setPrefRowCount(2);
        receiverHashArea.setWrapText(true);
        receiverHashArea.setEditable(false);
        receiverHashArea.setStyle("-fx-control-inner-background: #f5f5f5;");

        // Nút kiểm tra
        Button verifyButton = new Button("Kiểm tra");
        verifyButton.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-weight: bold;");
        verifyButton.setOnAction(e -> verifySignature());

        // Kết quả kiểm tra
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
        // Nút reset
        Button resetButton = new Button("RESET");
        resetButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        resetButton.setOnAction(e -> resetFields());
        
        // Thanh tiến trình
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);
        progressBar.setStyle("-fx-accent: #4CAF50;");
        
        HBox bottomBox = new HBox(20, resetButton, progressBar);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));
        
        return bottomBox;
    }

    private HBox createFooterBox() {
        Label footerLabel = new Label("Bản quyền thuộc về Nguyễn Đăng Khẩm - D.110DPM+HYOBCVT");
        footerLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
        footerLabel.setTextFill(Color.GRAY);
        
        HBox footerBox = new HBox(footerLabel);
        footerBox.setAlignment(Pos.CENTER);
        footerBox.setPadding(new Insets(10, 0, 0, 0));
        
        return footerBox;
    }

    private void chooseFile(TextField fileField, TextArea hashArea) throws Exception {
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
                String hash = calculateSHA1(fileBytes);
                hashArea.setText(hash);
                progressBar.setProgress(1.0);
            } catch (IOException ex) {
                showAlert("Lỗi", "Không thể đọc file: " + ex.getMessage());
                progressBar.setProgress(0);
            }
        }
    }

    private void generateKeys() {
        int bitLength = keySizeComboBox.getValue();
        progressBar.setProgress(0.1);
        
        // Hiển thị cảnh báo với khóa nhỏ
        if (bitLength < 1024) {
            showAlert("Cảnh báo", "Khóa có kích thước nhỏ hơn 1024 bit có thể không an toàn!");
        }
        
        try {
            // Tạo 2 số nguyên tố lớn
            progressBar.setProgress(0.3);
            BigInteger p = BigInteger.probablePrime(bitLength / 2, random);
            BigInteger q = BigInteger.probablePrime(bitLength / 2, random);
            
            // Tính n và phi(n)
            progressBar.setProgress(0.5);
            n = p.multiply(q);
            BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
            
            // Chọn e (số nguyên tố cùng nhau với phi)
            progressBar.setProgress(0.7);
            e = BigInteger.probablePrime(bitLength / 4, random);
            while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0) {
                e = e.add(BigInteger.ONE);
            }
            
            // Tính d (nghịch đảo modulo của e)
            progressBar.setProgress(0.9);
            d = e.modInverse(phi);
            
            // Hiển thị khóa
            privateKeyArea.setText("n: " + n + "\nd: " + d);
            publicKeyArea.setText("n: " + n + "\ne: " + e);
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
            // Đọc file và tính băm
            byte[] fileBytes = Files.readAllBytes(new File(senderFileField.getText()).toPath());
            progressBar.setProgress(0.5);
            String hash = calculateSHA1(fileBytes);
            
            // Chuyển băm thành số nguyên lớn
            BigInteger hashInt = new BigInteger(1, hash.getBytes());
            
            // Ký số: signature = hash^d mod n
            progressBar.setProgress(0.7);
            BigInteger signature = hashInt.modPow(d, n);
            
            // Hiển thị chữ ký
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
            // Đọc file và tính băm
            byte[] fileBytes = Files.readAllBytes(new File(receiverFileField.getText()).toPath());
            progressBar.setProgress(0.4);
            String receivedHash = calculateSHA1(fileBytes);
            
            // Chuyển băm thành số nguyên lớn
            BigInteger receivedHashInt = new BigInteger(1, receivedHash.getBytes());
            
            // Đọc chữ ký
            BigInteger signature = new BigInteger(signatureArea.getText());
            
            // Giải mã chữ ký: decrypted = signature^e mod n
            progressBar.setProgress(0.7);
            BigInteger decrypted = signature.modPow(e, n);
            
            // So sánh
            progressBar.setProgress(0.9);
            if (decrypted.equals(receivedHashInt)) {
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

    private String calculateSHA1(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] hash = md.digest(data);
        
        // Chuyển thành chuỗi hex
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        
        return hexString.toString();
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

