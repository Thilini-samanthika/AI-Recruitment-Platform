package com.recruitment.job.entity;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Application Status State Machine
 *
 * Lifecycle flow:
 * APPLIED -> REVIEWED | SHORTLISTED | REJECTED | WITHDRAWN
 * REVIEWED -> SHORTLISTED | INTERVIEW | REJECTED | WITHDRAWN
 * SHORTLISTED -> INTERVIEW | OFFERED | REJECTED | WITHDRAWN
 * INTERVIEW -> OFFERED | REJECTED | WITHDRAWN
 * OFFERED -> ACCEPTED | REJECTED | WITHDRAWN
 * ACCEPTED, REJECTED, WITHDRAWN -> Terminal States (no further transitions permitted)
 */
public enum ApplicationStatus {
    APPLIED,
    REVIEWED,
    SHORTLISTED,
    INTERVIEW,
    OFFERED,
    ACCEPTED,
    REJECTED,
    WITHDRAWN;

    /**
     * Checks whether transition from current status to nextStatus is permissible.
     */
    public boolean canTransitionTo(ApplicationStatus nextStatus) {
        if (nextStatus == null) {
            return false;
        }
        if (this == nextStatus) {
            return true; // Idempotent transition
        }

        return getAllowedTransitions().contains(nextStatus);
    }

    /**
     * Returns the set of valid next statuses reachable from the current state.
     */
    public Set<ApplicationStatus> getAllowedTransitions() {
        return switch (this) {
            case APPLIED -> EnumSet.of(REVIEWED, SHORTLISTED, REJECTED, WITHDRAWN);
            case REVIEWED -> EnumSet.of(SHORTLISTED, INTERVIEW, REJECTED, WITHDRAWN);
            case SHORTLISTED -> EnumSet.of(INTERVIEW, OFFERED, REJECTED, WITHDRAWN);
            case INTERVIEW -> EnumSet.of(OFFERED, REJECTED, WITHDRAWN);
            case OFFERED -> EnumSet.of(ACCEPTED, REJECTED, WITHDRAWN);
            case ACCEPTED, REJECTED, WITHDRAWN -> Collections.emptySet();
        };
    }

    /**
     * Checks if this status is a terminal state.
     */
    public boolean isTerminal() {
        return this == ACCEPTED || this == REJECTED || this == WITHDRAWN;
    }

    /**
     * Validates transition and throws an exception with a descriptive error message if invalid.
     */
    public void validateTransitionTo(ApplicationStatus nextStatus) {
        if (!canTransitionTo(nextStatus)) {
            if (isTerminal()) {
                throw new IllegalStateException(
                        String.format("Cannot transition application from terminal state '%s' to '%s'. Terminal states cannot be modified.",
                                this, nextStatus)
                );
            }
            throw new IllegalStateException(
                    String.format("Illegal status transition from '%s' to '%s'. Allowed transitions from '%s' are: %s",
                            this, nextStatus, this, getAllowedTransitions())
            );
        }
    }
}
