package com.coderpage.lib.update;

/**
 * @author lc. 2017-09-23 23:46
 * @since 0.5.0
 */

public interface DownloadProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
