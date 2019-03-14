package com.example.cmc_application.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ByteUtil {
    
    /**
     * 字节数组转换成对应的16进制表示的字符串
     *
     * @param src
     * @return
     */
    public static String bytes2HexStr(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return "";
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            builder.append(buffer);
        }
        return builder.toString().toUpperCase();
    }



    /**
     * 十六进制字节数组转字符串
     *
     * @param src 目标数组
     * @param dec 起始位置
     * @param length 长度
     * @return
     */
    public static String bytes2HexStr(byte[] src, int dec, int length) {
        byte[] temp = new byte[length];
        System.arraycopy(src, dec, temp, 0, length);
        return bytes2HexStr(temp);
    }

    /**
     * 16进制字符串转10进制数字
     *
     * @param hex
     * @return
     */
    public static long hexStr2decimal(String hex) {
        return Long.parseLong(hex, 16);
    }

    /**
     * 把十进制数字转换成足位的十六进制字符串,并补全空位
     *
     * @param num
     * @return
     */
    public static String decimal2fitHex(long num) {
        String hex = Long.toHexString(num).toUpperCase();
        if (hex.length() % 2 != 0) {
            return "0" + hex;
        }
        return hex.toUpperCase();
    }

    /**
     * 把十进制数字转换成足位的十六进制字符串,并补全空位
     *
     * @param num
     * @param strLength 字符串的长度
     * @return
     */
    public static String decimal2fitHex(long num, int strLength) {
        String hexStr = decimal2fitHex(num);
        StringBuilder stringBuilder = new StringBuilder(hexStr);
        while (stringBuilder.length() < strLength) {
            stringBuilder.insert(0, '0');
        }
        return stringBuilder.toString();
    }

    public static String fitDecimalStr(int dicimal, int strLength) {
        StringBuilder builder = new StringBuilder(String.valueOf(dicimal));
        while (builder.length() < strLength) {
            builder.insert(0, "0");
        }
        return builder.toString();
    }

    /**
     * 字符串转十六进制字符串
     *
     * @param str
     * @return
     */
    public static String str2HexString(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder();
        byte[] bs = null;
        try {

            bs = str.getBytes("utf8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString();
    }

    /**
     * 把十六进制表示的字节数组字符串，转换成十六进制字节数组
     *
     * @param
     * @return byte[]
     */
    public static byte[] hexStr2bytes(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toUpperCase().toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (hexChar2byte(achar[pos]) << 4 | hexChar2byte(achar[pos + 1]));
        }
//        String hexStr = ByteUtil.bytes2HexStr(result, 0, len);
//        Log.d("hexStr2bytes", hexStr);
        return result;
    }

    /**
     * 把16进制字符[0123456789abcde]（含大小写）转成字节
     *
     * @param c
     * @return
     */
    private static int hexChar2byte(char c) {
        switch (c) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            case 'a':
            case 'A':
                return 10;
            case 'b':
            case 'B':
                return 11;
            case 'c':
            case 'C':
                return 12;
            case 'd':
            case 'D':
                return 13;
            case 'e':
            case 'E':
                return 14;
            case 'f':
            case 'F':
                return 15;
            default:
                return -1;
        }
    }


    public static String getCrcCheckStr(String inStr) {

        int[] inHex = new int[inStr.length() / 2];
        for (int i = 0; i < inHex.length; i++) {
            inHex[i] = Integer.parseInt(inStr.substring(i * 2, i * 2 + 2), 16);
        }

        String checks = addZero(Integer.toBinaryString(CRC_Check(inHex)), 16);
        String srt_H = Integer.toHexString(Integer.valueOf(checks.substring(0, 8), 2));//取高八位
        String crc_L = Integer.toHexString(Integer.valueOf(checks.substring(checks.length() - 8, checks.length()), 2));//取低八位

        srt_H = srt_H.length() < 2 ? "0" + srt_H : srt_H;
        crc_L = crc_L.length() < 2 ? "0" + crc_L : crc_L;

        return crc_L + srt_H;
    }


    /**
     * 将元数据前补零，补后的总长度为指定的长度，以字符串的形式返回
     */
    public static String addZero(String sourceDate, int formatLength) {
        int originalLen = sourceDate.length();
        if (originalLen < formatLength) {
            for (int i = 0; i < formatLength - originalLen; i++) {
                sourceDate = "0" + sourceDate;
            }
        }
        return sourceDate;
    }

    /**
     * 通过CRC算法获取校验和
     *
     * @param buf 需要校验的数据
     * @return 16进制的校验和
     */
    public static int CRC_Check(int [] buf) {
        int returnValue = 0XFFFF;
        for (int i = 0; i < buf.length; i++) {
            returnValue ^= buf[i];
            for (int j = 0; j < 8; j++) {
                if ((returnValue & 0X01) != 0) {
                    returnValue = (returnValue >> 1) ^ 0XA001;
                } else {
                    returnValue = returnValue >> 1;
                }
            }
        }
        return returnValue;
    }

    public static void appendLog(String text)
    {
        File logFile = new File("sdcard/log.txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text+"\n");
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static boolean isCheckCRC(String command){
        if (command.length() < 14)
            return false;
        String address = command.substring(0, 10);
        String crc = getCrcCheckStr(address);
        if (crc.toUpperCase().equals(command.substring(10, 14)))
            return true;
        return false;
    }
}
