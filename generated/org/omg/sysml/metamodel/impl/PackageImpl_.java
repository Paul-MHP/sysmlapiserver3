package org.omg.sysml.metamodel.impl;

import java.util.UUID;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.omg.sysml.metamodel.Annotation;
import org.omg.sysml.metamodel.Documentation;
import org.omg.sysml.metamodel.Element;
import org.omg.sysml.metamodel.Expression;
import org.omg.sysml.metamodel.Import;
import org.omg.sysml.metamodel.Membership;
import org.omg.sysml.metamodel.Relationship;
import org.omg.sysml.metamodel.TextualRepresentation;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(PackageImpl.class)
public abstract class PackageImpl_ extends org.omg.sysml.lifecycle.impl.DataImpl_ {

	public static volatile SingularAttribute<PackageImpl, UUID> elementId;
	public static volatile ListAttribute<PackageImpl, TextualRepresentation> textualRepresentation;
	public static volatile SingularAttribute<PackageImpl, String> declaredShortName;
	public static volatile ListAttribute<PackageImpl, Membership> importedMembership;
	public static volatile SingularAttribute<PackageImpl, Boolean> isLibraryElement;
	public static volatile ListAttribute<PackageImpl, Annotation> ownedAnnotation;
	public static volatile ListAttribute<PackageImpl, Element> ownedElement;
	public static volatile ListAttribute<PackageImpl, String> aliasIds;
	public static volatile ListAttribute<PackageImpl, Relationship> ownedRelationship;
	public static volatile SingularAttribute<PackageImpl, String> qualifiedName;
	public static volatile ListAttribute<PackageImpl, Documentation> documentation;
	public static volatile ListAttribute<PackageImpl, Membership> membership;
	public static volatile ListAttribute<PackageImpl, Import> ownedImport;
	public static volatile SingularAttribute<PackageImpl, Boolean> isImpliedIncluded;
	public static volatile ListAttribute<PackageImpl, Expression> filterCondition;
	public static volatile ListAttribute<PackageImpl, Element> member;
	public static volatile SingularAttribute<PackageImpl, String> name;
	public static volatile SingularAttribute<PackageImpl, String> declaredName;
	public static volatile SingularAttribute<PackageImpl, String> shortName;
	public static volatile ListAttribute<PackageImpl, Element> ownedMember;
	public static volatile ListAttribute<PackageImpl, Membership> ownedMembership;

	public static final String ELEMENT_ID = "elementId";
	public static final String TEXTUAL_REPRESENTATION = "textualRepresentation";
	public static final String DECLARED_SHORT_NAME = "declaredShortName";
	public static final String IMPORTED_MEMBERSHIP = "importedMembership";
	public static final String IS_LIBRARY_ELEMENT = "isLibraryElement";
	public static final String OWNED_ANNOTATION = "ownedAnnotation";
	public static final String OWNED_ELEMENT = "ownedElement";
	public static final String ALIAS_IDS = "aliasIds";
	public static final String OWNED_RELATIONSHIP = "ownedRelationship";
	public static final String QUALIFIED_NAME = "qualifiedName";
	public static final String DOCUMENTATION = "documentation";
	public static final String MEMBERSHIP = "membership";
	public static final String OWNED_IMPORT = "ownedImport";
	public static final String IS_IMPLIED_INCLUDED = "isImpliedIncluded";
	public static final String FILTER_CONDITION = "filterCondition";
	public static final String MEMBER = "member";
	public static final String NAME = "name";
	public static final String DECLARED_NAME = "declaredName";
	public static final String SHORT_NAME = "shortName";
	public static final String OWNED_MEMBER = "ownedMember";
	public static final String OWNED_MEMBERSHIP = "ownedMembership";

}

