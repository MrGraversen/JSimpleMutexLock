package io.graversen.simplemutex;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Created by Martin on 2017-05-08.
 */
public final class MutexLock
{
    private static String lockKey = UUID.randomUUID().toString();
    private static FileOutputStream mutexOutStream;
    private static BufferedReader bufferedReader;

    private static final Object THREAD_SAFETY_MUTEX = new Object();

    static
    {
        Runtime.getRuntime().addShutdownHook(new Thread(MutexLock::release));
    }

    private MutexLock()
    {

    }

    public static void acquire()
    {
        synchronized (THREAD_SAFETY_MUTEX)
        {
            try
            {
                String mutexLockKey = IO.getMutexLockKey();

                if (mutexLockKey != null)
                {
                    throw new MutexAlreadyAcquiredException(mutexLockKey.equals(lockKey));
                }

                if (mutexOutStream == null)
                {
                    mutexOutStream = new FileOutputStream(IO.getMutexLockPath().toFile());
                    mutexOutStream.write(lockKey.getBytes());
                }
            }
            catch (MutexAlreadyAcquiredException maae)
            {
                throw maae;
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public static void release()
    {
        synchronized (THREAD_SAFETY_MUTEX)
        {
            try
            {
                lockKey = UUID.randomUUID().toString();

                bufferedReader.close();
                bufferedReader = null;

                mutexOutStream.close();
                mutexOutStream = null;

                IO.clearMutexLock();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static class IO
    {
        static final Path mutexPath = Paths.get(System.getProperty("user.home"), ".simplemutex");
        static final Path mutexFilePath = Paths.get(mutexPath.toString(), ".lock");

        private IO()
        {

        }

        static Path getMutexLockPath() throws IOException
        {
            if (!Files.exists(mutexPath, LinkOption.NOFOLLOW_LINKS))
            {
                File directory = mutexPath.toFile();
                directory.setWritable(true);
                directory.mkdirs();
            }

            if (!Files.exists(mutexFilePath))
            {
                File file = mutexFilePath.toFile();
                file.setWritable(true);
                file.createNewFile();
            }

            return mutexFilePath;
        }

        static void clearMutexLock() throws IOException
        {
            File file = mutexFilePath.toFile();
            file.delete();
        }

        static String getMutexLockKey() throws IOException
        {
            if (bufferedReader == null)
            {
                bufferedReader = new BufferedReader(new FileReader(IO.getMutexLockPath().toFile()));
            }

            return bufferedReader.readLine();
        }
    }
}
