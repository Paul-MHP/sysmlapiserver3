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

package services;

import dao.*;
import jackson.filter.AllowedPropertyFilter;
import org.omg.sysml.lifecycle.Commit;
import org.omg.sysml.lifecycle.Data;
import org.omg.sysml.lifecycle.Project;
import org.omg.sysml.query.Query;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Singleton
public class QueryService extends BaseService<Query, QueryDao> {

    private final ProjectDao projectDao;
    private final CommitDao commitDao;
    private final DataDao dataDao;

    @Inject
    public QueryService(QueryDao queryDao, ProjectDao projectDao, CommitDao commitDao, DataDao dataDao) {
        super(queryDao);
        this.projectDao = projectDao;
        this.commitDao = commitDao;
        this.dataDao = dataDao;
    }

    public Optional<Query> create(Query query) {
        return query.getId() != null ? update(query) : persist(query);
    }

    public Optional<Query> create(UUID projectId, Query query) {
        query.setOwningProject(projectDao.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Project " + projectId + " not found")));
        return create(query);
    }

    public List<Query> getByProjectId(UUID projectId, @Nullable UUID after, @Nullable UUID before, int maxResults) {
        return projectDao.findById(projectId)
                .map(project -> dao.findAllByProject(project, after, before, maxResults))
                .orElse(Collections.emptyList());
    }

    public Optional<Query> getByProjectIdAndId(UUID projectId, UUID queryId) {
        return projectDao.findById(projectId).flatMap(project -> dao.findByProjectAndId(project, queryId));
    }

    public Optional<Query> deleteByProjectIdAndId(UUID projectId, UUID queryId) {
        return projectDao.findById(projectId).flatMap(project -> dao.deleteByProjectAndId(project, queryId));
    }

    public QueryResults getQueryResultsByProjectIdQueryId(UUID projectId, UUID queryId, @Nullable UUID commitId) {
        return getQueryResults(projectId, project -> dao.findByProjectAndId(project, queryId).orElseThrow(() -> new IllegalArgumentException("Query " + queryId + " not found")), commitId);
    }

    public QueryResults getQueryResultsByProjectIdQuery(UUID projectId, Query query, @Nullable UUID commitId) {
        return getQueryResults(projectId, project -> query, commitId);
    }

    private QueryResults getQueryResults(UUID projectId, Function<Project, Query> queryFunction, @Nullable UUID commitId) {
        Project project = projectDao.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Project " + projectId + " not found"));

        final Commit commit;
        if (commitId != null) {
            commit = commitDao.findByProjectAndId(project, commitId).orElseThrow(() -> new IllegalArgumentException("Commit " + commitId + " not found"));
        }
        else if (project.getDefaultBranch() == null) {
            throw new IllegalStateException("Project does not have a default branch");
        }
        else if (project.getDefaultBranch().getHead() == null) {
            throw new IllegalStateException("Project's default branch does not have a head");
        }
        else {
            commit = project.getDefaultBranch().getHead();
        }

        Query query = queryFunction.apply(project);
        AllowedPropertyFilter propertyFilter = getPropertyFilter(query);
        return new QueryResults(dataDao.findByCommitAndQuery(commit, query), commit, propertyFilter);
    }

    public static class QueryResults {
        private final List<Data> data;
        private final Commit commit;
        private final AllowedPropertyFilter propertyFilter;

        public QueryResults(List<Data> data, Commit commit, AllowedPropertyFilter propertyFilter) {
            this.data = data;
            this.commit = commit;
            this.propertyFilter = propertyFilter;
        }

        public List<Data> getData() {
            return data;
        }

        public Commit getCommit() {
            return commit;
        }

        public AllowedPropertyFilter getPropertyFilter() {
            return propertyFilter;
        }
    }

    private AllowedPropertyFilter getPropertyFilter(Query query) {
        if (query.getSelect() == null || query.getSelect().isEmpty()) {
            return null;
        }
        return new AllowedPropertyFilter(query.getSelect());
    }

}
