package com.amplitude.security;

import java.security.DigestException;
import java.security.MessageDigest;

public final class MD5 extends MessageDigest implements Cloneable {
    private long bytes;
    private int hA;
    private int hB;
    private int hC;
    private int hD;
    private byte[] pad = new byte[64];
    private int padded;

    public MD5() {
        super("MD5");
        init();
    }

    public Object clone() throws CloneNotSupportedException {
        MD5 that = (MD5) super.clone();
        that.pad = (byte[]) this.pad.clone();
        return that;
    }

    public void engineReset() {
        this.padded = 0;
        this.bytes = 0;
        int i = 60;
        byte[] buf = this.pad;
        do {
            buf[i - 4] = (byte) 0;
            buf[i - 3] = (byte) 0;
            buf[i - 2] = (byte) 0;
            buf[i - 1] = (byte) 0;
            buf[i] = (byte) 0;
            buf[i + 1] = (byte) 0;
            buf[i + 2] = (byte) 0;
            buf[i + 3] = (byte) 0;
            i -= 8;
        } while (i >= 0);
        init();
    }

    protected void init() {
        this.hA = 1732584193;
        this.hB = -271733879;
        this.hC = -1732584194;
        this.hD = 271733878;
    }

    public void engineUpdate(byte input) {
        this.bytes++;
        if (this.padded < 63) {
            byte[] bArr = this.pad;
            int i = this.padded;
            this.padded = i + 1;
            bArr[i] = input;
            return;
        }
        this.pad[63] = input;
        engineUpdate(this.pad, this.padded);
        this.padded = 0;
    }

    public void engineUpdate(byte[] input, int offset, int length) {
        if (offset < 0 || length < 0 || offset + length > input.length) {
            throw new ArrayIndexOutOfBoundsException(offset);
        }
        this.bytes += (long) length;
        if (this.padded > 0 && this.padded + length >= 64) {
            int remaining = 64 - this.padded;
            System.arraycopy(input, offset, this.pad, this.padded, remaining);
            byte[] bArr = this.pad;
            this.padded = 0;
            engineUpdate(bArr, 0);
            offset += remaining;
            length -= remaining;
        }
        while (length >= 512) {
            engineUpdate(input, offset);
            engineUpdate(input, offset + 64);
            engineUpdate(input, offset + 128);
            engineUpdate(input, offset + 192);
            engineUpdate(input, offset + 256);
            engineUpdate(input, offset + 320);
            engineUpdate(input, offset + 384);
            engineUpdate(input, offset + 448);
            offset += 512;
            length -= 512;
        }
        while (length >= 64) {
            engineUpdate(input, offset);
            offset += 64;
            length -= 64;
        }
        if (length > 0) {
            System.arraycopy(input, offset, this.pad, this.padded, length);
            this.padded += length;
        }
    }

    public byte[] engineDigest() {
        try {
            byte[] hashvalue = new byte[16];
            engineDigest(hashvalue, 0, 16);
            return hashvalue;
        } catch (DigestException e) {
            return null;
        }
    }

