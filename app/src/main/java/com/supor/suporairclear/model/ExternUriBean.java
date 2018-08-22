package com.supor.suporairclear.model;

import java.util.List;

public class ExternUriBean {

        /**
         * meta : {"is_anonymous":false,"latest":1519978939,"limit":1000,"offset":1000,"page":1,"pages":1,"total_count":1}
         * objects : [{"id":"3147","title":"Consumer Service Center","type":"EXTERNAL_URL","domain":"PRO_AIR","state":"VALIDATED","externalLink":"http://www.rowenta.com/consumer-services/warranty-and-repairs/repairers","appliances":["APPLIANCE_1044","APPLIANCE_1045"]}]
         */

        private MetaBean meta;
        private List<ObjectsBean> objects;

        public MetaBean getMeta() {
            return meta;
        }

        public void setMeta(MetaBean meta) {
            this.meta = meta;
        }

        public List<ObjectsBean> getObjects() {
            return objects;
        }

        public void setObjects(List<ObjectsBean> objects) {
            this.objects = objects;
        }

        public static class MetaBean {
            /**
             * is_anonymous : false
             * latest : 1519978939
             * limit : 1000
             * offset : 1000
             * page : 1
             * pages : 1
             * total_count : 1
             */

            private boolean is_anonymous;
            private int latest;
            private int limit;
            private int offset;
            private int page;
            private int pages;
            private int total_count;

            public boolean isIs_anonymous() {
                return is_anonymous;
            }

            public void setIs_anonymous(boolean is_anonymous) {
                this.is_anonymous = is_anonymous;
            }

            public int getLatest() {
                return latest;
            }

            public void setLatest(int latest) {
                this.latest = latest;
            }

            public int getLimit() {
                return limit;
            }

            public void setLimit(int limit) {
                this.limit = limit;
            }

            public int getOffset() {
                return offset;
            }

            public void setOffset(int offset) {
                this.offset = offset;
            }

            public int getPage() {
                return page;
            }

            public void setPage(int page) {
                this.page = page;
            }

            public int getPages() {
                return pages;
            }

            public void setPages(int pages) {
                this.pages = pages;
            }

            public int getTotal_count() {
                return total_count;
            }

            public void setTotal_count(int total_count) {
                this.total_count = total_count;
            }
        }

        public static class ObjectsBean {
            /**
             * id : 3147
             * title : Consumer Service Center
             * type : EXTERNAL_URL
             * domain : PRO_AIR
             * state : VALIDATED
             * externalLink : http://www.rowenta.com/consumer-services/warranty-and-repairs/repairers
             * appliances : ["APPLIANCE_1044","APPLIANCE_1045"]
             */

            private String id;
            private String title;
            private String type;
            private String domain;
            private String state;
            private String externalLink;
            private List<String> appliances;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getDomain() {
                return domain;
            }

            public void setDomain(String domain) {
                this.domain = domain;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getExternalLink() {
                return externalLink;
            }

            public void setExternalLink(String externalLink) {
                this.externalLink = externalLink;
            }

            public List<String> getAppliances() {
                return appliances;
            }

            public void setAppliances(List<String> appliances) {
                this.appliances = appliances;
            }
        }
    }