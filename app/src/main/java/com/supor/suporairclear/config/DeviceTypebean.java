package com.supor.suporairclear.config;

public class DeviceTypebean {
        private String deviceType;
        private String deviceName;
        private String deivceIconUrl;
        private String subDominId;  //并不知道他们为啥是String
        private String subDominName;

        public String getSubDominId() {
            return subDominId;
        }

        public void setSubDominId(String subDominId) {
            this.subDominId = subDominId;
        }

        public String getSubDominName() {
            return subDominName;
        }

        public void setSubDominName(String subDominName) {
            this.subDominName = subDominName;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getDeivceIconUrl() {
            return deivceIconUrl;
        }

        public void setDeivceIconUrl(String deivceIconUrl) {
            this.deivceIconUrl = deivceIconUrl;
        }
    }
