package com.sanjuthomas.openslosdk;

/**
 * Configures OpenSLO object reference resolution for {@link ReferenceInliner} and {@link ReferenceExporter}.
 */
public class ReferenceConfig {
    private ReferenceConfigV1 v1 = ReferenceConfigV1.defaults();

    public ReferenceConfigV1 getV1() {
        return v1;
    }

    public void setV1(ReferenceConfigV1 v1) {
        this.v1 = v1;
    }

    public static ReferenceConfig defaults() {
        return new ReferenceConfig();
    }

    public static class ReferenceConfigV1 {
        private ReferenceConfigV1SLO slo = ReferenceConfigV1SLO.defaults();
        private ReferenceConfigV1AlertPolicy alertPolicy = ReferenceConfigV1AlertPolicy.defaults();

        public ReferenceConfigV1SLO getSlo() {
            return slo;
        }

        public void setSlo(ReferenceConfigV1SLO slo) {
            this.slo = slo;
        }

        public ReferenceConfigV1AlertPolicy getAlertPolicy() {
            return alertPolicy;
        }

        public void setAlertPolicy(ReferenceConfigV1AlertPolicy alertPolicy) {
            this.alertPolicy = alertPolicy;
        }

        static ReferenceConfigV1 defaults() {
            return new ReferenceConfigV1();
        }
    }

    public static class ReferenceConfigV1SLO {
        private boolean alertPolicy = true;
        private boolean sli = true;

        public boolean isAlertPolicy() {
            return alertPolicy;
        }

        public void setAlertPolicy(boolean alertPolicy) {
            this.alertPolicy = alertPolicy;
        }

        public boolean isSli() {
            return sli;
        }

        public void setSli(boolean sli) {
            this.sli = sli;
        }

        static ReferenceConfigV1SLO defaults() {
            return new ReferenceConfigV1SLO();
        }
    }

    public static class ReferenceConfigV1AlertPolicy {
        private boolean alertCondition = true;
        private boolean alertNotificationTarget = true;

        public boolean isAlertCondition() {
            return alertCondition;
        }

        public void setAlertCondition(boolean alertCondition) {
            this.alertCondition = alertCondition;
        }

        public boolean isAlertNotificationTarget() {
            return alertNotificationTarget;
        }

        public void setAlertNotificationTarget(boolean alertNotificationTarget) {
            this.alertNotificationTarget = alertNotificationTarget;
        }

        static ReferenceConfigV1AlertPolicy defaults() {
            return new ReferenceConfigV1AlertPolicy();
        }
    }
}
