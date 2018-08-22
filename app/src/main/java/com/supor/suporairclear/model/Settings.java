package com.supor.suporairclear.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/26.
 */

public class Settings {

    public class Language {
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getAvailable_lang() {
            return available_lang;
        }

        public void setAvailable_lang(List<String> available_lang) {
            this.available_lang = available_lang;
        }

        public String getDefault_lang() {
            return default_lang;
        }

        public void setDefault_lang(String default_lang) {
            this.default_lang = default_lang;
        }

        public Language() {
        }

        private String name;
        private List<String> available_lang;
        private String default_lang;
    }

    public String getFallback_market() {
        return fallback_market;
    }

    public void setFallback_market(String fallback_market) {
        this.fallback_market = fallback_market;
    }

    public Boolean getMust_upgrade() {
        return must_upgrade;
    }

    public void setMust_upgrade(Boolean must_upgrade) {
        this.must_upgrade = must_upgrade;
    }

    public List<Language> getMarkets() {
        return markets;
    }

    public void setMarkets(List<Language> markets) {
        this.markets = markets;
    }

    public Settings() {
    }

    private String fallback_market;
    private Boolean must_upgrade;
    private List<Language> markets = new ArrayList< Language>();

}
