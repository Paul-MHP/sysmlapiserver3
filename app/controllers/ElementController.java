/*
 * SysML v2 REST/HTTP Pilot Implementation
 * Copyright (C) 2020 InterCAX LLC
 * Copyright (C) 2020 California Institute of Technology ("Caltech")
 * Copyright (C) 2021-2022 Twingineer LLC
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

import com.fasterxml.jackson.databind.JsonNode;
import config.MetamodelProvider;
import jackson.JacksonHelper;
import jackson.jsonld.DataJsonLdAdorner;
import jackson.jsonld.JsonLdAdorner;
import org.omg.sysml.data.ProjectUsage;
import org.omg.sysml.metamodel.Element;
import play.Environment;
import play.libs.Json;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.mvc.Results;
import services.ElementService;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class ElementController extends JsonLdController<Element, DataJsonLdAdorner.Parameters> {

    private final ElementService elementService;
    private final MetamodelProvider metamodelProvider;
    private final JsonLdAdorner<Element, DataJsonLdAdorner.Parameters> adorner;

    @Inject
    public ElementController(ElementService elementService, MetamodelProvider metamodelProvider, Environment environment) {
        this.elementService = elementService;
        this.metamodelProvider = metamodelProvider;
        this.adorner = new DataJsonLdAdorner<>(metamodelProvider, environment, INLINE_JSON_LD_CONTEXT);
    }

    public Result getElementsByProjectIdCommitId(UUID projectId, UUID commitId, Optional<Boolean> excludeUsed, Request request) {
        PageRequest<UUID> pageRequest = uuidRequest(request);
        List<Element> elements = elementService.getElementsByProjectIdCommitId(projectId, commitId, excludeUsed.orElse(false), pageRequest.getAfter(), pageRequest.getBefore(), pageRequest.getSize());
        return buildPaginatedResult(elements, projectId, commitId, request, pageRequest);
    }

    public Result getElementByProjectIdCommitIdElementId(UUID projectId, UUID commitId, UUID elementId, Optional<Boolean> excludeUsed, Request request) {
        Optional<Element> element = elementService.getElementByProjectIdCommitIdElementId(projectId, commitId, elementId, excludeUsed.orElse(false));
        return buildResult(element.orElse(null), request, new DataJsonLdAdorner.Parameters(projectId, commitId));
    }

    public Result getProjectUsageByProjectIdCommitIdElementId(UUID projectId, UUID commitId, UUID elementId, Request request) {
        if (respondWithJsonLd(request)) {
            // TODO implement
            return Results.status(NOT_IMPLEMENTED);
        }
        Optional<ProjectUsage> projectUsage = elementService.getProjectUsageByProjectIdCommitIdElementId(projectId, commitId, elementId);
        if (projectUsage.isEmpty()) {
            return Results.notFound();
        }
        JsonNode json = JacksonHelper.objectToTree(
                projectUsage,
                Json.mapper()
        );
        return Results.ok(json);
    }

    public Result getRootsByProjectIdCommitId(UUID projectId, UUID commitId, Optional<Boolean> excludeUsed, Request request) {
        PageRequest<UUID> pageRequest = uuidRequest(request);
        List<Element> roots = elementService.getRootsByProjectIdCommitId(projectId, commitId, excludeUsed.orElse(false), pageRequest.getAfter(), pageRequest.getBefore(), pageRequest.getSize());
        return buildPaginatedResult(roots, projectId, commitId, request, pageRequest);
    }

    public Result getElementByProjectIdCommitIdQualifiedName(UUID projectId, UUID commitId, String qualifiedName, Request request) {
        Optional<Element> element = elementService.getElementByProjectIdCommitIdQualifiedName(projectId, commitId, qualifiedName);
        return buildResult(element.orElse(null), request, new DataJsonLdAdorner.Parameters(projectId, commitId));
    }

    private Result buildPaginatedResult(List<Element> elements, UUID projectId, UUID commitId, Request request, PageRequest<UUID> pageRequest) {
        return uuidResponse(
                buildResult(elements, List.class, metamodelProvider.getImplementationClass(Element.class), request, new DataJsonLdAdorner.Parameters(projectId, commitId)),
                elements.size(),
                idx -> elements.get(idx).getElementId(),
                request,
                pageRequest
        );
    }

    @Override
    protected JsonLdAdorner<Element, DataJsonLdAdorner.Parameters> getAdorner() {
        return adorner;
    }
}
