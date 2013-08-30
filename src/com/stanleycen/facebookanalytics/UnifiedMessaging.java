package com.stanleycen.facebookanalytics;

/**
 * Created by scen on 8/29/13.
 */
public class UnifiedMessaging {
    public final static long LARGE_TIMESTAMP = 10000000000000000L;

    private final static String GET_THREADS_FQL =
            "SELECT former_participants,has_attachments,is_group_conversation,is_named_conversation,title,num_messages,participants,thread_id,timestamp FROM unified_thread WHERE folder=\"inbox\" AND timestamp < %d LIMIT 500";

    public static String getThreadFQL(long timestamp) {
        return String.format(GET_THREADS_FQL, timestamp);
    }
}
