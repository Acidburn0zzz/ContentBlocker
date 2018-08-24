/*
 This file is part of AdGuard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 Copyright © 2018 AdGuard Content Blocker. All rights reserved.

 AdGuard Content Blocker is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or (at your option)
 any later version.

 AdGuard Content Blocker is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 AdGuard Content Blocker.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.adguard.android.contentblocker.commons;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StringHelperUtils {
    public static List<String> splitAndTrim(String string, String sep) {
        String[] strings = StringUtils.split(string, sep);
        List<String> result = new ArrayList<>();
        if (ArrayUtils.isNotEmpty(strings)) {
            for (String rule : strings) {
                rule = StringUtils.trim(rule);
                if (StringUtils.isNotBlank(rule)) {
                    result.add(rule);
                }
            }
        }
        return result;
    }
}
