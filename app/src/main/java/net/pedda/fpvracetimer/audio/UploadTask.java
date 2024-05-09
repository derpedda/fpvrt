package net.pedda.fpvracetimer.audio;

import android.util.Log;

import com.github.davidmoten.aws.lw.client.BaseUrlFactory;
import com.github.davidmoten.aws.lw.client.Client;
import com.github.davidmoten.aws.lw.client.HttpClient;
import com.github.davidmoten.aws.lw.client.HttpMethod;
import com.github.davidmoten.aws.lw.client.Request;
import com.github.davidmoten.aws.lw.client.Response;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

public class UploadTask implements Runnable{

    public UploadTask(String s3_BUCKET, String s3_ENDPOINT, String s3_ACCESS, String s3_SECRET, File file) {
        S3_BUCKET = s3_BUCKET;
        S3_ENDPOINT = s3_ENDPOINT;
        S3_ACCESS = s3_ACCESS;
        S3_SECRET = s3_SECRET;
        this.file = file;
    }

    private String S3_BUCKET;
    private String S3_ENDPOINT;
    private String S3_ACCESS;
    private String S3_SECRET;
    private File file;


    @Override
    public void run() {

        BaseUrlFactory buf = new BaseUrlFactory() {
            @Override
            public String create(String serviceName, Optional<String> region) {
                return S3_ENDPOINT;
            }
        };

        Client s3client = Client.s3().regionNone().accessKey(S3_ACCESS).secretKey(S3_SECRET).baseUrlFactory(buf).build();
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            bis.read(bytes, 0, bytes.length);
            bis.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Request rq = s3client.path(S3_BUCKET + "/" + file.getName()).method(HttpMethod.PUT).requestBody(bytes);

        try {
            rq.execute();
            Response response = rq.response();
            if(response.isOk()) {
                file.deleteOnExit();
            }
        } catch (Exception e) {
            Log.e("UploadTask", "run: ", e);
        }
        Response rs = rq.response();
        Log.d("UploadTask", String.format("run: %s", rs.contentUtf8()));
    }
}
