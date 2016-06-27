/*
 * Copyright (c) 2016 yvolk (Yuri Volkov), http://yurivolkov.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.andstatus.app.service;

import android.text.TextUtils;

import org.andstatus.app.data.DataInserter;
import org.andstatus.app.net.http.ConnectionException;
import org.andstatus.app.net.social.MbTimelineItem;
import org.andstatus.app.util.MyLog;

import java.util.List;

/**
 * @author yvolk@yurivolkov.com
 */
public class TimelineDownloaderSearch extends TimelineDownloader {
    @Override
    public void download() throws ConnectionException {
        DataInserter di = new DataInserter(execContext);
        String searchQuery = execContext.getCommandData().getSearchQuery();
        if (TextUtils.isEmpty(searchQuery)) {
            MyLog.e(this,  "Search query is empty");
            execContext.getResult().incrementParseExceptions();
        } else {
            int limit = 200;
            List<MbTimelineItem> messages;
            try {
                messages = execContext.getMyAccount().getConnection().search(searchQuery, limit);
                for (MbTimelineItem item : messages) {
                    switch (item.getType()) {
                        case MESSAGE:
                            di.insertOrUpdateMsg(item.mbMessage);
                            break;
                        case USER:
                            di.insertOrUpdateUser(item.mbUser);
                            break;
                        default:
                            break;
                    }
                }
            } catch (ConnectionException e) {
                logConnectionException(e, "Search '" + searchQuery + "'");
            }
        }
    }
}