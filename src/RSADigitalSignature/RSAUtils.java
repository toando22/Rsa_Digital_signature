package RSADigitalSignature;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class RSAUtils {
    private BigInteger n, d, e;
    private final SecureRandom random = new SecureRandom();

    public void generateKeys(int bitLength) {
        // Tạo 2 số nguyên tố lớn
        BigInteger p = BigInteger.probablePrime(bitLength / 2, random);
        BigInteger q = BigInteger.probablePrime(bitLength / 2, random);
        
        // Tính n và phi(n)
        n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        
        // Chọn e (số nguyên tố cùng nhau với phi)
        e = BigInteger.probablePrime(bitLength / 4, random);
        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0) {
            e = e.add(BigInteger.ONE);
        }
        
        // Tính d (nghịch đảo modulo của e)
        d = e.modInverse(phi);
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getD() {
        return d;
    }

    public BigInteger getE() {
        return e;
    }

    public void setKeys(BigInteger n, BigInteger d, BigInteger e) {
        this.n = n;
        this.d = d;
        this.e = e;
    }

    public BigInteger sign(byte[] data) throws Exception {
        String hash = calculateSHA1(data);
        BigInteger hashInt = new BigInteger(1, hash.getBytes());
        return hashInt.modPow(d, n);
    }

    public boolean verify(byte[] data, BigInteger signature) throws Exception {
        String receivedHash = calculateSHA1(data);
        BigInteger receivedHashInt = new BigInteger(1, receivedHash.getBytes());
        BigInteger decrypted = signature.modPow(e, n);
        return decrypted.equals(receivedHashInt);
    }

    public static String calculateSHA1(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] hash = md.digest(data);
        
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        
        return hexString.toString();
    }
}