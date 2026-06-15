package com.sanjuthomas.openslosdk;

import com.sanjuthomas.openslo.Kind;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ReferenceInliner {
    private ReferenceConfig config = ReferenceConfig.defaults();
    private final List<OpenSloObject> objects;
    private List<OpenSloObject> references;
    private List<OpenSloObject> inlined;
    private final Map<Integer, Boolean> referencedObjectIndexes = new HashMap<>();
    private boolean removeRefs;
    private boolean done;
    private List<OpenSloObject> result;
    private Exception error;

    public ReferenceInliner(List<OpenSloObject> objects) {
        this.objects = List.copyOf(objects);
    }

    public ReferenceInliner(OpenSloObject... objects) {
        this(List.of(objects));
    }

    public ReferenceInliner withConfig(ReferenceConfig config) {
        this.config = config;
        return this;
    }

    public ReferenceInliner removeReferencedObjects() {
        this.removeRefs = true;
        return this;
    }

    public List<OpenSloObject> inline() throws ReferenceNotFoundException {
        if (!done) {
            try {
                result = inlineObjects();
            } catch (ReferenceNotFoundException ex) {
                error = ex;
            }
            done = true;
        }
        if (error != null) {
            throw (ReferenceNotFoundException) error;
        }
        return result;
    }

    private List<OpenSloObject> inlineObjects() throws ReferenceNotFoundException {
        references = objects;
        inlined = new ArrayList<>(objects.size());
        for (OpenSloObject object : objects) {
            inlineObject(object);
        }
        if (removeRefs) {
            return filterInlinedWithoutReferences();
        }
        return inlined;
    }

    private void inlineObject(OpenSloObject object) throws ReferenceNotFoundException {
        if (object.getVersion() == Version.V1) {
            addResult(inlineV1Object(object));
        } else {
            addResult(object);
        }
    }

    private OpenSloObject inlineV1Object(OpenSloObject object) throws ReferenceNotFoundException {
        if (object instanceof SLO slo) {
            return inlineV1Slo(slo);
        }
        if (object instanceof AlertPolicy alertPolicy) {
            return inlineV1AlertPolicy(alertPolicy);
        }
        return object;
    }

    private AlertPolicy inlineV1AlertPolicy(AlertPolicy alertPolicy) throws ReferenceNotFoundException {
        if (config.getV1().getAlertPolicy().isAlertNotificationTarget()) {
            inlineV1AlertPolicyTargets(alertPolicy);
        }
        if (config.getV1().getAlertPolicy().isAlertCondition()) {
            inlineV1AlertPolicyConditions(alertPolicy);
        }
        return alertPolicy;
    }

    private void inlineV1AlertPolicyTargets(AlertPolicy alertPolicy) throws ReferenceNotFoundException {
        List<AlertPolicyNotificationTarget> targets = alertPolicy.getSpec().getNotificationTargets();
        for (int i = 0; i < targets.size(); i++) {
            AlertPolicyNotificationTarget target = targets.get(i);
            if (!target.isRef()) {
                continue;
            }
            FoundObject<AlertNotificationTarget> found =
                    findObject(references, target.getTargetRef(), AlertNotificationTarget.class, Kind.ALERT_NOTIFICATION_TARGET);
            if (found.index() < 0) {
                throw new ReferenceNotFoundException(
                        Kind.ALERT_NOTIFICATION_TARGET,
                        "spec.notificationTargets[" + i + "].targetRef",
                        target.getTargetRef());
            }
            target.clearRef();
            target.setKind(found.object().getKind());
            target.setMetadata(found.object().getMetadata());
            target.setSpec(found.object().getSpec());
            referencedObjectIndexes.put(found.index(), true);
            targets.set(i, target);
        }
    }

    private void inlineV1AlertPolicyConditions(AlertPolicy alertPolicy) throws ReferenceNotFoundException {
        List<AlertPolicyCondition> conditions = alertPolicy.getSpec().getConditions();
        for (int i = 0; i < conditions.size(); i++) {
            AlertPolicyCondition condition = conditions.get(i);
            if (!condition.isRef()) {
                continue;
            }
            FoundObject<AlertCondition> found =
                    findObject(references, condition.getConditionRef(), AlertCondition.class, Kind.ALERT_CONDITION);
            if (found.index() < 0) {
                throw new ReferenceNotFoundException(
                        Kind.ALERT_CONDITION,
                        "spec.conditions[" + i + "].conditionRef",
                        condition.getConditionRef());
            }
            condition.clearRef();
            condition.setKind(found.object().getKind());
            condition.setMetadata(found.object().getMetadata());
            condition.setSpec(found.object().getSpec());
            referencedObjectIndexes.put(found.index(), true);
            conditions.set(i, condition);
        }
    }

    private SLO inlineV1Slo(SLO slo) throws ReferenceNotFoundException {
        if (config.getV1().getSlo().isAlertPolicy()) {
            inlineV1SloAlertPolicies(slo);
        }
        if (config.getV1().getSlo().isSli()) {
            inlineV1SloSli(slo);
        }
        return slo;
    }

    private void inlineV1SloAlertPolicies(SLO slo) throws ReferenceNotFoundException {
        List<SLOAlertPolicy> alertPolicies = slo.getSpec().getAlertPolicies();
        if (alertPolicies == null) {
            return;
        }
        for (int i = 0; i < alertPolicies.size(); i++) {
            SLOAlertPolicy policy = alertPolicies.get(i);
            AlertPolicy alertPolicy;
            if (policy.isInline()) {
                alertPolicy = AlertPolicy.fromInline(policy.getKind(), policy.getMetadata(), policy.getSpec());
            } else {
                FoundObject<AlertPolicy> found =
                        findObject(references, policy.getAlertPolicyRef(), AlertPolicy.class, Kind.ALERT_POLICY);
                if (found.index() < 0) {
                    throw new ReferenceNotFoundException(
                            Kind.ALERT_POLICY,
                            "spec.alertPolicies[" + i + "].alertPolicyRef",
                            policy.getAlertPolicyRef());
                }
                alertPolicy = found.object();
                referencedObjectIndexes.put(found.index(), true);
            }
            try {
                alertPolicy = inlineV1AlertPolicy(alertPolicy);
            } catch (ReferenceNotFoundException ex) {
                throw ex.withFieldPathPrefix("spec.alertPolicies[" + i + "].");
            }
            policy.clearRef();
            policy.setKind(alertPolicy.getKind());
            policy.setMetadata(alertPolicy.getMetadata());
            policy.setSpec(alertPolicy.getSpec());
            alertPolicies.set(i, policy);
        }
    }

    private void inlineV1SloSli(SLO slo) throws ReferenceNotFoundException {
        String indicatorRef = slo.getSpec().getIndicatorRef();
        if (indicatorRef == null) {
            return;
        }
        FoundObject<SLI> found = findObject(references, indicatorRef, SLI.class, Kind.SLI);
        if (found.index() < 0) {
            throw new ReferenceNotFoundException(Kind.SLI, "spec.indicatorRef", indicatorRef);
        }
        slo.getSpec().setIndicatorRef(null);
        SLOIndicatorInline indicator = new SLOIndicatorInline();
        indicator.setMetadata(found.object().getMetadata());
        indicator.setSpec(found.object().getSpec());
        slo.getSpec().setIndicator(indicator);
        referencedObjectIndexes.put(found.index(), true);
    }

    private List<OpenSloObject> filterInlinedWithoutReferences() {
        List<OpenSloObject> filtered = new ArrayList<>(inlined.size());
        for (int i = 0; i < inlined.size(); i++) {
            if (!referencedObjectIndexes.containsKey(i)) {
                filtered.add(inlined.get(i));
            }
        }
        return filtered;
    }

    private void addResult(OpenSloObject object) {
        inlined.add(object);
    }

    private static <T extends OpenSloObject> FoundObject<T> findObject(
            List<OpenSloObject> objects, String name, Class<T> type, Kind expectedKind) {
        for (int i = 0; i < objects.size(); i++) {
            OpenSloObject object = objects.get(i);
            if (!name.equals(object.getName())) {
                continue;
            }
            if (type.isInstance(object)) {
                return new FoundObject<>(type.cast(object), i);
            }
        }
        return new FoundObject<>(null, -1);
    }

    private record FoundObject<T>(T object, int index) {}
}
