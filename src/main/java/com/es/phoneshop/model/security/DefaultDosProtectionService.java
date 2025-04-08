package com.es.phoneshop.model.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDosProtectionService implements DosProtectionService {
    private static final long THRESHOLD = 20;
    private static final long TIME_FRAME = 60 * 1000;
    private final Map<String, RequestData> countMap = new ConcurrentHashMap<>();

    private static class SingletonHelper {
        private static final DosProtectionService INSTANCE = new DefaultDosProtectionService();
    }

    public static DosProtectionService getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private static class RequestData {
        private long timestamp;
        private long count;

        public RequestData(long timestamp) {
            this.timestamp = timestamp;
            this.count = 1;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public long getCount() {
            return count;
        }

        public void incrementCount() {
            this.count++;
        }

        public void reset(long timestamp) {
            this.timestamp = timestamp;
            this.count = 1;
        }
    }

    @Override
    public synchronized boolean isAllowed(String ip) {
        long currentTime = System.currentTimeMillis();
        RequestData requestData = countMap.get(ip);

        if (requestData == null || currentTime - requestData.getTimestamp() > TIME_FRAME) {
            requestData = new RequestData(currentTime);
            countMap.put(ip, requestData);
            return true;
        }

        if (requestData.getCount() >= THRESHOLD) {
            return false;
        }

        requestData.incrementCount();
        return true;
    }
}
