package io.netty.example.chat.core.dex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import org.json.JSONObject;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;

public class DynamicCodeGenerator {
    
    private static final String RELATIVE_PATH_TO_SRC_FILE = "mckinley/app/src/remote/java/com/cootek/smartdialer/xcode/DynamicCode.java";
    
    public static final int ERROR_CODE_SUCCCESS = 1;
    public static final int ERROR_CODE_BUILD_ERROR = 2;
    
    public boolean preBuildProcess(int version) {
        BufferedReader reader = null;
        try {
            Process p = Runtime.getRuntime().exec("./clean_checkout.sh " + "netty_feedback");
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            return p.waitFor() == 0;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    
                }
            }
        }
        return false;
    }
    
    public boolean replaceDynamicCodeFile(String source) {
        File srcFile = new File(RELATIVE_PATH_TO_SRC_FILE);
        BufferedWriter writer = null;
        boolean result = true;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(srcFile)));
            writer.write(source);
            writer.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            result = false;
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    result = false;
                }
            }
        }
        return result;
    }
    
    public boolean build(String identifier) {
        BufferedReader reader = null;
        try {
            ProcessBuilder builder = new ProcessBuilder("./build.sh", identifier);
            builder.redirectErrorStream(true);
            Process p = builder.start();
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("wait start");
            int exitCode = p.waitFor();
            System.out.println("wait end");
            return exitCode == 0;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    
                }
            }
        }
        return false;
    }
    
    
    public static void main(String[] args) {
        JSONObject message = new JSONObject();
        message.put("type", "CODE");
        message.put("content", "ddd");
//        File sourceFile = new File("source_sample.java");
//        BufferedReader reader = null;
//        StringBuilder source = new StringBuilder();
//        try {
//            String line = null;
//            reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));
//            while ((line = reader.readLine()) != null) {
//                source.append(line + "\n");
//            }
//            System.out.print(source.toString());
//        } catch (IOException e) {
//            
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    
//                }
//            }
//        }
//        DynamicCodeGenerator generator = new DynamicCodeGenerator();
//        boolean result = false;
//        result = generator.preBuildProcess(0);
//        if (!result) {
//            System.out.println("preBuildProcess fail");
//        }
//        result = generator.replaceDynamicCodeFile(source.toString());
//        if (!result) {
//            System.out.println("replaceDynamicCodeFile fail");
//        }
//        String id = "12345678";
//        result = generator.build(id);
//        if (!result) {
//            System.out.println("build fail");
//        }
//        String accessId = "tA8pC6ItuicFZyEM";
//        String accessSecretKey = "Ds0EcOV9UDLwqLXgLvFfVIybDE4aUK";
//        String endPoint = "http://oss.aliyuncs.com";
//        OSSClient client = new OSSClient(endPoint, accessId, accessSecretKey);
//        List<Bucket> buckets = client.listBuckets();
//        for (Bucket bucket : buckets) {
//            System.out.println(bucket.getName());
//        }
//        String dexFileName = id + ".jar";
//        File dexFile = new File(dexFileName);
//        try {
//            InputStream content = new FileInputStream(dexFile);
//            ObjectMetadata meta = new ObjectMetadata();
//            meta.setContentLength(dexFile.length());
//            PutObjectResult putResult = client.putObject("cootek-dialer-download", "chat/" + dexFileName, content, meta);
//            System.out.println(putResult.getETag());
//        } catch (IOException e) {
//            
//        }
        
    }
}
