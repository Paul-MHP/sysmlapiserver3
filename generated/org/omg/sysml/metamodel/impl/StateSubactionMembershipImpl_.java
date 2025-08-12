package org.omg.sysml.metamodel.impl;

import java.util.UUID;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.omg.sysml.metamodel.Annotation;
import org.omg.sysml.metamodel.Documentation;
import org.omg.sysml.metamodel.Element;
import org.omg.sysml.metamodel.Relationship;
import org.omg.sysml.metamodel.StateSubactionKind;
import org.omg.sysml.metamodel.TextualRepresentation;
import org.omg.sysml.metamodel.VisibilityKind;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(StateSubactionMembershipImpl.class)
public abstract class StateSubactionMembershipImpl_ extends org.omg.sysml.lifecycle.impl.DataImpl_ {

	public static volatile SingularAttribute<StateSubactionMembershipImpl, String> declaredShortName;
	public static volatile SingularAttribute<StateSubactionMembershipImpl, String> ownedMemberElementId;
	public static volatile SingularAttribute<StateSubactionMembershipImpl, String> memberName;
	public static volatile ListAttribute<StateSubactionMembershipImpl, Element> ownedRelatedElement;
	public static volatile ListAttribute<StateSubactionMembershipImpl, Element> source;
	public static volatile SingularAttribute<StateSubactionMembershipImpl, String> memberElementId;
	public static volatile SingularAttribute<StateSubactionMembershipImpl, Boolean> isImpliedIncluded;
	public static volatile SingularAttribute<StateSubactionMembershipImpl, String> declaredName;
	public static volatile SingularAttribute<StateSubactionMembershipImpl, String> memberShortName;
	public static volatile SingularAttribute<StateSubactionMembershipImpl, String> ownedMemberName;
	public static volatile SingularAttribute<StateSubactionMembershipImpl, String> ownedMemberShortName;
	public static volatile SingularAttribute<StateSubactionMembershipImpl, UUID> elementId;
	public static volatile ListAttribute<StateSubactionMembershipImpl, TextualRepresentation> textualRepresentation;
	public static volatile SingularAttribute<StateSubactionMembershipImpl, Boolean> isLibraryElement;
	public static volatile SingularAttribute<StateSubactionMembershipImpl, Boolean> isImplied;
	public static volatile ListAttribute<StateSubactionMembershipImpl, Annotation> ownedAnnotation;
	public static volatile ListAttribute<StateSubactionMembershipImpl, Element> ownedElement;
	public static volatile ListAttribute<StateSubactionMembershipImpl, String> aliasIds;
	public static volatile SingularAttribute<StateSubactionMembershipImpl, VisibilityKind> visibility;
	public static volatile SingularAttribute<StateSubactionMembershipImpl, StateSubactionKind> kind;
	public static volatile ListAttribute<StateSubactionMembershipImpl, Relationship> ownedRelationship;
	public static volatile SingularAttribute<StateSubactionMembershipImpl, String> qualifiedName;
	public static volatile ListAttribute<StateSubactionMembershipImpl, Documentation> documentation;
	public static volatile ListAttribute<StateSubactionMembershipImpl, Element> target;
	public static volatile ListAttribute<StateSubactionMembershipImpl, Element> relatedElement;
	public static volatile SingularAttribute<StateSubactionMembershipImpl, String> name;
	public static volatile SingularAttribute<StateSubactionMembershipImpl, String> shortName;

	public static final String DECLARED_SHORT_NAME = "declaredShortName";
	public static final String OWNED_MEMBER_ELEMENT_ID = "ownedMemberElementId";
	public static final String MEMBER_NAME = "memberName";
	public static final String OWNED_RELATED_ELEMENT = "ownedRelatedElement";
	public static final String SOURCE = "source";
	public static final String MEMBER_ELEMENT_ID = "memberElementId";
	public static final String IS_IMPLIED_INCLUDED = "isImpliedIncluded";
	public static final String DECLARED_NAME = "declaredName";
	public static final String MEMBER_SHORT_NAME = "memberShortName";
	public static final String OWNED_MEMBER_NAME = "ownedMemberName";
	public static final String OWNED_MEMBER_SHORT_NAME = "ownedMemberShortName";
	public static final String ELEMENT_ID = "elementId";
	public static final String TEXTUAL_REPRESENTATION = "textualRepresentation";
	public static final String IS_LIBRARY_ELEMENT = "isLibraryElement";
	public static final String IS_IMPLIED = "isImplied";
	public static final String OWNED_ANNOTATION = "ownedAnnotation";
	public static final String OWNED_ELEMENT = "ownedElement";
	public static final String ALIAS_IDS = "aliasIds";
	public static final String VISIBILITY = "visibility";
	public static final String KIND = "kind";
	public static final String OWNED_RELATIONSHIP = "ownedRelationship";
	public static final String QUALIFIED_NAME = "qualifiedName";
	public static final String DOCUMENTATION = "documentation";
	public static final String TARGET = "target";
	public static final String RELATED_ELEMENT = "relatedElement";
	public static final String NAME = "name";
	public static final String SHORT_NAME = "shortName";

}

