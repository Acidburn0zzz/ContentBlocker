package com.adguard.android.db;

import android.content.Context;
import com.adguard.filter.rules.FilterRule;
import org.apache.commons.collections.list.SetUniqueList;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter rules dao implementation using android internal storage
 */
public class FilterRuleDaoImpl implements FilterRuleDao {

    private final Logger log = LoggerFactory.getLogger(FilterRuleDaoImpl.class);
    private final Context context;

    /**
     * Creates an instance of the filter rules internal storage
     *
     * @param context Current context
     */
    public FilterRuleDaoImpl(Context context) {
        this.context = context;
    }

    @Override
    public List<String> selectRuleTexts(List<Integer> filterIds, boolean useCosmetics) {
        //noinspection unchecked
        List<String> rules = SetUniqueList.decorate(new ArrayList<String>());

        for (int filterId : filterIds) {
            addRules(filterId, rules, useCosmetics);
        }

        return rules;
    }

    @Override
    public void setFilterRules(int filterId, List<String> rules) {
        try {
            String fileName = getOrCreateFilterFile(filterId);
            context.deleteFile(fileName);
            OutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            IOUtils.writeLines(rules, null, outputStream);
        } catch (Exception ex) {
            log.error("Cannot insert new rules to filter {}:\r\n{}", filterId, ex);
            throw new RuntimeException("Cannot insert new rules to filter " + filterId, ex);
        }
    }

    /**
     * Gets or creates filter file
     *
     * @param filterId Filter identifier
     * @return Filter file name
     */
    private String getOrCreateFilterFile(int filterId) {
        String[] files = context.fileList();
        String fileName = "filter_" + filterId;

        if (!ArrayUtils.contains(files, fileName)) {
            initDefaultFilterRules(fileName);
        }

        return fileName;
    }

    /**
     * Adds rules from the specified filter to the list
     *
     * @param filterId Filter ID
     * @param rules    Rules
     * @return List of rules
     */
    private List<String> addRules(int filterId, List<String> rules, boolean useCosmetics) {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        try {
            String fileName = getOrCreateFilterFile(filterId);
            inputStream = context.openFileInput(fileName);
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);

            String line = reader.readLine();
            while (line != null) {
                if (useCosmetics || !FilterRule.isCosmeticRule(line)) {
                    rules.add(line);
                }
                line = reader.readLine();
            }

            return rules;
        } catch (Exception ex) {
            log.error("Cannot select rules for filter {}", filterId, ex);
            throw new RuntimeException("Cannot select rules for filter " + filterId, ex);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(inputStreamReader);
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * Initializes file with default filter rules
     *
     * @param fileName File name
     */
    private void initDefaultFilterRules(String fileName) {
        log.info("Initializing filter rules file {}", fileName);

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            int id = context.getResources().getIdentifier(fileName, "raw", context.getPackageName());
            context.deleteFile(fileName);
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            if (id > 0) {
                log.info("Found default filter rules. Writing to the file.");
                inputStream = context.getResources().openRawResource(id);
                IOUtils.copy(inputStream, outputStream);
            }
            log.info("Default filter has been initialized");
        } catch (Exception ex) {
            // TODO: Handle these exceptions -- use default filter rules
            log.error("Cannot init default filter:\r\n{}", ex);
            throw new RuntimeException("Cannot init default filter", ex);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }
}
