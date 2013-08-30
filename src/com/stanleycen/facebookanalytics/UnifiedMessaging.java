package com.stanleycen.facebookanalytics;

/**
 * Created by scen on 8/29/13.
 */
public class UnifiedMessaging {
    public final static long LARGE_TIMESTAMP = 10000000000000000L;
    public final static int API_WAIT = 1500;

    private final static String GET_THREADS_FQL =
            "SELECT former_participants,is_group_conversation,title,num_messages,participants,thread_id,timestamp FROM unified_thread WHERE folder=\"inbox\" AND timestamp < %d LIMIT 1";
    private final static String GET_MESSAGES_FQL =
            "SELECT attachment_map,attachments,body,coordinates,message_id,sender,timestamp,shares,share_map FROM unified_message WHERE thread_id=\"%s\" AND timestamp > %d LIMIT 500";

    public static String getThreadFQL(long timestamp) {
        return String.format(GET_THREADS_FQL, timestamp);
    }

    public static String getMessagesFQL(String threadId, long timestamp) {
        return String.format(GET_MESSAGES_FQL, threadId, timestamp);
    }
}
