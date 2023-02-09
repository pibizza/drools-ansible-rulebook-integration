package org.drools.ansible.rulebook.integration.api.rulesengine;

import java.time.Instant;

import org.drools.core.facttemplates.Event;

import static java.util.function.Predicate.not;

public class SessionStats {
    private final Instant start;
    private final Instant end;

    private final int numberOfRules;
    private final int rulesTriggered;

    private final int eventsProcessed;
    private final int eventsMatched;
    private final int eventsSuppressed;

    private final int permanentStorageSize;

    private final int asyncResponses;
    private final int bytesSentOnAsync;

    public SessionStats(SessionStatsCollector stats, RulesExecutorSession session) {
        this.start = stats.getStart();
        this.end = Instant.now();
        this.numberOfRules = (int) session.rulesCount();
        this.rulesTriggered = stats.getRulesTriggered();
        this.eventsProcessed = stats.getTotalEvents();
        this.eventsMatched = stats.getMatchedEvents();
        this.eventsSuppressed = this.eventsProcessed - this.eventsMatched;
        this.permanentStorageSize = (int) session.getObjects().stream().filter(not(Event.class::isInstance)).count();
        this.asyncResponses = stats.getAsyncResponses();
        this.bytesSentOnAsync = stats.getBytesSentOnAsync();
    }

    public SessionStats(Instant start, Instant end, int numberOfRules, int rulesTriggered, int eventsProcessed,
                        int eventsMatched, int eventsSuppressed, int permanentStorageSize, int asyncResponses, int bytesSentOnAsync) {
        this.start = start;
        this.end = end;
        this.numberOfRules = numberOfRules;
        this.rulesTriggered = rulesTriggered;
        this.eventsProcessed = eventsProcessed;
        this.eventsMatched = eventsMatched;
        this.eventsSuppressed = eventsSuppressed;
        this.permanentStorageSize = permanentStorageSize;
        this.asyncResponses = asyncResponses;
        this.bytesSentOnAsync = bytesSentOnAsync;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }

    public int getNumberOfRules() {
        return numberOfRules;
    }

    public int getRulesTriggered() {
        return rulesTriggered;
    }

    public int getEventsProcessed() {
        return eventsProcessed;
    }

    public int getEventsMatched() {
        return eventsMatched;
    }

    public int getEventsSuppressed() {
        return eventsSuppressed;
    }

    public int getPermanentStorageSize() {
        return permanentStorageSize;
    }

    public int getAsyncResponses() {
        return asyncResponses;
    }

    public int getBytesSentOnAsync() {
        return bytesSentOnAsync;
    }

    public static SessionStats aggregate(SessionStats stats1, SessionStats stats2) {
        return new SessionStats(
                stats1.getStart().compareTo(stats2.getStart()) < 0 ? stats1.getStart() : stats2.getStart(),
                stats1.getEnd().compareTo(stats2.getEnd()) > 0 ? stats1.getStart() : stats2.getStart(),
                stats1.numberOfRules + stats2.numberOfRules,
                stats1.rulesTriggered + stats2.rulesTriggered,
                stats1.eventsProcessed + stats2.eventsProcessed,
                stats1.eventsMatched + stats2.eventsMatched,
                stats1.eventsSuppressed + stats2.eventsSuppressed,
                stats1.permanentStorageSize + stats2.permanentStorageSize,
                stats1.asyncResponses + stats2.asyncResponses,
                stats1.bytesSentOnAsync + stats2.bytesSentOnAsync
        );
    }
}