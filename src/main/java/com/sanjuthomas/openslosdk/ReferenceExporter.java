package com.sanjuthomas.openslosdk;

import com.sanjuthomas.openslo.OpenSloObject;
import com.sanjuthomas.openslo.Version;
import com.sanjuthomas.openslo.v1.AlertCondition;
import com.sanjuthomas.openslo.v1.AlertNotificationTarget;
import com.sanjuthomas.openslo.v1.AlertPolicy;
import com.sanjuthomas.openslo.v1.AlertPolicyCondition;
import com.sanjuthomas.openslo.v1.AlertPolicyNotificationTarget;
import com.sanjuthomas.openslo.v1.SLI;
import com.sanjuthomas.openslo.v1.SLO;
import com.sanjuthomas.openslo.v1.SLOAlertPolicy;
import com.sanjuthomas.openslo.v1.SLOIndicatorInline;

import java.util.ArrayList;
import java.util.List;

public final class ReferenceExporter {
    private ReferenceConfig config = ReferenceConfig.defaults();
    private final List<OpenSloObject> objects;
    private List<OpenSloObject> exported;
    private boolean done;

    public ReferenceExporter(List<OpenSloObject> objects) {
        this.objects = List.copyOf(objects);
    }

    public ReferenceExporter(OpenSloObject... objects) {
        this(List.of(objects));
    }

    public ReferenceExporter withConfig(ReferenceConfig config) {
        this.config = config;
        return this;
    }

    public List<OpenSloObject> export() {
        if (!done) {
            exported = exportObjects();
            done = true;
        }
        return exported;
    }

    private List<OpenSloObject> exportObjects() {
        exported = new ArrayList<>(objects.size());
        for (OpenSloObject object : objects) {
            exportObject(object);
        }
        return exported;
    }

    private void exportObject(OpenSloObject object) {
        if (object.getVersion() == Version.V1) {
            exported.addAll(exportV1Object(object));
        } else {
            exported.add(object);
        }
    }

    private List<OpenSloObject> exportV1Object(OpenSloObject object) {
        if (object instanceof AlertPolicy alertPolicy) {
            return exportV1AlertPolicy(alertPolicy);
        }
        if (object instanceof SLO slo) {
            return exportV1Slo(slo);
        }
        return List.of(object);
    }

    private List<OpenSloObject> exportV1AlertPolicy(AlertPolicy alertPolicy) {
        List<OpenSloObject> exported = new ArrayList<>();
        if (config.getV1().getAlertPolicy().isAlertNotificationTarget()) {
            exported.addAll(exportV1AlertPolicyTargets(alertPolicy));
        }
        if (config.getV1().getAlertPolicy().isAlertCondition()) {
            exported.addAll(exportV1AlertPolicyConditions(alertPolicy));
        }
        List<OpenSloObject> result = new ArrayList<>(exported.size() + 1);
        result.add(alertPolicy);
        result.addAll(exported);
        return result;
    }

    private List<OpenSloObject> exportV1AlertPolicyTargets(AlertPolicy alertPolicy) {
        List<OpenSloObject> exported = new ArrayList<>();
        List<AlertPolicyNotificationTarget> targets = alertPolicy.getSpec().getNotificationTargets();
        if (targets == null) {
            return exported;
        }
        for (int i = 0; i < targets.size(); i++) {
            AlertPolicyNotificationTarget target = targets.get(i);
            if (!target.isInline() || target.getMetadata() == null) {
                continue;
            }
            exported.add(AlertNotificationTarget.newAlertNotificationTarget(target.getMetadata(), target.getSpec()));
            String targetName = target.getMetadata().getName();
            target.clearInline();
            target.setTargetRef(targetName);
            targets.set(i, target);
        }
        return exported;
    }

    private List<OpenSloObject> exportV1AlertPolicyConditions(AlertPolicy alertPolicy) {
        List<OpenSloObject> exported = new ArrayList<>();
        List<AlertPolicyCondition> conditions = alertPolicy.getSpec().getConditions();
        if (conditions == null) {
            return exported;
        }
        for (int i = 0; i < conditions.size(); i++) {
            AlertPolicyCondition condition = conditions.get(i);
            if (!condition.isInline() || condition.getMetadata() == null) {
                continue;
            }
            exported.add(AlertCondition.newAlertCondition(condition.getMetadata(), condition.getSpec()));
            String conditionName = condition.getMetadata().getName();
            condition.clearInline();
            condition.setConditionRef(conditionName);
            conditions.set(i, condition);
        }
        return exported;
    }

    private List<OpenSloObject> exportV1Slo(SLO slo) {
        List<OpenSloObject> exported = new ArrayList<>();
        if (config.getV1().getSlo().isAlertPolicy()) {
            exported.addAll(exportV1SloAlertPolicies(slo));
        }
        if (config.getV1().getSlo().isSli()) {
            exported.addAll(exportV1SloSli(slo));
        }
        List<OpenSloObject> result = new ArrayList<>(exported.size() + 1);
        result.add(slo);
        result.addAll(exported);
        return result;
    }

    private List<OpenSloObject> exportV1SloAlertPolicies(SLO slo) {
        List<OpenSloObject> exported = new ArrayList<>();
        List<SLOAlertPolicy> alertPolicies = slo.getSpec().getAlertPolicies();
        if (alertPolicies == null) {
            return exported;
        }
        for (int i = 0; i < alertPolicies.size(); i++) {
            SLOAlertPolicy policy = alertPolicies.get(i);
            if (!policy.isInline()) {
                continue;
            }
            AlertPolicy alertPolicy = AlertPolicy.fromInline(policy.getKind(), policy.getMetadata(), policy.getSpec());
            exported.addAll(exportV1AlertPolicy(alertPolicy));
            String policyName = alertPolicy.getMetadata().getName();
            policy.clearInline();
            policy.setAlertPolicyRef(policyName);
            alertPolicies.set(i, policy);
        }
        return exported;
    }

    private List<OpenSloObject> exportV1SloSli(SLO slo) {
        SLOIndicatorInline indicator = slo.getSpec().getIndicator();
        if (indicator == null) {
            return List.of();
        }
        SLI sli = SLI.newSli(indicator.getMetadata(), indicator.getSpec());
        String indicatorName = indicator.getMetadata().getName();
        slo.getSpec().setIndicatorRef(indicatorName);
        slo.getSpec().setIndicator(null);
        return List.of(sli);
    }
}
