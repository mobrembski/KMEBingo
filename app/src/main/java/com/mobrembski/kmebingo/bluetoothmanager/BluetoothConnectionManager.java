package com.mobrembski.kmebingo.bluetoothmanager;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.mobrembski.kmebingo.SerialFrames.KMEFrame;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BluetoothConnectionManager implements ISerialConnectionManager, IConnectionStarted {

    private final int maximumWaitingTimeForResponse = 1;
    private final int maxExceptionCountForJob = 3;

    ExecutorService jobsExecutor;
    ScheduledExecutorService connectionExecutor;
    BlockingQueue<BluetoothCommandJob> commandQueue;
    HashMap<String, Integer> jobErrorCountMap = new HashMap<>();
    BluetoothConnectRunnable connectRunnable;
    BluetoothSocket bluetoothSocket;
    ScheduledFuture<?> connectThreadHandle;
    Thread doJobsThread;
    boolean runJobsThread = true;
    Lock pauseJobsThread = new ReentrantLock();
    int transmittedPackets = 0;
    int errorsCount = 0;

    public BluetoothConnectionManager(String deviceAddress) {
        commandQueue = new LinkedBlockingQueue<>();
        jobsExecutor = Executors.newSingleThreadExecutor();
        connectionExecutor = Executors.newSingleThreadScheduledExecutor();
        connectRunnable = new BluetoothConnectRunnable(this, deviceAddress);
    }

    @Override
    public void postNewRequest(final KMEFrame requestData, int priority) {
        Log.d("DebugBT", "Requested new frame with name" + requestData.getClass().getSimpleName());
        final BluetoothCommandJob jb = new BluetoothCommandJob(requestData, priority, this);
        commandQueue.add(jb);
    }

    @Override
    public void startConnecting() {
        Log.d("DebugBT", "startConnecting invoked");
         connectThreadHandle =
                 connectionExecutor.scheduleAtFixedRate(connectRunnable, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public void stopConnections() {
        runJobsThread = false;
        try {
            // TODO: Why this check is needed?
            if (doJobsThread != null)
                doJobsThread.join(10);
        } catch (InterruptedException e) {
            Log.e("DebugBT", "Exception during closing jobs thread?", e);
        }
        connectRunnable.closeSocket();
        connectThreadHandle.cancel(true);
        commandQueue.clear();
    }

    @Override
    public void onConnectionStarted(BluetoothSocket socket) {
        this.bluetoothSocket = socket;
        startJobsThread();
    }

    private void startJobsThread() {
        if (doJobsThread == null || !doJobsThread.isAlive()) {
            doJobsThread = new Thread(doJobsRunnable);
            runJobsThread = true;
            doJobsThread.start();
        }
    }


    public <T extends KMEFrame> T runRequestNow(final T requestData) {
        //doJobsThread.interrupt();
        pauseJobsThread.lock();
        BluetoothCommandJob job = new BluetoothCommandJob(requestData,1,this);
        T returnObject = null;
        Future<T> jobInProgress = jobsExecutor.submit(job);
        try {
            returnObject = jobInProgress.get(maximumWaitingTimeForResponse, TimeUnit.SECONDS);
            transmittedPackets++;
        } catch (Exception ex) {
            Log.e("DebugBT", "Something wrong while running job", ex);
            ++errorsCount;
        }
        //startJobsThread();
        pauseJobsThread.unlock();
        return returnObject;
    }

    public boolean isConnected() {
        return connectRunnable.isConnected();
    }

    Runnable doJobsRunnable = new Runnable() {
        @Override
        public void run() {
            while(runJobsThread) {
                try {
                    if (!connectRunnable.isConnected()) continue;
                    if (getOutputStream() == null || getInputStream() == null) continue;
                    final BluetoothCommandJob job = commandQueue.take();
                    pauseJobsThread.lock();
                    commandQueue.add(job);
                    Future<Void> jobInProgress = jobsExecutor.submit(job);
                    try {
                        jobInProgress.get(maximumWaitingTimeForResponse, TimeUnit.SECONDS);
                        pauseJobsThread.unlock();
                        ++transmittedPackets;
                    } catch (Exception ex) {
                        pauseJobsThread.unlock();
                        ++errorsCount;
                        jobInProgress.cancel(true);
                        onJobException(job);
                        connectRunnable.closeSocket();
                        connectRunnable.run();
                        throw ex;
                    }
                    commandQueue.remove(job);
                } catch (Exception ex) {
                    Log.e("DebugBT", "Something wrong while running job", ex);

                }
            }
        }
    };

    @Override
    public int getTransmittedPacketCount() {
        return transmittedPackets;
    }

    @Override
    public int getErrorsCount() {
        return errorsCount;
    }

    private void onJobException(BluetoothCommandJob job) {
        if (!jobErrorCountMap.containsKey(job.JobName)) {
            jobErrorCountMap.put(job.JobName, 1);
            return;
        }
        int currentErrorCount = jobErrorCountMap.get(job.JobName);
        ++currentErrorCount;
        jobErrorCountMap.put(job.JobName, currentErrorCount);

        if (currentErrorCount > maxExceptionCountForJob) {
            Log.e("DebugBT", "Too much exceptions for job "
                    + job.JobName + " Removing from queue...");
            commandQueue.remove(job);
        }
    }

    InputStream getInputStream() {
        try {
            if (bluetoothSocket != null && bluetoothSocket.isConnected())
                return bluetoothSocket.getInputStream();
        } catch (Exception ex) {
            Log.e("DebugBT", "Exception on returning InputStream", ex);

        }
        return null;
    }

    OutputStream getOutputStream() {
        try {
            if (bluetoothSocket != null && bluetoothSocket.isConnected())
                return bluetoothSocket.getOutputStream();
        } catch (Exception ex) {
            Log.e("DebugBT", "Excetpion on returning OutputStream", ex);
        }
        return null;
    }

    public SerialConnectionStatusEvent.SerialConnectionStatus getConnectionStatus() {
        return connectRunnable.getConnectionStatus();
    }
}
