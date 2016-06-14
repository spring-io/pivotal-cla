package okhttp3.mockwebserver;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EnqueueRequests {
	String[] value();
}
