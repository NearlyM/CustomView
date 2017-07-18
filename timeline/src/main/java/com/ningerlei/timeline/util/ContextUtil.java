package com.ningerlei.timeline.util;

import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Description :
 * CreateTime : 2017/7/7 11:57
 *
 * @author ningerlei@danale.com
 * @version <v1.0>
 * @Editor : Administrator
 * @ModifyTime : 2017/7/7 11:57
 * @ModifyDescription :
 */

public class ContextUtil {

    public static AttributeSet getAttributeSet(Resources resources, int resId) {
        XmlPullParser parser = resources.getXml(resId);
        AttributeSet attributes = Xml.asAttributeSet(parser);
        int type;
        try {
            while ((type = parser.next()) != XmlPullParser.START_TAG &&
                    type != XmlPullParser.END_DOCUMENT) {
                // Empty
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return attributes;
    }
}
