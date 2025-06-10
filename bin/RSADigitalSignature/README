--------------RSA Algorithm in Cryptography--------------
// Triển khai giải thuật theo mã nguồn
Đoạn mã Java bạn cung cấp triển khai thuật toán RSA một cách chính xác, tuân theo các bước chuẩn:

Tạo khóa: Chọn hai số nguyên tố p và q, tính n = p * q và φ(n) = (p - 1) * (q - 1).

Chọn số mũ công khai e: Tìm một số nguyên e sao cho 1 < e < φ(n) và gcd(e, φ(n)) = 1.

Tính số mũ bí mật d: Tính d là nghịch đảo modular của e modulo φ(n), tức là d ≡ e⁻¹ mod φ(n).

Mã hóa và giải mã: Sử dụng modPow để thực hiện phép lũy thừa modulo cho việc mã hóa và giải mã.

Tuy nhiên, để tăng cường hiệu suất và bảo mật, bạn có thể xem xét các cải tiến sau:

Chọn số mũ công khai e một cách hiệu quả: Thay vì tìm e bằng cách lặp từ 2 đến φ(n), bạn có thể chọn một giá trị chuẩn như 65537,
 là số nguyên tố và thường được sử dụng trong thực tế do tính hiệu quả và bảo mật của nó.

Sử dụng số nguyên tố lớn hơn: Các giá trị p và q trong mã hiện tại là 7919 và 1009, tương đối nhỏ.
 Để đảm bảo bảo mật trong thực tế, nên sử dụng các số nguyên tố lớn hơn, thường là 1024 bit hoặc hơn.

Xử lý thông điệp lớn: Hiện tại, mã chỉ xử lý các thông điệp nhỏ. Để xử lý các thông điệp lớn hơn,
 bạn cần chia nhỏ thông điệp thành các khối có kích thước phù hợp với n.


// Chương trình Java triển khai thuật toán RSA

import java.math.BigInteger;

public class RSAExample {

    // Hàm tính lũy thừa modulo: base^expo mod m
    static BigInteger power(BigInteger base, BigInteger expo, BigInteger m) {
        return base.modPow(expo, m);
    }

    // Hàm tìm nghịch đảo modular của e modulo phi(n)
    static BigInteger modInverse(BigInteger e, BigInteger phi) {
        return e.modInverse(phi);
    }

    // Tạo khóa RSA
    static void generateKeys(BigInteger[] keys) {
        // Chọn hai số nguyên tố p và q
        BigInteger p = new BigInteger("7919");
        BigInteger q = new BigInteger("1009");

        // Tính n = p * q
        BigInteger n = p.multiply(q);

        // Tính phi(n) = (p - 1) * (q - 1)
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        // Chọn e sao cho 1 < e < phi(n) và gcd(e, phi(n)) = 1
        BigInteger e = new BigInteger("65537"); // Giá trị chuẩn thường dùng

        // Kiểm tra nếu e và phi không nguyên tố cùng nhau, chọn e khác
        while (!e.gcd(phi).equals(BigInteger.ONE)) {
            e = e.add(BigInteger.TWO);
        }

        // Tính d là nghịch đảo modular của e modulo phi(n)
        BigInteger d = modInverse(e, phi);

        // Lưu các khóa vào mảng
        keys[0] = e;  // Khóa công khai (e)
        keys[1] = d;  // Khóa bí mật (d)
        keys[2] = n;  // Modulus (n)
    }

    // Mã hóa thông điệp sử dụng khóa công khai (e, n)
    static BigInteger encrypt(BigInteger m, BigInteger e, BigInteger n) {
        return power(m, e, n);
    }

    // Giải mã thông điệp sử dụng khóa bí mật (d, n)
    static BigInteger decrypt(BigInteger c, BigInteger d, BigInteger n) {
        return power(c, d, n);
    }

    public static void main(String[] args) {
        BigInteger[] keys = new BigInteger[3]; // Mảng chứa e, d, n

        // Tạo khóa
        generateKeys(keys);

        System.out.println("Khóa công khai (e, n): (" + keys[0] + ", " + keys[2] + ")");
        System.out.println("Khóa bí mật (d, n): (" + keys[1] + ", " + keys[2] + ")");

        // Thông điệp cần mã hóa
        BigInteger M = new BigInteger("123");
        System.out.println("Thông điệp ban đầu: " + M);

        // Mã hóa thông điệp
        BigInteger C = encrypt(M, keys[0], keys[2]);
        System.out.println("Thông điệp đã mã hóa: " + C);

        // Giải mã thông điệp
        BigInteger decrypted = decrypt(C, keys[1], keys[2]);
        System.out.println("Thông điệp sau giải mã: " + decrypted);
    }
}
