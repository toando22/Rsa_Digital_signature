package RSADigitalSignature;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class RSAUtils_1 {
    private BigInteger n, d, e;
    private final SecureRandom random = new SecureRandom();

    public void generateKeys(int bitLength) {
        BigInteger p = BigInteger.probablePrime(bitLength / 2, random);
        BigInteger q = BigInteger.probablePrime(bitLength / 2, random);
        
        n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        
        e = BigInteger.probablePrime(bitLength / 4, random);
        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0) {
            e = e.add(BigInteger.ONE);
        }
        
        d = e.modInverse(phi);
    }

    public BigInteger sign(byte[] data) throws Exception {
        byte[] hash = calculateSHA1Bytes(data);
        BigInteger hashInt = new BigInteger(1, hash);
        return hashInt.modPow(d, n);
    }

    public boolean verify(byte[] data, BigInteger signature) throws Exception {
        byte[] receivedHash = calculateSHA1Bytes(data);
        BigInteger receivedHashInt = new BigInteger(1, receivedHash);
        BigInteger decrypted = signature.modPow(e, n);
        return decrypted.equals(receivedHashInt);
    }

    public static String calculateSHA1(byte[] data) throws Exception {
        byte[] hash = calculateSHA1Bytes(data);
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static byte[] calculateSHA1Bytes(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        return md.digest(data);
    }

    public BigInteger getN() { return n; }
    public BigInteger getD() { return d; }
    public BigInteger getE() { return e; }
}