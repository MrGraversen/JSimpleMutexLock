package io.graversen.simplemutex;

/**
 * Created by Martin on 2017-05-08.
 */
public class MutexAlreadyAcquiredException extends RuntimeException
{
    private boolean acquiredByThis = false;

    public MutexAlreadyAcquiredException(boolean acquiredByThis)
    {
        this.acquiredByThis = acquiredByThis;
    }

    public boolean isAcquiredByThis()
    {
        return acquiredByThis;
    }
}
