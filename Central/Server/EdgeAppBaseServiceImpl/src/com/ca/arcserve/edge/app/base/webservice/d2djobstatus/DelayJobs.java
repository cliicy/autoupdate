package com.ca.arcserve.edge.app.base.webservice.d2djobstatus;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class DelayJobs<T> implements Delayed {

	/** Base of nanosecond timings, to avoid wrapping */
    private static final long NANO_ORIGIN = System.nanoTime();

    /**
     * Returns nanosecond time offset by origin
     */
    final static long now() {
        return System.nanoTime() - NANO_ORIGIN;
    }

    /**
     * Sequence number to break scheduling ties, and in turn to guarantee FIFO order among tied
     * entries.
     */
    private static final AtomicLong sequencer = new AtomicLong(0);

    /** Sequence number to break ties FIFO */
    private final long sequenceNumber;

    /** The time the task is enabled to execute in nanoTime units */
    private final long time;

    private final T item;

    public DelayJobs(T submit, long timeout) {
        this.time = now() + timeout;
        this.item = submit;
        this.sequenceNumber = sequencer.getAndIncrement();
    }

    public T getItem() {
        return this.item;
    }


    public long getDelay(TimeUnit unit) {
        long d = unit.convert(time - now(), TimeUnit.NANOSECONDS);
        return d;
    }

    public int compareTo(Delayed other) {
        if (other == this) // compare zero ONLY if same object
            return 0;
        if (other instanceof DelayJobs) {
            DelayJobs x = (DelayJobs) other;
            long diff = time - x.time;
            if (diff < 0)
                return -1;
            else if (diff > 0)
                return 1;
            else if (sequenceNumber < x.sequenceNumber)
                return -1;
            else
                return 1;
        }
        long d = (getDelay(TimeUnit.NANOSECONDS) - other.getDelay(TimeUnit.NANOSECONDS));
        return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean equals(Object otherObject) {
    	if( this == otherObject )
			return true;
		if( null == otherObject ){
			return false;
		}
		if( getClass() != otherObject.getClass() ){
			return false;
		}
		D2DJobStatusPair thisItem = (D2DJobStatusPair) this.item;
		DelayJobs<D2DJobStatusPair> otherItem = (DelayJobs)otherObject;
		if (thisItem.nodeId.equals(otherItem.getItem().nodeId)) {
			return true;
		} else {			
			return false;
		}
    }
}
