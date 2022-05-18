package com.helthyme.report.service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.helthyme.report.Constants;
import com.helthyme.report.util.InternalServerException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

@Slf4j
public class S3FileManager {

    private AmazonS3 s3;
    private final String bucketName;
    private final Regions region;

    public S3FileManager() {
        this.bucketName = Constants.BUCKET_NAME;
        this.region = Regions.US_EAST_1;
    }

    public boolean isConnected() {
        return s3 != null;
    }

    public boolean connect() {
        try {
            s3 = AmazonS3ClientBuilder.standard().withRegion(region).build();
            return true;
        } catch (Exception e) {
            log.error("Error in connecting to S3 bucket", e);
            return false;
        }
    }


    public void upload(final byte[] content, final String newFileName) throws InternalServerException {
        try {
            reconnectIfDisconnected();
            s3.putObject(bucketName, newFileName, new BufferedInputStream(new ByteArrayInputStream(content)), null);
        } catch (Exception e) {
            log.error("Error in uploading to s3", e);
            throw new InternalServerException(e.getMessage());
        }

    }


    public void reconnectIfDisconnected() throws InternalServerException {
        if (!isConnected()) {
            if (!connect()) {
                throw new InternalServerException("Cannot connect to File storage");
            }
        }
    }
}
