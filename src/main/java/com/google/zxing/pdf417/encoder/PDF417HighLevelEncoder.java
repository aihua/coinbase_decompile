package com.google.zxing.pdf417.encoder;

import com.coinbase.android.ui.NumericKeypadConnector;
import com.google.zxing.WriterException;
import java.math.BigInteger;
import java.util.Arrays;

final class PDF417HighLevelEncoder {
    private static final byte[] MIXED = new byte[128];
    private static final byte[] PUNCTUATION = new byte[128];
    private static final byte[] TEXT_MIXED_RAW = new byte[]{(byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 38, (byte) 13, (byte) 9, (byte) 44, (byte) 58, (byte) 35, (byte) 45, (byte) 46, (byte) 36, (byte) 47, (byte) 43, (byte) 37, (byte) 42, (byte) 61, (byte) 94, (byte) 0, (byte) 32, (byte) 0, (byte) 0, (byte) 0};
    private static final byte[] TEXT_PUNCTUATION_RAW = new byte[]{(byte) 59, (byte) 60, (byte) 62, (byte) 64, (byte) 91, (byte) 92, (byte) 93, (byte) 95, (byte) 96, (byte) 126, (byte) 33, (byte) 13, (byte) 9, (byte) 44, (byte) 58, (byte) 10, (byte) 45, (byte) 46, (byte) 36, (byte) 47, (byte) 34, (byte) 124, (byte) 42, (byte) 40, (byte) 41, (byte) 63, (byte) 123, (byte) 125, (byte) 39, (byte) 0};

    static {
        byte i;
        Arrays.fill(MIXED, (byte) -1);
        for (i = (byte) 0; i < TEXT_MIXED_RAW.length; i = (byte) (i + 1)) {
            byte b = TEXT_MIXED_RAW[i];
            if (b > (byte) 0) {
                MIXED[b] = i;
            }
        }
        Arrays.fill(PUNCTUATION, (byte) -1);
        for (i = (byte) 0; i < TEXT_PUNCTUATION_RAW.length; i = (byte) (i + 1)) {
            b = TEXT_PUNCTUATION_RAW[i];
            if (b > (byte) 0) {
                PUNCTUATION[b] = i;
            }
        }
    }

    private static byte[] getBytesForMessage(String msg) {
        return msg.getBytes();
    }

    static String encodeHighLevel(String msg, Compaction compaction) throws WriterException {
        byte[] bytes = null;
        StringBuilder sb = new StringBuilder(msg.length());
        int len = msg.length();
        int p = 0;
        int textSubMode = 0;
        if (compaction == Compaction.TEXT) {
            encodeText(msg, 0, len, sb, 0);
        } else if (compaction == Compaction.BYTE) {
            bytes = getBytesForMessage(msg);
            encodeBinary(bytes, 0, bytes.length, 1, sb);
        } else if (compaction == Compaction.NUMERIC) {
            sb.append('Ά');
            encodeNumeric(msg, 0, len, sb);
        } else {
            int encodingMode = 0;
            while (p < len) {
                int n = determineConsecutiveDigitCount(msg, p);
                if (n >= 13) {
                    sb.append('Ά');
                    encodingMode = 2;
                    textSubMode = 0;
                    encodeNumeric(msg, p, n, sb);
                    p += n;
                } else {
                    int t = determineConsecutiveTextCount(msg, p);
                    if (t >= 5 || n == len) {
                        if (encodingMode != 0) {
                            sb.append('΄');
                            encodingMode = 0;
                            textSubMode = 0;
                        }
                        textSubMode = encodeText(msg, p, t, sb, textSubMode);
                        p += t;
                    } else {
                        if (bytes == null) {
                            bytes = getBytesForMessage(msg);
                        }
                        int b = determineConsecutiveBinaryCount(msg, bytes, p);
                        if (b == 0) {
                            b = 1;
                        }
                        if (b == 1 && encodingMode == 0) {
                            encodeBinary(bytes, p, 1, 0, sb);
                        } else {
                            encodeBinary(bytes, p, b, encodingMode, sb);
                            encodingMode = 1;
                            textSubMode = 0;
                        }
                        p += b;
                    }
                }
            }
        }
        return sb.toString();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int encodeText(CharSequence msg, int startpos, int count, StringBuilder sb, int initialSubmode) {
        StringBuilder tmp = new StringBuilder(count);
        int submode = initialSubmode;
        int idx = 0;
        while (true) {
            char ch = msg.charAt(startpos + idx);
            switch (submode) {
                case 0:
                    if (isAlphaUpper(ch)) {
                        if (ch == ' ') {
                            tmp.append('\u001a');
                        } else {
                            tmp.append((char) (ch - 65));
                        }
                    } else if (isAlphaLower(ch)) {
                        submode = 1;
                        tmp.append('\u001b');
                        break;
                    } else if (isMixed(ch)) {
                        submode = 2;
                        tmp.append('\u001c');
                        break;
                    } else {
                        tmp.append('\u001d');
                        tmp.append((char) PUNCTUATION[ch]);
                    }
                case 1:
                    if (isAlphaLower(ch)) {
                        if (ch == ' ') {
                            tmp.append('\u001a');
                        } else {
                            tmp.append((char) (ch - 97));
                        }
                    } else if (isAlphaUpper(ch)) {
                        tmp.append('\u001b');
                        tmp.append((char) (ch - 65));
                    } else if (isMixed(ch)) {
                        submode = 2;
                        tmp.append('\u001c');
                        break;
                    } else {
                        tmp.append('\u001d');
                        tmp.append((char) PUNCTUATION[ch]);
                    }
                case 2:
                    if (!isMixed(ch)) {
                        if (!isAlphaUpper(ch)) {
                            if (!isAlphaLower(ch)) {
                                if ((startpos + idx) + 1 < count && isPunctuation(msg.charAt((startpos + idx) + 1))) {
                                    submode = 3;
                                    tmp.append('\u0019');
                                    break;
                                }
                                tmp.append('\u001d');
                                tmp.append((char) PUNCTUATION[ch]);
                            } else {
                                submode = 1;
                                tmp.append('\u001b');
                                break;
                            }
                        }
                        submode = 0;
                        tmp.append('\u001c');
                        break;
                    }
                    tmp.append((char) MIXED[ch]);
                    break;
                default:
                    if (!isPunctuation(ch)) {
                        submode = 0;
                        tmp.append('\u001d');
                        break;
                    }
                    tmp.append((char) PUNCTUATION[ch]);
                    idx++;
                    if (idx < count) {
                        break;
                    }
                    char h = '\u0000';
                    int len = tmp.length();
                    for (int i = 0; i < len; i++) {
                        if (i % 2 != 0) {
                            h = (char) ((h * 30) + tmp.charAt(i));
                            sb.append(h);
                        } else {
                            h = tmp.charAt(i);
                        }
                    }
                    if (len % 2 != 0) {
                        sb.append((char) ((h * 30) + 29));
                    }
                    return submode;
            }
        }
    }

    private static void encodeBinary(byte[] bytes, int startpos, int count, int startmode, StringBuilder sb) {
        int i;
        if (count == 1 && startmode == 0) {
            sb.append('Α');
        }
        int idx = startpos;
        if (count >= 6) {
            sb.append('Μ');
            char[] chars = new char[5];
            while ((startpos + count) - idx >= 6) {
                long t = 0;
                for (i = 0; i < 6; i++) {
                    t = (t << 8) + ((long) (bytes[idx + i] & 255));
                }
                for (i = 0; i < 5; i++) {
                    chars[i] = (char) ((int) (t % 900));
                    t /= 900;
                }
                for (i = chars.length - 1; i >= 0; i--) {
                    sb.append(chars[i]);
                }
                idx += 6;
            }
        }
        if (idx < startpos + count) {
            sb.append('΅');
        }
        for (i = idx; i < startpos + count; i++) {
            sb.append((char) (bytes[i] & 255));
        }
    }

    private static void encodeNumeric(String msg, int startpos, int count, StringBuilder sb) {
        int idx = 0;
        StringBuilder tmp = new StringBuilder((count / 3) + 1);
        BigInteger num900 = BigInteger.valueOf(900);
        BigInteger num0 = BigInteger.valueOf(0);
        while (idx < count - 1) {
            tmp.setLength(0);
            int len = Math.min(44, count - idx);
            BigInteger bigint = new BigInteger('1' + msg.substring(startpos + idx, (startpos + idx) + len));
            do {
                tmp.append((char) bigint.mod(num900).intValue());
                bigint = bigint.divide(num900);
            } while (!bigint.equals(num0));
            for (int i = tmp.length() - 1; i >= 0; i--) {
                sb.append(tmp.charAt(i));
            }
            idx += len;
        }
    }

    private static boolean isDigit(char ch) {
        return ch >= NumericKeypadConnector.ZERO && ch <= '9';
    }

    private static boolean isAlphaUpper(char ch) {
        return ch == ' ' || (ch >= 'A' && ch <= 'Z');
    }

    private static boolean isAlphaLower(char ch) {
        return ch == ' ' || (ch >= 'a' && ch <= 'z');
    }

    private static boolean isMixed(char ch) {
        return MIXED[ch] != (byte) -1;
    }

    private static boolean isPunctuation(char ch) {
        return PUNCTUATION[ch] != (byte) -1;
    }

    private static boolean isText(char ch) {
        return ch == '\t' || ch == '\n' || ch == '\r' || (ch >= ' ' && ch <= '~');
    }

    private static int determineConsecutiveDigitCount(CharSequence msg, int startpos) {
        int count = 0;
        int len = msg.length();
        int idx = startpos;
        if (idx < len) {
            char ch = msg.charAt(idx);
            while (isDigit(ch) && idx < len) {
                count++;
                idx++;
                if (idx < len) {
                    ch = msg.charAt(idx);
                }
            }
        }
        return count;
    }

    private static int determineConsecutiveTextCount(CharSequence msg, int startpos) {
        int len = msg.length();
        int idx = startpos;
        while (idx < len) {
            char ch = msg.charAt(idx);
            int numericCount = 0;
            while (numericCount < 13 && isDigit(ch) && idx < len) {
                numericCount++;
                idx++;
                if (idx < len) {
                    ch = msg.charAt(idx);
                }
            }
            if (numericCount >= 13) {
                return (idx - startpos) - numericCount;
            }
            if (numericCount <= 0) {
                if (!isText(msg.charAt(idx))) {
                    break;
                }
                idx++;
            }
        }
        return idx - startpos;
    }

    private static int determineConsecutiveBinaryCount(CharSequence msg, byte[] bytes, int startpos) throws WriterException {
        int len = msg.length();
        int idx = startpos;
        while (idx < len) {
            char ch = msg.charAt(idx);
            int numericCount = 0;
            while (numericCount < 13 && isDigit(ch)) {
                numericCount++;
                int i = idx + numericCount;
                if (i >= len) {
                    break;
                }
                ch = msg.charAt(i);
            }
            if (numericCount >= 13) {
                return idx - startpos;
            }
            int textCount = 0;
            while (textCount < 5 && isText(ch)) {
                textCount++;
                i = idx + textCount;
                if (i >= len) {
                    break;
                }
                ch = msg.charAt(i);
            }
            if (textCount >= 5) {
                return idx - startpos;
            }
            ch = msg.charAt(idx);
            if (bytes[idx] != (byte) 63 || ch == '?') {
                idx++;
            } else {
                throw new WriterException("Non-encodable character detected: " + ch + " (Unicode: " + ch + ')');
            }
        }
        return idx - startpos;
    }
}
