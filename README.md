# JSimpleMutexLock
I needed a simple way to mutually exclude duplicate instanses (i.e. separate processes) of the same program to run. The simplest possible way to do this is by enforcing global exclusion using the filesystem.

This program manages a lock-file Windows / Unix agnostically and will throw an exception if a process already acquired the lock.

## How to use

In accordance with the naming of this project, it's quite simple.

### Acquiring locks

The following method throws a `MutexAlreadyAcquiredException` (unchecked) if the lock could not be acquired.

`MutexLock.acquire();`

### Releasing locks

`MutexLock.release();`
