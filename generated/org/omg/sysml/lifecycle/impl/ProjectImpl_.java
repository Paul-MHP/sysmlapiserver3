package org.omg.sysml.lifecycle.impl;

import java.time.ZonedDateTime;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ProjectImpl.class)
public abstract class ProjectImpl_ extends org.omg.sysml.record.impl.RecordImpl_ {

	public static volatile SingularAttribute<ProjectImpl, ZonedDateTime> created;
	public static volatile SingularAttribute<ProjectImpl, BranchImpl> defaultBranch;
	public static volatile SingularAttribute<ProjectImpl, String> name;
	public static volatile SingularAttribute<ProjectImpl, String> description;

	public static final String CREATED = "created";
	public static final String DEFAULT_BRANCH = "defaultBranch";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";

}

