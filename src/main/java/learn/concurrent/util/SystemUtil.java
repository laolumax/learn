package learn.concurrent.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class SystemUtil {
    private static String OS = System.getProperty("os.name").toLowerCase();

    private static int TRY_TIME = 5;

    public static float getCpuRatioAvailable() throws IOException {
        double sum = 0;
        if (OS.indexOf("linux") >= 0) {
            for (int time = 0; time < TRY_TIME; time++) {
                sum += getCpuRatioForLinux();
            }
        } else if (OS.indexOf("windows") >= 0) {
            for (int time = 0; time < TRY_TIME; time++) {
                sum += getCpuRatioForWindows();
            }
        } else {
            for (int time = 0; time < TRY_TIME; time++) {
                sum += 1;
            }
        }
        return Float.parseFloat(Math.round(100 - sum/TRY_TIME) + "")/100;
    }

    private static double getCpuRatioForWindows() throws IOException {
        Process process = null;
        InputStreamReader in = null;
        BufferedReader bufferedReader = null;
        try {
            process = Runtime.getRuntime().exec("wmic cpu get loadpercentage");
            in = new InputStreamReader(process.getInputStream());
            bufferedReader = new BufferedReader(in);
            String result = bufferedReader.lines().collect(Collectors.joining());
            return getFirstInt(result);
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (in != null) {
                in.close();
            }
            if (process != null) {
                process.getInputStream().close();
                process.getOutputStream().close();
                process.getErrorStream().close();
            }
        }
    }

    private static double getCpuRatioForLinux() throws IOException {
        Process process = null;
        InputStreamReader in = null;
        BufferedReader bufferedReader = null;
        try {
            String[] cmd = { "/bin/sh", "-c", "top -b -n 1 | sed -n '3p' | awk '{print $5}'"};
            process = Runtime.getRuntime().exec(cmd);
            in = new InputStreamReader(process.getInputStream());
            bufferedReader = new BufferedReader(in);
            String str = bufferedReader.readLine();
            str = str.substring(0,3);
            float idleUsage = Float.parseFloat(str);
            return 100 - idleUsage;
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (in != null) {
                in.close();
            }
            if (process != null) {
                process.getInputStream().close();
                process.getOutputStream().close();
                process.getErrorStream().close();
            }
        }
    }

    private static int getFirstInt(String str) {
        char[] chars = str.toCharArray();
        boolean isFoundNumberEnd = false;
        StringBuilder numBuilder =  new StringBuilder();
        for (int index = 0; index < chars.length; index++) {
            if (chars[index] >= '0' && chars[index] <= '9') {
                numBuilder.append(chars[index]);
                for (index++; index < chars.length; index++) {
                    if (chars[index] >= '0' && chars[index] <= '9') {
                        numBuilder.append(chars[index]);
                    } else {
                        isFoundNumberEnd = true;
                        break;
                    }
                }
            }
            if (isFoundNumberEnd) {
                break;
            }
        }
        return Integer.parseInt(numBuilder.toString());
    }
}
