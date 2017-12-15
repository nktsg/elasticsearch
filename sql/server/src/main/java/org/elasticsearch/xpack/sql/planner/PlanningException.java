/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.sql.planner;

import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.xpack.sql.ClientSqlException;
import org.elasticsearch.xpack.sql.planner.Verifier.Failure;
import org.elasticsearch.xpack.sql.util.StringUtils;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class PlanningException extends ClientSqlException {

    private final Optional<RestStatus> status;

    public PlanningException(String message, Object... args) {
        super(message, args);
        status = Optional.empty();
    }

    public PlanningException(String message, RestStatus restStatus, Object... args) {
        super(message, args);
        status = Optional.of(restStatus);
    }

    public PlanningException(Collection<Failure> sources) {
        super(extractMessage(sources));
        status = Optional.empty();
    }

    private static String extractMessage(Collection<Failure> failures) {
        return failures.stream()
                .map(f -> format(Locale.ROOT, "line %s:%s: %s", f.source().location().getLineNumber(), f.source().location().getColumnNumber(), f.message()))
                .collect(Collectors.joining(StringUtils.NEW_LINE, "Found " + failures.size() + " problem(s)\n", StringUtils.EMPTY));
    }

    @Override
    public RestStatus status() {
        return status.orElse(super.status());
    }
}
