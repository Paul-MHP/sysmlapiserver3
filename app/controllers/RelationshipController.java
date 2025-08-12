/*
 * SysML v2 REST/HTTP Pilot Implementation
 * Copyright (C) 2020 InterCAX LLC
 * Copyright (C) 2020 California Institute of Technology ("Caltech")
 * Copyright (C) 2021 Twingineer LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * @license LGPL-3.0-or-later <http://spdx.org/licenses/LGPL-3.0-or-later>
 */

package controllers;

import config.MetamodelProvider;
import jackson.jsonld.DataJsonLdAdorner;
import jackson.jsonld.JsonLdAdorner;
import org.omg.sysml.metamodel.Relationship;
import org.omg.sysml.util.RelationshipDirection;
import play.Environment;
import play.mvc.Http.Request;
import play.mvc.Result;
import services.RelationshipService;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class RelationshipController extends JsonLdController<Relationship, DataJsonLdAdorner.Parameters> {

    private final RelationshipService relationshipService;
    private final MetamodelProvider metamodelProvider;
    private final JsonLdAdorner<Relationship, DataJsonLdAdorner.Parameters> adorner;

    @Inject
    public RelationshipController(RelationshipService relationshipService, MetamodelProvider metamodelProvider, Environment environment) {
        this.relationshipService = relationshipService;
        this.metamodelProvider = metamodelProvider;
        this.adorner = new DataJsonLdAdorner<>(metamodelProvider, environment, INLINE_JSON_LD_CONTEXT);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public Result getRelationshipsByProjectIdCommitIdRelatedElementId(UUID projectId, UUID commitId, UUID relatedElementId, Optional<String> direction, Optional<Boolean> excludeUsed, Request request) {
        PageRequest<UUID> pageRequest = uuidRequest(request);
        RelationshipDirection relationshipDirection = direction
                .flatMap(d -> Arrays.stream(RelationshipDirection.values())
                        .filter(rd -> rd.toString().equalsIgnoreCase(d))
                        .findAny())
                .orElse(RelationshipDirection.BOTH);

        List<Relationship> relationships = relationshipService.getRelationshipsByProjectCommitRelatedElement(
                projectId,
                commitId,
                relatedElementId,
                relationshipDirection,
                excludeUsed.orElse(false),
                pageRequest.getAfter(),
                pageRequest.getBefore(),
                pageRequest.getSize()
        );
        return buildPaginatedResult(relationships, projectId, commitId, request, pageRequest);
    }

    private Result buildPaginatedResult(List<Relationship> relationships, UUID projectId, UUID commitId, Request request, PageRequest pageRequest) {
        return uuidResponse(
                buildResult(relationships, List.class, metamodelProvider.getImplementationClass(Relationship.class), request, new DataJsonLdAdorner.Parameters(projectId, commitId)),
                relationships.size(),
                idx -> relationships.get(idx).getElementId(),
                request,
                pageRequest
        );
    }

    @Override
    protected JsonLdAdorner<Relationship, DataJsonLdAdorner.Parameters> getAdorner() {
        return adorner;
    }
}
