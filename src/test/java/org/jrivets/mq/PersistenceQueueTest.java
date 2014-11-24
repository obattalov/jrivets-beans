package org.jrivets.mq;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.jrivets.journal.Journal;
import org.jrivets.journal.JournalBuilder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class PersistenceQueueTest {

    private final static String PREFIX = "persistQueue";
    
    static final String temporaryDirectory = System.getProperty("java.io.tmpdir");

    private Journal journal;
    
    private PersistenceQueue pQueue;
    
    private static class Message {
        
        private String message;
        
        private byte[] data;
        
        Message(int msgLen, int dataLen) {
            message = RandomStringUtils.random(msgLen, true, true);
            data = new byte[dataLen]; 
            new Random().nextBytes(data);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(data);
            result = prime * result + ((message == null) ? 0 : message.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Message other = (Message) obj;
            if (!Arrays.equals(data, other.data))
                return false;
            if (message == null) {
                if (other.message != null)
                    return false;
            } else if (!message.equals(other.message))
                return false;
            return true;
        }
    }
    
    @BeforeMethod
    public void init() throws IOException {
        FileUtils.deleteQuietly(new File(temporaryDirectory, PREFIX));
        createJournal();
        pQueue = new PersistenceQueue("test", 10000, journal);
    }

    @AfterMethod
    public void tearDown() {
        pQueue.terminate();
        FileUtils.deleteQuietly(new File(temporaryDirectory, PREFIX));
    }
    
    @Test
    public void putGetTest() throws OverflowException, IOException {
        Message msg = new Message(10, 10);
        pQueue.put(msg);
        Message msg1 = (Message) pQueue.get(0);
        assertEquals(msg, msg1);
    }

    @Test
    public void overflowTest() throws OverflowException, IOException {
        Message msg = new Message(10, 10);
        Message msg1 = new Message(100, 1000);
        pQueue.put(msg);
        try {
            pQueue.put(new Message(10000, 1000));
            fail("This code should throw exceptions!");
        } catch (OverflowException oe) {
            pQueue.put(msg1);
        }
        assertEquals((Message) pQueue.get(0), msg);
        assertEquals((Message) pQueue.get(10), msg1);
    }
    
    @Test(expectedExceptions = {NullPointerException.class})
    public void nullTest() throws OverflowException, IOException {
        pQueue.put(null);
    }
    
    @Test
    public void getTimeoutTest() throws IOException {
        long start = System.currentTimeMillis();
        assertNull(pQueue.get(0));
        assertTrue(System.currentTimeMillis() - start < 50L);
        
        start = System.currentTimeMillis();
        assertNull(pQueue.get(50L));
        assertTrue(System.currentTimeMillis() - start >= 50L);
    }
    
    @Test
    public void putCloseGetTest() throws OverflowException, IOException {
        Message msg = new Message(5000, 1000);
        pQueue.put(msg);
        pQueue.terminate();
        
        createJournal();
        pQueue = new PersistenceQueue("test", 10000, journal);
        assertEquals((Message) pQueue.get(0), msg);
    }
    
    @Test
    public void performanceTest() throws OverflowException, IOException {
        pQueue.terminate();
        journal = new JournalBuilder().withMaxCapacity(1500000).withMaxChunkSize(500000).withPrefixName(PREFIX)
                .withFolderName(temporaryDirectory).withSingleWrite(true).buildExpandable();
        pQueue = new PersistenceQueue("test", 1000000, journal);
        long stop = System.currentTimeMillis() + 100L;
        int count = 0;
        while (stop > System.currentTimeMillis()) {
            Message msg = new Message(4000, 96);
            pQueue.put(msg);
            assertEquals((Message) pQueue.get(0), msg);
            count++;
        }
        System.out.println(count*10 + " messages for 1 second, " + 40*count + "KB/sec");
    }
    
    private void createJournal() throws IOException {
        journal = new JournalBuilder().withMaxCapacity(15000).withMaxChunkSize(5000).withPrefixName(PREFIX)
                .withFolderName(temporaryDirectory).withSingleWrite(true).buildExpandable();
    }
}
