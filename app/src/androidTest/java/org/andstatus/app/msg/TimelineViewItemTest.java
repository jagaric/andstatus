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

package org.andstatus.app.msg;

import android.test.InstrumentationTestCase;

import org.andstatus.app.context.TestSuite;
import org.andstatus.app.context.Travis;
import org.andstatus.app.widget.DuplicationLink;

/**
 * @author yvolk@yurivolkov.com
 */
@Travis
public class TimelineViewItemTest extends InstrumentationTestCase {

    public static final String HTML_BODY = "@<a href=\"https://bsdnode.xyz/user/2\" class=\"h-card mention\">username</a> " +
            "On duplicated posts, sent by AndStatus, please read <a href=\"https://github.com/andstatus/andstatus/issues/83\" " +
            "title=\"https://github.com/andstatus/andstatus/issues/83\" class=\"attachment\" id=\"attachment-15180\" " +
            "rel=\"nofollow external\">https://github.com/andstatus/andstatus/issues/83</a><br />\n" +
            "Sorry if I misunderstood your post :-)";
    public static final String THIS_USER_FAVORITED_SOMETHING_BY_THAT_USER =
            "thisUser favorited something by thatUser: ";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestSuite.initialize(this);
    }

    public void testDuplicationLink() {
        TimelineViewItem item1 = new TimelineViewItem();
        item1.setBody(HTML_BODY);
        TimelineViewItem item2 = new TimelineViewItem();
        item2.setBody("Some other text");
        assertDuplicates(item1, DuplicationLink.DUPLICATES, item2);

        item2.setMsgId(2);
        assertDuplicates(item1, DuplicationLink.NONE, item2);

        item2.setBody(THIS_USER_FAVORITED_SOMETHING_BY_THAT_USER + item1.getBody());
        assertTrue("Is not favoriting action: " + item2.getBody(), item2.isFavoritingAction);
        assertDuplicates(item1, DuplicationLink.IS_DUPLICATED, item2);
        assertDuplicates(item2, DuplicationLink.DUPLICATES, item1);

        item2.setBody("@<a href=\"https://bsdnode.xyz/user/2\" class=\"h-card mention\">username</a> On duplicated posts, sent by AndStatus, please read <a href=\"https://github.com/andstatus/andstatus/issues/83\" title=\"https://github.com/andstatus/andstatus/issues/83\" class=\"attachment\" rel=\"nofollow\">https://github.com/andstatus/andstatus/issues/83</a><br /> Sorry if I misunderstood your post :-)");
        assertDuplicates(item1, DuplicationLink.DUPLICATES, item2);

        item1.setBody("&quot;Interactions&quot; timeline in Twidere is the same or close to existing &quot;Mentions&quot; timeline in AndStatus");
        item2.setBody("\"Interactions\" timeline in Twidere is the same or close to existing \"Mentions\" timeline in AndStatus");
        assertDuplicates(item1, DuplicationLink.DUPLICATES, item2);

        item1.setBody("What is good about Android is that I can use Quitter.se via AndStatus.");
        item2.setBody("What is good about Android is that I can use <a href=\"https://quitter.se/\" title=\"https://quitter.se/\" class=\"attachment\" id=\"attachment-1205381\" rel=\"nofollow external\">Quitter.se</a> via AndStatus.");
        assertDuplicates(item1, DuplicationLink.DUPLICATES, item2);

        item1.createdDate = 1468509659000L;
        item2.createdDate = 1468509658000L;
        assertDuplicates(item1, DuplicationLink.DUPLICATES, item2);
        assertDuplicates(item2, DuplicationLink.IS_DUPLICATED, item1);
        item2.createdDate = item1.createdDate;

        item2.favorited = true;
        assertDuplicates(item1, DuplicationLink.DUPLICATES, item2);
        assertDuplicates(item2, DuplicationLink.IS_DUPLICATED, item1);

        item1.reblogged = true;
        assertDuplicates(item1, DuplicationLink.DUPLICATES, item2);
        assertDuplicates(item2, DuplicationLink.IS_DUPLICATED, item1);

        item2.favorited = false;
        assertDuplicates(item2, DuplicationLink.DUPLICATES, item1);
        assertDuplicates(item1, DuplicationLink.IS_DUPLICATED, item2);

        item1.setBody("cat favorited something by nstr: test from andstatus on freshly r00ted phone");
        item2.setBody("mmn favorited something by nstr: test from andstatus on freshly r00ted phone");
        assertDuplicates(item1, DuplicationLink.IS_DUPLICATED, item2);
    }

    protected void assertDuplicates(TimelineViewItem item1, DuplicationLink duplicates, TimelineViewItem item2) {
        assertEquals(item1.toString() + " vs " + item2, duplicates, item1.duplicates(item2));
    }
}