    public int engineGetDigestLength() {
        return 16;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int engineDigest(byte[] hashvalue, int offset, int length) throws DigestException {
        if (length < 16) {
            throw new DigestException("partial digests not returned");
        } else if (hashvalue.length - offset >= 16) {
            byte[] buf = this.pad;
            int i = this.padded;
            buf[i] = Byte.MIN_VALUE;
            switch (i) {
                case 56:
                    buf[57] = (byte) 0;
                    break;
                case 57:
                    break;
                case 58:
                    break;
                case 59:
                    break;
                case 60:
                    break;
                case 61:
                    break;
                case 62:
                    break;
                case 63:
                    break;
            }
        } else {
            throw new DigestException("insufficient space in output buffer to store the digest");
        }
    }

    private final void engineUpdate(byte[] input, int offset) {
        int i = this.hB;
        int c = this.hC;
        int d = this.hD;
        int i0 = (((input[offset] & 255) | ((input[offset + 1] & 255) << 8)) | ((input[offset + 2] & 255) << 16)) | (input[offset + 3] << 24);
        int a = (((((c ^ d) & i) ^ d) + i0) - 680876936) + this.hA;
        a = i + ((a << 7) | (a >>> 25));
        offset += 10;
        int i1 = (((input[offset + 4] & 255) | ((input[offset + 5] & 255) << 8)) | ((input[offset - 4] & 255) << 16)) | (input[offset - 3] << 24);
        d += ((((i ^ c) & a) ^ c) + i1) - 389564586;
        d = a + ((d << 12) | (d >>> 20));
        int i2 = (((input[offset - 2] & 255) | ((input[offset - 1] & 255) << 8)) | ((input[offset] & 255) << 16)) | (input[offset + 1] << 24);
        c += ((((a ^ i) & d) ^ i) + i2) + 606105819;
        c = d + ((c << 17) | (c >>> 15));
        int i3 = (((input[offset + 2] & 255) | ((input[offset + 3] & 255) << 8)) | ((input[offset + 4] & 255) << 16)) | (input[offset + 5] << 24);
        int b = (((((d ^ a) & c) ^ a) + i3) - 1044525330) + i;
        b = c + ((b << 22) | (b >>> 10));
        offset += 10;
        int i4 = (((input[offset - 4] & 255) | ((input[offset - 3] & 255) << 8)) | ((input[offset - 2] & 255) << 16)) | (input[offset - 1] << 24);
        a += ((((c ^ d) & b) ^ d) + i4) - 176418897;
        a = b + ((a << 7) | (a >>> 25));
        int i5 = (((input[offset] & 255) | ((input[offset + 1] & 255) << 8)) | ((input[offset + 2] & 255) << 16)) | (input[offset + 3] << 24);
        d += ((((b ^ c) & a) ^ c) + i5) + 1200080426;
        d = a + ((d << 12) | (d >>> 20));
        offset += 10;
        int i6 = (((input[offset + 4] & 255) | ((input[offset + 5] & 255) << 8)) | ((input[offset - 4] & 255) << 16)) | (input[offset - 3] << 24);
        c += ((((a ^ b) & d) ^ b) + i6) - 1473231341;
        c = d + ((c << 17) | (c >>> 15));
        int i7 = (((input[offset - 2] & 255) | ((input[offset - 1] & 255) << 8)) | ((input[offset] & 255) << 16)) | (input[offset + 1] << 24);
        b += ((((d ^ a) & c) ^ a) + i7) - 45705983;
        b = c + ((b << 22) | (b >>> 10));
        int i8 = (((input[offset + 2] & 255) | ((input[offset + 3] & 255) << 8)) | ((input[offset + 4] & 255) << 16)) | (input[offset + 5] << 24);
        a += ((((c ^ d) & b) ^ d) + i8) + 1770035416;
        a = b + ((a << 7) | (a >>> 25));
        offset += 10;
        int i9 = (((input[offset - 4] & 255) | ((input[offset - 3] & 255) << 8)) | ((input[offset - 2] & 255) << 16)) | (input[offset - 1] << 24);
        d += ((((b ^ c) & a) ^ c) + i9) - 1958414417;
        d = a + ((d << 12) | (d >>> 20));
        int iA = (((input[offset] & 255) | ((input[offset + 1] & 255) << 8)) | ((input[offset + 2] & 255) << 16)) | (input[offset + 3] << 24);
        c += ((((a ^ b) & d) ^ b) + iA) - 42063;
        c = d + ((c << 17) | (c >>> 15));
        offset += 10;
        int iB = (((input[offset + 4] & 255) | ((input[offset + 5] & 255) << 8)) | ((input[offset - 4] & 255) << 16)) | (input[offset - 3] << 24);
        b += ((((d ^ a) & c) ^ a) + iB) - 1990404162;
        b = c + ((b << 22) | (b >>> 10));
        int iC = (((input[offset - 2] & 255) | ((input[offset - 1] & 255) << 8)) | ((input[offset] & 255) << 16)) | (input[offset + 1] << 24);
        a += ((((c ^ d) & b) ^ d) + iC) + 1804603682;
        a = b + ((a << 7) | (a >>> 25));
        int iD = (((input[offset + 2] & 255) | ((input[offset + 3] & 255) << 8)) | ((input[offset + 4] & 255) << 16)) | (input[offset + 5] << 24);
        d += ((((b ^ c) & a) ^ c) + iD) - 40341101;
        d = a + ((d << 12) | (d >>> 20));
        offset += 10;
        int iE = (((input[offset - 4] & 255) | ((input[offset - 3] & 255) << 8)) | ((input[offset - 2] & 255) << 16)) | (input[offset - 1] << 24);
        c += ((((a ^ b) & d) ^ b) + iE) - 1502002290;
        c = d + ((c << 17) | (c >>> 15));
        int iF = (((input[offset] & 255) | ((input[offset + 1] & 255) << 8)) | ((input[offset + 2] & 255) << 16)) | (input[offset + 3] << 24);
        b += ((((d ^ a) & c) ^ a) + iF) + 1236535329;
        b = c + ((b << 22) | (b >>> 10));
        a += ((((c ^ b) & d) ^ c) + i1) - 165796510;
        a = b + ((a << 5) | (a >>> 27));
        d += ((((b ^ a) & c) ^ b) + i6) - 1069501632;
        d = a + ((d << 9) | (d >>> 23));
        c += ((((a ^ d) & b) ^ a) + iB) + 643717713;
        c = d + ((c << 14) | (c >>> 18));
        b += ((((d ^ c) & a) ^ d) + i0) - 373897302;
        b = c + ((b << 20) | (b >>> 12));
        a += ((((c ^ b) & d) ^ c) + i5) - 701558691;
        a = b + ((a << 5) | (a >>> 27));
        d += ((((b ^ a) & c) ^ b) + iA) + 38016083;
        d = a + ((d << 9) | (d >>> 23));
        c += ((((a ^ d) & b) ^ a) + iF) - 660478335;
        c = d + ((c << 14) | (c >>> 18));
        b += ((((d ^ c) & a) ^ d) + i4) - 405537848;
        b = c + ((b << 20) | (b >>> 12));
        a += ((((c ^ b) & d) ^ c) + i9) + 568446438;
        a = b + ((a << 5) | (a >>> 27));
        d += ((((b ^ a) & c) ^ b) + iE) - 1019803690;
        d = a + ((d << 9) | (d >>> 23));
        c += ((((a ^ d) & b) ^ a) + i3) - 187363961;
        c = d + ((c << 14) | (c >>> 18));
        b += ((((d ^ c) & a) ^ d) + i8) + 1163531501;
        b = c + ((b << 20) | (b >>> 12));
        a += ((((c ^ b) & d) ^ c) + iD) - 1444681467;
        a = b + ((a << 5) | (a >>> 27));
        d += ((((b ^ a) & c) ^ b) + i2) - 51403784;
        d = a + ((d << 9) | (d >>> 23));
        c += ((((a ^ d) & b) ^ a) + i7) + 1735328473;
        c = d + ((c << 14) | (c >>> 18));
        b += ((((d ^ c) & a) ^ d) + iC) - 1926607734;
        b = c + ((b << 20) | (b >>> 12));
        a += (((c ^ b) ^ d) + i5) - 378558;
        a = b + ((a << 4) | (a >>> 28));
        d += (((b ^ a) ^ c) + i8) - 2022574463;
        d = a + ((d << 11) | (d >>> 21));
        c += (((a ^ d) ^ b) + iB) + 1839030562;
        c = d + ((c << 16) | (c >>> 16));
        b += (((d ^ c) ^ a) + iE) - 35309556;
        b = c + ((b << 23) | (b >>> 9));
        a += (((c ^ b) ^ d) + i1) - 1530992060;
        a = b + ((a << 4) | (a >>> 28));
        d += (((b ^ a) ^ c) + i4) + 1272893353;
        d = a + ((d << 11) | (d >>> 21));
        c += (((a ^ d) ^ b) + i7) - 155497632;
        c = d + ((c << 16) | (c >>> 16));
        b += (((d ^ c) ^ a) + iA) - 1094730640;
        b = c + ((b << 23) | (b >>> 9));
        a += (((c ^ b) ^ d) + iD) + 681279174;
        a = b + ((a << 4) | (a >>> 28));
        d += (((b ^ a) ^ c) + i0) - 358537222;
        d = a + ((d << 11) | (d >>> 21));
        c += (((a ^ d) ^ b) + i3) - 722521979;
        c = d + ((c << 16) | (c >>> 16));
        b += (((d ^ c) ^ a) + i6) + 76029189;
        b = c + ((b << 23) | (b >>> 9));
        a += (((c ^ b) ^ d) + i9) - 640364487;
        a = b + ((a << 4) | (a >>> 28));
        d += (((b ^ a) ^ c) + iC) - 421815835;
        d = a + ((d << 11) | (d >>> 21));
        c += (((a ^ d) ^ b) + iF) + 530742520;
        c = d + ((c << 16) | (c >>> 16));
        b += (((d ^ c) ^ a) + i2) - 995338651;
        b = c + ((b << 23) | (b >>> 9));
        a += ((((d ^ -1) | b) ^ c) + i0) - 198630844;
        a = b + ((a << 6) | (a >>> 26));
        d += ((((c ^ -1) | a) ^ b) + i7) + 1126891415;
        d = a + ((d << 10) | (d >>> 22));
        c += ((((b ^ -1) | d) ^ a) + iE) - 1416354905;
        c = d + ((c << 15) | (c >>> 17));
        b += ((((a ^ -1) | c) ^ d) + i5) - 57434055;
        b = c + ((b << 21) | (b >>> 11));
        a += ((((d ^ -1) | b) ^ c) + iC) + 1700485571;
        a = b + ((a << 6) | (a >>> 26));
        d += ((((c ^ -1) | a) ^ b) + i3) - 1894986606;
        d = a + ((d << 10) | (d >>> 22));
        c += ((((b ^ -1) | d) ^ a) + iA) - 1051523;
        c = d + ((c << 15) | (c >>> 17));
        b += ((((a ^ -1) | c) ^ d) + i1) - 2054922799;
        b = c + ((b << 21) | (b >>> 11));
        a += ((((d ^ -1) | b) ^ c) + i8) + 1873313359;
        a = b + ((a << 6) | (a >>> 26));
        d += ((((c ^ -1) | a) ^ b) + iF) - 30611744;
        d = a + ((d << 10) | (d >>> 22));
        c += ((((b ^ -1) | d) ^ a) + i6) - 1560198380;
        c = d + ((c << 15) | (c >>> 17));
        b += ((((a ^ -1) | c) ^ d) + iD) + 1309151649;
        b = c + ((b << 21) | (b >>> 11));
        a += ((((d ^ -1) | b) ^ c) + i4) - 145523070;
        a = b + ((a << 6) | (a >>> 26));
        d += ((((c ^ -1) | a) ^ b) + iB) - 1120210379;
        d = a + ((d << 10) | (d >>> 22));
        c += ((((b ^ -1) | d) ^ a) + i2) + 718787259;
        c = d + ((c << 15) | (c >>> 17));
        int i10 = i + c;
        i = (((((a ^ -1) | c) ^ d) + i9) - 343485551) + b;
        this.hB = i10 + ((i << 21) | (i >>> 11));
        this.hC += c;
        this.hD += d;
        this.hA += a;
    }
}
